package org.activityinfo.datamodel.server.record.impl;

import com.google.common.collect.Maps;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.record.Record;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class RecordBeanProxy implements InvocationHandler {

    private final Record record;
    private Map<Method, MethodImpl> methods = Maps.newHashMap();

    public RecordBeanProxy(Class beanClass, Record record) throws IntrospectionException {
        this.record = record;

        BeanInfo descriptor = Introspector.getBeanInfo(beanClass);
        for(PropertyDescriptor property : descriptor.getPropertyDescriptors()) {
            if(property.getPropertyType().equals(String.class)) {
                methods.put(property.getReadMethod(), new StringGetter(Cuid.create(property.getName())));
            } else {
                // ensure that we have a value
                methods.put(property.getReadMethod(), new ListGetter(Cuid.create(property.getName())));
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return methods.get(method).invoke(args);
    }

    private interface MethodImpl {
        public Object invoke(Object args[]);
    }

    private class StringGetter implements MethodImpl {

        private final Cuid fieldId;

        private StringGetter(Cuid fieldId) {
            this.fieldId = fieldId;
        }

        @Override
        public Object invoke(Object[] args) {
            return record.getString(fieldId);
        }
    }

    private class ListGetter implements MethodImpl {

        private final Cuid fieldId;

        private ListGetter(Cuid fieldId) {
            this.fieldId = fieldId;
        }

        @Override
        public Object invoke(Object[] args) {
            List<Record> value = record.getDataRecordList(fieldId);
            if(value == null) {
                return null;
            }
            return value;
        }
    }
}
