package org.activityinfo.core.shared.criteria;

import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.form.tree.FieldPath;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Accepts an instance if the given field value matches
 * exactly.
 */
public class FieldCriteria implements Criteria {

    private FieldPath fieldPath;
    private Object value;

    public FieldCriteria(@Nonnull Cuid fieldId, @Nonnull Object value) {
        this.fieldPath = new FieldPath(fieldId);
        this.value = value;
    }

    public FieldCriteria(FieldPath fieldPath, Object value) {
        this.fieldPath = fieldPath;
        this.value = value;
    }

    @Override
    public void accept(CriteriaVisitor visitor) {
        visitor.visitFieldCriteria(this);
    }

    @Override
    public boolean apply(@Nullable FormInstance input) {
        if(fieldPath.isNested()) {
            return true;
        } else {
            return Objects.equals(input.get(fieldPath.getRoot()), value);
        }
    }

    @Override
    public boolean apply(@Nonnull Projection input) {
        return Objects.equals(input.getValue(fieldPath), value);
    }

    public Cuid getFieldId() {
        return fieldPath.getRoot();
    }

    public FieldPath getFieldPath() {
        return fieldPath;
    }

    public Object getValue() {
        return value;
    }
}
