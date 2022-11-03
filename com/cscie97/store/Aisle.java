package com.cscie97.store;

import javax.swing.plaf.TableHeaderUI;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an aisle within a store which contains shelves. Aisles can be located either in the STORE_ROOM or FLOOR.
 *
 * @author Burak Ufuktepe
 */
public class Aisle {

	/**
	 * Unique identifier of the aisle.
	 */
	private String id;

	/**
	 * Name of the aisle.
	 */
	private String name;

	/**
	 * Aisle description.
	 */
	private String description;

	/**
	 * Location of the aisle (STORE_ROOM or FLOOR)
	 */
	private RoomType location;

	/**
	 * Map of shelf IDs and Shelf objects which the aisle contains.
	 */
	private Map<String, Shelf> shelfMap;

	/**
	 * Class constructor that sets the ID, name, description, location of the aisle. Also initializes the shelfMap.
	 *
	 * @param anId  String that uniquely identifies the aisle
	 * @param aName  Name of the aisle
	 * @param aDescription  Description of the aisle
	 * @param aLocation  RoomType that identifies the location of the aisle
	 */
	public Aisle(String anId, String aName, String aDescription, RoomType aLocation) {
		this.id = anId;
		this.name = aName;
		this.description = aDescription;
		this.location = aLocation;
		this.shelfMap = new HashMap<>();
	}

	/**
	 * Retrieves the unique ID of the aisle.
	 *
	 * @return  Aisle ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Retrieves the name of the aisle.
	 *
	 * @return  Aisle name
	 */
	public String getName() {
		return this.name;
	}


	/**
	 * Retrieves the location of the aisle
	 * @return  RoomType that identifies the location of the aisle
	 */
	public RoomType getLocation() {
		return this.location;
	}

	/**
	 * Creates a Shelf object and adds it to shelfMap.
	 *
	 * @param shelfId  Unique ID of the shelf
	 * @param name  Name of the shelf
	 * @param level  LevelType that identifies the height of the shelf
	 * @param description  Description of shelf contents
	 * @param temperature  TemperatureType that identifies the shelf temperature
	 * @return  The new Shelf object
	 */
	public Shelf defineShelf(String shelfId, String name, LevelType level, String description,
							 TemperatureType temperature) {
		// Check if the given shelf id is unique
		if (this.shelfMap.containsKey(shelfId)) {
			throw new IllegalArgumentException("Shelf ID " + shelfId + " already exists");
		}

		Shelf shelf = new Shelf(shelfId, name, level, description, temperature);
		shelfMap.put(shelf.getId(), shelf);

		System.out.println(shelf.getId() + " created.");

		return shelf;
	}

	/**
	 * Verifies the shelf ID and returns the shelf.
	 *
	 * @param shelfId  Unique ID of the shelf
	 * @return  The Shelf object for the given ID
	 */
	public Shelf getShelf(String shelfId) {
		// Check if the given shelf id exists
		if (!this.shelfMap.containsKey(shelfId)) {
			throw new IllegalArgumentException(this.id + " does not include " + shelfId + ".");
		}

		return this.shelfMap.get(shelfId);
	}

	/**
	 * Returns the string representation of the Aisle object that includes the ID, name, description, and location.
	 *
	 * @return  String representation of the Aisle object
	 */
	public String toString() {
		return String.format("Aisle ID: %-10s Name: %-10s Description: %-20s Location: %-10s", this.id, this.name,
				this.description, this.location);
	}
}
