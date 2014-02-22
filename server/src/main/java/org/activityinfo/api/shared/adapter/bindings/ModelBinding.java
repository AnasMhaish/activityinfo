package org.activityinfo.api.shared.adapter.bindings;

import com.google.common.collect.Lists;
import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api.shared.model.EntityDTO;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines a two-way binding between a legacy model and a FormInstance
 */
public abstract class ModelBinding<T extends EntityDTO>  {

    private final Cuid classId;
    private final char instanceDomain;
    private List<FieldBinding<? super T>> fieldBindings = Lists.newArrayList();

    public ModelBinding(Cuid classId, char instanceDomain) {
        this.classId = classId;
        this.instanceDomain = instanceDomain;
    }

    public void addField(FieldBinding<T> binding) {
        this.fieldBindings.add(binding);
    }

    public void addField(Cuid fieldId, String propertyName) {
        this.fieldBindings.add(new SimpleFieldBinding(fieldId, propertyName));
    }

    public void addNestedField(Cuid fieldId, char domain, String propertyName) {
        this.fieldBindings.add(new NestedFieldBinding(fieldId, domain, propertyName));
    }

    public Map<String, Object> toChangePropertyMap(FormInstance instance) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", CuidAdapter.getLegacyIdFromCuid(instance.getId()));
        for(FieldBinding<? super T> binding : fieldBindings) {
            binding.populateChangeMap(instance, map);
        }
        return map;
    }

    public FormInstance newInstance(T entity) {
        FormInstance instance = new FormInstance(classId, CuidAdapter.cuid(instanceDomain, entity.getId()));
        for(FieldBinding<? super T> binding : fieldBindings) {
            binding.updateInstanceFromModel(instance, entity);
        }
        return instance;

    }

}