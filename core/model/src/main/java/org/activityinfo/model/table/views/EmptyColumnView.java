package org.activityinfo.model.table.views;

import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;

import java.util.Date;

public class EmptyColumnView implements ColumnView {

    private ColumnType type;
    private int rowCount;

    public EmptyColumnView(int rowCount, ColumnType type) {
        this.type = type;
        this.rowCount = rowCount;
    }

    @Override
    public ColumnType getType() {
        return type;
    }

    @Override
    public int numRows() {
        return rowCount;
    }

    @Override
    public Object get(int row) {
        return null;
    }

    @Override
    public double getDouble(int row) {
        return Double.NaN;
    }

    @Override
    public String getString(int row) {
        return null;
    }

    @Override
    public Date getDate(int row) {
        return null;
    }

    @Override
    public int getBoolean(int row) {
        return NA;
    }

    @Override
    public String toString() {
        return "[ " + numRows() + " empty values ]";
    }
}
