package org.activityinfo.datamodel.server.record.impl;


import com.google.common.collect.Lists;
import com.google.gson.stream.JsonReader;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.record.Record;
import org.activityinfo.datamodel.shared.record.RecordBean;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Proxy;
import java.util.List;

public class RecordsImpl {


    private RecordsImpl() {
    }

    public static Record create() {
        return new RecordMapImpl();
    }


    public static <T extends RecordBean> T create(Class<T> beanClass) {
        return createProxy(beanClass, new RecordMapImpl());
    }

    public static RecordMapImpl fromJson(String json) {
        try(JsonReader reader = new JsonReader(new StringReader(json))) {
            return parseRecord(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends RecordBean> T fromJson(Class<T> beanClass, String json) {
        return createProxy(beanClass, fromJson(json));
    }

    public static <T extends RecordBean> T createProxy(Class<T> beanClass, RecordMapImpl record) {
        try {
            return (T) Proxy.newProxyInstance(RecordsImpl.class.getClassLoader(),
                    new Class[]{beanClass},
                    new RecordBeanProxy(beanClass, record));
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    public static RecordMapImpl parseRecord(JsonReader reader) throws IOException {
        RecordMapImpl record = new RecordMapImpl();
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
                case BEGIN_ARRAY:
                    record.set(fieldId, parseArray(reader));
                    break;
                default:
                    throw new IllegalStateException("Unexpected token " + reader.peek().name());
            }
        }
        reader.endObject();
        return record;
    }

    private static List<Object> parseArray(JsonReader reader) throws IOException {
        List<Object> objects = Lists.newArrayList();
        reader.beginArray();
        while(reader.hasNext()) {
            switch(reader.peek()) {
                case STRING:
                    objects.add(reader.nextString());
                    break;
                case NUMBER:
                    objects.add(reader.nextDouble());
                    break;
                case BOOLEAN:
                    objects.add(reader.nextBoolean());
                    break;
                case NULL:
                    objects.add(null);
                    break;
                case BEGIN_OBJECT:
                    objects.add(parseRecord(reader));
                    break;
                case BEGIN_ARRAY:
                    objects.add(parseArray(reader));
                default:
                    throw new IllegalStateException("Unexpected token " + reader.peek().name());
            }
        }
        reader.endArray();
        return objects;
    }

}
