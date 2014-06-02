package org.activityinfo.datamodel.server.impl;


import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.DataRecord;
import org.activityinfo.datamodel.shared.DataRecordBean;

import java.util.HashMap;
import java.util.List;

/**
 * DataRecords are the basic unit of (semi)structured
 * data in ActivityInfo.
 *
 * On the client, DataRecords are implemented with
 * JavaScriptOverlay types,
 *
 */
public class DataRecordMapImpl implements DataRecord {

    private final HashMap<Cuid, Object> propertyMap = new HashMap<>();


    /**
     * Returns the value for the field with the given {@code fieldId}
     * as a String, or {@code null} if there is no value for this
     * field.
     *
     */
    @Override
    public final String getString(Cuid fieldId) {
        Object value = propertyMap.get(fieldId);
        if(value instanceof String) {
            return (String) value;
        } else if(value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        return null;
    }

    /**
     *
     * Returns the value of the field with the given {@code fieldId}
     * as a double value, or {@code null} if there is no value for this
     * field, or the field value is not a number.
     *
     * @param fieldId
     * @return
     */
    @Override
    public final Double getNumber(Cuid fieldId) {
        Object value = propertyMap.get(fieldId);
        if(value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if(value instanceof String) {
            String stringValue = (String)value;
            switch (stringValue) {
                case "NaN":
                    return Double.NaN;
                case "Inf":
                    return Double.POSITIVE_INFINITY;
                case "-Inf":
                    return Double.NEGATIVE_INFINITY;
                default:
                    try {
                        return Double.parseDouble(stringValue);
                    } catch (NumberFormatException e) {
                        return null;
                    }
            }
        }
        return null;
    }

    @Override
    public Boolean getBoolean(Cuid fieldId) {
        Object value = propertyMap.get(fieldId);
        if(value instanceof Boolean) {
            return (Boolean)value;
        } else if(value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        }
        return null;
    }

    @Override
    public DataRecord getDataRecord(Cuid fieldId) {
        Object value = propertyMap.get(fieldId);
        if(value instanceof DataRecord) {
            return (DataRecord) value;
        }
        return null;
    }

    @Override
    public List<DataRecord> getDataRecordList(Cuid fieldId) {
        Object value = propertyMap.get(fieldId);
        if(value instanceof List) {
            return (List)value;
        } else {
            return null;
        }
    }

    /**
     * Sets the value
     * @param fieldId
     * @param value
     */
    @Override
    public final void set(Cuid fieldId, String value) {
        propertyMap.put(fieldId, value);
    }

    @Override
    public final void set(Cuid fieldId, double value) {
        propertyMap.put(fieldId, value);
    }

    @Override
    public final void set(Cuid fieldId, DataRecord record) {
        propertyMap.put(fieldId, record);
    }

    @Override
    public void set(Cuid fieldId, boolean value) {
        propertyMap.put(fieldId, value);
    }

    public final <T extends DataRecordBean> T as(Class<T> beanClass) {
        return DataRecordsImpl.createProxy(beanClass, this);
    }
}
