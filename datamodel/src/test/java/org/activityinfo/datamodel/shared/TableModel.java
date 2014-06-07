package org.activityinfo.datamodel.shared;


import org.activityinfo.datamodel.shared.record.RecordBean;

import java.util.List;

public interface TableModel extends RecordBean {


    public String getName();

    public List<ColumnModel> getColumns();


}
