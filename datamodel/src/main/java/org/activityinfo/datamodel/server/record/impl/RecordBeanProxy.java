package org.activityinfo.datamodel.server.record.impl;

import com.google.common.collect.Maps;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.record.Record;
import org.activityinfo.datamodel.shared.record.RecordArray;
import org.activityinfo.datamodel.shared.record.RecordBean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class RecordBeanProxy implements InvocationHandler {

    private final Record record;
    private Map<Method, MethodImpl> methods = Maps.newHashMap();

    public RecordBeanProxy(Class beanClass, Record record) throws IntrospectionException {
        this.record = record;

        BeanInfo descriptor = Introspector.getBeanInfo(beanClass);
        for(PropertyDescriptor property : descriptor.getPropertyDescriptors()) {
            Cuid fieldId = Cuid.create(property.getName());
            Class<?> type = property.getPropertyType();
            Method readMethod = property.getReadMethod();

            if(type.equals(String.class)) {
                methods.put(readMethod, new StringGetter(fieldId));

            } else if(type.equals(int.class)) {
                methods.put(readMethod, new IntGetter(fieldId));

            } else if(RecordArray.class.isAssignableFrom(type)) {
                // ensure that we have a value
                methods.put(readMethod, new ListGetter(fieldId,
                        readMethod.getGenericReturnType()));

            } else {
                throw new UnsupportedOperationException("returnType: " + readMethod.getReturnType());
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


    private class IntGetter implements MethodImpl {

        private final Cuid fieldId;

        private IntGetter(Cuid fieldId) {
            this.fieldId = fieldId;
        }

        @Override
        public Object invoke(Object[] args) {
            Number value = record.getDouble(fieldId);
            if(value == null) {
                return 0;
            } else {
                return value.intValue();
            }
        }
    }

    private class ListGetter implements MethodImpl {

        private final Cuid fieldId;
        private final Class elementType;

        private ListGetter(Cuid fieldId, Type type) {
            this.fieldId = fieldId;
            ParameterizedType listType = (ParameterizedType) type;
            elementType = (Class) listType.getActualTypeArguments()[0];
            if(!RecordBean.class.isAssignableFrom(elementType)) {
                throw new UnsupportedOperationException("elementType: " + elementType);
            }
        }

        @Override
        public Object invoke(Object[] args) {
            List<Object> value = (List<Object>) record.get(fieldId);
            if(value instanceof List) {
                return new RecordBeanArrayImpl(value, elementType);
            } else {
                return null;
            }
        }
    }
}
