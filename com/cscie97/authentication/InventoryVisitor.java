package com.cscie97.authentication;

import java.util.Map;

/**
 * The InventoryVisitor class implements the Visitor interface and visits each user, resource, and entitlement of the
 * Authentication Service in order to provide an inventory of all Users (including their AuthTokens and Credentials),
 * Resources, Roles, and Permissions. The InventoryVisitor has an inventory property that stores information about the
 * objects of the Authentication Service.
 *
 * @author Burak Ufuktepe
 */
public class InventoryVisitor implements Visitor {

	/**
	 * Stores information about the objects of the Authentication Service.
	 */
	private StringBuilder inventory;

	/**
	 * Class constructor that initializes the inventory property.
	 */
	public InventoryVisitor() {
		this.inventory = new StringBuilder();
	}

	/**
	 * Iterates over each user, resource, and entitlement of the AuthenticationService and call accept methods on
	 * each of them.
	 *
	 * @param service  AuthenticationService instance
	 */
	public void visit(AuthenticationService service) {
		// Iterate over each user
		for (Map.Entry<String, User> entry : service.getUserMap().entrySet()) {
			entry.getValue().accept(this);
		}

		// Iterate over each resource
		for (Map.Entry<String, Resource> entry : service.getResourceMap().entrySet()) {
			entry.getValue().accept(this);
		}

		// Iterate over each entitlement
		for (Map.Entry<String, Entitlement> entry : service.getEntitlementMap().entrySet()) {
			entry.getValue().accept(this);
		}
	}

	/**
	 * Appends the string representation of the given user to the inventory property.
	 *
	 * @param user  User object
	 */
	public void visit(User user) {
		inventory.append(user);
	}

	/**
	 * Appends the string representation of the given entitlement to the inventory property.
	 *
	 * @param entitlement  Entitlement object
	 */
	public void visit(Entitlement entitlement) {
		inventory.append(entitlement);
	}

	/**
	 * Appends the string representation of the given resource to the inventory property.
	 *
	 * @param resource  Resource object
	 */
	public void visit(Resource resource) {
		inventory.append(resource);
	}

	/**
	 * Retrieves the inventory.
	 *
	 * @return  StringBuilder that includes information about the objects of the Authentication Service
	 */
	public StringBuilder getInventory() {
		return this.inventory;
	}
}
