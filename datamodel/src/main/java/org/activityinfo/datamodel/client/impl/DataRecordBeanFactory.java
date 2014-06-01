package org.activityinfo.datamodel.client.impl;

import com.google.gwt.core.client.JavaScriptObject;
import org.activityinfo.datamodel.shared.DataRecordBean;

/**
 * Interface responsible for "creating" DataRecordBeans.
 *
 * Since we are implementing DataRecordBeans as JavaScriptObjects on the clients,
 * the implementation of this class simply casts the given JavaScriptObject
 * first to the overlay type we generate for the DataRecordBean interfaces, and then
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
public interface DataRecordBeanFactory {

    <T extends DataRecordBean> T create(Class<T> beanClass, JavaScriptObject jso);
}
