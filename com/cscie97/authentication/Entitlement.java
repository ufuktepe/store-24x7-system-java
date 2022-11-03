package com.cscie97.authentication;

import java.util.HashSet;
import java.util.Set;

/**
 * The Entitlement abstract class implements the Visitable interface and represents Permissions, Roles, and
 * Resource Roles. Each Entitlement object has a unique ID, name, and description.
 *
 * @author Burak Ufuktepe
 */
public abstract class Entitlement implements Visitable {

	/**
	 * Unique ID of the Entitlement.
	 */
	private String id;

	/**
	 * Name of the Entitlement.
	 */
	private String name;

	/**
	 * Name of the Description.
	 */
	private String description;

	/**
	 * Class constructor that sets the ID, name, and description of the Entitlement.
	 * @param anId  Unique ID of the Entitlement
	 * @param aName  Name of the Entitlement
	 * @param aDescription  Name of the Description
	 */
	public Entitlement(String anId, String aName, String aDescription) {
		this.id = anId.toLowerCase();
		this.name = aName.toLowerCase();
		this.description = aDescription.toLowerCase();
	}

	/**
	 * Accepts a Visitor object and calls its visitor method per the Visitor Pattern.
	 *
	 * @param visitor  Visitor object
	 */
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Determines if its entitlement ID is the same as the given entitlement's ID.
	 *
	 * @param anEntitlement  Entitlement object
	 * @return  True if the given entitlement's ID matches the Entitlement's ID.
	 */
	public boolean hasEntitlement(Entitlement anEntitlement) {
		return this.getId().equals(anEntitlement.getId());
	}

	/**
	 * Returns true. Any child class that is associated with a resource needs to overwrite this method.
	 *
	 * @param resourceId  Unique ID of the resource
	 * @return  True
	 */
	public boolean matchResource(String resourceId) {
		return true;
	}

	/**
	 * Retrieves the unique ID of the Entitlement.
	 *
	 * @return  Unique ID of the Entitlement
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Retrieves the name of the Entitlement.
	 *
	 * @return  Entitlement's name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Retrieves the description of the Entitlement.
	 *
	 * @return  Entitlement's description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Returns an empty HashSet. Any class that inherits from Entitlement and includes other Entitlements needs
	 * to overwrite this method.
	 *
	 * @return  Empty HashSet.
	 */
	public Set<Entitlement> getEntitlements() {
		return new HashSet<>();
	}

	/**
	 * Returns null as the Entitlement class is not associated with any resource. Any child class that is associated
	 * with a resource needs to overwrite this method.
	 *
	 * @return  null
	 */
	public Resource getResource() {
		return null;
	}

	/**
	 * String representation of the Entitlement object.
	 *
	 * @return  String that includes the Entitlement ID, name, and description.
	 */
	public String toString() {
		return String.format("ENTITLEMENT ID: %-24sNAME: %-30sDESCRIPTION: %-46s%n", this.id, this.name,
				this.description);
	}
}
