package org.activityinfo.datamodel.shared.table.view;

import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.datamodel.shared.table.view.ColumnView;

/**
 * Created by alex on 5/28/14.
 */
public class DoubleArrayColumnView implements ColumnView {

    private FieldPath id;
    private int formClassCacheId;;
    private double array[];

    public DoubleArrayColumnView(double[] array) {
        this.array = array;
    }

    public FieldPath getId() {
        return id;
    }

    public void setId(FieldPath id) {
        this.id = id;
    }

    public int getFormClassCacheId() {
        return formClassCacheId;
    }

    public void setFormClassCacheId(int formClassCacheId) {
        this.formClassCacheId = formClassCacheId;
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
