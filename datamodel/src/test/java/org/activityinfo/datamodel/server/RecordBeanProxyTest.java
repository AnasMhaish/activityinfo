package org.activityinfo.datamodel.server;


import org.activityinfo.datamodel.shared.TableModel;
import org.activityinfo.datamodel.shared.TableModelJson;
import org.activityinfo.datamodel.shared.record.Records;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RecordBeanProxyTest {

    @Test
    public void test() {

        TableModel tableModel = Records.fromJson(TableModel.class, TableModelJson.SIMPLE);

        assertEquals("My table model", tableModel.getName());
        assertEquals(1, tableModel.getColumns().size());
        assertEquals("A", tableModel.getColumns().get(0).getName());
        assertEquals(35, tableModel.getColumns().get(0).getWidth());
    }


}
