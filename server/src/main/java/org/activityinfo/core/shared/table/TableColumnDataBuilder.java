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

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.table.provider.ColumnViewProvider;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.ui.client.component.table.FieldColumn;

import java.util.List;

/**
 * @author yuriyz on 5/29/14.
 */
public class TableColumnDataBuilder {

    private final ColumnViewProvider viewProvider;
    private final ResourceLocator resourceLocator;

    public TableColumnDataBuilder(ColumnViewProvider viewProvider, ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
        this.viewProvider = viewProvider;
    }

    public Promise<TableColumnData> build(final TableModel tableModel) {
        final Promise<FormClass> formClass = resourceLocator.getFormClass(tableModel.getFormClassId());
        final List<Promise<? extends ColumnView>> promises = Lists.newArrayList();
        for (FieldColumn column : tableModel.getColumns()) {
            promises.add(viewProvider.view(column, formClass.get()));
        }

        return Promise.waitAll(promises).then(new Supplier<TableColumnData>() {
            @Override
            public TableColumnData get() {
                TableColumnData tableData = new TableColumnData();
                for (Promise<? extends ColumnView> promise : promises) {
                    tableData.getColumnIdToViewMap().put(promise.get().getId(), promise.get());
                }
                return tableData;
            }
        });

    }
}
