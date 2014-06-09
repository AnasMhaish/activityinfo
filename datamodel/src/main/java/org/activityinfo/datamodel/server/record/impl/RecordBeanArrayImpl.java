package org.activityinfo.datamodel.server.record.impl;

import org.activityinfo.datamodel.shared.record.RecordArray;
import org.activityinfo.datamodel.shared.record.RecordBean;

import javax.annotation.Nonnull;
import java.util.AbstractList;
import java.util.List;

public class RecordBeanArrayImpl<T extends RecordBean> implements RecordArray<T> {

    private final List array;
    private final Class<T> beanClass;

    public RecordBeanArrayImpl(List list, Class<T> beanClass) {
        this.array = list;
        this.beanClass = beanClass;
    }

    @Override
    public int size() {
        return array.size();
    }

    @Nonnull
    @Override
    public T get(int index) {
        return RecordsImpl.createProxy(beanClass, (RecordMapImpl) array.get(index));
    }

    @Override
    public void add(@Nonnull T record) {
        array.add(record);
    }

    @Override
    public List<T> asList() {
        return new AbstractList<T>() {
            @Override
            public T get(int index) {
                return RecordBeanArrayImpl.this.get(index);
            }

            @Override
            public int size() {
                return RecordBeanArrayImpl.this.size();
            }
        };
    }
}
