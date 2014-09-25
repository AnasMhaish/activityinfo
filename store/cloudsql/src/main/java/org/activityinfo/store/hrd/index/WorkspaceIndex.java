package org.activityinfo.store.hrd.index;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.common.collect.Lists;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.store.hrd.Authorization;
import org.activityinfo.store.hrd.entity.Content;
import org.activityinfo.store.hrd.entity.Workspace;
import org.activityinfo.store.hrd.entity.WorkspaceTransaction;

import java.util.Collection;
import java.util.List;

/**
 * Index of workspaces owned, shared with, and starred by users.
 */
public class WorkspaceIndex {

    public static final String PARENT_KIND = "User";

    public static final String INDEX_KIND = "Workspace";

    public static final String OWNED_PROPERTY = "own";

    public static final String STARRED_PROPERTY = "star";


    public static Key parentKey(AuthenticatedUser user) {
        return KeyFactory.createKey(PARENT_KIND, user.getUserResourceId().asString());
    }

    private static Key entryKey(ResourceId workspaceId, AuthenticatedUser user) {
        return KeyFactory.createKey(parentKey(user), INDEX_KIND, workspaceId.asString());
    }

    /**
     * Adds an entry for the workspace to the creator's workspace index.
     *
     * <p>This update should be done within the context of the WorkspaceTransaction
     * to avoid marooning the newly created workspace.</p>
     */
    public static void addOwnerIndexEntry(WorkspaceTransaction tx, Workspace workspace) {
        Entity entity = new Entity(entryKey(workspace.getWorkspaceId(), tx.getUser()));
        entity.setProperty(OWNED_PROPERTY, true);
        entity.setProperty(STARRED_PROPERTY, true);

        tx.put(entity);
    }


    public static List<ResourceNode> queryUserWorkspaces(DatastoreService datastore, AuthenticatedUser user, WorkspaceLookup workspaceLookup) {

        Query query = new Query(INDEX_KIND)
            .setAncestor(parentKey(user))
            .setKeysOnly();

        List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(100));


        List<Key> workspaceKeys = Lists.newArrayList();
        for(Entity entity : entities) {
            ResourceId workspaceId = ResourceId.valueOf(entity.getKey().getName());
            Workspace workspace = new Workspace(workspaceId);
            Authorization authorization = new Authorization(user, workspaceId, datastore);

            if (authorization.canView()) {
                workspaceKeys.add(workspace.getLatestContent(workspaceId).getKey());
                workspaceLookup.cache(workspaceId, workspace);
            }
        }


        // Provide a transaction here explicitly so that this request doesn't get grouped
        // a previous transaction that was part of this request.
        Collection<Entity> indices = datastore.get(null, workspaceKeys).values();
        List<ResourceNode> resourceNodes = Lists.newArrayList();
        for(Entity workspaceEntity : indices) {
            resourceNodes.add(Content.deserializeResourceNode(workspaceEntity));
        }
        return resourceNodes;
    }

}
