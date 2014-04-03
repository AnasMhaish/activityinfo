package org.activityinfo.ui.client.component.table.filter;
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

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.user.client.ui.PopupPanel;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.ui.client.component.table.DataGrid;
import org.activityinfo.ui.client.component.table.FieldColumn;

/**
 * @author yuriyz on 4/3/14.
 */
public class FilterCellAction implements ActionCell.Delegate {

    private final DataGrid<Projection> table;
    private final FieldColumn column;

    public FilterCellAction(DataGrid<Projection> table, FieldColumn column) {
        this.table = table;
        this.column = column;
    }

    @Override
    public void execute(Object object) {
        final TableSectionElement tableHeadElement = table.getTableHeadElement();
        final FilterPanel filterPanel = new FilterPanel(table, column);
        filterPanel.show(new PopupPanel.PositionCallback() {
            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                final TableRowElement row = tableHeadElement.getRows().getItem(0);
                final int columnIndex = table.getColumnIndex(column);
                final TableCellElement cellElement = row.getCells().getItem(columnIndex);
                final int absoluteTop = cellElement.getAbsoluteTop();
                final int absoluteLeft = cellElement.getAbsoluteLeft();
                final int height = cellElement.getOffsetHeight();
                final int width = cellElement.getOffsetWidth();

                filterPanel.getPopup().setWidth(width + "px");
                filterPanel.getPopup().setPopupPosition(absoluteLeft,
                        absoluteTop + height);
            }
        });

    }
}
