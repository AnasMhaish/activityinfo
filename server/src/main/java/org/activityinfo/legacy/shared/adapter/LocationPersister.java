package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.model.AiLatLng;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.command.CreateLocation;
import org.activityinfo.legacy.shared.command.GetAdminEntities;
import org.activityinfo.legacy.shared.command.result.AdminEntityResult;
import org.activityinfo.legacy.shared.model.AdminEntityDTO;
import org.activityinfo.legacy.shared.model.AdminLevelDTO;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static org.activityinfo.legacy.shared.adapter.CuidAdapter.*;

/**
 * Updates/creates a location object.
 *
 * <p>The legacy api expects the location to be denormalized with
 * ALL ancestors, so we have to fetch them before we're able to persist</p>
 */
public class LocationPersister {

    private final Dispatcher dispatcher;
    private final FormInstance instance;
    private Promise<Void> callback;
    private Map<String,Object> properties;
    private Queue<Integer> parents = new LinkedList<>();
    private Cuid classId;

    public LocationPersister(Dispatcher dispatcher, FormInstance instance) {
        this.dispatcher = dispatcher;
        this.instance = instance;
        this.classId = instance.getClassId();
    }

    public Promise<Void> persist() {
        callback = new Promise<Void>();

        properties = Maps.newHashMap();
        properties.put("id", CuidAdapter.getLegacyIdFromCuid(instance.getId()));
        properties.put("locationTypeId", getLegacyIdFromCuid(classId));
        properties.put("name", instance.get(field(classId, NAME_FIELD)));
        properties.put("axe", instance.get(field(classId, AXE_FIELD)));

        AiLatLng point = (AiLatLng) instance.get(field(classId, GEOMETRY_FIELD));
        if(point != null) {
            properties.put("latitude", point.getLat());
            properties.put("longitude", point.getLng());
        }

        Set<Cuid> adminEntities = instance.getReferences(field(classId, ADMIN_FIELD));
        for(Cuid adminEntityCuid : adminEntities) {
            parents.add(getLegacyIdFromCuid(adminEntityCuid));
        }

        resolveNextParent();
        return callback;
    }

    private void resolveNextParent() {
        if(parents.isEmpty()) {
            persistLocation();
            return;
        }
        final GetAdminEntities query = new GetAdminEntities();
        query.setEntityId(parents.poll());

        dispatcher.execute(query, new AsyncCallback<AdminEntityResult>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(AdminEntityResult result) {
                if(result.getData().isEmpty()) {
                    callback.onFailure(new IllegalStateException("No entity with id = " + query.getEntityIds()));
                }
                AdminEntityDTO entity = result.getData().get(0);
                properties.put(AdminLevelDTO.getPropertyName(entity.getLevelId()), entity.getId());
                if(entity.getParentId() != null) {
                    parents.add(entity.getParentId());
                }
                resolveNextParent();
            }
        });
    }

    private void persistLocation() {
        CreateLocation command = new CreateLocation(properties);
        dispatcher.execute(command).then(Functions.<Void>constant(null)).then(callback);
    }


}
