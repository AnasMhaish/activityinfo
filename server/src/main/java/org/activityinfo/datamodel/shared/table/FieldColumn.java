package org.activityinfo.datamodel.shared.table;

import com.google.common.collect.Lists;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.datamodel.shared.form.FieldPath;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.ui.client.component.table.renderer.RendererFactory;
import org.activityinfo.ui.client.component.table.renderer.ValueRenderer;

import java.util.List;

/**
 * Column that displays the value of a given field
 */
public class FieldColumn extends Column<Projection, String> {

    private Cuid id = CuidAdapter.newFormClass();
    private FormTree.Node node;
    private List<FieldPath> fieldPaths;
    private String header;
    private Criteria criteria;

    public FieldColumn(FormTree.Node node) {
        super(new TextCell());
        this.node = node;
        this.header = composeHeader(node);
        this.fieldPaths = Lists.newArrayList(node.getPath());
    }

    public FieldColumn(FieldPath fieldPath, String header) {
        super(new TextCell());
        this.header = header;
        this.fieldPaths = Lists.newArrayList(fieldPath);
    }

    public Object getValueAsObject(Projection projection) {
        for (FieldPath path : fieldPaths) {
            final Object value = projection.getValue(path);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String getValue(Projection projection) {
        final Object valueAsObject = getValueAsObject(projection);
        if (valueAsObject != null) {
            final ValueRenderer valueRenderer = RendererFactory.create(getNode().getFieldType());
            return valueRenderer.asString(valueAsObject);
        }

        return "";
    }

    public void addFieldPath(FieldPath path) {
        fieldPaths.add(path);
    }

    public FormTree.Node getNode() {
        return node;
    }

    public List<FieldPath> getFieldPaths() {
        return fieldPaths;
    }

    public String getHeader() {
        return header;
    }

    private String composeHeader(FormTree.Node node) {
        if (node.getPath().isNested()) {
            return node.getDefiningFormClass().getLabel() + " " + node.getField().getLabel();
        } else {
            return node.getField().getLabel();
        }
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public String toString() {
        return "FieldColumn{" +
                "header='" + header + '\'' +
                ", criteria=" + criteria +
                '}';
    }

    public Cuid getId() {
        return id;
    }
}
