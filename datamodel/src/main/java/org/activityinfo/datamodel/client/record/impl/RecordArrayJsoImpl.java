package org.activityinfo.datamodel.client.record.impl;

import com.google.gwt.core.client.JavaScriptObject;
import org.activityinfo.datamodel.shared.record.Record;
import org.activityinfo.datamodel.shared.record.RecordArray;

import javax.annotation.Nonnull;
import java.util.AbstractList;
import java.util.List;

/**
 * Implementation of RecordArray that overlays a JavaScript array.
 */
public final class RecordArrayJsoImpl<T extends Record> extends JavaScriptObject implements RecordArray<T> {


    protected RecordArrayJsoImpl() {
    }

    @Override
    public native int size() /*-{
      return this.length;
    }-*/;

    @Nonnull
    @Override
    public native T get(int index) /*-{
        return this[index];
    }-*/;

    @Override
    public native void add(@Nonnull T record) /*-{
      return this.push(record);
    }-*/;

    @Override
    public List<T> asList() {
        return new AbstractList<T>() {
            @Override
            public T get(int index) {
                return RecordArrayJsoImpl.this.get(index);
            }

            @Override
            public int size() {
                return RecordArrayJsoImpl.this.size();
            }
        };
    }
}
