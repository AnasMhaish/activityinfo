package org.activityinfo.datamodel.server;


import org.activityinfo.datamodel.shared.TableModel;
import org.activityinfo.datamodel.shared.record.Records;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RecordBeanProxyTest {

    @Test
    public void test() {

        String json = "{ \"name\": \"Jimbob\" } ";


        TableModel tableModel = Records.fromJson(TableModel.class, json);

        assertEquals("Jimbob", tableModel.getName());
    }


}
