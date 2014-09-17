package org.activityinfo.model.resource;


import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Nonnull;

/**
 * Globally, universally unique and persistent identifier
 * for {@code Resources}
 *
 */
public final class ResourceId {

    private final String text;

    /**
     * Creates a new ResourceId from its string representation
     *
     * <p>Note: This method must be named {@code valueOf} in order to be
     * used as a Jersey {@code @PathParam}
     */
    public static ResourceId valueOf(@Nonnull String string) {
        assert string != null;
        return new ResourceId(string);
    }

    private ResourceId(@Nonnull String text) {
        this.text = text;
    }

    public ResourceId getWorkspaceId() {
        int index = text.indexOf('-');
        if(index < 0) {
            throw new IllegalArgumentException("malformed argument");
        }
        return ResourceId.valueOf(text.substring(0, index));
    }

    @JsonValue
    public String asString() {
        return this.text;
    }

    public char getDomain() {
        return text.charAt(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourceId resourceId = (ResourceId) o;

        if (text == null) return resourceId.text == null;
        return text.equals(resourceId.text);
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public String toString() {
        return text;
    }

}
