package org.activityinfo.datamodel.server;


import com.google.gwt.core.client.JsonUtils;
import org.activityinfo.datamodel.client.impl.DataRecordJsoImpl;
import org.activityinfo.datamodel.shared.DataRecord;
import org.activityinfo.datamodel.shared.DataRecords;
import org.activityinfo.datamodel.shared.TableModel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class DataRecordBeanProxyTest {

    @Test
    public void test() {

        String json = "{ \"name\": \"Jimbob\" } ";


        TableModel tableModel = DataRecords.fromJson(TableModel.class, json);

        assertEquals("Jimbob", tableModel.getName());
    }


}
