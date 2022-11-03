package com.cscie97.store;

import com.cscie97.authentication.AccessDeniedException;
import com.cscie97.authentication.AuthenticationService;
import com.cscie97.authentication.InvalidAuthTokenException;
import com.cscie97.controller.Event;

import java.util.*;
import java.util.List;

/**
 * Provides the API used by clients of the Store Model Service. It contains Stores, Products, Customers, and Baskets.
 *
 * @author Burak Ufuktepe
 */
public class StoreModelService implements Subject{

	/**
	 * Map of Store IDs and Store objects.
	 */
	private Map<String, Store> storeMap;

	/**
	 * Map of Customer IDs and Customer objects.
	 */
	private Map<String, Customer> customerMap;

	/**
	 * Map of Product IDs and Product objects.
	 */
	private Map<String, Product> productMap;

	/**
	 * Map of Basket IDs and Basket objects.
	 */
	private Map<String, Basket> basketMap;

	/**
	 * Map of Inventory IDs and Store objects.
	 */
	private Map<String, Store> inventoryStoreMap;

	/**
	 * Map of Device IDs and Store objects.
	 */
	private Map<String, Store> deviceStoreMap;

	/**
	 * Map of Basket IDs and associated Customer objects.
	 */
	private Map<String, Customer> basketCustomerMap;

	/**
	 * List of observers that are notified when events occur (Observer Pattern).
	 */
	private List<Observer> observerList;

	/**
	 * Authentication Service instance.
	 */
	private AuthenticationService authenticationService;

	/**
	 * Class constructor that initializes all the Maps.
	 */
	public StoreModelService(AuthenticationService authenticationService) {
		this.storeMap = new HashMap<>();
		this.customerMap = new HashMap<>();
		this.productMap = new HashMap<>();
		this.basketMap = new HashMap<>();
		this.inventoryStoreMap = new HashMap<>();
		this.deviceStoreMap = new HashMap<>();
		this.basketCustomerMap = new HashMap<>();
		this.observerList = new ArrayList<>();
		this.authenticationService = authenticationService;
	}

	public void checkPermission(String permissionId, String resourceId, String authToken) throws StoreModelServiceException {

		if (authToken == null || authToken == "") {
			throw new StoreModelServiceException("check permission", "AuthToken must be non-null and non-empty");
		}

		try {
			this.authenticationService.checkPermission(permissionId, resourceId, authToken);
		} catch (InvalidAuthTokenException e) {
			throw new StoreModelServiceException(e.getAction(), e.getReason());
		} catch (AccessDeniedException e) {
			throw new StoreModelServiceException(e.getAction(), e.getReason());
		}
	}

	/**
	 * Validates the given store ID and creates a new store. Adds the new store to storeMap.
	 *
	 * @param id  Unique ID of the store
	 * @param name  Name of the store
	 * @param physicalAddress  Physical address of the store
	 * @param authToken  Authorization token
	 * @return  The Store object
	 * @throws StoreModelServiceException  if the given store ID already exists
	 */
	public Store defineStore(String id, String name, String physicalAddress, String authToken)
			throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("provision_store", null, authToken);

		// Check if the given id is unique
		if (this.storeMap.containsKey(id)) {
			throw new StoreModelServiceException("define store", id + " already exists");
		}

		Store store = new Store(id, name, physicalAddress);
		this.storeMap.put(store.getId(), store);

		return store;
	}

	/**
	 * Validates the given store ID and retrieves the Store object for the given store ID.
	 *
	 * @param id  Unique ID of the store
	 * @param authToken  Authorization token
	 * @return  The Store object
	 * @throws StoreModelServiceException  if the given store ID does not exist
	 */
	public Store getStore(String id, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_resource", null, authToken);

		// Check if the given id exists
		if (!this.storeMap.containsKey(id)) {
			throw new StoreModelServiceException("get store", id + " does not exist");
		}

		return this.storeMap.get(id);
	}

	/**
	 * Returns the store object for the given device ID. Used by the Controller Service to retrieve the store ID from
	 * a given device ID.
	 *
	 * @param id  Unique ID of the device
	 * @param authToken  Authorization token
	 * @return  The store object
	 * @throws StoreModelServiceException  if the device ID does not exist
	 */
	public Store getStoreFromDeviceId(String id, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_resource", null, authToken);

		// Check if the given id exists
		if (!this.deviceStoreMap.containsKey(id)) {
			throw new StoreModelServiceException("get store from device id", id + " does not exist");
		}

		return this.deviceStoreMap.get(id);
	}

	/**
	 * Validates the address and room, then creates an aisle.
	 *
	 * @param addressString  String representing the location in the form of storeId:aisleId
	 * @param name  Name of the aisle
	 * @param description  Description of the aisle
	 * @param room  The room which the aisle is located in (STORE_ROOM or FLOOR)
	 * @param authToken  Authorization token
	 * @return  The Aisle object
	 * @throws StoreModelServiceException  if address or room is invalid
	 */
	public Aisle defineAisle(String addressString, String name, String description, String room, String authToken)
			throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("create_resource", null, authToken);

		// Get the address components
		String[] aisleAddress;
		try {
			aisleAddress = Address.extractAisleAddress(addressString);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("define aisle", e.getMessage());
		}
		String storeId = aisleAddress[0];
		String aisleId = aisleAddress[1];

		// Validate the store ID
		if (!this.storeMap.containsKey(storeId)) {
			throw new StoreModelServiceException("define aisle", storeId + " does not exist");
		}

		// Validate the room
		RoomType roomType;
		try {
			roomType = RoomType.valueOf(room.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("define aisle", room + " is not a valid room");
		}

		// Have the store create the aisle and return the aisle
		try {
			return this.storeMap.get(storeId).defineAisle(aisleId, name, description, roomType);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("define aisle", e.getMessage());
		}
	}

	/**
	 * Validates the address and retrieves the Aisle object for the given address.
	 *
	 * @param addressString  String representing the location in the form of storeId:aisleId
	 * @param authToken  Authorization token
	 * @return  The Aisle object
	 * @throws StoreModelServiceException  if address is invalid
	 */
	public Aisle getAisle(String addressString, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_resource", null, authToken);

		// Get the address components
		String[] aisleAddress;
		try {
			aisleAddress = Address.extractAisleAddress(addressString);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("get aisle", e.getMessage());
		}
		String storeId = aisleAddress[0];
		String aisleId = aisleAddress[1];

		// Validate the store ID
		if (!this.storeMap.containsKey(storeId)) {
			throw new StoreModelServiceException("get aisle", storeId + " does not exist");
		}

		// Get the Aisle object from the store
		try {
			return this.storeMap.get(storeId).getAisle(aisleId);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("get aisle", e.getMessage());
		}
	}

	/**
	 * Validates the address, level, and temperature, then creates a shelf.
	 *
	 * @param addressString  String representing the location in the form of storeId:aisleId:shelfId
	 * @param name  Name of the shelf
	 * @param level  LevelType that identifies the height of the shelf
	 * @param description  Description of shelf contents
	 * @param temperature  TemperatureType that identifies the shelf temperature
	 * @param authToken  Authorization token
	 * @return  The Shelf object
	 * @throws StoreModelServiceException  if address, level, or temperature is invalid
	 */
	public Shelf defineShelf(String addressString, String name, String level, String description, String temperature,
							 String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("create_resource", null, authToken);

		// Create the address
		Address address;
		try {
			address = Address.constructFromShelfAddress(addressString);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("define shelf", e.getMessage());
		}
		String storeId = address.getStoreId();
		String aisleId = address.getAisleId();
		String shelfId = address.getShelfId();

		// Validate the store ID
		if (!this.storeMap.containsKey(storeId)) {
			throw new StoreModelServiceException("define shelf", storeId + " does not exist");
		}

		// Validate the level
		LevelType levelType;
		try {
			levelType = LevelType.valueOf(level.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("define shelf", level + " is not a valid level");
		}

		// Validate the temperature
		TemperatureType temperatureType;
		try {
			temperatureType = TemperatureType.valueOf(temperature.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("define shelf", temperature + " is not a valid temperature");
		}

		// Have the store create the shelf and return the shelf
		try {
			return this.storeMap.get(storeId).defineShelf(aisleId, shelfId, name, levelType, description,
					temperatureType);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("define shelf", e.getMessage());
		}
	}

	/**
	 * Validates the address and retrieves the Shelf object for the given address.
	 *
	 * @param addressString  String representing the location in the form of storeId:aisleId:shelfId
	 * @param authToken  Authorization token
	 * @return  The Shelf object
	 * @throws StoreModelServiceException  if address is invalid
	 */
	public Shelf getShelf(String addressString, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_resource", null, authToken);

		// Create the address
		Address address;
		try {
			address = Address.constructFromShelfAddress(addressString);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("get shelf", e.getMessage());
		}
		String storeId = address.getStoreId();
		String aisleId = address.getAisleId();
		String shelfId = address.getShelfId();

		// Validate the store ID
		if (!this.storeMap.containsKey(storeId)) {
			throw new StoreModelServiceException("get shelf", storeId + " does not exist");
		}

		// Get the Shelf object from the store
		try {
			return this.storeMap.get(storeId).getShelf(aisleId, shelfId);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("get shelf", e.getMessage());
		}
	}

	/**
	 * Validates the product ID, unit price, weight, and temperature, then creates a product and adds it to productMap.
	 *
	 * @param id  String that uniquely identifies the product
	 * @param name  Name of the product
	 * @param description  Description of the product
	 * @param weight  Weight of the product in ounces
	 * @param category  Type of the product
	 * @param unitPrice  Unit price of the product
	 * @param temperature  String which identifies the storage temperature of the product
	 * @param authToken  Authorization token
	 * @return  The Product object
	 * @throws StoreModelServiceException
	 */
	public Product defineProduct(String id, String name, String description, String weight, String category,
								 String unitPrice, String temperature, String authToken)
			throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("create_resource", null, authToken);

		// Check if the given id is unique
		if (this.productMap.containsKey(id)) {
			throw new StoreModelServiceException("define product", id + " already exists");
		}

		// Validate unit price
		int unitPriceInt;
		try {
			unitPriceInt = Integer.parseInt(unitPrice);
			if (unitPriceInt <= 0) {
				throw new StoreModelServiceException("define product", "Unit price must be positive");
			}
		} catch (NumberFormatException e) {
			throw new StoreModelServiceException("define product", "Invalid unit price");
		}

		// Validate the weight
		double weightDouble;
		try {
			weightDouble = Double.parseDouble(weight);
			if (weightDouble <= 0) {
				throw new StoreModelServiceException("define product", "Weight must be positive");
			}
		} catch (NumberFormatException e) {
			throw new StoreModelServiceException("define product", "Invalid unit price");
		}

		// Validate the temperature
		TemperatureType temperatureType;
		try {
			temperatureType = TemperatureType.valueOf(temperature.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("define product", temperature + " is not a valid temperature");
		}

		Product product = new Product(id, name, description, weightDouble, category, unitPriceInt, temperatureType);
		this.productMap.put(product.getId(), product);

		System.out.println(product.getId() + " created.");

		return product;
	}

	/**
	 * Validates the given product ID and retrieves the Product object from productMap.
	 *
	 * @param id  String that uniquely identifies the product
	 * @param authToken  Authorization token
	 * @return  The Product object
	 * @throws StoreModelServiceException  if the given product ID is invalid
	 */
	public Product getProduct(String id, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_resource", null, authToken);

		// Check if the given id exists
		if (!this.productMap.containsKey(id)) {
			throw new StoreModelServiceException("get product", id + " does not exist");
		}

		return this.productMap.get(id);
	}

	/**
	 * Validates the address, inventory ID, product ID, capacity, and count, then creates an inventory and adds it to
	 * inventoryStoreMap.
	 *
	 * @param inventoryId  Unique identifier of the inventory
	 * @param addressString  String representing the location in the form of storeId:aisleId:shelfId
	 * @param capacity  Total capacity of the inventory on the shelf
	 * @param count  Current count of product items
	 * @param productId  Unique identifier of the product
	 * @param authToken  Authorization token
	 * @return  The Inventory object
	 * @throws StoreModelServiceException
	 */
	public Inventory defineInventory(String inventoryId, String addressString, String capacity, String count,
									 String productId, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("create_resource", null, authToken);

		// Create the address
		Address address;
		try {
			address = Address.constructFromShelfAddress(addressString);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("define inventory", e.getMessage());
		}
		String storeId = address.getStoreId();

		// Validate the store ID
		if (!this.storeMap.containsKey(storeId)) {
			throw new StoreModelServiceException("define inventory", storeId + " does not exist");
		}

		// Validate the inventory ID
		if (this.inventoryStoreMap.containsKey(inventoryId)) {
			throw new StoreModelServiceException("define inventory", inventoryId + " already exists");
		}

		// Validate the Product ID
		if (!this.productMap.containsKey(productId)) {
			throw new StoreModelServiceException("define inventory", productId + " does not exist");
		}

		// Validate capacity
		int capacityInt;
		try {
			capacityInt = Integer.parseInt(capacity);
			if (capacityInt < 1) {
				throw new StoreModelServiceException("define inventory", "Capacity must be positive");
			}
		} catch (NumberFormatException e) {
			throw new StoreModelServiceException("define inventory", "Invalid capacity");
		}

		// Validate count
		int countInt;
		try {
			countInt = Integer.parseInt(count);
			if (countInt < 0 || countInt > capacityInt) {
				throw new StoreModelServiceException("define inventory", "Count must be less than or equal to the "
						+ "capacity and non-negative");
			}
		} catch (NumberFormatException e) {
			throw new StoreModelServiceException("define inventory", "Invalid count");
		}

		// Have the Store create the inventory
		Inventory inventory;
		try {
			inventory = this.storeMap.get(storeId).defineInventory(inventoryId, address, capacityInt, countInt,
					productId);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("define inventory", e.getMessage());
		}

		this.inventoryStoreMap.put(inventory.getId(), this.storeMap.get(storeId));

		System.out.println(inventory.getId() + " created.");

		return inventory;
	}

	/**
	 * Validates the given inventory ID and retrieves the Inventory object from inventoryStoreMap.
	 *
	 * @param id  String that uniquely identifies the inventory
	 * @param authToken  Authorization token
	 * @return  The Inventory object
	 * @throws StoreModelServiceException  if the given inventory ID is invalid
	 */
	public Inventory getInventory(String id, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_resource", null, authToken);

		// Check if the given id exists
		if (!this.inventoryStoreMap.containsKey(id)) {
			throw new StoreModelServiceException("get inventory", id + " does not exist");
		}

		return this.inventoryStoreMap.get(id).getInventory(id);
	}

	/**
	 * Validates the store ID and passes the responsibility of finding the inventory to the store. If no inventory is
	 * found throws a StoreModelServiceException
	 *
	 * @param address  location of the inventory
	 * @param productId  Unique ID of the product that is inlcuded in the inventory
	 * @param authToken  Authorization token
	 * @return  The Inventory object
	 * @throws StoreModelServiceException  if no inventory is found
	 */
	public Inventory findInventoryAtAddress(Address address, String productId, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_resource", null, authToken);

		String storeId = address.getStoreId();

		// Validate the store ID
		if (!this.storeMap.containsKey(storeId)) {
			throw new StoreModelServiceException("find inventory at address", storeId + " does not exist");
		}

		Inventory inventory = this.storeMap.get(storeId).findInventoryAtAddress(address, productId);

		if (inventory == null) {
			throw new StoreModelServiceException("find inventory at address", "No inventory found in " + address +
					" that includes " + productId);
		}

		return inventory;
	}

	/**
	 * Validates the given inventory ID and count. Then calls the store's validation method.
	 *
	 * @param id  String that uniquely identifies the inventory
	 * @param count  Current count of product items in the inventory
	 * @param authToken  Authorization token
	 * @return  The Inventory object
	 * @throws StoreModelServiceException  if the inventory ID or count is invalid
	 */
	public void validateInventoryUpdate(String id, String count, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_resource", null, authToken);

		// Check if the given id exists
		if (!this.inventoryStoreMap.containsKey(id)) {
			throw new StoreModelServiceException("validate inventory update", id + " does not exist");
		}

		// Validate count
		int countInt;
		try {
			countInt = Integer.parseInt(count);
		} catch (NumberFormatException e) {
			throw new StoreModelServiceException("validate inventory update", "Invalid count");
		}

		// Have the store validate the inventory
		try {
			this.inventoryStoreMap.get(id).validateInventoryUpdate(id, countInt);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("validate inventory update", e.getMessage());
		}
	}

	/**
	 * Validates the update and then updates the inventory object according to the given count. This action is
	 * triggered in response to a camera event. Hence, control_camera permission is checked.
	 *
	 * @param id  String that uniquely identifies the inventory
	 * @param count  Current count of product items in the inventory
	 * @param authToken  Authorization token
	 * @return  The Inventory object
	 * @throws StoreModelServiceException  if the inventory ID or count is invalid
	 */
	public Inventory updateInventory(String id, String count, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("control_camera", null, authToken);

		// Validate the update
		this.validateInventoryUpdate(id, count, authToken);

		return this.inventoryStoreMap.get(id).updateInventory(id, Integer.parseInt(count));
	}

	/**
	 * Validates the customer ID and type. Then creates a new customer and adds it to customerMap.
	 *
	 * @param id  Unique identifier of the customer
	 * @param first  Customer's first name
	 * @param last  Customer's last name
	 * @param type  Customer type (REGISTERED or GUEST)
	 * @param email  Customer's email address
	 * @param accountAddress  Customer's blockchain account address
	 * @param authToken  Authorization token
	 * @return  The Customer object
	 * @throws StoreModelServiceException  if the given customer ID or type is invalid
	 */
	public Customer defineCustomer(String id, String first, String last, String type, String email,
								   String accountAddress, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("create_user", null, authToken);

		// Validate the Customer ID
		if (this.customerMap.containsKey(id)) {
			throw new StoreModelServiceException("define customer", id + " already exists");
		}

		// Validate type
		CustomerType customerType;
		try {
			customerType = CustomerType.valueOf(type.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("define customer", type + " is not a valid customer type");
		}

		Customer customer = new Customer(id, first, last, customerType, email, accountAddress);
		this.customerMap.put(customer.getId(), customer);

		System.out.println(customer.getId() + " created.");

		return customer;
	}

	/**
	 * Validates the given address and customer ID. Then updates the location of the customer and returns
	 * the Customer object.
	 *
	 * @param customerId  Unique identifier of the customer
	 * @param address  Location of the customer
	 * @param authToken  Authorization token
	 * @return  The Customer object
	 * @throws StoreModelServiceException  if the given address, customer ID, or aisle ID is invalid
	 */
	public Customer updateCustomer(String customerId, Address address, String authToken)
			throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("update_user", null, authToken);

		// Validate the Customer ID
		if (!this.customerMap.containsKey(customerId)) {
			throw new StoreModelServiceException("update customer", customerId + " does not exist");
		}

		// Validate the store and aisle IDs if the address is not null
		if (address != null) {
			String storeId = address.getStoreId();
			String aisleId = address.getAisleId();

			// Validate the store ID
			if (!this.storeMap.containsKey(storeId)) {
				throw new StoreModelServiceException("update customer", storeId + " does not exist");
			}

			// Validate the Aisle ID
			try {
				this.storeMap.get(storeId).getAisle(aisleId);
			} catch (IllegalArgumentException e) {
				throw new StoreModelServiceException("update customer", e.getMessage());
			}
		}

		return this.customerMap.get(customerId).updateLocation(address);
	}

	/**
	 * Validates the customer ID and then returns the Customer object for the given customer ID.
	 *
	 * @param id  Unique identifier of the customer
	 * @param authToken  Authorization token
	 * @return  The Customer object
	 * @throws StoreModelServiceException  if the given customer ID is invalid
	 */
	public Customer getCustomer(String id, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_user", null, authToken);

		// Validate the Customer ID
		if (!this.customerMap.containsKey(id)) {
			throw new StoreModelServiceException("get customer", id + " does not exist");
		}

		return this.customerMap.get(id);
	}

	/**
	 * Returns the given customer's account address.
	 *
	 * @param customerId  Unique ID of the customer
	 * @param authToken  Authorization Token
	 * @return  Account of the customer
	 * @throws StoreModelServiceException  if the given customer ID is invalid
	 */
	public String getAccountAddress(String customerId, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_user", null, authToken);

		Customer customer = this.getCustomer(customerId, authToken);
		return customer.getAccountAddress();
	}

	/**
	 * Validates the given basket ID. Then creates a new basket and adds it to basketMap.
	 *
	 * @param id  Unique identifier of the basket
	 * @param authToken  Authorization token
	 * @return  The Basket object
	 * @throws StoreModelServiceException  if the given basket ID is invalid
	 */
	public Basket defineBasket(String id, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("create_resource", null, authToken);

		// Validate the Basket ID
		if (this.basketMap.containsKey(id)) {
			throw new StoreModelServiceException("define basket", id + " already exists");
		}

		Basket basket = new Basket(id);
		this.basketMap.put(basket.getId(), basket);

		return basket;
	}

	/**
	 * Validates the customer and basket IDs and checks if the customer is registered and if the basket is already
	 * assigned to a customer. Then assigns the basket to the customer.
	 *
	 * @param basketId  Unique identifier of the basket
	 * @param customerId  Unique identifier of the customer
	 * @param authToken  Authorization token
	 * @return  The Basket object
	 * @throws StoreModelServiceException  if any of the following conditions are satisfied:
	 * - The given customer ID is invalid
	 * - The given basket ID is invalid
	 * - The given customer is not a registered customer
	 * - The given customer already has a basket
	 * - The given basket is already assigned to a customer
	 */
	public Basket assignBasket(String basketId, String customerId, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("update_user", null, authToken);

		// Validate the customer ID
		if (!this.customerMap.containsKey(customerId)) {
			throw new StoreModelServiceException("assign basket", customerId + " does not exist");
		}

		// Validate the basket ID
		if (!this.basketMap.containsKey(basketId)) {
			throw new StoreModelServiceException("assign basket", basketId + " does not exist");
		}

		// Check if the customer is registered
		if (!this.customerMap.get(customerId).isRegistered()) {
			throw new StoreModelServiceException("assign basket", customerId + " is not registered");
		}

		Basket basket = this.basketMap.get(basketId);
		Customer customer = this.customerMap.get(customerId);

		// Check if the customer already has a basket
		if (customer.getBasket() != null) {
			throw new StoreModelServiceException("assign basket", customerId + " already has a basket "
					+ "(Basket " + customer.getBasket().getId() + ")");
		}

		// Check if the basket is already assigned to a customer
		if (this.basketCustomerMap.containsKey(basket.getId())) {
			throw new StoreModelServiceException("assign basket", basketId + " is already assigned to "
					+ "Customer " + this.basketCustomerMap.get(basket.getId()).getId());
		}

		customer.setBasket(basket);
		this.basketCustomerMap.put(basket.getId(), customer);

		System.out.println(basket.getId() + " assigned to " + customer.getId());

		return basket;
	}

	/**
	 * Validates the given customer ID, checks if registered and returns the customer's basket.
	 * If the customer does not already have a basket, assigns a new basket to the customer.
	 *
	 * @param id  Unique identifier of the customer
	 * @param authToken  Authorization token
	 * @return  The Basket object
	 * @throws StoreModelServiceException  if the given customer ID is invalid
	 */
	public Basket getCustomerBasket(String id, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_user", null, authToken);

		// Validate the Customer ID
		if (!this.customerMap.containsKey(id)) {
			throw new StoreModelServiceException("get customer basket", id + " does not exist");
		}

		// Check if the customer is registered
		if (!this.customerMap.get(id).isRegistered()) {
			throw new StoreModelServiceException("get customer basket", id + " is not registered");
		}

		Basket basket = this.customerMap.get(id).getBasket();

		// Create new basket if the customer does not already have a basket.
		if (basket == null) {
			// Generate new basket
			basket = new Basket(this.generateBasketId());
			this.basketMap.put(basket.getId(), basket);
			// Assign the basket
			this.assignBasket(basket.getId(), id, authToken);
		}

		return basket;
	}

	/**
	 * Generates a unique basket ID that starts with the letter 'b'.
	 *
	 * @return  Unique basket ID
	 */
	private String generateBasketId() {
		// Generate an initial basket ID that starts with the letter 'b' and followed by an integer
		String id = "BASKET_" + (this.basketMap.size() + 1);

		// Increment the integer part until a unique ID is found
		while (true) {
			if (!this.basketMap.containsKey(id)) break;
			id = "BASKET_" + (Integer.parseInt(id.substring(1)) + 1);
		}

		return id;
	}

	/**
	 * Validates the given basket ID, product ID, and quantity. Then adds the product to the basket.
	 *
	 * @param basketId  Unique identifier of the basket
	 * @param productId  Unique identifier of the product
	 * @param quantity  Number of product items to be added to the basket
	 * @param authToken  Authorization token
	 * @return  The Basket object.
	 * @throws StoreModelServiceException  if the given basket ID, product ID, or quantity is invalid
	 */
	public Basket addToBasket(String basketId, String productId, int quantity, String authToken)
			throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("update_resource", null, authToken);

		// Validate the Basket ID
		if (!this.basketMap.containsKey(basketId)) {
			throw new StoreModelServiceException("add to basket", basketId + " does not exist");
		}

		// Check if the basket is assigned to a customer
		if (!this.basketCustomerMap.containsKey(basketId)) {
			throw new StoreModelServiceException("add to basket", basketId + " is not associated with any"
					+ " customer");
		}

		// Validate the Product ID
		if (!this.productMap.containsKey(productId)) {
			throw new StoreModelServiceException("add to basket", productId + " does not exist");
		}

		// Validate quantity
		if (quantity < 1) {
			throw new StoreModelServiceException("add to basket", "Invalid quantity");
		}

		return this.basketMap.get(basketId).addToBasket(this.productMap.get(productId), quantity);
	}

	/**
	 * Validates the given basket ID, product ID, and quantity. Then removed the product from the basket.
	 *
	 * @param basketId  Unique identifier of the basket
	 * @param productId  Unique identifier of the product
	 * @param quantity  Number of product items to be removed from the basket
	 * @param authToken  Authorization token
	 * @return  The Basket object.
	 * @throws StoreModelServiceException  if the given basket ID, product ID, or quantity is invalid
	 */
	public Basket removeFromBasket(String basketId, String productId, int quantity, String authToken)
			throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("update_resource", null, authToken);

		// Validate the Basket ID
		if (!this.basketMap.containsKey(basketId)) {
			throw new StoreModelServiceException("remove from basket", basketId + " does not exist");
		}

		// Check if the basket is assigned to a customer
		if (!this.basketCustomerMap.containsKey(basketId)) {
			throw new StoreModelServiceException("remove from basket", basketId + " is not associated with "
					+ "any customer");
		}

		// Validate the Product ID
		if (!this.productMap.containsKey(productId)) {
			throw new StoreModelServiceException("remove from basket", productId + " does not exist");
		}

		try {
			return this.basketMap.get(basketId).removeFromBasket(this.productMap.get(productId), quantity);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("remove from basket", e.getMessage());
		}

	}

	/**
	 * Validates the given basket ID, sets the customer's basket to null, removes the basket-customer association and
	 * clears the contents of the basket.
	 *
	 * @param id  Unique identifier of the basket
	 * @param authToken  Authorization token
	 * @return  The Basket object
	 * @throws StoreModelServiceException  if the given basket ID is invalid
	 */
	public Basket clearBasket(String id, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("update_resource", null, authToken);

		// Validate the Basket ID
		if (!this.basketMap.containsKey(id)) {
			throw new StoreModelServiceException("clear basket", id + " does not exist");
		}

		// Check if the basket is assigned to a customer
		if (!this.basketCustomerMap.containsKey(id)) {
			throw new StoreModelServiceException("clear basket", id + " is not associated with any "
					+ "customer");
		}

		// Set customer's basket to null
		this.basketCustomerMap.get(id).setBasket(null);

		// Remove the basket-customer association
		this.basketCustomerMap.remove(id);

		// Clear contents of the Basket and return the Basket
		return this.basketMap.get(id).clearBasket();
	}

	/**
	 * Validates the given basket ID and checks if it is assigned to a customer. Returns a string including basket
	 * items.
	 *
	 * @param id  Unique identifier of the basket
	 * @param authToken  Authorization token
	 * @return  String including basket items
	 * @throws StoreModelServiceException  if the given basket ID is invalid or if the basket is not assigned to a
	 * customer
	 */
	public String getBasketItems(String id, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_resource", null, authToken);

		// Validate the Basket ID
		if (!this.basketMap.containsKey(id)) {
			throw new StoreModelServiceException("get basket items", id + " does not exist");
		}

		// Check if the Basket is assigned to a customer
		if (this.basketCustomerMap.get(id) == null) {
			throw new StoreModelServiceException("get basket items", id + " is not assigned to any customer");
		}

		return this.basketMap.get(id).toString();
	}

	/**
	 * Validates the given basket ID and checks if it is assigned to a customer. Returns the total cost of items in
	 * the basket.
	 *
	 * @param id  Unique identifier of the basket
	 * @param authToken  Authorization token
	 * @return  Total cost of items in the basket
	 * @throws StoreModelServiceException
	 */
	public int computeBasketTotal(String id, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_resource", null, authToken);

		// Validate the Basket ID
		if (!this.basketMap.containsKey(id)) {
			throw new StoreModelServiceException("compute basket total", id + " does not exist");
		}

		// Check if the Basket is assigned to a customer
		if (this.basketCustomerMap.get(id) == null) {
			throw new StoreModelServiceException("compute basket total", id + " is not assigned to any customer");
		}

		return this.basketMap.get(id).computeTotal();
	}

	/**
	 * Validates the customer ID, checks if the customer is registered and has an assigned basket. Passes the
	 * responsibility of computing the basket total to the Basket class. The event that triggers this action is
	 * generated from a  microphone. Hence, the control_microphone permission is checked.
	 *
	 * @param customerId  Unique ID of the customer
	 * @param authToken  Authorization token
	 * @return  The total value of items in the customer's basket
	 * @throws StoreModelServiceException  if the customer ID is invalid or if the customer has no basket
	 */
	public int computeCustomersBasketTotal(String customerId, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_resource", null, authToken);

		// Validate the customer ID
		if (!this.customerMap.containsKey(customerId)) {
			throw new StoreModelServiceException("compute customer basket", customerId + " does not exist");
		}

		Customer customer = this.getCustomer(customerId, authToken);

		// Check if customer is registered
		if (!customer.isRegistered()) {
			throw new IllegalArgumentException("Customer is not registered");
		}

		Basket basket = customer.getBasket();
		if (basket == null) {
			throw new StoreModelServiceException("compute customer basket", customer.getId() + " does not have an assigned basket");
		}

		return this.computeBasketTotal(basket.getId(), authToken);
	}

	/**
	 * Validates the given basket ID and checks if it is assigned to a customer. Returns the total weight of items in
	 * the basket.
	 *
	 * @param id  Unique identifier of the basket
	 * @param authToken  Authorization token
	 * @return  Total weight of items in the basket
	 * @throws StoreModelServiceException
	 */
	public int computeBasketWeight(String id, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_resource", null, authToken);

		// Validate the Basket ID
		if (!this.basketMap.containsKey(id)) {
			throw new StoreModelServiceException("compute basket weight", id + " does not exist");
		}

		// Check if the Basket is assigned to a customer
		if (this.basketCustomerMap.get(id) == null) {
			throw new StoreModelServiceException("compute basket weight", id + " is not assigned to any customer");
		}

		return this.basketMap.get(id).computeWeight();
	}

	/**
	 * Validates the address and device ID then creates a device and adds it to deviceStoreMap.
	 *
	 * @param deviceId  Unique identifier of the device
	 * @param name  Name of the device
	 * @param type  Type of the device
	 * @param addressString  String representing the location in the form of storeId:aisleId
	 * @param authToken  Authorization token
	 * @return  The Device object
	 * @throws StoreModelServiceException  if the given address or device ID is invalid
	 */
	public Device defineDevice(String deviceId, String name, String type, String addressString, String authToken)
			throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("create_resource", null, authToken);

		// Get the address components
		String[] aisleAddress;
		try {
			aisleAddress = Address.extractAisleAddress(addressString);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("define device", e.getMessage());
		}
		String storeId = aisleAddress[0];
		String aisleId = aisleAddress[1];

		// Validate the store ID
		if (!this.storeMap.containsKey(storeId)) {
			throw new StoreModelServiceException("define device", storeId + " does not exist");
		}

		// Validate the Device ID
		if (this.deviceStoreMap.containsKey(deviceId)) {
			throw new StoreModelServiceException("define device", deviceId + " already exists");
		}

		// Create the address
		Address address = new Address(storeId, aisleId);

		// Have the store create the Device object
		Device device;
		try {
			device = this.storeMap.get(storeId).defineDevice(deviceId, name, type, address);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("define device", e.getMessage());
		}

		this.deviceStoreMap.put(device.getId(), this.storeMap.get(storeId));

		return device;
	}

	/**
	 * Validates the given device ID and then returns the Device object for the given device ID.
	 *
	 * @param id  Unique identifier of the device
	 * @param authToken  Authorization token
	 * @return  The Device object
	 * @throws StoreModelServiceException  if the given device ID is invalid
	 */
	public Device getDevice(String id, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_resource", null, authToken);

		// Validate the Device ID
		if (!this.deviceStoreMap.containsKey(id)) {
			throw new StoreModelServiceException("show device", id + " does not exist");
		}

		return this.deviceStoreMap.get(id).getDevice(id);
	}

	/**
	 * Validates the store ID and calls the getDevices method of the store.
	 *
	 * @param storeId  Unique ID of the store
	 * @param cls  Device child class
	 * @param authToken  Authorization token
	 * @return  List of devices
	 */
	public List<Device> getDevicesFromStore(String storeId, Class<?> cls, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_resource", null, authToken);

		// Validate the Store ID
		if (!this.storeMap.containsKey(storeId)) {
			throw new IllegalArgumentException("Store " + storeId + " does not exist");
		}

		return this.storeMap.get(storeId).getDevices(cls);
	}

	/**
	 * Validates the store ID and calls the getDeviceAtAddress method of the store.
	 *
	 * @param address  location of the device
	 * @param cls  Device child class
	 * @param exactLocation  boolean that determines if the device has to be at the given address location. For
	 *                       example, if exactLocation is true then the device's address has to match the given
	 *                       address.
	 * @param authToken  Authorization token
	 * @return  The Device object
	 */
	public Device getDeviceAtAddress(Address address, Class<?> cls, boolean exactLocation, String authToken)
			throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("read_resource", null, authToken);

		String storeId = address.getStoreId();

		// Validate the Store ID
		if (!this.storeMap.containsKey(storeId)) {
			throw new IllegalArgumentException("Store " + storeId + " does not exist");
		}

		return this.storeMap.get(storeId).getDeviceAtAddress(address, cls, exactLocation);
	}

	/**
	 * If the event is an emergency, validates the store ID. Otherwise, validates the device ID. Calls the
	 * setTurnstileState method of the store to set the turnstile state.
	 *
	 * @param deviceId  Unique ID of the device
	 * @param storeId  Unique ID of the store
	 * @param isEmergency  boolean that identifies if the event is an emergency or not
	 * @param open  boolean that identifies if the turnstile needs to be opened or closed.
	 * @param authToken  Authorization token
	 * @throws StoreModelServiceException  if the store ID or device ID is invalid
	 */
	public void setTurnstileState(String deviceId, String storeId, boolean isEmergency, boolean open, String authToken)
			throws StoreModelServiceException{
		// Check for permission
		this.checkPermission("control_turnstile", null, authToken);

		Store store;
		if (isEmergency) {
			// Validate the Store ID
			if (!this.storeMap.containsKey(storeId)) {
				throw new StoreModelServiceException("set turnstile state", "Store " + storeId + " does not exist");
			}
			store = this.storeMap.get(storeId);
		} else {
			// Validate the Device ID
			if (!this.deviceStoreMap.containsKey(deviceId)) {
				throw new StoreModelServiceException("set turnstile state", deviceId + " does not exist");
			}
			store = this.deviceStoreMap.get(deviceId);
		}

		store.setTurnstileState(deviceId, isEmergency, open);
	}

	/**
	 * Validates the store ID and calls the restockShelf method of the store.
	 *
	 * @param address  Location of the inventory to be restocked
	 * @param productId  Unique ID of the product that the inventory contains
	 * @param authToken  Authorization token
	 * @return  The Inventory object
	 * @throws StoreModelServiceException  if the store ID is invalid
	 */
	public Inventory restockShelf(Address address, String productId, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("control_robot", null, authToken);

		String storeId = address.getStoreId();

		// Validate the store ID
		if (!this.storeMap.containsKey(storeId)) {
			throw new StoreModelServiceException("restock shelf", storeId + " does not exist");
		}

		try {
			return this.storeMap.get(storeId).restockShelf(address, productId);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("restock shelf", e.getMessage());
		}
	}

	/**
	 * Validates the store ID and checks if the given count is positive. Then calls the fetchProduct method of the
	 * store.
	 *
	 * @param toAddress  location where the product needs to be brought to
	 * @param productId  Unique ID of the product to be fetched
	 * @param count  Number of products to be fetched
	 * @param authToken  Authorization token
	 * @return  Number of fetched products
	 * @throws StoreModelServiceException  if the store ID is invalid
	 */
	public int fetchProduct(Address toAddress, String productId, int count, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("control_robot", null, authToken);

		String storeId = toAddress.getStoreId();

		// Validate the store ID
		if (!this.storeMap.containsKey(storeId)) {
			throw new StoreModelServiceException("fetch product", storeId + " does not exist");
		}

		if (count < 1) {
			throw new StoreModelServiceException("fetch product", "Count must be a positive integer");
		}

		try {
			return this.storeMap.get(storeId).fetchProduct(toAddress, productId, count);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("fetch product", e.getMessage());
		}
	}

	/**
	 * Validates the store ID and calls the cleanUpAisle method of the store.
	 *
	 * @param address  location for the cleaning event
	 * @param item  String representing the thing that needs to be cleaned up
	 * @param authToken  Authorization token
	 * @throws StoreModelServiceException  if the store ID is invalid
	 */
	public void cleanUpAisle(Address address, String item, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("control_robot", null, authToken);

		String storeId = address.getStoreId();

		// Validate the store ID
		if (!this.storeMap.containsKey(storeId)) {
			throw new StoreModelServiceException("clean up aisle", storeId + " does not exist");
		}

		try {
			this.storeMap.get(storeId).cleanUpAisle(address, item);
		} catch (IllegalArgumentException e) {
			throw new StoreModelServiceException("clean up aisle", e.getMessage());
		}
	}

	/**
	 * Validates the source device ID and calls the announceMessage method of the store.
	 *
	 * @param sourceDeviceId  Unique ID of the source device
	 * @param isEmergency  boolean that identifies if the event is an emergency or not
	 * @param message  Message to be announced
	 * @param authToken  Authorization token
	 * @throws StoreModelServiceException  if the source device ID is invalid
	 */
	public void announceMessage(String sourceDeviceId, boolean isEmergency, String message, String authToken)
			throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("control_speaker", null, authToken);

		// Validate the source device ID
		if (!this.deviceStoreMap.containsKey(sourceDeviceId)) {
			throw new StoreModelServiceException("announce message", sourceDeviceId + " does not exist");
		}

		this.deviceStoreMap.get(sourceDeviceId).announceMessage(sourceDeviceId, isEmergency, message);
	}

	/**
	 * Validates the device ID and checks if the device is in the given store. Then calls the addressEmergency method
	 * of the store.
	 *
	 * @param deviceId  Unique ID of the device
	 * @param emergencyType  Type of the emergency
	 * @param address  Location of the emergency
	 * @param authToken  Authorization token
	 * @throws StoreModelServiceException  if the device ID is invalid or if the device is not in the given store
	 */
	public void addressEmergency(String deviceId, EmergencyType emergencyType, Address address, String authToken)
			throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("control_robot", null, authToken);

		String storeId = address.getStoreId();

		// Validate the Device ID
		if (!this.deviceStoreMap.containsKey(deviceId)) {
			throw new StoreModelServiceException("address emergency", deviceId + " does not exist");
		}

		// Check if the device is in the given store
		if (!this.deviceStoreMap.get(deviceId).getId().equals(storeId)) {
			throw new StoreModelServiceException("address emergency", deviceId + " does not belong to " + storeId);
		}

		this.deviceStoreMap.get(deviceId).addressEmergency(deviceId, emergencyType, address);
	}

	/**
	 * For each given device, validates the device ID and checks if the device is in the given store. Calls the
	 * assistCustomersExit method of the store. Finally, removes the basket-customer association, clears the
	 * contents of the basket, and updates the location of the customers.
	 *
	 * @param devices  List of Device objects
	 * @param storeId  Unique ID of the store
	 * @param authToken  Authorization token
	 * @throws StoreModelServiceException  if the device ID is invalid or if the device is not in the given store
	 */
	public void assistCustomersExit(List<Device> devices, String storeId, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("control_robot", null, authToken);

		for (Device device : devices) {
			String deviceId = device.getId();
			// Validate the Device ID
			if (!this.deviceStoreMap.containsKey(deviceId)) {
				throw new StoreModelServiceException("assist customers exit", deviceId + " does not exist");
			}

			// Check if the device is located in the given store
			if (!this.deviceStoreMap.get(deviceId).getId().equals(storeId)) {
				throw new StoreModelServiceException("assist customers exit", deviceId + " does not belong to "
						+ storeId);
			}

			this.deviceStoreMap.get(deviceId).assistCustomersExit(deviceId);
		}

		for (Map.Entry<String, Customer> entry : this.customerMap.entrySet()) {
			Customer customer = entry.getValue();
			if (customer.getLocation() != null && customer.getLocation().getStoreId().equals(storeId)) {
				// Remove the basket-customer association and clear the contents of the basket
				Basket basket = customer.getBasket();
				if (basket != null) {
					basket.clearBasket();
				}
				// Customer leaves the store
				customer.updateLocation(null);
			}
		}

	}

	/**
	 * Validates the customer ID and store ID, retrieves the customer's location and calls the assistCustomerToCar
	 * method of the store.
	 *
	 * @param customerId  Unique ID of the customer
	 * @param authToken  Authorization token
	 * @throws StoreModelServiceException  if the customer ID is invalid or if the customer is not located in a store
	 */
	public void assistCustomerToCar(String customerId, String authToken) throws StoreModelServiceException {
		// Check for permission
		this.checkPermission("control_robot", null, authToken);

		// Validate the customer ID
		if (!this.customerMap.containsKey(customerId)) {
			throw new StoreModelServiceException("compute customer basket", customerId + " does not exist");
		}

		Customer customer = this.getCustomer(customerId, authToken);

		// Get customer's current location
		Address customersLocation = customer.getLocation();

		if (customersLocation == null) {
			throw new StoreModelServiceException("assist customer to car", customer.getId() + " is not located in a "
					+ "store");
		}

		// Get the store ID
		String storeId = customersLocation.getStoreId();

		// Validate the store ID
		if (!this.storeMap.containsKey(storeId)) {
			throw new StoreModelServiceException("assist customer to car", storeId + " does not exist");
		}

		this.storeMap.get(storeId).assistCustomerToCar(customer);
	}

	/**
	 * Processes the given event by calling the notifyObservers method in order to notify the observers.
	 *
	 * @param event  Event that is emitted by the sensors. (For the purposes of this assignment, the events are
	 *                  generated by the CommandProcessor class based on the given script.)
	 * @throws StoreModelServiceException  if error conditions occur in the Store Controller Service commands
	 */
	public void processEvent(Event event) throws StoreModelServiceException {
		this.notifyObservers(event);
	}

	/**
	 * Adds an observer to the observerList.
	 *
	 * @param observer  object that wants to listen for events emitted by the sensors of the Store Model Service
	 */
	public void registerObserver(Observer observer) {
		this.observerList.add(observer);
	}

	/**
	 * Removes an observer from the observerList.
	 *
	 * @param observer  object that wants to stop being an observer
	 */
	public void removeObserver(Object observer) {
		this.observerList.remove(observer);
	}

	/**
	 * Notifies the observers by calling their update methods and passing the event object.
	 *
	 * @param event  event emitted by the sensors of the Store Model Service
	 * @throws StoreModelServiceException  if error conditions occur in the Store Controller Service commands
	 */
	public void notifyObservers(Event event) throws StoreModelServiceException {
		for (Observer observer : this.observerList) {
			observer.update(event);
		}
	}

}
