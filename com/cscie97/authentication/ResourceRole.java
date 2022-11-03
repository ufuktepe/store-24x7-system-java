package com.cscie97.authentication;

import java.util.Set;

/**
 * Binds the Consumer with the Store and the appropriate Role. Extends the Entitlement class which has a unique ID,
 * name, and description. Additionally, each ResourceRole is associated with a Resource and an Entitlement object. The
 * ResourceRole class is part of the Composite Pattern and each ResourceRole represents a composite object.
 *
 * @author Burak Ufuktepe
 */
public class ResourceRole extends Entitlement {

	/**
	 * Role associated with the ResourceRole
	 */
	private Entitlement role;

	/**
	 * Resource associated with the ResourceRole
	 */
	private Resource resource;

	/**
	 * Class constructor that calls the constructor of the super class to set the ID, name, and description. Also, it
	 * sets the Role and Resource.
	 *
	 * @param anId  Unique ID of the ResourceRole
	 * @param aName  Name of the ResourceRole
	 * @param aDescription  Description of the ResourceRole
	 * @param aRole  Role associated with the ResourceRole
	 * @param aResource  Resource associated with the ResourceRole
	 */
	public ResourceRole(String anId, String aName, String aDescription, Entitlement aRole, Resource aResource) {
		super(anId, aName, aDescription);
		this.role = aRole;
		this.resource = aResource;
	}

	/**
	 * Checks whether the given resource ID matches the ID of the resource that is associated with the ResourceRole
	 *
	 * @param resourceId  Unique ID of the resource
	 * @return  True if the given resource ID and the ID of the resource that is associated with the ResourceRole are
	 * the same. Otherwise, returns false.
	 */
	public boolean matchResource(String resourceId) {
		return this.resource.getId().equals(resourceId);
	}

	/**
	 * Return's the entitlements of the Role that is associated with the ResourceRole
	 *
	 * @return  Set of entitlements associated with the Role of the ResourceRole
	 */
	public Set<Entitlement> getEntitlements() {
		return this.role.getEntitlements();
	}

	/**
	 * Retrieves the resource associated with the ResourceRole
	 *
	 * @return  The resource object associated with the ResourceRole
	 */
	public Resource getResource() {
		return this.resource;
	}
}
