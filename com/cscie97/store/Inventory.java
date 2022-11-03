package com.cscie97.store;

/**
 * The Inventory class helps identify the location of products within the Store. It contains a single product type
 * and has a location in the form of storeId:aisleId:shelfId. It also maintains the count of products and has a
 * capacity that identifies the maximum number of products that can fit on the shelf.
 *
 * @author Burak Ufuktepe
 */
public class Inventory {

	/**
	 * Percentage threshold for restocking inventory. When inventory levels fall below the threshold, the inventory
	 * will be restocked. Must be a value between 0 <= INVENTORY_THRESHOLD <= 1.
	 */
	public static final double INVENTORY_THRESHOLD = 0.5;

	/**
	 * Unique identifier of the inventory.
	 */
	private String id;

	/**
	 * The maximum number of product items that can fit on the shelf.
	 */
	private int capacity;

	/**
	 * The current count of product items on the shelf that is less than or equal to the capacity and non-negative.
	 */
	private int count;

	/**
	 * Location of the inventory.
	 */
	private Address location;

	/**
	 * Product ID of the product in the inventory.
	 */
	private String productId;

	/**
	 * Class constructor that sets the ID, capacity, count, location, and product ID.
	 *
	 * @param anId  Unique identifier of the inventory
	 * @param aCapacity  Total capacity of the inventory on the shelf
	 * @param aCount  Current count of product items
	 * @param aLocation  Location of the inventory
	 * @param aProductId  Product ID of the inventory
	 */
	public Inventory(String anId, int aCapacity, int aCount, Address aLocation, String aProductId) {
		this.id = anId;
		this.capacity = aCapacity;
		this.count = aCount;
		this.location = aLocation;
		this.productId = aProductId;
	}

	/**
	 * Retrieves the unique ID of the inventory.
	 *
	 * @return  Inventory ID
	 */
	public String getId() {
		return this.id;
	}

	public int getCapacity() {
		return this.capacity;
	}

	public int getCount() {
		return this.count;
	}

	public Address getLocation() {
		return this.location;
	}

	public String getProductId() {
		return this.productId;
	}

	/**
	 * Verifies the following conditions:
	 * - New product count does not exceed capacity
	 * - New product count is non-negative
	 * Throws an IllegalArgumentException if any of the above conditions are not satisfied.
	 *
	 * @param changeCount  number of product items to increment/decrement
	 */
	public void validateInventoryUpdate(int changeCount) {
		int newCount = this.count + changeCount;

		// Check if the new number of product items exceeds shelf capacity
		if (newCount > this.capacity) {
			throw new IllegalArgumentException("The number of product items exceeds shelf capacity");
		}

		// Check if the new number of product items is negative
		if (newCount < 0) {
			throw new IllegalArgumentException("The number of product items cannot be negative");
		}
	}

	/**
	 * Increments or decrements the inventory count.
	 *
	 * @param changeCount  number of product items to increment/decrement
	 * @return  The inventory object
	 */
	public Inventory updateInventory(int changeCount) {

		int newCount = this.count + changeCount;

		System.out.println("The number of products in " + this.id + " changed from " + this.count + " to " + newCount);

		this.count = newCount;

		return this;
	}

	/**
	 * Returns the string representation of the Inventory object that includes the ID, capacity, count, location, and
	 * product ID.
	 *
	 * @return  String representation of the Inventory object
	 */
	public String toString() {
		return String.format("Inventory ID: %-15s Capacity: %-10s Count: %-10s Location: %-20s Product ID: %-15s",
				this.id, this.capacity, this.count, this.location, this.productId);
	}
}
