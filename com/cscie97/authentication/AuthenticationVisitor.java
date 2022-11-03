package com.cscie97.authentication;

import java.util.Map;

/**
 * The AuthenticationVisitor class implements the Visitor interface and is used to determine if a user has the
 * required permission for the requested action. The implementation details for traversing the objects of the
 * Authentication Service is contained in the visit methods.
 *
 * @author Burak Ufuktepe
 */
public class AuthenticationVisitor implements Visitor {

	/**
	 * Unique ID of the resource that is associated with the permission.
	 */
	private String resourceId;

	/**
	 * Unique ID of the permission.
	 */
	private String permissionId;

	/**
	 * ID of the Authorization Token.
	 */
	private String tokenId;

	/**
	 * ID of the user.
	 */
	private String userId;

	/**
	 * Identifies the status of the token.
	 */
	private boolean tokenActive;

	/**
	 * Boolean value that determines whether the given Authorization Token has the required permission.
	 */
	private boolean hasPermission;

	/**
	 * Class constructor that sets the resource ID, permission ID and token ID.
	 *
	 * @param aResourceId  Unique ID of the resource (may be null)
	 * @param aPermissionId  Unique ID of the permission
	 * @param aTokenId  ID of the Authorization Token
	 */
	public AuthenticationVisitor(String aResourceId, String aPermissionId, String aTokenId) {
		if (aResourceId != null) {
			this.resourceId = aResourceId.toLowerCase();
		}
		this.permissionId = aPermissionId.toLowerCase();
		this.tokenId = aTokenId;
	}

	/**
	 * Iterates over each user until a user with a matching auth token is found. Once the user is found, it iterates
	 * over the user's entitlements to check if any entitlement includes the required permission (provided that the
	 * user's token is not expired).
	 *
	 * @param service  AuthenticationService object
	 */
	public void visit(AuthenticationService service) {
		// Iterate over each user
		for (Map.Entry<String, User> entry : service.getUserMap().entrySet()) {
			User user = entry.getValue();
			user.accept(this);
			// Check if the token is found
			if (this.userId != null) {
				// Return if the user's token is expired
				if (!this.tokenActive) {
					return;
				}
				// Iterate over the entitlements
				for (Entitlement entitlement : user.getEntitlements()) {
					entitlement.accept(this);
					if (this.hasPermission) {
						return;
					}
				}
				return;
			}
		}
	}

	/**
	 * Retrieves the given user's existing auth token. If the auth token matches the tokenId, assigns the given user
	 * to the userId property.
	 *
	 * @param user  User object
	 */
	public void visit(User user) {
		AuthToken authToken = user.getExistingAuthToken();
		if (authToken != null && authToken.getId().equals(this.tokenId)) {
			this.userId = user.getId();
			// Check the status of the token
			if (user.getExistingAuthToken().isExpired()) {
				this.tokenActive = false;
			} else {
				this.tokenActive = true;
			}
		}
	}

	/**
	 * First checks if the resourceId is null or if the resourceId is applicable to the given entitlement. Then
	 * checks if the given entitlement's ID matches the permission ID and also visits the entitlement's children.
	 *
	 * @param anEntitlement  Entitlement object
	 */
	public void visit(Entitlement anEntitlement) {
		// Check if the resource condition is satisfied
		if (this.resourceId == null || anEntitlement.matchResource(this.resourceId)) {
			// Check if the Entitlement has the required permission
			if (anEntitlement.getId().equals(this.permissionId)) {
				this.hasPermission = true;
				return;
			}

			// Iterate over children entitlements
			for (Entitlement entitlement : anEntitlement.getEntitlements()) {
				entitlement.accept(this);
				if (this.hasPermission) {
					return;
				}
			}
		}
	}

	/**
	 * Not applicable. The AuthenticationVisitor does not visit resources. However, this method is included as the
	 * AuthenticationVisitor implements the Visitor interface.
	 *
	 * @param resource
	 */
	public void visit(Resource resource) {
		// N/A
	}

	/**
	 * Retrieves the ID of the user whose auth token matches tokenId.
	 *
	 * @return  Unique ID of the user.
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 * Determines whether the tokenId has the required permission or not.
	 *
	 * @return  True if the tokenId has the required permisson. Otherwise, returns false.
	 */
	public boolean hasPermission() {
		return this.hasPermission;
	}

	public boolean isTokenActive() {
		return this.tokenActive;
	}
}
