package org.activityinfo.datamodel.server.record.impl;


import com.google.gson.stream.JsonReader;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.record.Record;
import org.activityinfo.datamodel.shared.record.RecordBean;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Proxy;

public class RecordsImpl {


    private RecordsImpl() {
    }

    public static Record create() {
        return new RecordMapImpl();
    }


    public static <T extends RecordBean> T create(Class<T> beanClass) {
        return createProxy(beanClass, new RecordMapImpl());
    }

    public static Record fromJson(String json) {
        try(JsonReader reader = new JsonReader(new StringReader(json))) {
            return parseRecord(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends RecordBean> T fromJson(Class<T> beanClass, String json) {
        return createProxy(beanClass, fromJson(json));
    }

    public static <T extends RecordBean> T createProxy(Class<T> beanClass, Record record) {
        try {
            return (T) Proxy.newProxyInstance(RecordsImpl.class.getClassLoader(),
                    new Class[]{beanClass},
                    new RecordBeanProxy(beanClass, record));
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    public static Record parseRecord(JsonReader reader) throws IOException {
        Record record = new RecordMapImpl();
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
