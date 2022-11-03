package com.cscie97.authentication;

/**
 * The AccessDeniedException is returned to indicate that a user does not have access to a resource or function. It
 * includes the action that was performed, the user ID, permission ID, and resource ID
 *
 * @author Burak Ufuktepe
 */
public class AccessDeniedException extends Exception {

    /**
     * Performed action.
     */
    private String action;

    /**
     * User ID of the user who made the request.
     */
    private String userId;

    /**
     * Permission ID associated with the request.
     */
    private String permissionId;

    /**
     * Resource ID associated with the request.
     */
    private String resourceId;

    /**
     * Class constructor that sets the action, user ID, permission ID, and resource ID.
     *
     * @param anAction  Performed action
     * @param userId  Unique ID of the user
     * @param permissionId  Unique ID of the permission
     * @param resourceId  Unique ID of the resource
     */
    public AccessDeniedException(String anAction, String userId, String permissionId, String resourceId) {
        super(String.format("Failed action: %s%n, User ID: %s%n, Permission ID: %s%n, Resource ID: %s%n", anAction,
                userId, permissionId, resourceId));
        this.action = anAction;
        this.userId = userId;
        this.permissionId = permissionId;
        this.resourceId = resourceId;
    }

    /**
     * Retrieves the action that was attempted.
     *
     * @return  attempted action
     */
    public String getAction() {
        return this.action;
    }

    /**
     * Retrieves the user ID who made the request.
     *
     * @return  Unique ID for the user.
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Retrieves the permission ID that was denied.
     *
     * @return  Permission ID
     */
    public String getPermissionId() {
        return this.permissionId;
    }

    /**
     * Retrieves the resource ID associated with the permission.
     *
     * @return  Resource ID
     */
    public String getResourceId() {
        return this.resourceId;
    }

    public String getReason() {
        if (this.resourceId == null) {
            return this.userId + " does not have " + this.permissionId + " permission.";
        } else {
            return this.userId + " does not have " + this.permissionId + " permission for " + this.resourceId + ".";
        }
    }
}

