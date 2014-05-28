package org.activityinfo.core.shared.table;

/**
 * Created by alex on 5/28/14.
 */
public class DoubleArrayColumnView implements ColumnView {
    private double array[];

    public DoubleArrayColumnView(double[] array) {
        this.array = array;
    }

    @Override
    public int numRows() {
        return array.length;
    }

    @Override
    public double getDouble(int row) {
        return array[row];
    }

    @Override
    public int getInt(int row) {
        return (int)array[row];
    }

    @Override
    public String getString(int row) {
        return Double.toString(array[row]);
    }

    @Override
    public Object get(int i) {
        return array[i];
    }
}
