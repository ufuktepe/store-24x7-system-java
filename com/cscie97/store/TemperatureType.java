package com.cscie97.store;

/**
 * Identifies the temperature of a shelf or storage temperature of a product.
 *
 * @author Burak Ufuktepe
 */
public enum TemperatureType {
	/**
	 * Temperature below 32°F.
	 */
	FROZEN,

	/**
	 * Temperature at 40°F.
	 */
	REFRIGERATED,

	/**
	 * Temperature at 73°F.
	 */
	AMBIENT,

	/**
	 * Temperature at 140°F.
	 */
	WARM,

	/**
	 * Temperature above 140°F.
	 */
	HOT
}
