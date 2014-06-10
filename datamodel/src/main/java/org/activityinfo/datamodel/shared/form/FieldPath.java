package org.activityinfo.datamodel.shared.form;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.activityinfo.datamodel.shared.Cuid;

import java.util.Iterator;
import java.util.List;

/**
 * Describes a path of nested fields
 */
public class FieldPath  {

    private final List<Cuid> path;

    /**
     * The name of the field component
     */
    private String componentName;

    public FieldPath(Cuid rootFieldId, FieldPath relativePath) {
        path = Lists.newArrayList(rootFieldId);
        path.addAll(relativePath.path);
    }


    public FieldPath(FieldPath prefix, Cuid key) {
        path = Lists.newArrayList();
        if(prefix != null) {
            path.addAll(prefix.path);
        }
        path.add(key);
    }

    public FieldPath(FieldPath parent, FieldPath relativePath) {
        this.path = Lists.newArrayList();
        this.path.addAll(parent.path);
        this.path.addAll(relativePath.path);
    }

    public FieldPath(List<Cuid> fieldIds) {
        path = Lists.newArrayList(fieldIds);
    }

    public FieldPath(Cuid... fieldIds) {
        path = Lists.newArrayList(fieldIds);
    }


    public boolean isNested() {
        return path.size() > 1;
    }


    public int getDepth() {
        return path.size();
    }

    public FieldPath relativeTo(Cuid rootFieldId) {
        Preconditions.checkArgument(path.get(0).equals(rootFieldId));
        return new FieldPath(path.subList(1, path.size()));
    }

    public Cuid getLeafId() {
        return path.get(path.size()-1);
    }

    /**
     * Creates a new FieldPath that describes the nth ancestor of this
     * path. n=1 is the direct parent, n=2, grand parent, etc.
     */
    public FieldPath ancestor(int n) {
        return new FieldPath(path.subList(0, path.size()-n));
    }

    public Cuid getRoot() {
        return path.get(0);
    }

    public boolean isDescendantOf(Cuid fieldId) {
        return path.get(0).equals(fieldId);
    }

    public FieldPath component(String componentName) {
        FieldPath path = new FieldPath(this.path);
        path.componentName = componentName;
        return path;
    }

    public String getComponentName() {
        return componentName;
    }

    public List<Cuid> getPath() {
        return path;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(Cuid fieldId : path) {
            if(s.length() > 0) {
                s.append(".");
            }
            s.append(fieldId.asString());
        }
        return s.toString();
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FieldPath)) {
            return false;
        }
        FieldPath otherPath = (FieldPath) obj;

        return path.equals(otherPath.path);
    }

    public Iterator<Cuid> iterator() {
        return path.iterator();
    }
}
