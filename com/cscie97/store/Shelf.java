package com.cscie97.store;

/**
 * Represents a shelf within an aisle within a store.
 *
 * @author Burak Ufuktepe
 */
public class Shelf {

	/**
	 * Unique identifier of the shelf.
	 */
	private String id;

	/**
	 * Name of the shelf.
	 */
	private String name;

	/**
	 * Height of the shelf (HIGH, MEDIUM, LOW).
	 */
	private LevelType level;

	/**
	 * Description of shelf contents.
	 */
	private String description;

	/**
	 * Temperature of the shelf (FROZEN, REFRIGERATED, AMBIENT, WARM, HOT).
	 */
	private TemperatureType temperature;

	/**
	 * Class constructor that sets the ID, name, level, description, and temperature of the shelf.
	 *
	 * @param anId  String that uniquely identifies the shelf
	 * @param aName  Name of the shelf
	 * @param aLevel  LevelType that identifies the height of the shelf
	 * @param aDescription  Description of shelf contents
	 * @param aTemperature  TemperatureType that identifies the shelf temperature
	 */
	public Shelf(String anId, String aName, LevelType aLevel, String aDescription, TemperatureType aTemperature) {
		this.id = anId;
		this.name = aName;
		this.level = aLevel;
		this.description = aDescription;
		this.temperature = aTemperature;
	}

	/**
	 * Retrieves the unique ID of the shelf.
	 *
	 * @return  Shelf ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Retrieves the name of the shelf.
	 *
	 * @return  Shelf name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the string representation of the Shelf object that includes the ID, name, level, description, and
	 * temperature.
	 *
	 * @return  String representation of the Shelf object
	 */
	public String toString() {
		return String.format("Shelf ID: %-10s Name: %-10s Level: %-6s Description: %-20s Temperature: %-12s%n", this.id,
				this.name, this.level, this.description, this.temperature);
	}
}
