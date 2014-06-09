package org.activityinfo.core.shared.model;

import org.activityinfo.datamodel.shared.record.RecordBean;

/**
 * Geographic Point
 */
public interface GeoPoint extends RecordBean {

    double getLatitude();

    double getLongitude();

    void setLatitude(double latitude);

    void setLongitude(double longitude);

}
