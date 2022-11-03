package com.cscie97.store;

import java.awt.image.ByteLookupTable;

/**
 * Represents a turnstile within an aisle within a store. Inherits from the Device class and implements Appliance
 * functionalities.
 *
 * @author Burak Ufuktepe
 */
public class Turnstile extends Device {

	/**
	 * Identifies the current state of the turnstile. True indicates that the turnstile is open.
	 */
	private boolean isOpen;

	/**
	 * Class constructor that sets the ID, name, and aisle ID.
	 *
	 * @param anId  Unique identifier of the turnstile
	 * @param aName  Name of the turnstile
	 * @param anAddress  Address of the turnstile
	 */
	public Turnstile(String anId, String aName, Address anAddress) {
		super(anId, aName, anAddress);
	}

	/**
	 * Sets the state of the turnstile.
	 *
	 * @param open  boolean value that identifies to open or close the turnstile.
	 */
	public void setState(boolean open) {
		if (open) {
			if (!this.isOpen) {
				// Open the turnstile if it is not open already
				this.isOpen = true;
				System.out.println(this.getId() + " opened.");
			} else {
				System.out.println(this.getId() + " is already open.");
			}

		} else {
			if (this.isOpen) {
				// Close the turnstile if it is not closed already
				this.isOpen = false;
				System.out.println(this.getId() + " closed.");
			} else {
				System.out.println(this.getId() + " is already closed.");
			}
		}
	}
}
