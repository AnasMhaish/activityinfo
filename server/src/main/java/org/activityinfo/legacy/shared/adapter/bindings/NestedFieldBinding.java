package org.activityinfo.legacy.shared.adapter.bindings;

import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.model.DTO;
import org.activityinfo.legacy.shared.model.EntityDTO;

import java.util.Map;

import static org.activityinfo.legacy.shared.adapter.CuidAdapter.getLegacyIdFromCuid;

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
        DTO value = model.get(propertyName);
        if (value instanceof EntityDTO) {
            instance.set(fieldId, CuidAdapter.cuid(domain, ((EntityDTO) value).getId()));
        }
    }

    @Override
    public void populateChangeMap(FormInstance instance, Map<String, Object> changeMap) {
        Cuid cuid = instance.getInstanceId(fieldId);
        if (cuid != null) {
            changeMap.put(propertyName + "Id", getLegacyIdFromCuid(cuid));
        }
    }
}
