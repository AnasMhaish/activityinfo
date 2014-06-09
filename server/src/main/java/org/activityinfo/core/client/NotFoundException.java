package org.activityinfo.core.client;

/**
 * Indicates that the resource was not found on the server
 * or was not visible to the user
 */
public class NotFoundException extends RemoteException {

    public NotFoundException() {
    }

    public NotFoundException(String resourceId) {
        super("Resource: " + resourceId);
    }


}
