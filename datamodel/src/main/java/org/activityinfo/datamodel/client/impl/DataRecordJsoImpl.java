package org.activityinfo.datamodel.client.impl;

import com.google.gwt.core.client.JavaScriptObject;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.DataRecord;

import java.util.List;

/**
 * An implementation of DataRecord that compiles down to
 * a pure JavaScript object using JavaScript overlay types.
 */
public class DataRecordJsoImpl extends JavaScriptObject implements DataRecord {

    static {
        IsArray.ensurePresent();
    }

    protected DataRecordJsoImpl() {
    }

    @Override
    public final String getString(Cuid fieldId) {
        return getString(fieldId.asString());
    }

    protected final native JavaScriptObject get(String fieldId) /*-{
        return this[fieldId];
    }-*/;

    public final native String getString(String fieldId) /*-{
        return this[fieldId];
    }-*/;

    
    public final native Double getNumber(Cuid fieldId) /*-{
        // todo: special strings?
        return +this[fieldId];
    }-*/;

    @Override
    public final native Boolean getBoolean(Cuid fieldId) /*-{
        return !!this[fieldId];
    }-*/;

    @Override
    public final native DataRecordJsoImpl getDataRecord(Cuid fieldId) /*-{
        var val = this[fieldId];
        if (val === null) {
          return null;
        }
        if (typeof val === 'object') {
          return val;
        }
    }-*/;

    @Override
    @SuppressWarnings("unchecked")
    public final List<DataRecord> getDataRecordList(Cuid fieldId) {
        return (List)this.<DataRecordJsoImpl>getList(fieldId.asString());
    }

    protected final native <T extends JavaScriptObject> DataRecordJsoListImpl<T> getList(String fieldId) /*-{
      var val = this[fieldId];
      if ($wnd.Array.isArray(val)) {
        return val;
      } else {
        return [];
      }
    }-*/;

//    protected final native <T extends JavaScriptObject> DataRecordJsoListImpl<T> ensureArray(String field) /*-{
//      var val = this[fieldId];
//      if (!$wnd.Array.isArray(val)) {
//        val = [];
//        this[fieldId] = val;
//      }
//      return val;
//    }-*/;

    public final native boolean has(Cuid fieldId) /*-{
        var val = this[fieldId];
        var type = typeof val;
        return type === 'object' || type == 'number' || type == 'string';
    }-*/;

    
    public native final void set(Cuid fieldId, String value) /*-{
        this[fieldId] = value;
    }-*/;

    
    public native final void set(Cuid fieldId, double value) /*-{
        this[fieldId] = value;
    }-*/;

    public final void set(Cuid fieldId, DataRecord record) {
        set(fieldId, (DataRecordJsoImpl)record);
    }

    protected final native void set(Cuid fieldId, DataRecordJsoImpl value) /*-{
        this[fieldId] = value;
    }-*/;

    @Override
    public final native void set(Cuid fieldId, boolean value) /*-{
        this[fieldId] = value;
    }-*/;


}
