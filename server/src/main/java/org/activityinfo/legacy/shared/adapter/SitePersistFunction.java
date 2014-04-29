package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.fp.shared.BiFunction;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.adapter.bindings.SiteBinding;
import org.activityinfo.legacy.shared.command.*;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.model.AdminEntityDTO;
import org.activityinfo.legacy.shared.model.AdminLevelDTO;
import org.activityinfo.legacy.shared.model.LocationTypeDTO;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persists a FormInstance as a Site
 */
public class SitePersistFunction extends BiFunction<SiteBinding, FormInstance, Promise<? extends CommandResult>> {

    private final Dispatcher dispatcher;

    public SitePersistFunction(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public Promise<? extends CommandResult> apply(SiteBinding siteBinding, FormInstance instance) {

        Map<String, Object> siteProperties = siteBinding.toChangePropertyMap(instance);
        siteProperties.put("activityId", siteBinding.getActivity().getId());

        CreateSite createSite = new CreateSite(siteProperties);

        if (siteBinding.getLocationType().isAdminLevel()) {
            // we need to create the dummy location as well

            Promise<Command> createLocation = Promise.resolved(siteBinding.getAdminEntityId(instance))
                    .join(new FetchEntityFunction())
                    .then(new CreateDummyLocation(createSite.getLocationId(), siteBinding.getLocationType()));

            return Promise.fmap(new BatchConstructor()
                    .apply(createSite))
                    .apply(createLocation)
                    .join(dispatcher);

        } else {
            return dispatcher.execute(createSite);
        }
    }

    private class FetchEntityFunction implements Function<Integer, Promise<List<AdminEntityDTO>>> {

        @Nullable
        @Override
        public Promise<List<AdminEntityDTO>> apply(@Nullable Integer input) {
            GetAdminEntities query = new GetAdminEntities()
                    .setEntityId(input);

            Promise<AdminEntityDTO> entity = dispatcher
                    .execute(query)
                    .then(new SingleListResultAdapter<AdminEntityDTO>());

            Promise<List<AdminEntityDTO>> parents = entity
                    .join(new FetchParentsFunction());

            return Promise.prepend(entity, parents);
        }
    }

    private class FetchParentsFunction implements Function<AdminEntityDTO, Promise<List<AdminEntityDTO>>> {

        @Override
        public Promise<List<AdminEntityDTO>> apply(AdminEntityDTO input) {
            if(input.getParentId() == null) {
                return Promise.resolved(Collections.<AdminEntityDTO>emptyList());
            } else {
                return Promise.resolved(input.getParentId()).join(new FetchEntityFunction());
            }
        }
    }

    private class CreateDummyLocation implements Function<List<AdminEntityDTO>, Command> {

        private final LocationTypeDTO locationType;
        private int locationId;

        private CreateDummyLocation(int locationId, LocationTypeDTO locationType) {
            this.locationType = locationType;
            this.locationId = locationId;
        }

        @Override
        public CreateLocation apply(List<AdminEntityDTO> entities) {

            AdminEntityDTO entity = entities.get(0);
            Preconditions.checkState(entity.getLevelId() == locationType.getBoundAdminLevelId());

            Map<String, Object> properties = new HashMap<>();
            properties.put("id", locationId);
            properties.put("locationTypeId", locationType.getId());
            properties.put("name", entity.getName());

            for(AdminEntityDTO parent : entities) {
                properties.put(AdminLevelDTO.getPropertyName(parent.getLevelId()), parent.getId());
            }

            return new CreateLocation(properties);
        }
    }

    private class BatchConstructor extends BiFunction<Command, Command, Command> {

        @Override
        public Command apply(Command createSite, Command createLocation) {
            return new BatchCommand(createSite, createLocation);
        }
    }
}
