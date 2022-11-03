package com.cscie97.store;

/**
 * Represents a speaker within an aisle within a store. Inherits from the Device class and implements Appliance
 * functionalities.
 *
 * @author Burak Ufuktepe
 */
public class Speaker extends Device {

	/**
	 * Class constructor that sets the ID, name, and aisle ID.
	 *
	 * @param anId  Unique identifier of the speaker
	 * @param aName  Name of the speaker
	 * @param anAddress  Address of the speaker
	 */
	public Speaker(String anId, String aName, Address anAddress) {
		super(anId, aName, anAddress);
	}

	/**
	 * Announce the given message.
	 *
	 * @param message  Message to be announced
	 */
	public void announceMessage(String message) {
		System.out.println(this.getId() + " Announcing: <<" + message + ">>");
	}
}
