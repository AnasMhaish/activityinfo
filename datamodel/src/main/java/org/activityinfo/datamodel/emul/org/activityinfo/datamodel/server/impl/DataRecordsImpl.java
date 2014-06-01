package org.activityinfo.datamodel.server.impl;

import com.google.gwt.core.client.JsonUtils;

import com.google.gson.stream.JsonReader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import org.activityinfo.datamodel.client.impl.DataRecordBeanFactory;
import org.activityinfo.datamodel.client.impl.DataRecordJsoImpl;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.DataRecord;
import org.activityinfo.datamodel.shared.DataRecordBean;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Proxy;

public class DataRecordsImpl {

    private static final DataRecordBeanFactory BEAN_FACTORY =
            GWT.create(DataRecordBeanFactory.class);

    public static DataRecordJsoImpl create() {
        return DataRecordJsoImpl.createObject().cast();
    }

    public static <T extends DataRecordBean> T create(Class<T> beanClass) {
        return BEAN_FACTORY.create(beanClass, create());
    }

    public static DataRecordJsoImpl fromJson(String json) {
        return JsonUtils.unsafeEval(json);
    }

    public static <T extends DataRecordBean> T fromJson(Class<T> beanClass, String json) {
        return BEAN_FACTORY.create(beanClass, JsonUtils.safeEval(json));
    }
}
