package org.activityinfo.core.shared.table;

import org.activityinfo.core.shared.form.tree.FieldPath;

/**
 * Created by alex on 5/28/14.
 */
public interface ColumnView {

    FieldPath getId();

    int getFormClassCacheId();

    int numRows();

    double getDouble(int row);

    int getInt(int row);

    String getString(int row);

    Object get(int i);
}
