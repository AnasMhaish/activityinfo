package org.activityinfo.datamodel.shared.table;

import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.record.RecordArray;
import org.activityinfo.datamodel.shared.record.RecordBean;

public interface ColumnModel extends RecordBean {

    RecordArray<FieldPath> getPaths();

    RecordArray<Cuid> getFormClasses();

    String getHeader();

    Cuid getId();
}
