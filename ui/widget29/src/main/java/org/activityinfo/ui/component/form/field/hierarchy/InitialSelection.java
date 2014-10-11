package org.activityinfo.ui.component.form.field.hierarchy;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.model.hierarchy.Hierarchy;
import org.activityinfo.model.hierarchy.Level;
import org.activityinfo.model.hierarchy.Node;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.ResourceLocator;

import java.util.List;
import java.util.Map;
import java.util.Set;

class InitialSelection {

    private final Hierarchy hierarchy;
    private final Map<ResourceId, Node> selection = Maps.newHashMap();

    public InitialSelection(Hierarchy hierarchy) {
        this.hierarchy = hierarchy;
    }

    public Promise<Void> fetch(ResourceLocator locator, Set<ResourceId> ids) {
        if(ids == null || ids.isEmpty()) {
            return Promise.done();
        } else {
            return fetchLabelAndParentIds(locator, ids);
        }
    }

    private Promise<Void> fetchLabelAndParentIds(final ResourceLocator locator, Set<ResourceId> instanceIds) {

        return locator.get(instanceIds)
                  .join(new Function<List<UserResource>, Promise<Void>>() {
                      @Override
                      public Promise<Void> apply(List<UserResource> instances) {

                          Set<ResourceId> parents = populateSelection(instances);
                          if (parents.isEmpty()) {
                              return Promise.done();
                          } else {
                              return fetchLabelAndParentIds(locator, parents);
                          }
                      }
                  });
    }

    private Set<ResourceId> populateSelection(List<UserResource> resources) {
        Set<ResourceId> parents = Sets.newHashSet();
        for(UserResource userResource : resources) {
            Resource resource = userResource.getResource();
            Level level = hierarchy.getLevel(resource.getValue().getClassId());
            if(level != null) {
                Node node = level.createNode(resource);
                selection.put(level.getClassId(), node);
                if(!level.isRoot()) {
                    assert node.getParentId() != null;
                    parents.add(node.getParentId());
                }
            }
        }
        parents.removeAll(selection.keySet());
        return parents;
    }

    public Map<ResourceId, Node> getSelection() {
        return selection;
    }
}
