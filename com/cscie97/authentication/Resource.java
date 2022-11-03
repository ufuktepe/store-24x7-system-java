package com.cscie97.authentication;

/**
 * The Resource class represents physical entities such as stores or appliances. Each resource has a unique ID and
 * description. The Resource class implements the Visitable interface.
 *
 * @author Burak Ufuktepe
 */
public class Resource implements Visitable {

	/**
	 * Unique ID of the resource.
	 */
	private String id;

	/**
	 * Description of the resource.
	 */
	private String description;

	/**
	 * Class constructor that sets the ID and description.
	 *
	 * @param anId  Unique ID of the resource
	 * @param aDescription  Description of the resource
	 */
	public Resource(String anId, String aDescription) {
		this.id = anId;
		this.description = aDescription;
	}

	/**
	 * Accepts a visitor and calls the visit method of the Visitor per the Visitor Pattern.
	 *
	 * @param visitor  Visitor instance
	 */
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Retrieves the unique ID of the resource.
	 *
	 * @return  Resource's unique ID.
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * String representation of the Resource object.
	 *
	 * @return  String that includes the Resource ID and description.
	 */
	public String toString() {
		return String.format("RESOURCE ID   : %-24sDESCRIPTION: %-30s%n", this.id, this.description);
	}
}
