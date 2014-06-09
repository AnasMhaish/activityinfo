package org.activityinfo.datamodel.shared;


import org.activityinfo.datamodel.shared.record.RecordBean;

public interface ColumnModel extends RecordBean {

    String getName();

    String getType();

    int getWidth();


}
