package org.activityinfo.core.shared.table;
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

import com.google.common.base.Joiner;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.ui.client.component.table.FieldColumn;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.activityinfo.core.client.PromiseMatchers.assertResolves;
import static org.junit.Assert.assertThat;

/**
 * @author yuriyz on 5/29/14.
 */
@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class TableColumnDataTest extends CommandTestCase2 {

    private static final int PEAR_DATABASE_ID = 1;

    private static final int HEALTH_CENTER_LOCATION_TYPE = 1;

    private static final Cuid HEALTH_CENTER_CLASS = CuidAdapter.locationFormClass(HEALTH_CENTER_LOCATION_TYPE);

    private ResourceLocatorAdaptor resourceLocator;

    @Before
    public final void setup() {
        resourceLocator = new ResourceLocatorAdaptor(getDispatcher());
    }

    @Test
    public void simplePartnerQuery() {

        TableModel tableModel = new TableModel();
        tableModel.setFormClassId(CuidAdapter.activityFormClass(1));
        FieldPath path1 = new FieldPath(CuidAdapter.indicatorField(1));
        FieldPath path2 = new FieldPath(CuidAdapter.partnerField(1),
                CuidAdapter.field(CuidAdapter.partnerFormClass(1), CuidAdapter.NAME_FIELD));

        FieldColumn beneficiaries = new FieldColumn(path1, "Beneficiaries");
        FieldColumn partner = new FieldColumn(path2, "Partner");

        tableModel.setColumns(Arrays.asList(beneficiaries, partner));

        TableColumnDataBuilder tableColumnDataBuilder = new TableColumnDataBuilder(resourceLocator);
        TableColumnData tableColumnData = assertResolves(tableColumnDataBuilder.build(tableModel));

        assertThat(tableColumnData, Matchers.notNullValue());
        assertThat(tableColumnData.getColumnIdToViewMap().get(path1).numRows(), Matchers.equalTo(3));
        assertThat(tableColumnData.getColumnIdToViewMap().get(path2).numRows(), Matchers.equalTo(3));
    }
}
