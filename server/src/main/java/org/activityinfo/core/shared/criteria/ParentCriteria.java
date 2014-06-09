package org.activityinfo.core.shared.criteria;

import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.form.FormInstance;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Criteria that filters on the parent's id
 */
public class ParentCriteria implements Criteria {

    private final Cuid parentId;

    private ParentCriteria(Cuid id) {
        this.parentId = id;
    }

    @Override
    public void accept(CriteriaVisitor visitor) {
        visitor.visitParentCriteria(this);
    }

    public boolean selectsRoot() {
        return parentId == null;
    }

    public Cuid getParentId() {
        return parentId;
    }

    @Override
    public boolean apply(@Nonnull FormInstance instance) {
        return Objects.equals(parentId, instance.getParentId());
    }

    @Override
    public boolean apply(@Nonnull Projection projection) {
        // todo
        return true;
    }

    public static ParentCriteria isRoot() {
        return new ParentCriteria(null);
    }

    public static ParentCriteria isChildOf(Cuid id) {
        return new ParentCriteria(id);
    }
}
