package com.cscie97.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a store which contains aisles, inventories, and devices.
 *
 * @author Burak Ufuktepe
 */
public class Store {

	/**
	 * Unique identifier of the store.
	 */
	private String id;

	/**
	 * Name of the store.
	 */
	private String name;

	/**
	 * Physical address of the store.
	 */
	private String phsicalAddress;

	/**
	 * Map of Aisle numbers and Aisle objects.
	 */
	private Map<String, Aisle> aisleMap;

	/**
	 * Map of Inventory Ids and Inventory objects.
	 */
	private Map<String, Inventory> inventoryMap;

	/**
	 * Map of Device Ids and Device objects.
	 */
	private Map<String, Device> deviceMap;

	/**
	 * Class constructor. Sets the ID, name, and address of the store. Also, initializes aisleMap, inventoryMap, and
	 * deviceMap.
	 *
	 * @param anId  Unique identifier for the store
	 * @param aName  Name of the store
	 * @param aPhysicalAddress  Physical address of the store
	 */
	public Store(String anId, String aName, String aPhysicalAddress) {
		this.id = anId;
		this.name = aName;
		this.phsicalAddress = aPhysicalAddress;
		this.aisleMap = new HashMap<>();
		this.inventoryMap = new HashMap<>();
		this.deviceMap = new HashMap<>();
	}

	/**
	 * Retrieves the ID of the store.
	 *
	 * @return  Store ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Retrieves the name of the store.
	 *
	 * @return  Store name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Validates the ID of an aisle for uniqueness or existence.
	 * If the given ID is a new ID, checks for uniqueness. Throws an IllegalArgumentException if the ID is not unique.
	 * If the given ID isn't a new ID, checks for existence. Throws an IllegalArgumentException if the ID doesn't
	 * exist.
	 *
	 * @param id  ID of the aisle
	 * @param newId  Boolean that identifies if the given ID is a new ID or not.
	 */
	private void validateAisleId(String id, boolean newId) {
		if (newId) {
			// Check if the given id is unique
			if (this.aisleMap.containsKey(id)) {
				throw new IllegalArgumentException(id + " already exists");
			}
		} else {
			// Check if the given id exists
			if (!this.aisleMap.containsKey(id)) {
				throw new IllegalArgumentException(this.id + " does not include " + id);
			}
		}
	}

	/**
	 * Validates the given aisle ID and creates a new aisle. Adds the new aisle to aisleMap.
	 *
	 * @param id  Unique ID of the aisle
	 * @param name  Name of the aisle
	 * @param description  Description of the aisle
	 * @param location  RoomType that identifies the location of the aisle
	 * @return  The new Aisle object
	 */
	public Aisle defineAisle(String id, String name, String description, RoomType location) {
		// Check if the given id is unique
		this.validateAisleId(id, true);

		Aisle aisle = new Aisle(id, name, description, location);
		this.aisleMap.put(aisle.getId(), aisle);

		System.out.println(aisle.getId() + " created.");

		return aisle;
	}

	/**
	 * Validates the given aisle ID and retrieves the Aisle object for the given aisle ID.
	 *
	 * @param id Unique ID of the aisle
	 * @return The Aisle object for the given aisle ID
	 */
	public Aisle getAisle(String id) {

		// Check if the given id exists
		this.validateAisleId(id, false);

		return this.aisleMap.get(id);
	}

	/**
	 * Validates the given aisle ID and creates a new shelf in the given aisle.
	 *
	 * @param aisleId  Unique ID of the aisle
	 * @param shelfId  Unique ID of the shelf
	 * @param name  Name of the shelf
	 * @param level  LevelType that identifies the height of the shelf
	 * @param description  Description of shelf contents
	 * @param temperature  TemperatureType that identifies the shelf temperature
	 * @return  The Shelf object
	 */
	public Shelf defineShelf(String aisleId, String shelfId, String name, LevelType level, String description,
							 TemperatureType temperature) {
		// Check if the given aisle id exists
		this.validateAisleId(aisleId, false);

		// Have the aisle create the shelf and return the shelf
		return this.aisleMap.get(aisleId).defineShelf(shelfId, name, level, description, temperature);
	}

	/**
	 * Validates the given aisle ID and retrieves the Shelf object for the given aisle ID and shelf ID.
	 *
	 * @param aisleId  unique ID of the aisle
	 * @param shelfId  unique ID of the shelf
	 * @return  The Shelf object
	 */
	public Shelf getShelf(String aisleId, String shelfId) {
		// Check if the given aisle id exists
		this.validateAisleId(aisleId, false);

		return this.aisleMap.get(aisleId).getShelf(shelfId);
	}

	/**
	 * Validates the aisle ID and shelf ID then creates the inventory and adds it to inventoryMap.
	 * The inventory ID, product ID, capacity, and count are validated by the Store Model Service.
	 *
	 * @param inventoryId  Unique identifier of the inventory
	 * @param address  Address of the inventory
	 * @param capacity  Total capacity of the inventory on the shelf
	 * @param count  Current count of product items
	 * @param productId  Product ID of the inventory
	 * @return  The Inventory object
	 */
	public Inventory defineInventory(String inventoryId, Address address, int capacity, int count, String productId) {
		String aisleId = address.getAisleId();
		String shelfId = address.getShelfId();

		if (shelfId == null) {
			throw new IllegalArgumentException("No shelf ID found in location " + address);
		}

		// Check if the given aisle id exists
		this.validateAisleId(aisleId, false);

		// Check if the given shelf id exists
		this.aisleMap.get(aisleId).getShelf(shelfId);

		Inventory inventory = new Inventory(inventoryId, capacity, count, address, productId);
		this.inventoryMap.put(inventory.getId(), inventory);

		return inventory;
	}

	/**
	 * Finds a robot and the inventory to be restocked. Calculates the number of products required for restocking.
	 * Then, finds an inventory for the given productId in the STORE_ROOM and adjusts the required quantity if there is
	 * not enough products in the source inventory. Instructs the robot to move the products.
	 *
	 * @param address  Location of the inventory to be restocked
	 * @param productId  Unique ID of the product contained in the inventory
	 * @return  The Inventory that is restocked
	 */
	public Inventory restockShelf(Address address, String productId) {
		// Find a robot
		Device robot = this.getDeviceAtAddress(address, Robot.class, false);

		// Get the inventory to be restocked
		Inventory toInventory = this.findInventoryAtAddress(address, productId);

		// Calculate the number of products required for restocking
		int requiredQuantity = toInventory.getCapacity() - toInventory.getCount();

		// Find an inventory for the given productId in the STORE_ROOM
		Inventory fromInventory = this.findInventoryInRoom(RoomType.STORE_ROOM, productId);

		// Check if an inventory is found
		if (fromInventory == null) {
			System.out.println("Inventory cannot be restocked. No " + productId + " found in the Store Room.");
			return null;
		}

		// Adjust requiredQuantity if there is not enough products in fromInventory
		if (fromInventory.getCount() < requiredQuantity) {
			requiredQuantity = fromInventory.getCount();
		}

		// Move the product(s)
		robot.moveFromInventoryToInventory(fromInventory, toInventory, requiredQuantity);

		return toInventory;
	}

	/**
	 * Finds a robot and the inventory for the given productId. Instructs the robot to move the products.
	 *
	 * @param toAddress  Location where the products need to be brought to
	 * @param productId  Unique ID of the product contained in the inventory
	 * @param count  Number of products to be fetched
	 * @return  Number of fetched products
	 */
	public int fetchProduct(Address toAddress, String productId, int count) {
		// Find a robot
		Device robot = this.getDeviceAtAddress(toAddress, Robot.class, false);

		// Find an inventory for the given productId in the FLOOR
		Inventory inventory = this.findInventoryInRoom(RoomType.FLOOR, productId);

		// Check if an inventory is found. If no inventory is found, search the store room.
		if (inventory == null) {
			inventory = this.findInventoryInRoom(RoomType.STORE_ROOM, productId);
			if (inventory == null) {
				return 0;
			}
		}

		return robot.moveFromInventoryToAddress(inventory, toAddress, count);
	}

	/**
	 * Retrieves the Inventory object for the given inventory ID.
	 * The inventory ID is validated by the Store Model Service.
	 *
	 * @param id  Unique identifier of the inventory
	 * @return  The Inventory object
	 */
	public Inventory getInventory(String id) {
		return this.inventoryMap.get(id);
	}

	/**
	 * Retrieves the inventory that includes the given product ID in the given room.
	 *
	 * @param room  FLOOR or STORE_ROOM
	 * @param productId  Unique ID of the product contained in the inventory
	 * @return  The Inventory object
	 */
	public Inventory findInventoryInRoom(RoomType room, String productId) {
		// Find the inventory with the productId in the given room
		for (Map.Entry<String, Inventory> entry : this.inventoryMap.entrySet()) {
			Inventory inventory = entry.getValue();
			String aisleId = inventory.getLocation().getAisleId();
			Aisle aisle = this.aisleMap.get(aisleId);

			// Check if the aisle is in the given room, product ID matches the given productId, and there is product
			// in inventory
			if (aisle.getLocation() == room && inventory.getProductId().equals(productId) && inventory.getCount() > 0) {
				return inventory;
			}
		}

		return null;
	}

	/**
	 * Validates the aisle ID and shelf ID and then retrieves the inventory that includes the given product ID at the
	 * given address.
	 *
	 * @param address  Location of the inventory
	 * @param productId  Unique ID of the product contained in the inventory
	 * @return  The Inventory object
	 */
	public Inventory findInventoryAtAddress(Address address, String productId) {
		// Validate the aisle ID
		if (!this.aisleMap.containsKey(address.getAisleId())) {
			throw new IllegalArgumentException(this.id + " does not include " + address.getAisleId());
		}

		// Validate the shelf ID
		this.getAisle(address.getAisleId()).getShelf(address.getShelfId());

		for (Map.Entry<String, Inventory> entry : this.inventoryMap.entrySet()) {
			Inventory inventory = entry.getValue();
			if (inventory.getLocation().equals(address) && inventory.getProductId().equals(productId)) {
				return inventory;
			}
		}

		return null;
	}

	/**
	 * Finds the inventory object for the given ID and calls the inventory object's validation method.
	 * The inventory ID is validated by the Store Model Service.
	 *
	 * @param id  Unique identifier of the inventory
	 * @param changeCount  Number of items to increment/decrement
	 */
	public void validateInventoryUpdate(String id, int changeCount) {
		this.inventoryMap.get(id).validateInventoryUpdate(changeCount);
	}

	/**
	 * Increments or decrements the inventory count for the given inventory ID. A positive changeCount indicates an
	 * increment whereas a negative changeCount indicates a decrement.
	 * The inventory ID is validated by the Store Model Service.
	 *
	 * @param id  Unique identifier of the inventory
	 * @param changeCount  Number of items to increment/decrement
	 * @return  The Inventory object
	 */
	public Inventory updateInventory(String id, int changeCount) {
		return this.inventoryMap.get(id).updateInventory(changeCount);
	}

	/**
	 * Validates the aisle ID and type then creates a device and adds it to deviceMap.
	 * The device ID is validated by the Store Model Service.
	 *
	 * @param deviceId  Unique identifier of the device
	 * @param name  Name of the device
	 * @param type  Type of the device (camera, microphone, robot, speaker, turnstile)
	 * @param address  Location of the device
	 * @return  The Device object
	 */
	public Device defineDevice(String deviceId, String name, String type, Address address) {
		// Check if the given aisle id exists
		this.validateAisleId(address.getAisleId(), false);

		// Create a device based on the type
		Device device = switch (type.toLowerCase()) {
			case "camera" -> new Camera(deviceId, name, address);
			case "microphone" -> new Microphone(deviceId, name, address);
			case "robot" -> new Robot(deviceId, name, address);
			case "speaker" -> new Speaker(deviceId, name, address);
			case "turnstile" -> new Turnstile(deviceId, name, address);
			default -> throw new IllegalArgumentException(type + " is not a recognized device type");
		};

		this.deviceMap.put(device.getId(), device);
		return device;
	}

	/**
	 * Retrieves the Device object for the given device ID. The device ID is validated by the Store Model Service.
	 *
	 * @param id  Unique identifier of the device
	 * @return  The Device object
	 */
	public Device getDevice(String id) {
		return this.deviceMap.get(id);
	}

	public List<Device> getDevices(Class<?> cls) {
		List<Device> devices = new ArrayList<>();
		for (Map.Entry<String, Device> entry : this.deviceMap.entrySet()) {
			if (entry.getValue().getClass() == cls) {
				devices.add( entry.getValue());
			}
		}
		return devices;
	}

	/**
	 * Loops through each device in the store. If exactLocation is true, then returns the device that is located at
	 * the given address. If exactLocation is false, then returns any device if no device is found at the given
	 * address.
	 *
	 * @param address  location of the device to be retrieved
	 * @param cls  Child Device class
	 * @param exactLocation  boolean that determines if the device has to be at the given address location. For
	 *                       example, if exactLocation is true then the device's address has to match the given
	 *                       address.
	 * @return  The Device object
	 */
	public Device getDeviceAtAddress(Address address, Class<?> cls, boolean exactLocation) {

		Device device = null;

		// Iterate over each device in the store
		for (Map.Entry<String, Device> entry : this.deviceMap.entrySet()) {
			Device currentDevice = entry.getValue();
			if (currentDevice.getClass() == cls) {
				// If the device is at the given address break out of the loop and return the device.
				if (currentDevice.getAddress().equals(address)) {
					return currentDevice;
				} else if (device == null && !exactLocation) {
					device = currentDevice;
				}
			}
		}

		// Throw IllegalArgumentExceptions if no device was found
		if (device == null && exactLocation) {
			throw new IllegalArgumentException(this.getId() + " has no " + cls.getSimpleName() + "s in " + address.getAisleId());
		} else if (device == null) {
			throw new IllegalArgumentException(this.getId() + " has no " + cls.getSimpleName() + "s");
		}

		return device;
	}

	/**
	 * Validates the aisle ID and finds a robot. Then instructs the robot to clean up the aisle.
	 *
	 * @param address  Location for the cleaning event
	 * @param item  String representing the thing that needs to be cleaned up
	 */
	public void cleanUpAisle(Address address, String item) {
		// Check if the given aisle ID exists
		this.validateAisleId(address.getAisleId(), false);

		// Find a robot
		Device robot = this.getDeviceAtAddress(address, Robot.class, false);

		// Clean up the aisle
		robot.cleanUpAisle(address, item);
	}

	/**
	 * If the event is an emergency, retrieves all the turnstiles in the store and sets their state property.
	 * Otherwise, checks if the given device is a turnstile and sets the state property of the turnstile.
	 *
	 * @param deviceId  Unique ID of the device
	 * @param isEmergency  boolean that identifies if the event is an emergency or not
	 * @param open  boolean that identifies if the turnstile needs to be opened or closed.
	 */
	public void setTurnstileState(String deviceId, boolean isEmergency, boolean open) {
		if (isEmergency) {
			// Get all turnstiles of the store
			List<Device> turnstiles = this.getDevices(Turnstile.class);

			if (turnstiles.size() > 0) {
				for (Device turnstile : turnstiles) {
					// Set the state of the turnstile
					turnstile.setState(open);
				}
			} else {
				System.out.println("WARNING: " + this.getId() + " has no turnstiles.");
			}

		} else {
			// Get the device
			Device device = this.getDevice(deviceId);

			// Check if it is a turnstile
			if (!(device instanceof Turnstile)) {
				throw new IllegalArgumentException(device.getId() + " is not a turnstile");
			}

			// Set the state of the turnstile
			device.setState(open);
		}
	}

	/**
	 * If the event is an emergency, retrieves all the speakers in the store and announces the given message.
	 * Otherwise, retrieves the location of the source device (device that emitted the event), finds a speaker at
	 * that exact location and announces the message.
	 *
	 * @param sourceDeviceId  Unique ID of the source device
	 * @param isEmergency  boolean that identifies if the event is an emergency or not
	 * @param message  Message to be announced
	 */
	public void announceMessage(String sourceDeviceId, boolean isEmergency, String message) {

		if (isEmergency) {
			// Get all speakers of the store
			List<Device> speakers = this.getDevices(Speaker.class);

			if (speakers.size() > 0) {
				for (Device speaker : speakers) {
					// Announce the message
					speaker.announceMessage(message);
				}
			} else {
				System.out.println("WARNING: " + this.getId() + " has no speakers.");
			}
		} else {
			// Get the source device that emitted the event
			Device sourceDevice = this.getDevice(sourceDeviceId);

			// Get the location of the source device
			Address address = sourceDevice.getAddress();

			// Find the speaker that has the same location as the source device
			Device speaker = this.getDeviceAtAddress(address, Speaker.class, true);

			// Announce the message
			speaker.announceMessage(message);
		}
	}


	/**
	 * Checks if the given device is a Robot and then instructs the robot to address the emergency.
	 *
	 * @param robotId  Unique ID of the robot
	 * @param emergencyType  Type of the emergency
	 * @param address  Location for the emergency
	 */
	public void addressEmergency(String robotId, EmergencyType emergencyType, Address address) {
		Device device = this.getDevice(robotId);

		// Validate the device
		if (!(device instanceof Robot)) {
			throw new IllegalArgumentException(device.getId() + " is not a robot");
		}

		// Address the emergency
		this.deviceMap.get(robotId).addressEmergency(emergencyType, address);
	}

	/**
	 * Checks if the given device is a Robot and then instructs the robot to assist customers exit the store.
	 *
	 * @param robotId  Unique ID of the robot
	 */
	public void assistCustomersExit(String robotId) {
		Device device = this.getDevice(robotId);

		// Validate the device
		if (!(device instanceof Robot)) {
			throw new IllegalArgumentException(device.getId() + " is not a robot");
		}

		// Assist customers exit the store
		this.deviceMap.get(robotId).assistCustomersExit(this.id);
	}

	/**
	 * Retrieves the customer's current location and finds a robot. Then instructs the robot to assist customer to car.
	 *
	 * @param customer  The customer who needs assistance
	 */
	public void assistCustomerToCar(Customer customer) {
		// Get customer's current location
		Address customersLocation = customer.getLocation();

		// Find a robot to assist the customer
		Device robot = this.getDeviceAtAddress(customersLocation, Robot.class, false);

		// Assist customer to car
		robot.assistCustomerToCar(customer);
	}

	/**
	 * Returns the string representation of the Store object that includes the ID, name, and address of the store. It
	 * also includes the inventory list, aisle list, and device list if they are not empty.
	 *
	 * @return  String representation of the Store object
	 */
	public String toString() {
		// Add the Store ID, name, and address to the output string
		StringBuilder output = new StringBuilder(String.format("Store ID: %-15s Name: %-15s Address: %-30s%n", this.id,
				this.name, this.phsicalAddress));

		// Add the inventory list to the output string if it is not empty
		if (!this.inventoryMap.isEmpty()) {
			output.append("\nINVENTORY LIST");
			for (Map.Entry<String, Inventory> entry : this.inventoryMap.entrySet()) {
				output.append(String.format("%s%n", entry.getValue()));
			}
		}

		// Add the aisle list to the output string if it is not empty
		if (!this.aisleMap.isEmpty()) {
			output.append("\nAISLE LIST");
			for (Map.Entry<String, Aisle> entry : this.aisleMap.entrySet()) {
				output.append(String.format("%s%n", entry.getValue()));
			}
		}

		// Add the device list to the output string if it is not empty
		if (!this.deviceMap.isEmpty()) {
			output.append("\nDEVICE LIST");
			for (Map.Entry<String, Device> entry : this.deviceMap.entrySet()) {
				output.append(String.format("%s%n", entry.getValue()));
			}
		}

		return output.toString();
	}
}
