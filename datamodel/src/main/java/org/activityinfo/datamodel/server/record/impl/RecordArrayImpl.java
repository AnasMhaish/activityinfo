package org.activityinfo.datamodel.server.record.impl;

import org.activityinfo.datamodel.shared.record.Record;
import org.activityinfo.datamodel.shared.record.RecordArray;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class RecordArrayImpl<T extends Record> implements RecordArray<T> {

    private final List<T> list;
    private List<T> unmodifiable;

    public RecordArrayImpl(List<T> list) {
        this.list = list;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Nonnull
    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public void add(@Nonnull T record) {
       list.add(record);
    }

    @Override
    public List<T> asList() {
        if(unmodifiable == null) {
            unmodifiable = Collections.unmodifiableList(list);
        }
        return unmodifiable;
    }
}
