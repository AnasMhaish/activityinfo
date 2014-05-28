package org.activityinfo.core.shared.table;

/**
 * Created by alex on 5/28/14.
 */
public interface ColumnView {

    int numRows();

    double getDouble(int row);

    int getInt(int row);

    String getString(int row);

    Object get(int i);
}
