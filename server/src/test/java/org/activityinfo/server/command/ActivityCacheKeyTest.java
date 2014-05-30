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

import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.shared.command.*;
import org.activityinfo.legacy.shared.command.result.CreateResult;
import org.activityinfo.legacy.shared.command.result.ListResult;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.LocationDTO;
import org.activityinfo.legacy.shared.model.SchemaDTO;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.server.database.OnDataSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author yuriyz on 5/30/14.
 */
@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class ActivityCacheKeyTest extends CommandTestCase2 {

    public static final int DATABASE_OWNER = 1;
    public static final int ACTIVITY_ID = 1;

    @Before
    public void setUser() {
        setUser(DATABASE_OWNER);
    }

    @Test
    public void cacheKeyIncreased() {
        int originalCacheKey = getActivityCacheKey();

        CreateResult newSite = createNewSite();
        int cacheKeyAfterNewSite = getActivityCacheKey();
        Assert.assertEquals(originalCacheKey + 1, cacheKeyAfterNewSite);

        updateSite(newSite.getNewId());
        int cacheKeyAfterSiteUpdate = getActivityCacheKey();
        Assert.assertEquals(cacheKeyAfterNewSite + 1, cacheKeyAfterSiteUpdate);

        deleteSite(newSite.getNewId());
        int cacheKeyAfterSiteDelete = getActivityCacheKey();
        Assert.assertEquals(cacheKeyAfterSiteUpdate + 1, cacheKeyAfterSiteDelete);
    }

    private void deleteSite(int siteId) {
        execute(new DeleteSite(siteId));
    }

    private CreateResult createNewSite() {
        LocationDTO location = LocationDTOs.newLocation();
        execute(new CreateLocation(location));

        SiteDTO newSite = SiteDTOs.newSite();
        newSite.setLocation(location);

        CreateSite cmd = new CreateSite(newSite);

        CreateResult result = execute(cmd);
        assertThat(result.getNewId(), notNullValue());
        return result;
    }

    private void updateSite(int siteId) {
        ListResult<SiteDTO> result = execute(GetSites.byId(siteId));

        SiteDTO original = result.getData().get(0);
        SiteDTO modified = original.copy();

        assertThat(modified.getId(), equalTo(original.getId()));

        // modify and generate command
        modified.setComments("NEW <b>Commentaire</b>");
        modified.setAttributeValue(1, true);
        modified.setAttributeValue(2, null);
        modified.setAttributeValue(3, true);
        modified.setAttributeValue(4, false);
        modified.setIndicatorValue(2, 995.0);
        modified.setAdminEntity(2, null);

        UpdateSite cmd = new UpdateSite(original, modified);
        assertThat((String) cmd.getChanges().get("comments"),
                equalTo(modified.getComments()));

        execute(cmd);
    }

    private int getActivityCacheKey() {
        SchemaDTO schema = execute(new GetSchema());
        ActivityDTO activityById = schema.getActivityById(ACTIVITY_ID);
        return activityById.getCacheKey();
    }
}
