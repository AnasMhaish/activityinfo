package org.activityinfo.datamodel.server.record.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import org.activityinfo.datamodel.client.record.impl.RecordBeanFactory;
import org.activityinfo.datamodel.client.record.impl.RecordJsoImpl;
import org.activityinfo.datamodel.shared.record.RecordBean;

public class RecordsImpl {

    private static final RecordBeanFactory BEAN_FACTORY =
            GWT.create(RecordBeanFactory.class);

    public static RecordJsoImpl create() {
        return RecordJsoImpl.createObject().cast();
    }

    public static <T extends RecordBean> T create(Class<T> beanClass) {
        return BEAN_FACTORY.create(beanClass, create());
    }

    public static RecordJsoImpl fromJson(String json) {
        return JsonUtils.unsafeEval(json);
    }

    public static <T extends RecordBean> T fromJson(Class<T> beanClass, String json) {
        return BEAN_FACTORY.create(beanClass, JsonUtils.safeEval(json));
    }
}
