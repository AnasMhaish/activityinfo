package org.activityinfo.datamodel.shared;


import org.activityinfo.datamodel.shared.record.RecordBean;

import java.util.List;

/**
 * The TableModel defines a projection from a set of FormModels
 * to a flat list of columns.
 */
public interface TableModel extends RecordBean {

    public String getName();

    public List<ColumnModel> getColumns();

}
