package com.cscie97.store;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a shopping basket used by the customers to carry product items. It has a unique ID and a
 * productCountMap that maps Product objects to their count in the basket.
 *
 * @author Burak Ufuktepe
 */
public class Basket {

	/**
	 * Weight threshold in ounces for assisting customers to their cars. If the total weight of basket items exceed
	 * this threshold, a robot will assist the customer to his/her.
	 */
	public static final double WEIGHT_THRESHOLD = 160;

	/**
	 * Unique identifier of the basket.
	 */
	private String id;

	/**
	 * Map of Product objects that are contained in the basket and their count.
	 */
	private Map<Product, Integer> productCountMap;

	/**
	 * Class constructor. Sets the unique ID and initializes productCountMap.
	 *
	 * @param anId  Unique ID of the basket
	 */
	public Basket(String anId) {
		this.id = anId;
		this.productCountMap = new HashMap<>();
		System.out.println(this.id + " created");
	}

	/**
	 * Retrieves the unique ID of the basket.
	 *
	 * @return  Basket ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Add a product item to the basket. Checks if quantity is positive and throws an IllegalArgumentException if
	 * quantity is not positive.
	 *
	 * @param aProduct  Product object to be added to the basket
	 * @param quantity  Number of items to be added to the basket
	 * @return  the Basket object
	 */
	public Basket addToBasket(Product aProduct, int quantity) {
		// Check if quantity is positive
		if (quantity <= 0) {
			throw new IllegalArgumentException("Quantity must be a positive integer");
		}

		// Get the actual product object from productCountMap
		Product product = this.getProduct(aProduct);

		// Add the product to productCountMap if it does not already exist
		if (product == null) {
			product = aProduct;
			this.productCountMap.put(product, 0);
		}

		int preQuantity = this.productCountMap.get(product);
		int postQuantity = this.productCountMap.get(product) + quantity;

		// Update the quantity
		this.productCountMap.put(product, postQuantity);

		System.out.println("The number of " + product.getId() + " in " + this.id + " changed from " + preQuantity
				+ " to " + postQuantity);

		return this;
	}

	/**
	 * Removes a product item from the basket. Validates the following conditions:
	 * - quantity is positive
	 * - product exists in the basket
	 * - new quantity is non-negative
	 * Throws an IllegalArgumentException if any of the above conditions are not satisfied.
	 *
	 * @param aProduct  Product object to be removed from the basket
	 * @param quantity  Number of product items to be removed from the basket
	 * @return  The Basket object
	 */
	public Basket removeFromBasket(Product aProduct, int quantity) {
		// Check if quantity is positive
		if (quantity <= 0) {
			throw new IllegalArgumentException("Quantity must be a positive integer");
		}

		// Get the actual product object from productCountMap
		Product product = this.getProduct(aProduct);

		// Check if the product exists in the basket
		if (product == null) {
			throw new IllegalArgumentException("Product " + aProduct.getId() + " was not found in " + this.id);
		}

		int preQuantity = this.productCountMap.get(product);
		int postQuantity = this.productCountMap.get(product) - quantity;

		// Check if the new quantity is negative
		if (postQuantity < 0) {
			throw new IllegalArgumentException("New quantity cannot be negative");
		}

		// Remove product from productCountMap if newQuantity is zero. Otherwise, update the quantity for the product.
		if (postQuantity == 0) {
			this.productCountMap.remove(product);
		} else {
			this.productCountMap.put(product, postQuantity);
		}

		System.out.println("The number of " + product.getId() + " in " + this.id + " changed from " + preQuantity +
				" to "
				+ postQuantity);

		return this;
	}

	/**
	 * Clears the productCountMap.
	 *
	 * @return  the Basket object
	 */
	public Basket clearBasket() {
		this.productCountMap.clear();
		System.out.println("Cleared all items in " + this.id);
		return this;
	}

	/**
	 * Computes the total bill for the basket.
	 *
	 * @return  total bill
	 */
	public int computeTotal() {
		int total = 0;
		for (Map.Entry<Product, Integer> entry : this.productCountMap.entrySet()) {
			total += entry.getKey().getUnitPrice() * entry.getValue();
		}

		System.out.println("The total value of items in " + this.id + " is " + total);

		return total;
	}

	/**
	 * Computes the weight of the basket.
	 *
	 * @return  basket's weight
	 */
	public int computeWeight() {
		int total = 0;
		for (Map.Entry<Product, Integer> entry : this.productCountMap.entrySet()) {
			total += entry.getKey().getWeight() * entry.getValue();
		}

		int weightLbs = (int) Math.floor(total / 16.0);
		double weightOz = total - weightLbs * 16;

		System.out.println("The total weight of items in " + this.id + " is " + weightLbs + " lbs and " + weightOz +
				" oz.");

		return total;
	}

	/**
	 * Retrieves the Product object from productCountMap which has the same product ID as the given Product object.
	 *
	 * @param product  The given Product object
	 * @return  The Product object from productCountMap. Returns null if no match is found.
	 */
	private Product getProduct(Product product) {
		// Iterate over productCountMap and return the Product that has the same product ID as the given Product object.
		for (Map.Entry<Product, Integer> entry : this.productCountMap.entrySet()) {
			if (entry.getKey().getId().equals(product.getId())) {
				return entry.getKey();
			}
		}

		// If no match is found return null
		return null;
	}

	/**
	 * Returns the string representation of the Basket object. It includes the basket ID. If the Basket is not empty
	 * it also includes product details, quantity, and total price of each product in the basket.
	 *
	 * @return  String representation of the Basket object
	 */
	public String toString() {

		// Add the basket ID to the output string
		StringBuilder output = new StringBuilder(String.format("Basket ID: %-10s%n", this.id));

		if (!this.productCountMap.isEmpty()) {
			// Add titles for product details, quantity, and total price
			output.append(String.format("%-15s%-15s%-20s%-15s%-15s%-15s%-15s%-15s%-15s", "PRODUCT_ID", "NAME",
					"DESCRIPTION", "SIZE", "CATEGORY", "UNIT_PRICE", "TEMPERATURE", "QUANTITY", "TOTAL_PRICE"));

			// Add product details, quantities, and total prices
			for (Map.Entry<Product, Integer> entry : this.productCountMap.entrySet()) {
				Product p = entry.getKey();
				output.append(String.format("%n%-15s%-15s%-20s%-15s%-15s%-15s%-15s%-15s%-15s", p.getId(), p.getName(),
						p.getDescription(), p.getWeight(), p.getCategory(), p.getUnitPrice(), p.getTemperature(),
						entry.getValue(), entry.getValue() * entry.getKey().getUnitPrice()));
			}
		} else {
			output.append("Basket is empty");
		}

		return output.toString();
	}
}
