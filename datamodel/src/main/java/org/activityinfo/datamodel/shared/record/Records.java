package org.activityinfo.datamodel.shared.record;


import org.activityinfo.datamodel.server.record.impl.RecordsImpl;

public class Records {

    public static Record create() {
        return RecordsImpl.create();
    }

    /**
     * Creates a new, empty RecordBean of the given
     * class
     */
    public static <T extends RecordBean> T create(Class<T> beanClass) {
        return RecordsImpl.create(beanClass);
    }

    /**
     * Creates a new {@code Record} instance from Json
     * @param json the data record serialized as JSON
     * @return a new {@code Record}
     */
    public static Record fromJson(String json) {
        return RecordsImpl.fromJson(json);
    }

    /**
     * Creates a new {@code RecordBean} instance populated
     * with the json-serialized values
     *
     * @param json the data record serialized as JSON
     * @return a new {@code TypedDataRecord} of class {@code T}
     */
    public static <T extends RecordBean> T fromJson(Class<T> recordClass, String json) {
        return RecordsImpl.fromJson(recordClass, json);
    }
}
