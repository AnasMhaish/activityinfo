package org.activityinfo.server.forms;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.command.Month;
import org.activityinfo.legacy.shared.model.IndicatorDTO;
import org.activityinfo.legacy.shared.util.JsonUtil;
import org.activityinfo.server.database.hibernate.entity.Site;
import org.activityinfo.server.database.hibernate.entity.SiteHistory;
import org.activityinfo.server.forms.timeline.TimelineBuilder;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.activityinfo.legacy.shared.adapter.CuidAdapter.cuid;
import static org.activityinfo.legacy.shared.adapter.CuidAdapter.monthlyReportInstanceId;

public class SiteHistoryAdapter {


    private final EntityManager entityManager;
    private final SiteFormAdapterFactory adapterFactory;
    private final JsonParser jsonParser;

    @Inject
    public SiteHistoryAdapter(EntityManager entityManager, SiteFormAdapterFactory adapterFactory,
                              JsonParser jsonParser) {
        this.entityManager = entityManager;
        this.adapterFactory = adapterFactory;
        this.jsonParser = jsonParser;
    }

    public TimelineBuilder createTimeline(int siteId) {
        return createTimeline(entityManager.find(Site.class, siteId));
    }

    public TimelineBuilder createTimeline(Site site) {

        // fetch/create the adapter for Sites
        SiteFormAdapter adapter = adapterFactory.create(site.getActivity().getId());

        // and the snapshots of the site...
        List<SiteHistory> historyList = entityManager
                .createQuery("select h from SiteHistory h " +
                             "left join fetch h.user " +
                             "where h.site = :site " +
                             "order by h.timeCreated", SiteHistory.class)
                .setParameter("site", site)
                .getResultList();


        // build the timeline
        TimelineBuilder timeline = new TimelineBuilder(adapter);
        FormInstance siteSnapshot = new FormInstance(
                cuid(CuidAdapter.SITE_DOMAIN, site.getId()),
                adapter.getSiteForm().getId());

        Map<Month, FormInstance> monthlySnapshots = Maps.newHashMap();

        for (SiteHistory history : historyList) {
            Map<String, Object> changeMap = JsonUtil.decodeMap((JsonObject) jsonParser.parse(history.getJson()));
            Set<Month> periods = getMonth(changeMap);
            if(periods.isEmpty()) {
                adapter.applySiteChanges(siteSnapshot, changeMap);
                timeline.addSnapshot(history.getTimeCreated(), history.getUser(), siteSnapshot.copy());

            } else {
                for(Month period : periods) {
                    FormInstance monthlySnapshot = monthlySnapshots.get(period);
                    if(monthlySnapshot == null) {
                        monthlySnapshot = new FormInstance(
                                monthlyReportInstanceId(history.getSite().getId(), period),
                                adapter.getMonthlyForm().getId());
                        monthlySnapshots.put(period, monthlySnapshot);
                    }
                    adapter.applyMonthlyReportChanges(monthlySnapshot, period, changeMap);
                    timeline.addSnapshot(history.getTimeCreated(), history.getUser(), monthlySnapshot.copy());
                }
            }
        }

        return timeline;
    }

    public Set<Month> getMonth(Map<String, Object> changeMap) {
        Set<Month> months = Sets.newHashSet();
        for(String propertyName : changeMap.keySet()) {
            if(propertyName.startsWith(IndicatorDTO.PROPERTY_PREFIX)) {
                Month month = IndicatorDTO.monthForPropertyName(propertyName);
                if(month != null) {
                    months.add(month);
                }
            }
        }
        return months;
    }

}
