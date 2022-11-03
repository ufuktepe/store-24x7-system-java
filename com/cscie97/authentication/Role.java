package com.cscie97.authentication;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides a way to group Permissions and other Roles. Extends the Entitlement class which has a unique ID, name,
 * and description. The Role class is part of the Composite Pattern and each Role represents a composite object.
 *
 * @author Burak Ufuktepe
 */
public class Role extends Entitlement {

	/**
	 * Set of entitlements associated with the role
	 */
	private Set<Entitlement> entitlements;

	/**
	 * Class constructor that calls the constructor of the super class to set the ID, name, and description. Also, it
	 * initializes the entitlements set.
	 *
	 * @param anId  Unique ID of the Role
	 * @param aName  Name of the Role
	 * @param aDescription  Description of the Role
	 */
	public Role(String anId, String aName, String aDescription) {
		super(anId, aName, aDescription);
		this.entitlements = new HashSet<>();
	}

	/**
	 * Adds the given entitlement to the role's set of entitlements.
	 * Throws an IllegalArgumentException if the given entitlement is already added.
	 *
	 * @param anEntitlement
	 */
	public void addEntitlement(Entitlement anEntitlement) {
		if (this.hasEntitlement(anEntitlement)) {
			throw new IllegalArgumentException(this.getId() + " already has " + anEntitlement.getId());
		}

		this.entitlements.add(anEntitlement);
	}

	/**
	 * Iterates over the role's entitlements and checks to see if the given entitlement already exists.
	 *
	 * @param anEntitlement  Entitlement that is searched for
	 * @return  True if the entitlement already exists. Otherwise, returns false.
	 */
	public boolean hasEntitlement(Entitlement anEntitlement) {
		if (this.getId().equals(anEntitlement.getId())) {
			return true;
		}

		for (Entitlement entitlement : this.entitlements) {
			if (entitlement.hasEntitlement(anEntitlement)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return's the entitlements of the role
	 *
	 * @return  Set of entitlements associated with the role
	 */
	public Set<Entitlement> getEntitlements() {
		return this.entitlements;
	}
}
