package com.cscie97.store;

/**
 * Represents a camera within an aisle within a store. Inherits from the Device class and implements Sensor
 * functionalities.
 *
 * @author Burak Ufuktepe
 */
public class Camera extends Device {

	/**
	 * Class constructor that sets the ID, name, and address.
	 *
	 * @param anId  Unique identifier of the camera
	 * @param aName  Name of the camera
	 * @param anAddress  Address of the device
	 */
	public Camera(String anId, String aName, Address anAddress) {
		super(anId, aName, anAddress);
	}



}
