package com.cscie97.store;

/**
 * Represents a microphone within an aisle within a store. Inherits from the Device class and implements Sensor
 * functionalities.
 *
 * @author Burak Ufuktepe
 */
public class Microphone extends Device {

	/**
	 * Class constructor that sets the ID, name, and address.
	 *
	 * @param anId  Unique identifier of the microphone
	 * @param aName  Name of the microphone
	 * @param anAddress  Address of the device
	 */
	public Microphone(String anId, String aName, Address anAddress) {
		super(anId, aName, anAddress);
	}


}
