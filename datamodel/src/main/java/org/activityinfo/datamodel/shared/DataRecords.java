package org.activityinfo.datamodel.shared;


import org.activityinfo.datamodel.server.impl.DataRecordsImpl;

public class DataRecords {

    public static DataRecord create() {
        return DataRecordsImpl.create();
    }

    /**
     * Creates a new, empty DataRecordBean of the given
     * class
     */
    public static <T extends DataRecordBean> T create(Class<T> beanClass) {
        return DataRecordsImpl.create(beanClass);
    }

    /**
     * Creates a new {@code DataRecord} instance from Json
     * @param json the data record serialized as JSON
     * @return a new {@code DataRecord}
     */
    public static DataRecord fromJson(String json) {
        return DataRecordsImpl.fromJson(json);
    }

    /**
     * Creates a new {@code DataRecordBean} instance populated
     * with the json-serialized values
     *
     * @param json the data record serialized as JSON
     * @return a new {@code TypedDataRecord} of class {@code T}
     */
    public static <T extends DataRecordBean> T fromJson(Class<T> recordClass, String json) {
        return DataRecordsImpl.fromJson(recordClass, json);
    }
}
