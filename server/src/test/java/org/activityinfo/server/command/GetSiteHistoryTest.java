package org.activityinfo.server.command;

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.shared.command.*;
import org.activityinfo.legacy.shared.command.result.HtmlResult;
import org.activityinfo.legacy.shared.model.AttributeDTO;
import org.activityinfo.server.authentication.AuthenticationModuleStub;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.server.database.hibernate.entity.Attribute;
import org.activityinfo.server.event.sitehistory.SiteHistoryProcessor;
import org.activityinfo.server.forms.SiteHistoryAdapter;
import org.activityinfo.server.forms.timeline.TimelineBuilder;
import org.activityinfo.server.forms.timeline.TimelineEvent;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class GetSiteHistoryTest extends CommandTestCase2 {

    public static final int ECHO = 9921;
    public static final int USAID = 9922;
    public static final int DFID = 9923;

    @Inject
    SiteHistoryAdapter historyAdapter;

    @Inject
    SiteHistoryProcessor historyProcessor;

    @Test
    @OnDataSet("/dbunit/site-history.db.xml")
    public void testOldSiteWithNoDetails() {
        setUser(3);

        HtmlResult html = execute(new GetSiteHistory(3882));
        System.out.println(html.getHtml());
    }

    @Test
    @OnDataSet("/dbunit/site-history.db.xml")
    public void testOldSiteWithGap() {
        setUser(3);

        HtmlResult html = execute(new GetSiteHistory(1297074580));
        System.out.println(html.getHtml());
    }

    @Test
    public void testMonthlySiteHistory() {
        setUser(1);


        Map<String, Object> siteProperties = Maps.newHashMap();
        int siteId = 999;
        siteProperties.put("id", siteId);
        siteProperties.put("activityId", 3);
        siteProperties.put("locationId", 9);
        siteProperties.put("partnerId", 1);
        siteProperties.put("comments", "Insightful commentary");
        siteProperties.put(AttributeDTO.getPropertyName(ECHO), true);

        doUpdate(new CreateSite(siteProperties));

        // update the site
        siteProperties.put("partnerId", 2);
        siteProperties.put("locationId", 3);
        siteProperties.put("comments", "NTR");
        siteProperties.put(AttributeDTO.getPropertyName(USAID), true);

        doUpdate(new UpdateSite(siteId, siteProperties));

        // update a single month
        execute(new UpdateMonthlyReports(siteId,
                Lists.newArrayList(
                        new UpdateMonthlyReports.Change(5, new Month(2014, 1), 100d))));

        // update two months
        execute(new UpdateMonthlyReports(siteId,
                Lists.newArrayList(
                        new UpdateMonthlyReports.Change(5, new Month(2014, 1), 200d),
                        new UpdateMonthlyReports.Change(5, new Month(2014, 2), 100d))));

        // update last months
        execute(new UpdateMonthlyReports(siteId,
                Lists.newArrayList(
                        new UpdateMonthlyReports.Change(5, new Month(2014, 2), 100d))));


        // Build a list of events

        TimelineBuilder timeline = historyAdapter.createTimeline(siteId);

        System.out.println(timeline.toString());

        assertThat(timeline.getEvents(), hasSize(5));

        TimelineEvent create = timeline.getEvents().get(0);
        assertThat(create.getType(), equalTo(TimelineEvent.Type.CREATE));

        TimelineEvent siteUpdate = timeline.getEvents().get(1);
        assertThat(siteUpdate.getType(), equalTo(TimelineEvent.Type.UPDATE));
        assertThat(siteUpdate.getChanges(), hasSize(4));

        TimelineEvent monthlyUpdate = timeline.getEvents().get(4);
        assertThat(monthlyUpdate.getChanges(), hasSize(1));

        // And now render to HTML
        HtmlResult html = execute(new GetSiteHistory(siteId));
        System.out.println(html.getHtml());
    }

    private void doUpdate(SiteCommand command) {
        execute(command);
        historyProcessor.process(command, AuthenticationModuleStub.getCurrentUser().getUserId(), command.getSiteId());
    }

}