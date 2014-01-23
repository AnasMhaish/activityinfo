package org.activityinfo.ui.full.client.page.entry;

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

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.TreeStore;
import org.activityinfo.api.shared.command.GetAdminEntities;
import org.activityinfo.api.shared.command.GetSchema;
import org.activityinfo.api.shared.model.DTOs;
import org.activityinfo.ui.full.client.dispatch.DispatcherStub;
import org.activityinfo.ui.full.client.page.entry.grouping.AdminGroupingModel;
import org.junit.Test;

public class SiteAdminTreeLoaderTest {

    @Test
    public void load() {

        DispatcherStub dispatcher = new DispatcherStub();
        dispatcher.setResult(new GetSchema(), DTOs.PEAR.SCHEMA);
        dispatcher.setResult(new GetAdminEntities(1), DTOs.PROVINCES);

        SiteAdminTreeLoader loader = new SiteAdminTreeLoader(dispatcher,
                new AdminGroupingModel(1));

        new TreeStore<ModelData>(loader);

        loader.load();
    }

}
