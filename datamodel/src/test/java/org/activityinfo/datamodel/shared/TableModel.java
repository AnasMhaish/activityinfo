package org.activityinfo.datamodel.shared;


import java.util.List;

public interface TableModel extends DataRecordBean {


    public String getName();

    public List<ColumnModel> getColumns();


}
