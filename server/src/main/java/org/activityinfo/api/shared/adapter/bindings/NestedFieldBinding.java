package org.activityinfo.api.shared.adapter.bindings;

import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api.shared.model.EntityDTO;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;

import java.util.Map;

import static org.activityinfo.api.shared.adapter.CuidAdapter.getLegacyIdFromCuid;

/**
* Created by alex on 2/22/14.
*/
public class NestedFieldBinding implements FieldBinding<EntityDTO> {
    private final Cuid fieldId;
    private final char domain;
    private final String propertyName;

    public NestedFieldBinding(Cuid fieldId, char domain, String propertyName) {
        this.fieldId = fieldId;
        this.domain = domain;
        this.propertyName = propertyName;
    }

    @Override
    public void updateInstanceFromModel(FormInstance instance, EntityDTO model) {
        EntityDTO value = model.get(propertyName);
        if(value != null) {
            instance.set(fieldId, CuidAdapter.cuid(domain, value.getId()));
        }
    }

    @Override
    public void populateChangeMap(FormInstance instance, Map<String, Object> changeMap) {
        Cuid cuid = instance.getInstanceId(fieldId);
        if(cuid != null) {
            changeMap.put(propertyName + "Id", getLegacyIdFromCuid(cuid));
        }
    }
}
