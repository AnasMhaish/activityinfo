package org.activityinfo.datamodel.shared.table;

import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.record.RecordBean;

import java.util.Set;

public interface ColumnModel extends RecordBean {

    Set<FieldPath> getPaths();

    Set<Cuid> getFormClasses();

    Criteria getCriteria();

    String getHeader();

    Cuid getId();
}
