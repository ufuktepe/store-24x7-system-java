package com.cscie97.store;

import java.sql.Timestamp;

/**
 * Represents a person who shops at the store. A customer has a unique ID, name, email address and blockchain account
 * address, and it can be a registered customer or a guest. The location attribute identifies the location of the
 * customer (storeId:asileId) and the lastSeen attribute shows the time of the last location update.
 *
 * @author Burak Ufuktepe
 */
public class Customer {

	/**
	 * Unique identifier of the customer.
	 */
	private String id;

	/**
	 * First name of the customer.
	 */
	private String firstName;

	/**
	 * Last name of the customer.
	 */
	private String lastName;

	/**
	 * Type of the customer (REGISTERED or GUEST).
	 */
	private CustomerType type;

	/**
	 * email address of the customer.
	 */
	private String email;

	/**
	 * Blockchain account address of the customer.
	 */
	private String accountAddress;

	/**
	 * Location of the customer.
	 */
	private Address location;

	/**
	 * Time last seen (gets updated when location updates).
	 */
	private Timestamp lastSeen;

	/**
	 * Shopping basket of the customer.
	 */
	private Basket basket;

	/**
	 * Class constructor. Sets the ID, name, type, email, and account address of the customer.
	 *
	 * @param anId  Unique identifier of the customer
	 * @param aFirstName  Customer's first name
	 * @param aLastName  Customer's last name
	 * @param aType  Customer type (REGISTERED or GUEST)
	 * @param anEmail  Customer's email address
	 * @param anAccountAddress  Customer's blockchain account address
	 */
	public Customer(String anId, String aFirstName, String aLastName, CustomerType aType, String anEmail,
					String anAccountAddress) {
		this.id = anId;
		this.firstName = aFirstName;
		this.lastName = aLastName;
		this.type = aType;
		this.email = anEmail;
		this.accountAddress = anAccountAddress;
	}

	/**
	 * Sets the basket of the customer.
	 *
	 * @param basket  Basket object representing a shopping basket
	 */
	public void setBasket(Basket basket) {
		this.basket = basket;
	}

	/**
	 * Retrieves the basket of the customer.
	 *
	 * @return  Customer's basket
	 */
	public Basket getBasket() {
		return this.basket;
	}

	/**
	 * Retrieves the ID of the customer.
	 *
	 * @return  Customer ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Retrieves the first name of the customer.
	 *
	 * @return  Customer's first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Retrieves the location of the customer.
	 *
	 * @return  Customer's location
	 */
	public Address getLocation() {
		return this.location;
	}

	/**
	 * Retrieves the account address of the customer.
	 *
	 * @return  Customer's account address
	 */
	public String getAccountAddress() {
		return this.accountAddress;
	}

	/**
	 * Determines whether the customer is registered or not.
	 *
	 * @return  True if registered. Otherwise, returns false.
	 */
	public boolean isRegistered() {
		return this.type == CustomerType.REGISTERED;
	}

	/**
	 * Sets the location of the customer. Also updates time last seen.
	 *
	 * @param location  Location of the customer in the form of storeId:aisleId
	 * @return  The Customer object
	 */
	public Customer updateLocation(Address location) {
		if (location == null && this.location != null) {
			System.out.println(this.id + " left " + this.location.getStoreId());
		} else if (location != null && this.location == null) {
			System.out.println(this.id + " entered " + location.getStoreId() + " and moved to "
					+ location.getAisleId());
		} else if (location != null) {
			System.out.println(this.id + " moved from " + this.location.getAisleId() + " to " + location.getAisleId());
		}

		this.location = location;
		this.lastSeen = new Timestamp(System.currentTimeMillis());

		return this;
	}

	/**
	 * Returns the string representation of the Customer object. It also includes basket details if the customer has
	 * a basket.
	 *
	 * @return  String representation of the Customer object
	 */
	public String toString() {
		// Get customer details
		StringBuilder output = new StringBuilder(String.format("Customer ID: %-15s Name: %-20s Type: %-10s "
						+ "Email: %-30s Account: %-15s Location: %-20s Last Seen: %-25s", this.id,
				this.firstName + " " + this.lastName, this.type.toString(), this.email, this.accountAddress,
				this.location, this.lastSeen));

		// Add basket details if basket is not null
		if (this.basket != null) {
			output.append(this.basket);
		}
		output.append("\n");

		return output.toString();
	}
}
