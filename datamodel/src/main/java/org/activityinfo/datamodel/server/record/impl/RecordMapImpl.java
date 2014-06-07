package org.activityinfo.datamodel.server.record.impl;


import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.record.Record;
import org.activityinfo.datamodel.shared.record.RecordBean;

import java.util.HashMap;
import java.util.List;

/**
 * Records are the basic unit of (semi)structured
 * data in ActivityInfo.
 *
 * On the client, Records are implemented with
 * JavaScriptOverlay types,
 *
 */
public class RecordMapImpl implements Record {

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
    public Record getDataRecord(Cuid fieldId) {
        Object value = propertyMap.get(fieldId);
        if(value instanceof Record) {
            return (Record) value;
        }
        return null;
    }

    @Override
    public List<Record> getDataRecordList(Cuid fieldId) {
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
    public final void set(Cuid fieldId, Record record) {
        propertyMap.put(fieldId, record);
    }

    @Override
    public void set(Cuid fieldId, boolean value) {
        propertyMap.put(fieldId, value);
    }

    public final <T extends RecordBean> T as(Class<T> beanClass) {
        return RecordsImpl.createProxy(beanClass, this);
    }
}
