package org.activityinfo.core.shared.model;


import org.activityinfo.datamodel.shared.record.RecordBean;

public interface LocalDateRange extends RecordBean {

    LocalDate getStart();
    void setStart(LocalDate date);

    LocalDate getEnd();
    void setEnd(LocalDate date);
}
