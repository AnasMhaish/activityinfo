package org.activityinfo.datamodel.client;

import com.google.gwt.junit.client.GWTTestCase;
import org.activityinfo.datamodel.shared.ColumnModel;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.TableModel;
import org.activityinfo.datamodel.shared.TableModelJson;
import org.activityinfo.datamodel.shared.record.FieldType;
import org.activityinfo.datamodel.shared.record.Records;


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

        TableModel tableModel = Records.fromJson(TableModel.class, TableModelJson.SIMPLE);
        assertEquals("My table model", tableModel.getName());
        assertEquals(FieldType.STRING, tableModel.getFieldType(Cuid.create("name")));
        assertTrue(tableModel.has(Cuid.create("name")));

        assertEquals(1, tableModel.getColumns().size());
        assertEquals(FieldType.ARRAY, tableModel.getFieldType(Cuid.create("columns")));

        assertEquals("A", tableModel.getColumns().get(0).getName());
        assertEquals(35, tableModel.getColumns().get(0).getWidth());

        ColumnModel b = Records.create(ColumnModel.class);
        b.set(Cuid.create("name"), "B");

        tableModel.getColumns().add(b);

        assertEquals(2, tableModel.getColumns().size());
    }
}
