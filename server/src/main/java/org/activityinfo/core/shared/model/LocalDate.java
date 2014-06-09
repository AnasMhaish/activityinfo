package org.activityinfo.core.shared.model;

import org.activityinfo.datamodel.shared.record.RecordBean;


public interface LocalDate extends RecordBean {

    int getYear();

    void setYear(int year);

    int getMonth();

    void setMonth(int month);

    int getDay();

    void setDay(int day);

}
