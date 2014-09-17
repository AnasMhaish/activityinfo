package org.activityinfo.service.store;

import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.FolderProjection;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;

import javax.ws.rs.*;
import java.util.List;

@Path("/service/store")
public interface ResourceStore {


    /**
     * Generates a 31-bit client id for the given user, encoded as
     * @param user
     * @return
     */
    long generateClientId(AuthenticatedUser user);

    /**
     * Fetches the latest version of the resource from the store.
     */
    @GET
    @Path("resource/{id}")
    @Produces("application/json")
    Resource get(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId);


    @GET
    @Path("resource/{id}/acr")
    @Produces("application/json")
    List<Resource> getAccessControlRules(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId);


    /**
     * Creates or updates a {@code Resource} within the store.
     */
    @PUT
    @Path("resource/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    UpdateResult put(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId, Resource resource);


    /**
     * Creates or updates a {@code Resource} within the store.
     *
     * <p>Non Jax-rs convenience method
     *
     * @param user
     * @param resource
     * @return
     */
    UpdateResult put(AuthenticatedUser user, Resource resource);

    /**
     * Creates a {@code Resource} within the store.
     *
     * <p>Non Jax-rs convenience method
     *
     * @param user
     * @param resource
     * @return
     */
    @POST
    @Path("resources")
    @Consumes("application/json")
    @Produces("application/json")
    UpdateResult create(@InjectParam AuthenticatedUser user, Resource resource);

    /**
     * Fetches an outline of Resources, returning only their id and label.
     */
    @POST
    @Path("query/tree")
    @Produces("application/json")
    FolderProjection queryTree(@InjectParam AuthenticatedUser user, FolderRequest request);


    /**
     * Fetches an outline of Resources, returning only their id and label.
     */
    @POST
    @Path("query/table")
    @Consumes("application/json")
    @Produces("application/json")
    TableData queryTable(@InjectParam AuthenticatedUser user, TableModel tableModel);

    @POST
    @Path("query/cube")
    @Consumes("application/json")
    @Produces("application/json")
    List<Bucket> queryCube(@InjectParam AuthenticatedUser user, PivotTableModel tableModel);


    /**
     *
     * @return a list of workspaces owned or explicitly shared with a
     * given user
     */
    @GET
    @Path("query/roots")
    @Produces("application/json")
    List<ResourceNode> getOwnedOrSharedWorkspaces(@InjectParam AuthenticatedUser user);

    @GET
    @Path("query/updates/{workspaceId}")
    @Produces("application/json")
    List<Resource> getUpdates(@InjectParam AuthenticatedUser user, @PathParam("workspaceId") ResourceId workspaceId,
                              @QueryParam("version") long version);
}
