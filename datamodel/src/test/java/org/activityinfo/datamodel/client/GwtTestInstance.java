package org.activityinfo.datamodel.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.junit.client.GWTTestCase;
import org.activityinfo.datamodel.shared.ColumnModel;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.DataRecords;
import org.activityinfo.datamodel.shared.TableModel;


public class GwtTestInstance extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "org.activityinfo.datamodel.DataModelTest";
    }

    public void testCuid() {
        Cuid cuid = Cuid.create("xyz123");
        assertEquals("xyz123", cuid.asString());
    }

    public void testRecord() {
        String json = "{ \"name\": \"My table model\", \"columns\": [ { \"name\": \"A\"} ] } ";

        TableModel tableModel = DataRecords.fromJson(TableModel.class, json);
        assertEquals("My table model", tableModel.getName());
        assertEquals(1, tableModel.getColumns().size());
        assertEquals("A", tableModel.getColumns().get(0).getName());

        ColumnModel b = DataRecords.create(ColumnModel.class);
        b.set(Cuid.create("name"), "B");

        tableModel.getColumns().add(b);

        assertEquals(2, tableModel.getColumns().size());
    }
}
