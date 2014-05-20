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

import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.legacy.shared.command.GetAdminEntities;
import org.activityinfo.legacy.shared.command.result.AdminEntityResult;
import org.activityinfo.legacy.shared.model.AdminEntityDTO;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.ui.client.page.entry.admin.AdminEntityProxy;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class GetAdminEntitiesHandlerTest extends CommandTestCase2 {

    private static final int PROVINCE = 1;

    @Test
    public void testRootLevelQuery() throws Exception {

        GetAdminEntities cmd = new GetAdminEntities(PROVINCE);

        AdminEntityResult result = execute(cmd);

        assertThat(result.getData().size(), equalTo(4));
    }

    @Test
    public void testChildQuery() throws Exception {

        GetAdminEntities cmd = new GetAdminEntities(2, 2);

        AdminEntityResult result = execute(cmd);

        assertThat(result.getData().size(), equalTo(3));

        AdminEntityDTO kalehe = result.getData().get(0);
        assertThat(kalehe.getName(), equalTo("Kalehe"));
        assertThat(kalehe.getBounds(), is(not(nullValue())));
        assertThat(kalehe.getBounds().getMinLon(), equalTo(-44d));
        assertThat(kalehe.getBounds().getMinLat(), equalTo(-22d));
        assertThat(kalehe.getBounds().getMaxLon(), equalTo(33.5d));
        assertThat(kalehe.getBounds().getMaxLat(), equalTo(40d));
    }


    @Test
    public void testIdQuery() {
        GetAdminEntities cmd = new GetAdminEntities();
        cmd.setEntityIds(Arrays.asList(10, 11));

        AdminEntityResult result = execute(cmd);

        assertThat(result.getData().size(), equalTo(2));
    }

    @Test
    public void testSiteQuery() throws Exception {
        GetAdminEntities cmd = new GetAdminEntities();
        cmd.setLevelId(1);
        cmd.setFilter(Filter.filter().onActivity(2));

        AdminEntityResult result = execute(cmd);

        assertThat(result.getData().size(), equalTo(2));
    }

}
