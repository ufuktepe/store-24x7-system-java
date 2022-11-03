package com.cscie97.authentication;

/**
 * Represents a permission required to access a resource or function of the Store system. Each Permission has a
 * unique ID, name, and description which are inherited from the Entitlement abstract class. The Permission class is
 * part of the Composite Pattern and each Permission represents a leaf object.
 *
 * @author Burak Ufuktepe
 */
public class Permission extends Entitlement {

	/**
	 * Class constructor that calls the constructor of the super class to set the ID, name, and description.
	 *
	 * @param anId  Unique ID of the Permission
	 * @param aName  Name of the Permission
	 * @param aDescription  Description of the Permission
	 */
	public Permission(String anId, String aName, String aDescription) {
		super(anId, aName, aDescription);
	}
}
