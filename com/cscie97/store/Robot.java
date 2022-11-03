package com.cscie97.store;

import java.util.stream.Stream;

/**
 * Represents a robot within an aisle within a store. Inherits from the Device class and implements Appliance
 * functionalities.
 *
 * @author Burak Ufuktepe
 */
public class Robot extends Device {

	/**
	 * Class constructor that sets the ID, name, and address.
	 *
	 * @param anId  Unique identifier of the robot
	 * @param aName  Name of the robot
	 * @param anAddress  Address of the device
	 */
	public Robot(String anId, String aName, Address anAddress) {
		super(anId, aName, anAddress);
	}

	/**
	 * Sets the location of the robot
	 *
	 * @param address  Address of the device in the form of storeId:aisleIde
	 */
	public void setAddress(Address address) {

		// Return if the robot is already at the given address
		if (address != null && this.getAddress() != null && address.equals(this.getAddress())) {
			return;
		}

		if (address == null && this.getAddress() != null) {
			System.out.println(this.getId() + " left " + this.getAddress().getStoreId());
			super.setAddress(null);
			return;
		} else if (address != null && this.getAddress() == null) {
			System.out.println(this.getId() + " entered " + address.getStoreId() + " and moved to "
					+ address.getAisleId());
		} else if (address != null) {
			System.out.println(this.getId() + " moved from " + this.getAddress().getAisleId() + " to "
					+ address.getAisleId());
		}
		super.setAddress(new Address(address.getStoreId(), address.getAisleId()));
	}

	/**
	 * Move to the given location and clean up the item.
	 *
	 * @param address  Location for the cleaning event
	 * @param item  String representing the thing that needs to be cleaned up
	 */
	public void cleanUpAisle(Address address, String item) {
		// Move the robot to the relevant address if it is located in another location
		if (!address.equals(this.getAddress())) {
			this.setAddress(address);
		}

		System.out.println(this.getId() + " cleaned up " + item + " in " + this.getAddress().getAisleId());
	}

	/**
	 * Move to the given location, check if there is enough products in the source inventory and check if there is
	 * enough space in the destination inventory. Validate the product IDs and move the inventory.
	 *
	 * @param fromInventory  Source inventory
	 * @param toInventory  Destination inventory
	 * @param count  Number of products to be moved
	 */
	public void moveFromInventoryToInventory(Inventory fromInventory, Inventory toInventory, int count) {
		// Move the robot to the relevant address
		this.setAddress(fromInventory.getLocation());

		// Check if there is enough products in the fromInventory
		if (fromInventory.getCount() < count) {
			throw new IllegalArgumentException(fromInventory.getId() + " includes less than " + count + " product(s)");
		}

		// Check if there is enough space in the toInventory
		if (toInventory.getCapacity() < toInventory.getCount() + count) {
			throw new IllegalArgumentException(toInventory.getId() + " does not have enough space for " + count + " "
					+ "product(s)");
		}

		// Check if the product IDs match
		if (!fromInventory.getProductId().equals(toInventory.getProductId())) {
			throw new IllegalArgumentException(fromInventory.getId() + " and " + toInventory.getId() + " do not " +
					"include the same type of product");
		}

		System.out.println(this.getId() + " picked up " + count + " of " + fromInventory.getProductId() + " from "
				+ fromInventory.getId() + " in " + fromInventory.getLocation().getAisleId() + ":"
				+ fromInventory.getLocation().getShelfId());

		// Update the fromInventory
		fromInventory.updateInventory(-1 * count);

		// Move to where toInventory is located
		this.setAddress(toInventory.getLocation());

		System.out.println(this.getId() + " added " + count + " of " + fromInventory.getProductId() + " to "
				+ toInventory.getId() + " in " + toInventory.getLocation().getAisleId() + ":"
				+ toInventory.getLocation().getShelfId());

		// Update the toInventory
		toInventory.updateInventory(count);
	}

	/**
	 * Move to the given location, check if there is enough products in the source inventory. Then move the inventory
	 * to the requested location.
	 *
	 * @param fromInventory  Source inventory
	 * @param toAddress  Location where the inventory needs to be moved to
	 * @param count  Number of products to be moved
	 * @return  Number of moved products
	 */
	public int moveFromInventoryToAddress(Inventory fromInventory, Address toAddress, int count) {
		// Move the robot to the relevant address
		this.setAddress(fromInventory.getLocation());

		// Check if there is enough products in the fromInventory
		if (fromInventory.getCount() == 0) {
			throw new IllegalArgumentException("No products left in " + fromInventory.getId());
		} else if (fromInventory.getCount() < count) {
			System.out.println("Only " + fromInventory.getCount() + " product(s) left in " + fromInventory.getId());
			count = fromInventory.getCount();
		}

		System.out.println(this.getId() + " picked up " + count + " of " + fromInventory.getProductId() + " from "
				+ fromInventory.getId() + " in " + fromInventory.getLocation().getAisleId() + ":"
				+ fromInventory.getLocation().getShelfId());

		// Update the fromInventory
		fromInventory.updateInventory(-1 * count);

		// Move to the destination address
		this.setAddress(toAddress);

		System.out.println(this.getId() + " brought " + count + " of " + fromInventory.getProductId() + " to "
				+ toAddress.getAisleId());

		return count;
	}

	/**
	 * Move to the given location and address the emergency.
	 *
	 * @param emergencyType  Type of the emergency
	 * @param address  Location of the emergency
	 */
	public void addressEmergency(EmergencyType emergencyType, Address address) {
		// Move the robot to the relevant address if it is located in another location
		if (!address.equals(this.getAddress())) {
			this.setAddress(address);
		}

		System.out.println(this.getId() + " addressing " + emergencyType.toString() + " emergency in "
				+ this.getAddress().getAisleId() + ".");
	}

	/**
	 * Assist customers exit the store.
	 *
	 * @param storeId  Unique ID of the store
	 */
	public void assistCustomersExit(String storeId) {
		System.out.println(this.getId() + " assisting customers exit " + storeId);
	}

	/**
	 * Retrieve the given customer's current location, move to that location, assist customer to car, and come back
	 * to the store.
	 *
	 * @param customer  The customer who needs assistance
	 */
	public void assistCustomerToCar(Customer customer) {
		// Get customer's current location
		Address customersLocation = customer.getLocation();

		// Send the robot to the customer
		this.setAddress(customersLocation);

		System.out.println(this.getId() + " assisting " + customer.getFirstName() + " to car.");

		// Leave the store
		this.setAddress(null);
		customer.updateLocation(null);

		// Come back to the store
		this.setAddress(customersLocation);
	}

}
