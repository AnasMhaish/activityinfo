package org.activityinfo.datamodel.server.record.impl;


import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.record.FieldType;
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

    @Override
    public boolean has(Cuid fieldId) {
        return propertyMap.get(fieldId) != null;
    }

    @Override
    public FieldType getFieldType(Cuid fieldId) {
        Object value = propertyMap.get(fieldId);
        if(value == null) {
            return null;
        } else if(value instanceof String) {
            return FieldType.STRING;
        } else if(value instanceof Number) {
            return FieldType.NUMBER;
        } else if(value instanceof Boolean) {
            return FieldType.BOOLEAN;
        } else if(value instanceof Record) {
            return FieldType.RECORD;
        } else if(value instanceof List) {
            return FieldType.ARRAY;
        } else {
            throw new IllegalStateException("Unexpected field value type: " + value.getClass().getName());
        }
    }

    @Override
    public Object get(Cuid fieldId) {
        return propertyMap.get(fieldId);
    }

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
    public final Double getDouble(Cuid fieldId) {
        Object value = propertyMap.get(fieldId);
        if(value instanceof Number) {
            return ((Number) value).doubleValue();
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
    public Record getRecord(Cuid fieldId) {
        Object value = propertyMap.get(fieldId);
        if(value instanceof Record) {
            return (Record) value;
        }
        return null;
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

    public void set(Cuid fieldId, List value) {
        propertyMap.put(fieldId, value);
    }

    public final <T extends RecordBean> T as(Class<T> beanClass) {
        return RecordsImpl.createProxy(beanClass, this);
    }
}
