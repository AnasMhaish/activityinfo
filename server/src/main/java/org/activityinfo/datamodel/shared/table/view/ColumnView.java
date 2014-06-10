package org.activityinfo.datamodel.shared.table.view;

import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.datamodel.shared.record.RecordBean;

/**
 * Created by alex on 5/28/14.
 */
public interface ColumnView extends RecordBean {

    FieldPath getId();

    int getFormClassCacheId();

    int numRows();

    double getDouble(int row);

    int getInt(int row);

    String getString(int row);

    Object get(int i);
}
