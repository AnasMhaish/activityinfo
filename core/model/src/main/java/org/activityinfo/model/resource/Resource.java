package org.activityinfo.model.resource;


import com.google.common.base.Joiner;

/**
 * ActivityInfo is, at it's core, concerned with the management of users' `Resource`s.
 *
 * Our users need to manage a variety of `Resource`s, from form definitions (`FormClass`es) to results
 * submitted by other users (`FormInstance`s), to a diverse set of report models, access control rules, etc.
 *
 * All these "things" we will consider to be "resources", which have a stable, globally unique
 * identity, a version, and owner.
 *
 * The owner of a resource is another may be an individual user, a group of users, or another resource from
 * which access control rules will be inherited. Users and user groups may be also be modelled as
 * resources, so we can say that every `Resource` is owned by another `Resource`; except the root resource.
 *
 * As a `Resource` can have exactly one owner, resources form a tree structure that we will
 * present to the user as a sort-of folder structure.
 *
 * Resources have zero or more, named properties.
 *
 */
public final class Resource extends PropertyBag<Resource> {

    private ResourceId id;
    private ResourceId owner;


    Resource() {
    }

    public Resource copy() {
        Resource copy = new Resource();
        copy.id = this.id;
        copy.owner = this.owner;
        copy.getProperties().putAll(this.getProperties());
        return copy;
    }

    /**
     * Returns the Resource's globally-unique ID.
     *
     */
    public ResourceId getId() {
        return id;
    }

    /**
     * Sets this {@code Resource}'s id.
     *
     * Though the ResourceId should be generally considered an opaque string,
     * there are a number of invariants:
     *
     * <ul>
     *     <li>ids must start with a character in the range [_A-Za-z]</li>
     *     <li>ids starting with an underscore ('_') are reserved for system use</li>
     *     <li>ids independently generated by clients must begin with 'c'</li>
     *     <li>The remaining characters of the id must be in the range [A-Za-z0-9]</li>
     * </ul>
     */
    public Resource setId(String id) {
        if(id == null) {
            throw new NullPointerException("id");
        }
        this.id = ResourceId.create(id);
        return this;
    }

    public Resource setId(ResourceId id) {
        if(id == null) {
            throw new NullPointerException("id");
        }
        this.id = id;
        return this;
    }

    /**
     * Returns the id of the {@code Resource} which owns this {@code Resource}
     *
     */
    public ResourceId getOwnerId() {
        return owner;
    }

    /**
     * Sets the owner of this {@code Resource}
     *
     * @param owningResourceId the id of the {@code Resource} that owns this
     *     resource
     */
    public Resource setOwnerId(String owningResourceId) {
        if(owningResourceId == null) {
            throw new NullPointerException("owner");
        }
        this.owner = ResourceId.create(owningResourceId);
        return this;
    }

    public Resource setOwnerId(ResourceId owningResourceId) {
        if(owningResourceId == null) {
            throw new NullPointerException("owner");
        }
        this.owner = owningResourceId;
        return this;
    }

    @Override
    public String toString() {
        return "{" + id.asString() + ": " + Joiner.on(", ").withKeyValueSeparator("=").join(getProperties()) + "}";
    }
}
