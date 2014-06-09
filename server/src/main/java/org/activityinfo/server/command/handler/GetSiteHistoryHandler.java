package org.activityinfo.server.command.handler;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;
import com.google.inject.Inject;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.form.FormFieldType;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.command.GetSiteHistory;
import org.activityinfo.legacy.shared.command.Month;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.HtmlResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.exception.IllegalAccessCommandException;
import org.activityinfo.server.database.hibernate.entity.Site;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.forms.LabelProvider;
import org.activityinfo.server.forms.SiteHistoryAdapter;
import org.activityinfo.server.forms.timeline.FieldChange;
import org.activityinfo.server.forms.timeline.TimelineBuilder;
import org.activityinfo.server.forms.timeline.TimelineEvent;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.activityinfo.legacy.shared.adapter.CuidAdapter.monthFromReportId;

public class GetSiteHistoryHandler implements CommandHandler<GetSiteHistory> {

    private static final LocalDate HISTORY_AVAILABLE_FROM = new LocalDate(2012, 12, 20);

    private final EntityManager entityManager;
    private final PermissionOracle permissionOracle;
    private final SiteHistoryAdapter historyAdapter;
    private final LabelProvider labelProvider;
    private final MemcacheService memcache;


    @Inject
    public GetSiteHistoryHandler(EntityManager entityManager,
                                 PermissionOracle permissionOracle,
                                 SiteHistoryAdapter historyAdapter,
                                 LabelProvider labelProvider,
                                 MemcacheService memcache) {
        this.entityManager = entityManager;
        this.permissionOracle = permissionOracle;
        this.historyAdapter = historyAdapter;
        this.labelProvider = labelProvider;
        this.memcache = memcache;
    }

    @Override
    public CommandResult execute(GetSiteHistory cmd, User user) throws CommandException {

        // ensure that the user has permission to access this site
        Site site = entityManager.getReference(Site.class, cmd.getSiteId());
        if(!permissionOracle.isVisible(site, user)) {
            throw new IllegalAccessCommandException();
        }

        // do we have this cached?
        // use the site's update time as a key so that we don't risk
        // get stale data
        Object cachedHtml = memcache.get(cacheKey(site));
        if(cachedHtml instanceof String) {
            return new HtmlResult((String)cachedHtml);
        }

        TimelineBuilder timeline = historyAdapter.createTimeline(site);
        Map<Cuid, String> labels = labelProvider.query(timeline.getReferences());

        // Was this site created before we started recording detailed history?
        boolean legacySite = site.getDateCreated().before(HISTORY_AVAILABLE_FROM.atMidnightInMyTimezone());

        // Render the events that we have
        Renderer renderer = new Renderer(labels, legacySite);
        for(TimelineEvent event : Lists.reverse(timeline.getEvents())) {
            renderer.render(event);
        }

        // for sites that were created before we started tracking history, provide
        // some explanation
        if(legacySite) {

            if(timeline.getEvents().isEmpty() && !site.getDateCreated().equals(site.getDateEdited())) {
                // there have been no changes since 2012-12-20
                // but we do know when it was last edited
                renderer.message("legacy", I18N.MESSAGES.siteHistoryUnavailable(site.getDateCreated(), site.getDateEdited()));

            } else {
                // warn that there is gap
                if(!timeline.getEvents().isEmpty()) {
                    renderer.message("warning", I18N.MESSAGES.siteHistoryAvailableFrom(HISTORY_AVAILABLE_FROM.atMidnightInMyTimezone()));
                }

                // provide the creation date at least
                renderer.message("legacy", I18N.MESSAGES.siteHistoryCreationDate(site.getDateCreated()));
            }
        }

        String html = renderer.finish();
        memcache.put(cacheKey(site), html);

        return new HtmlResult(html);
    }

    private String cacheKey(Site site) {
        return getClass().getName() + ":" + site.getId() + ":" + site.getTimeEdited();
    }

    private static class Renderer {
        private StringBuilder html = new StringBuilder();
        private Map<Cuid, String> labels = Maps.newHashMap();
        private boolean legacySite;
        private Escaper escaper;

        private Renderer(Map<Cuid, String> labels, boolean legacySite) {
            this.labels = labels;
            this.legacySite = legacySite;
            html.append("<ul class='timeline'>\n");
        }

        public void message(String className, String message) {
            escaper = HtmlEscapers.htmlEscaper();
            html.append("<li class='timeline-").append(className).append("'>")
                    .append("<span class='timeline-message'>")
                    .append(escaper.escape(message))
                    .append("</span>")
                    .append("</li>\n");
        }
        
        public void render(TimelineEvent event) {
            html.append("<li class='timeline-event'>");
            html.append(message(event));

            if (event.getType() == TimelineEvent.Type.UPDATE &&
                !event.getChanges().isEmpty()) {
                html.append("\n<ul class='timeline-details'>\n");
                for (FieldChange change : event.getChanges()) {
                    html.append("<li class='timeline-field-change'>");
                    describeChange(change);
                    html.append("</li>\n");
                }
                html.append("</ul>");
            }
            html.append("</li>\n");
        }

        private void describeChange(FieldChange change) {
            html.append(change.getField().getLabel());
            html.append(": ");

            html.append(formatValue(change.getField(), change.getNewValue()));

            if (change.getField().getType() == FormFieldType.QUANTITY) {
                html.append(" ");
                html.append(change.getField().getUnit());
            }
            html.append(" (");
            if (change.getOldValue() == null) {
                html.append(I18N.MESSAGES.siteHistoryOldValueBlank());
            } else {
                html.append(I18N.MESSAGES.siteHistoryOldValue(formatValue(change.getField(), change.getOldValue())));
            }
            html.append(")");
        }

        private String formatValue(FormField field, Object value) {
            if(field.getType().equals(FormFieldType.REFERENCE)) {
                StringBuilder sb = new StringBuilder();
                Set<Cuid> references = asReferenceSet(value);
                for(Cuid referenceId : references) {
                    if(labels.containsKey(referenceId)) {
                        if(sb.length() > 0) {
                            sb.append(", ");
                        }
                        sb.append(labels.get(referenceId));
                    }
                }
                return sb.toString();
            } else {
                return value.toString();
            }
        }

        private Set<Cuid> asReferenceSet(Object value) {
            if(value instanceof Set) {
                return (Set<Cuid>) value;
            } else if(value instanceof Cuid) {
                return Sets.newHashSet((Cuid)value);
            } else {
                return Collections.emptySet();
            }
        }

        private String message(TimelineEvent event) {
            if(event.getInstance().getId().getDomain() == CuidAdapter.SITE_DOMAIN) {
                if(event.getType() == TimelineEvent.Type.CREATE && !legacySite) {
                    return I18N.MESSAGES.siteHistoryCreated(formatTime(event.getTime()),
                            event.getUser().getName(),
                            event.getUser().getEmail());
                } else {
                    return I18N.MESSAGES.siteHistoryUpdated(formatTime(event.getTime()),
                            event.getUser().getName(),
                            event.getUser().getEmail());
                }
            } else if(event.getInstance().getId().getDomain() == CuidAdapter.MONTHLY_REPORT_INSTANCE) {
                Month month = monthFromReportId(event.getInstance().getId());
                Date monthDate = month.toLocalDate().atMidnightInMyTimezone();

                switch(event.getType()) {
                    case CREATE:
                        return I18N.MESSAGES.siteHistoryNewMonthlyReport(formatTime(event.getTime()),
                                event.getUser().getName(),
                                event.getUser().getEmail(),
                                monthDate);
                    case UPDATE:
                        return I18N.MESSAGES.siteHistoryNewMonthlyReport(formatTime(event.getTime()),
                                event.getUser().getName(),
                                event.getUser().getEmail(),
                                monthDate);
                }
            }
            throw new IllegalStateException("id: " + event.getInstance().getId() + ", type: " + event.getType());

        }

        private Date formatTime(long time) {
            return new Date(time);
        }

        private String finish() {
            html.append("</li>");
            return html.toString();
        }
    }
}
