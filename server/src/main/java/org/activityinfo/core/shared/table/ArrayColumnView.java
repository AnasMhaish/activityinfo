package org.activityinfo.core.shared.table;

/**
 * Created by alex on 5/28/14.
 */
public class ArrayColumnView implements ColumnView {

    private final Object[] values;

    public ArrayColumnView(Object[] values) {
        this.values = values;
    }

    @Override
    public int numRows() {
        return values.length;
    }

    @Override
    public double getDouble(int row) {
        Object value = values[row];
        if(value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            return Double.NaN;
        }
    }

    @Override
    public int getInt(int row) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getString(int row) {
        return "" + values[row];
    }

    @Override
    public Object get(int i) {
        return values[i];
    }
}
