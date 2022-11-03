package com.cscie97.store;

/**
 * Abstract class that represents a device within an aisle within a store.
 * A device has a unique ID, name, and aisle ID which identifies the location of the device in the store.
 *
 * @author Burak Ufuktepe
 */
public abstract class Device {

	/**
	 * Unique identifier of the device.
	 */
	private String id;

	/**
	 * Name of the device.
	 */
	private String name;

	/**
	 * Address of the device.
 	 */
	private Address address;

	/**
	 * Class constructor that sets the ID, name, and address.
	 *
	 * @param anId  Unique identifier of the device
	 * @param aName  Name of the device
	 * @param anAddress  Address of the device
	 */
	public Device(String anId, String aName, Address anAddress) {
		this.id = anId;
		this.name = aName;
		this.address = anAddress;
	}

	/**
	 * Retrieves the unique ID of the device.
	 *
	 * @return  Device ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Retrieves the name of the device.
	 *
	 * @return  Device name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Retrieves the address of the device
	 *
	 * @return  Address of the device
	 */
	public Address getAddress() {
		return this.address;
	}

	/**
	 * Sets the location of the device
	 *
	 * @param address  Address of the device
	 */
	public void setAddress(Address address) {
		this.address = address;
	}

	/**
	 * Returns the string representation of the Device object.
	 *
	 * @return  String including the device ID, name, and aisle ID
	 */
	public String toString() {
		return String.format("Device ID: %-15s Name: %-15s Address: %-15s", this.id, this.name, this.address);
	}

	/**
	 * Below methods are default implementations which throw UnsupportedOperationExceptions since each of these
	 * methods are applicable to different child classes.
	 */

	public void setState(boolean open) {
		throw new UnsupportedOperationException();
	}

	public void announceMessage(String message) {
		throw new UnsupportedOperationException();
	}

	public void addressEmergency(EmergencyType emergencyType, Address address)  {
		throw new UnsupportedOperationException();
	}

	public void assistCustomersExit(String storeId) {
		throw new UnsupportedOperationException();
	}

	public void assistCustomerToCar(Customer customer) {
		throw new UnsupportedOperationException();
	}

	public void cleanUpAisle(Address address, String item)  {
		throw new UnsupportedOperationException();
	}

	public int moveFromInventoryToAddress(Inventory fromInventory, Address toAddress, int count) {
		throw new UnsupportedOperationException();
	}

	public void moveFromInventoryToInventory(Inventory fromInventory, Inventory toInventory, int count)  {
		throw new UnsupportedOperationException();
	}

}
