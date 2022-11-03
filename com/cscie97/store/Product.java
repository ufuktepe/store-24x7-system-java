package com.cscie97.store;

/**
 * Represents a product that is available for sale within a store.
 *
 * @author Burak Ufuktepe
 */
public class Product {

	/**
	 * Unique identifier of the product.
	 */
	private String id;

	/**+
	 * Name of the product.
	 */
	private String name;

	/**
	 * Product description.
	 */
	private String description;

	/**
	 * Weight of the product in ounces.
	 */
	private double weight;

	/**
	 * Type of the product.
	 */
	private String category;

	/**
	 * Unit price of the product in blockchain currency.
	 */
	private int unitPrice;

	/**
	 * Temperature of the product (FROZEN, REFRIGERATED, AMBIENT, WARM, HOT).
	 */
	private TemperatureType temperature;

	/**
	 * Class constructor that sets the ID, name, description, size, category, unit price, and temperature.
	 *
	 * @param anId  String that uniquely identifies the product
	 * @param aName  Name of the product
	 * @param aDescription  Description of the product
	 * @param aWeight  Weight of the product in ounces
	 * @param aCategory  Type of the product
	 * @param aUnitPrice  Unit price of the product in blockchain currency
	 * @param aTemperature  TemperatureType that identifies the storage temperature of the product
	 */
	public Product(String anId, String aName, String aDescription, double aWeight, String aCategory, int aUnitPrice,
				   TemperatureType aTemperature) {
		this.id = anId;
		this.name = aName;
		this.description = aDescription;
		this.weight = aWeight;
		this.category = aCategory;
		this.unitPrice = aUnitPrice;
		this.temperature = aTemperature;
	}

	/**
	 * Retrieves the unique ID of the product.
	 *
	 * @return  Product ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Retrieves the name of the product.
	 *
	 * @return  Product name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Retrieves the description of the product.
	 *
	 * @return  Product description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Retrieves the weight of the product in ounces.
	 *
	 * @return  Product weight
	 */
	public double getWeight() {
		return this.weight;
	}

	/**
	 * Retrieves the category of the product.
	 *
	 * @return  Product category
	 */
	public String getCategory() {
		return this.category;
	}

	/**
	 * Retrieves the unit price of the product.
	 *
	 * @return  Unit price of the product
	 */
	public int getUnitPrice() {
		return this.unitPrice;
	}

	/**
	 * Retrieves the storage temperature of the product.
	 *
	 * @return  Storage temperature of the product
	 */
	public TemperatureType getTemperature() {
		return this.temperature;
	}

	/**
	 * Returns the string representation of the Product object that includes the ID, name, description, size,
	 * category, unit price, and temperature.
	 *
	 * @return  String representation of the Product object
	 */
	public String toString() {
		return String.format("Product ID: %-15s Name: %-15s Description: %-20s Size: %-15s Category: %-15s Unit "
						+ "Price: %-15d Temperature: %-15s%n", this.id, this.name, this.description, this.weight,
				this.category, this.unitPrice, this.temperature);
	}
}
