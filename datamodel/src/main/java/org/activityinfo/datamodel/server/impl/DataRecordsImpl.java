package org.activityinfo.datamodel.server.impl;


import com.google.gson.stream.JsonReader;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.DataRecord;
import org.activityinfo.datamodel.shared.DataRecordBean;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Proxy;

public class DataRecordsImpl {


    private DataRecordsImpl() {
    }

    public static DataRecord create() {
        return new DataRecordMapImpl();
    }


    public static <T extends DataRecordBean> T create(Class<T> beanClass) {
        return createProxy(beanClass, new DataRecordMapImpl());
    }

    public static DataRecord fromJson(String json) {
        try(JsonReader reader = new JsonReader(new StringReader(json))) {
            return parseRecord(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends DataRecordBean> T fromJson(Class<T> beanClass, String json) {
        return createProxy(beanClass, fromJson(json));
    }

    public static <T extends DataRecordBean> T createProxy(Class<T> beanClass, DataRecord record) {
        try {
            return (T) Proxy.newProxyInstance(DataRecordsImpl.class.getClassLoader(),
                    new Class[]{beanClass},
                    new DataRecordBeanProxy(beanClass, record));
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    public static DataRecord parseRecord(JsonReader reader) throws IOException {
        DataRecord record = new DataRecordMapImpl();
        reader.beginObject();
        while(reader.hasNext()) {
            Cuid fieldId = Cuid.create(reader.nextName());
            switch(reader.peek()) {
                case STRING:
                    record.set(fieldId, reader.nextString());
                    break;
                case NUMBER:
                    record.set(fieldId, reader.nextDouble());
                    break;
                case BOOLEAN:
                    record.set(fieldId, reader.nextBoolean());
                case NULL:
                    // ignore
                    break;
                case BEGIN_OBJECT:
                    record.set(fieldId, parseRecord(reader));
                    break;
                default:
                    throw new IllegalStateException("Unexpected token " + reader.peek().name());
            }
        }
        reader.endObject();
        return record;
    }

}
