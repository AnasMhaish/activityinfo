package org.activityinfo.datamodel.client.record.impl;

import com.google.gwt.core.client.JavaScriptObject;
import org.activityinfo.datamodel.shared.record.RecordBean;

/**
 * Interface responsible for "creating" RecordBeans.
 *
 * Since we are implementing DataRecordBeans as JavaScriptObjects on the clients,
 * the implementation of this class simply casts the given JavaScriptObject
 * first to the overlay type we generate for the RecordBean interfaces, and then
 * the interface type:
 *
 * <blockquote><pre>
 * {@code
 * if(recordClass == TableModel.class) return (T)(TableModel_JsoImpl)jso;
 * if(recordClass == ColumnModel.class) return (T)(ColumnModel_JsoImpl)jso;
 * // etc
 * }
 * </blockquote></pre>
 *
 */
public interface RecordBeanFactory {

    <T extends RecordBean> T create(Class<T> beanClass, JavaScriptObject jso);
}
