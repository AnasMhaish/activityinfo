package org.activityinfo.core.shared.table;

import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.ui.client.component.table.FieldColumn;

import java.util.List;


public class TableModel {

    private Cuid formClassId;
    private List<FieldColumn> columns;
    private Criteria criteria;

    public Cuid getFormClassId() {
        return formClassId;
    }

    public void setFormClassId(Cuid formClassId) {
        this.formClassId = formClassId;
    }

    public List<FieldColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<FieldColumn> columns) {
        this.columns = columns;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }
}
