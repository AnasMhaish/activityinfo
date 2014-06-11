package org.activityinfo.datamodel.server.record.impl;

import com.google.gwt.core.client.JavaScriptObject;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.record.FieldType;
import org.activityinfo.datamodel.shared.record.Record;

/**
 * An implementation of Record that compiles down to
 * a pure JavaScript object using JavaScript overlay types.
 */
public class RecordMapImpl extends JavaScriptObject implements Record {

    static {
        IsArray.ensurePresent();
    }

    protected RecordJsoImpl() {
    }

    @Override
    public final String getString(Cuid fieldId) {
        return getString(fieldId.asString());
    }

    @Override
    public final native FieldType getFieldType(Cuid fieldId) /*-{
        if(this.hasOwnProperty(fieldId)) {
            var value = this[fieldId];
            if(typeof value === "number") {
                return @org.activityinfo.datamodel.shared.record.FieldType::NUMBER;
            } else if(typeof value == "string") {
                return @org.activityinfo.datamodel.shared.record.FieldType::STRING;
            } else if(value instanceof Array) {
                return @org.activityinfo.datamodel.shared.record.FieldType::ARRAY;
            } else {
                return @org.activityinfo.datamodel.shared.record.FieldType::RECORD;
            }
        } else {
            return null;
        }
    }-*/;

    protected final native JavaScriptObject get(String fieldId) /*-{
        return this[fieldId];
    }-*/;

    public final native String getString(String fieldId) /*-{
        var val = this[fieldId];
        if(typeof val === "string") {
            return this[fieldId];
        }
    }-*/;

    public final native Double getDouble(Cuid fieldId) /*-{
        return +this[fieldId];
    }-*/;

    public final native int getInt(String fieldId, int defaultValue) /*-{
        return +this[fieldId] || defaultValue;
    }-*/;

    public final native int getDouble(String fieldId, int defaultValue) /*-{
        return +this[fieldId] || defaultValue;
    }-*/;

    public final native boolean getBoolean(String fieldId, boolean defaultValue) /*-{
        return !!this[fieldId] || defaultValue;
    }-*/;


    @Override
    public final native Boolean getBoolean(Cuid fieldId) /*-{
        return !!this[fieldId];
    }-*/;

    @Override
    public final native RecordJsoImpl getRecord(Cuid fieldId) /*-{
        var val = this[fieldId];
        if (val === null) {
            return null;
        }
        if (typeof val === 'object') {
            return val;
        }
    }-*/;

    protected final native <T extends Record> RecordArrayJsoImpl<T> getArray(String fieldId) /*-{
        var val = this[fieldId];
        if ($wnd.Array.isArray(val)) {
            return val;
        } else {
            return [];
        }
    }-*/;

    public final native boolean has(Cuid fieldId) /*-{
        return this.hasOwnProperty(fieldId);
    }-*/;

    @Override
    public final native Object get(Cuid fieldId) /*-{
        return this[fieldId];
    }-*/;


    public native final void set(Cuid fieldId, String value) /*-{
        this[fieldId] = value;
    }-*/;


    public native final void set(Cuid fieldId, double value) /*-{
        this[fieldId] = value;
    }-*/;

    public final void set(Cuid fieldId, Record record) {
        set(fieldId, (RecordJsoImpl)record);
    }

    protected final native void set(Cuid fieldId, RecordJsoImpl value) /*-{
        this[fieldId] = value;
    }-*/;

    @Override
    public final native void set(Cuid fieldId, boolean value) /*-{
        this[fieldId] = value;
    }-*/;
}
