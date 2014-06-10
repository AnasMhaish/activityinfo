package org.activityinfo.core.shared;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.activityinfo.datamodel.shared.form.FieldPath;
import org.activityinfo.datamodel.shared.Cuid;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A projection of a set of {@code FormField}s as well as nested
 * {@code FormField}s reachable through {@code REFERENCE} fields.
 */
public class Projection {

    private final Map<FieldPath, Object> values = Maps.newHashMap();
    private final Cuid rootInstanceId;
    private final Cuid rootClassId;

    public Projection(Cuid rootInstanceId, Cuid rootClassId) {
        assert rootInstanceId != null;
        assert rootClassId != null;

        this.rootInstanceId = rootInstanceId;
        this.rootClassId = rootClassId;
    }

    public Cuid getRootInstanceId() {
        return rootInstanceId;
    }


    public Cuid getRootClassId() {
        return rootClassId;
    }

    public void setValue(FieldPath path, Object value) {
        if(value == null) {
            values.remove(path);
        } else {
            values.put(path, value);
        }
    }

    public Object getValue(FieldPath path) {
        return values.get(path);
    }

    public Set<Cuid> getReferenceValue(FieldPath path) {
        Object value = values.get(path);
        if(value == null) {
            return Collections.emptySet();
        } else if(value instanceof Set) {
            return (Set<Cuid>)value;
        } else {
            return Collections.singleton((Cuid) value);
        }
    }


    public Set<Cuid> getReferenceValue(Cuid fieldId) {
        return getReferenceValue(new FieldPath(fieldId));
    }

    public String getStringValue(FieldPath fieldPath) {
        Object value = values.get(fieldPath);
        if(value instanceof String) {
            return (String) value;
        }
        return null;
    }


    public String getStringValue(Cuid rootFieldId) {
        return getStringValue(new FieldPath(rootFieldId));
    }

    @Override
    public String toString() {
        return "[" + Joiner.on(", ").withKeyValueSeparator("=").join(values) + "]";
    }

    public Map<FieldPath, Object> getValueMap() {
        return values;
    }
}
