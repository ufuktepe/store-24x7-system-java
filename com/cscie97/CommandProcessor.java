package com.cscie97;

import com.cscie97.authentication.AuthenticationException;
import com.cscie97.authentication.AuthenticationService;
import com.cscie97.controller.StoreControllerService;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.ledger.Transaction;
import com.cscie97.store.*;
import com.cscie97.controller.Event;
import com.cscie97.controller.EventType;

import java.io.FileNotFoundException;
import java.io.FileReader;

import static java.util.Map.entry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Utility class for feeding the Store Model Service a set of operations, using command syntax.
 * Reads a script file and parses command lines.
 *
 * @author Burak Ufuktepe
 */
public class CommandProcessor {

	/**
	 * Store Model Service instance.
	 */
	private StoreModelService storeModelService;

	/**
	 * Store Controller Service instance.
	 */
	private StoreControllerService storeControllerService;

	/**
	 * Ledger Service instance.
	 */
	private Ledger ledger;

	/**
	 * Authentication Service instance.
	 */
	private AuthenticationService authenticationService;

	/**
	 * Map of command patterns that are used for parsing commands.
	 */
	private static final Map<String, Map<String, String>> CMD_PATTERNS_MAP = Map.ofEntries(
			entry("create-ledger", Map.ofEntries(
					entry("create-ledger", ""), entry("description", ""), entry("seed", ""))),
			entry("create-account", Map.ofEntries(
					entry("create-account", ""))),
			entry("process-transaction", Map.ofEntries(
					entry("amount", ""), entry("fee", ""), entry("note", ""),
					entry("payer", ""), entry("receiver", ""))),
			entry("get-account-balance", Map.ofEntries(
					entry("get-account-balance", ""))),
			entry("get-account-balances", Map.ofEntries()),
			entry("get-block", Map.ofEntries(
					entry("get-block", ""))),
			entry("get-transaction", Map.ofEntries(
					entry("get-transaction", ""))),
			entry("validate", Map.ofEntries()),
			entry("define-store", Map.ofEntries(
					entry("define-store", ""), entry("name", ""), entry("address", ""))),
			entry("show-store", Map.ofEntries(
					entry("show-store", ""))),
			entry("define-aisle", Map.ofEntries(
					entry("define-aisle", ""), entry("name", ""), entry("description", ""),
					entry("location", ""))),
			entry("show-aisle", Map.ofEntries(
					entry("show-aisle", ""))),
			entry("define-shelf", Map.ofEntries(
					entry("define-shelf", ""), entry("name", ""), entry("level", ""),
					entry("description", ""), entry("temperature", "ambient"))),
			entry("show-shelf", Map.ofEntries(
					entry("show-shelf", ""))),
			entry("define-product", Map.ofEntries(
					entry("define-product", ""), entry("name", ""), entry("description", ""),
					entry("weight", ""), entry("category", ""), entry("unit_price", ""),
					entry("temperature", "ambient"))),
			entry("show-product", Map.ofEntries(
					entry("show-product", ""))),
			entry("define-inventory", Map.ofEntries(
					entry("define-inventory", ""), entry("location", ""), entry("capacity", ""),
					entry("count", ""), entry("product", ""))),
			entry("show-inventory", Map.ofEntries(
					entry("show-inventory", ""))),
			entry("update-inventory", Map.ofEntries(
					entry("update-inventory", ""), entry("update_count", ""))),
			entry("define-customer", Map.ofEntries(
					entry("define-customer", ""), entry("first_name", ""), entry("last_name", ""),
					entry("type", ""), entry("email_address", ""), entry("account", ""))),
			entry("show-customer", Map.ofEntries(
					entry("show-customer", ""))),
			entry("update-customer", Map.ofEntries(
					entry("update-customer", ""), entry("location", ""))),
			entry("define-basket", Map.ofEntries(
					entry("define-basket", ""))),
			entry("assign-basket", Map.ofEntries(
					entry("assign-basket", ""), entry("customer", ""))),
			entry("get-customer-basket", Map.ofEntries(
					entry("get-customer-basket", ""))),
			entry("add-basket-item", Map.ofEntries(
					entry("add-basket-item", ""), entry("product", ""), entry("item_count", ""))),
			entry("remove-basket-item", Map.ofEntries(
					entry("remove-basket-item", ""), entry("product", ""), entry("item_count", ""))),
			entry("clear-basket", Map.ofEntries(
					entry("clear-basket", ""))),
			entry("show-basket-items", Map.ofEntries(
					entry("show-basket-items", ""))),
			entry("define-device", Map.ofEntries(
					entry("define-device", ""), entry("name", ""), entry("type", ""),
					entry("location", ""))),
			entry("show-device", Map.ofEntries(
					entry("show-device", ""))),
			entry("emergency_event", Map.ofEntries(
					entry("emergency_event", ""), entry("type", ""), entry("in", ""))),
			entry("basket_event", Map.ofEntries(
					entry("basket_event", ""), entry("customer", ""),
					entry("product", ""), entry("from", ""), entry("count", ""))),
			entry("customer_seen_event", Map.ofEntries(
					entry("customer", ""), entry("in", ""))),
			entry("cleaning_event", Map.ofEntries(
					entry("product", ""), entry("in", ""))),
			entry("broken_glass_event", Map.ofEntries(
					entry("in", ""))),
			entry("missing_person_event", Map.ofEntries(
					entry("missing_person_event", ""), entry("find", ""), entry("voiceprint", ""))),
			entry("fetch_product_event", Map.ofEntries(
					entry("fetch_product_event", ""), entry("customer", ""), entry("product", ""),
					entry("count", ""), entry("voiceprint", ""))),
			entry("check_account_balance_event", Map.ofEntries(
					entry("check_account_balance_event", ""), entry("customer", ""), entry("voiceprint", ""))),
			entry("enter_store_event", Map.ofEntries(
					entry("enter_store_event", ""), entry("customer", ""), entry("faceprint", ""))),
			entry("checkout_event", Map.ofEntries(
					entry("checkout_event", ""), entry("customer", ""), entry("faceprint", ""))),
			entry("define-permission", Map.ofEntries(
					entry("define-permission", ""), entry("name", ""), entry("description", ""))),
			entry("create-user", Map.ofEntries(
					entry("create-user", ""), entry("name", ""))),
			entry("define-role", Map.ofEntries(
					entry("define-role", ""), entry("name", ""), entry("description", ""))),
			entry("add-entitlement-to-role", Map.ofEntries(
					entry("entitlement", ""), entry("role", ""))),
			entry("add-user-credential", Map.ofEntries(
					entry("add-user-credential", ""), entry("type", ""), entry("value", ""))),
			entry("login", Map.ofEntries(
					entry("login", ""), entry("password", ""))),
			entry("add-role-to-user", Map.ofEntries(
					entry("role", ""), entry("user", ""))),
			entry("create-resource-role", Map.ofEntries(
					entry("create-resource-role", ""), entry("name", ""), entry("description", ""),
					entry("role", ""), entry("resource", ""))),
			entry("add-resource-role-to-user", Map.ofEntries(
					entry("resource-role", ""), entry("user", ""))),
			entry("logout", Map.ofEntries()),
			entry("get-inventory", Map.ofEntries())
	);

	/**
	 * Map of commands and corresponding methods.
	 */
	private static final Map<String, String> methodsMap = Map.ofEntries(
			entry("create-ledger", "createLedger"),
			entry("create-account", "createAccount"),
			entry("process-transaction", "processTransaction"),
			entry("get-account-balance", "getAccountBalance"),
			entry("get-account-balances", "getAccountBalances"),
			entry("get-block", "getBlock"),
			entry("get-transaction", "getTransaction"),
			entry("validate", "validateLedger"),
			entry("define-store", "defineStore"),
			entry("show-store", "showStore"),
			entry("define-aisle", "defineAisle"),
			entry("show-aisle", "showAisle"),
			entry("define-shelf", "defineShelf"),
			entry("show-shelf", "showShelf"),
			entry("define-product", "defineProduct"),
			entry("show-product", "showProduct"),
			entry("define-inventory", "defineInventory"),
			entry("show-inventory", "showInventory"),
			entry("update-inventory", "updateInventory"),
			entry("define-customer", "defineCustomer"),
			entry("show-customer", "showCustomer"),
			entry("define-basket", "defineBasket"),
			entry("assign-basket", "assignBasket"),
			entry("get-customer-basket", "getCustomerBasket"),
			entry("clear-basket", "clearBasket"),
			entry("show-basket-items", "showBasketItems"),
			entry("define-device", "defineDevice"),
			entry("show-device", "showDevice"),
			entry("basket_event", "basketEvent"),
			entry("emergency_event", "emergencyEvent"),
			entry("customer_seen_event", "customerSeenEvent"),
			entry("cleaning_event", "cleaningEvent"),
			entry("broken_glass_event", "brokenGlassEvent"),
			entry("missing_person_event", "missingPersonEvent"),
			entry("fetch_product_event", "fetchProductEvent"),
			entry("check_account_balance_event", "checkAccountBalanceEvent"),
			entry("enter_store_event", "enterStoreEvent"),
			entry("checkout_event", "checkoutEvent"),
			entry("define-permission", "definePermission"),
			entry("create-user", "createUser"),
			entry("define-role", "defineRole"),
			entry("add-entitlement-to-role", "addEntitlementToRole"),
			entry("add-user-credential", "addUserCredential"),
			entry("login", "login"),
			entry("logout", "logout"),
			entry("add-role-to-user", "addRoleToUser"),
			entry("create-resource-role", "createResourceRole"),
			entry("add-resource-role-to-user", "addResourceRoleToUser"),
			entry("get-inventory", "getInventory")
	);

	/**
	 * Set of events that are emitted by the sensors of the Store Model Service. (For the purposes of this assignment,
	 * the events are generated by the CommandProcessor class based on the given script.)
	 */
	private Set<String> EVENTS_SET = new HashSet<>(Arrays.asList("EMERGENCY", "BASKET", "CUSTOMER_SEEN", "CLEANING",
			"BROKEN_GLASS", "MISSING_PERSON", "FETCH_PRODUCT", "CHECK_ACCOUNT_BALANCE", "ENTER_STORE", "CHECKOUT"));

	/**
	 * Class constructor that initializes storeModelService.
	 */
	public CommandProcessor() {
		this.authenticationService = AuthenticationService.getInstance();
		this.storeModelService = new StoreModelService(this.authenticationService);
		this.storeControllerService = new StoreControllerService(this.storeModelService, this.authenticationService);

	}

	/**
	 * Creates a ledger object for the given parameters.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void createLedger(Map<String, String> args) throws CommandProcessorException {
		try {
			// Create the ledger
			this.ledger = new Ledger(args.get("create-ledger"), args.get("description"), args.get("seed"));
		} catch (LedgerException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if a ledger exists and creates an account for the given parameters.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void createAccount(Map<String, String> args) throws CommandProcessorException {
		// Check if a ledger exists
		if (this.ledger == null) {
			throw new CommandProcessorException("", "create account", "No ledger found", 0);
		}

		try {
			// Create the account
			this.ledger.createAccount(args.get("create-account"));
		} catch (LedgerException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if a ledger exists and creates and processes a transaction.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void processTransaction(Map<String, String> args) throws CommandProcessorException {
		// Check if a ledger exists
		if (this.ledger == null) {
			throw new CommandProcessorException("", "process transaction", "No ledger found", 0);
		}

		Transaction txn;
		try {
			// Create the transaction
			txn = this.ledger.createTransaction(args.get("amount"), args.get("fee"), args.get("note"),
					args.get("payer"), args.get("receiver"));

			// Process the transaction
			this.ledger.processTransaction(txn);

		} catch (LedgerException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if a ledger exists and retrieves the account balance.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void getAccountBalance(Map<String, String> args) throws CommandProcessorException {
		// Check if a ledger exists
		if (this.ledger == null) {
			throw new CommandProcessorException("", "get account balance", "No ledger found", 0);
		}

		try {
			// Get the account balance and print it out
			int balance = this.ledger.getAccountBalance(args.get("get-account-balance"));
			System.out.println(args.get("get-account-balance") + " has a balance of " + balance);
		} catch (LedgerException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if a ledger exists and retrieves the account balances.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void getAccountBalances(Map<String, String> args) throws CommandProcessorException {
		// Check if a ledger exists
		if (this.ledger == null) {
			throw new CommandProcessorException("", "get account balances", "No ledger found", 0);
		}

		try {
			// Get the account balances and print them out
			Map<String, Integer> addressBalanceMap = this.ledger.getAccountBalances();
			for (Map.Entry<String, Integer> entry : addressBalanceMap.entrySet()) {
				System.out.println(entry.getKey() + ": " + entry.getValue());
			}
		} catch (LedgerException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if a ledger exists and retrieves the block for the given block number.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void getBlock(Map<String, String> args) throws CommandProcessorException {
		// Check if a ledger exists
		if (this.ledger == null) {
			throw new CommandProcessorException("", "get block", "No ledger found", 0);
		}

		int blockNum;

		// Parse the block number as an integer
		try {
			blockNum = Integer.parseInt(args.get("get-block"));
		} catch (NumberFormatException e) {
			throw new CommandProcessorException("", "get block", "Block number must be an integer", 0);
		}

		// Print out details of the block
		try {
			System.out.println(this.ledger.getBlock(blockNum));
		} catch (LedgerException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if a ledger exists and retrieves the transaction for the given transaction ID.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void getTransaction(Map<String, String> args) throws CommandProcessorException {
		// Check if a ledger exists
		if (this.ledger == null) {
			throw new CommandProcessorException("", "get transaction", "No ledger found", 0);
		}

		// Get the transaction and print it out
		try {
			System.out.println(this.ledger.getTransaction(args.get("get-transaction")));
		} catch (LedgerException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if a ledger exists and validates the ledger.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void validateLedger(Map<String, String> args) throws CommandProcessorException {
		// Check if a ledger exists
		if (this.ledger == null) {
			throw new CommandProcessorException("", "validate ledger", "No ledger found", 0);
		}

		// Validate the current state of the blockchain
		try {
			this.ledger.validate();
		} catch (LedgerException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}


	public String getAuthTokenOfCurrentUser() throws CommandProcessorException {
		// Check if the authentication service exists
		if (this.authenticationService == null) {
			throw new CommandProcessorException("", "add role to user", "No authentication service found", 0);
		}

		try {
			return this.authenticationService.getAuthTokenOfCurrentUser().getId();
		} catch (AuthenticationException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service and Authentication Service APIs to define the store.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void defineStore(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			Store store = this.storeModelService.defineStore(args.get("define-store"), args.get("name"),
					args.get("address"), authToken);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}

		try {
			this.authenticationService.defineResource(args.get("define-store"), args.get("name"));
		} catch (AuthenticationException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to show the store.
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void showStore(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			System.out.println(this.storeModelService.getStore(args.get("show-store"), authToken));
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to define an aisle.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void defineAisle(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			Aisle aisle = this.storeModelService.defineAisle(args.get("define-aisle"), args.get("name"),
					args.get("description"), args.get("location"), authToken);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to show the aisle.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void showAisle(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			System.out.println(this.storeModelService.getAisle(args.get("show-aisle"), authToken));
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to define a shelf.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void defineShelf(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			Shelf shelf = this.storeModelService.defineShelf(args.get("define-shelf"), args.get("name"),
					args.get("level"), args.get("description"), args.get("temperature"), authToken);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to show the shelf.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void showShelf(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			System.out.println(this.storeModelService.getShelf(args.get("show-shelf"), authToken));
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to define a product.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void defineProduct(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			Product product = this.storeModelService.defineProduct(args.get("define-product"), args.get("name"),
					args.get("description"), args.get("weight"), args.get("category"), args.get("unit_price"),
					args.get("temperature"), authToken);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to show the product.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void showProduct(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			System.out.println(this.storeModelService.getProduct(args.get("show-product"), authToken));
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to define an inventory.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void defineInventory(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			Inventory inventory = this.storeModelService.defineInventory(args.get("define-inventory"),
					args.get("location"), args.get("capacity"), args.get("count"), args.get("product"),
					authToken);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to show the inventory.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void showInventory(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			System.out.println(this.storeModelService.getInventory(args.get("show-inventory"), authToken));
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to update the inventory.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void updateInventory(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			this.storeModelService.updateInventory(args.get("update-inventory"), args.get("update_count"), authToken);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to define a customer.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void defineCustomer(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			Customer customer = this.storeModelService.defineCustomer(args.get("define-customer"),
					args.get("first_name"), args.get("last_name"), args.get("type"),
					args.get("email_address"), args.get("account"), authToken);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to show the customer.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void showCustomer(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			System.out.println(this.storeModelService.getCustomer(args.get("show-customer"), authToken));
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to define a basket.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void defineBasket(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			this.storeModelService.defineBasket(args.get("define-basket"), authToken);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to assign a basket to a customer.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void assignBasket(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			this.storeModelService.assignBasket(args.get("assign-basket"), args.get("customer"), authToken);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to get the customer's basket.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void getCustomerBasket(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			System.out.println(this.storeModelService.getCustomerBasket(args.get("get-customer-basket"), authToken));
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to clear the basket.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void clearBasket(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			Basket basket = this.storeModelService.clearBasket(args.get("clear-basket"), authToken);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to show basket items.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void showBasketItems(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			System.out.println(this.storeModelService.getBasketItems(args.get("show-basket-items"), authToken));
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to define a device.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void defineDevice(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			Device device = this.storeModelService.defineDevice(args.get("define-device"), args.get("name"),
					args.get("type"), args.get("location"), authToken);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}

		try {
			this.authenticationService.defineResource(args.get("define-device"), args.get("name"));
		} catch (AuthenticationException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Calls the Store Model Service API to show the device.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void showDevice(Map<String, String> args) throws CommandProcessorException {

		String authToken = this.getAuthTokenOfCurrentUser();

		try {
			System.out.println(this.storeModelService.getDevice(args.get("show-device"), authToken));
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Creates a basket event and passes the event to the Store Model Service API.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void basketEvent(Map<String, String> args) throws CommandProcessorException {
		// Validate the count
		int countInt;
		try {
			countInt = Integer.parseInt(args.get("count"));
			if (countInt == 0) {
				throw new CommandProcessorException("", "basket event", "count must be non-zero", 0);
			}
		} catch (NumberFormatException e) {
			throw new CommandProcessorException("", "basket event", "Invalid count", 0);
		}

		Event event = new Event(EventType.BASKET, this.storeModelService, args.get("basket_event"));
		event.setCustomerId(args.get("customer"));
		event.setProductId(args.get("product"));
		event.setAddressString(args.get("from"));
		event.setCount(countInt);
		try {
			this.storeModelService.processEvent(event);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Validates the emergency type, creates an emergency event and passes the event to the Store Model Service API.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void emergencyEvent(Map<String, String> args) throws CommandProcessorException {
		// Validate the emergency type
		EmergencyType emergencyType;
		try {
			emergencyType = EmergencyType.valueOf(args.get("type").toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new CommandProcessorException("", "emergency event", args.get("type") + " is not a valid emergency "
					+ "type", 0);
		}

		Event event = new Event(EventType.EMERGENCY, this.storeModelService, args.get("emergency_event"));
		event.setEmergencyType(emergencyType);
		event.setAddressString(args.get("in"));
		try {
			this.storeModelService.processEvent(event);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Creates a customer seen event and passes the event to the Store Model Service API.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void customerSeenEvent(Map<String, String> args) throws CommandProcessorException {
		Event event = new Event(EventType.CUSTOMER_SEEN, this.storeModelService, args.get("customer_seen_event"));
		event.setCustomerId(args.get("customer"));
		event.setAddressString(args.get("in"));
		try {
			this.storeModelService.processEvent(event);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Creates a cleaning event and passes the event to the Store Model Service API.
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void cleaningEvent(Map<String, String> args) throws CommandProcessorException {
		Event event = new Event(EventType.CLEANING, this.storeModelService, args.get("cleaning_event"));
		event.setItem(args.get("product"));
		event.setAddressString(args.get("in"));
		try {
			this.storeModelService.processEvent(event);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Creates a cleaning event and passes the event to the Store Model Service API.
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void brokenGlassEvent(Map<String, String> args) throws CommandProcessorException {
		Event event = new Event(EventType.CLEANING, this.storeModelService, args.get("broken_glass_event"));
		event.setItem("broken glass");
		event.setAddressString(args.get("in"));
		try {
			this.storeModelService.processEvent(event);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Creates a missing person event and passes the event to the Store Model Service API.
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void missingPersonEvent(Map<String, String> args) throws CommandProcessorException {
		Event event = new Event(EventType.MISSING_PERSON, this.storeModelService, args.get("missing_person_event"));
		event.setCustomerId(args.get("find"));
		event.setVoicePrint(args.get("voiceprint"));
		try {
			this.storeModelService.processEvent(event);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Creates a fetch product event and passes the event to the Store Model Service API.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void fetchProductEvent(Map<String, String> args) throws CommandProcessorException {
		// Validate the count
		int countInt;
		try {
			countInt = Integer.parseInt(args.get("count"));
			if (countInt <= 0) {
				throw new CommandProcessorException("", "fetch product event", "count must be positive", 0);
			}
		} catch (NumberFormatException e) {
			throw new CommandProcessorException("", "fetch product event", "Invalid count", 0);
		}

		Event event = new Event(EventType.FETCH_PRODUCT, this.storeModelService, args.get("fetch_product_event"));
		event.setCustomerId(args.get("customer"));
		event.setProductId(args.get("product"));
		event.setCount(countInt);
		event.setVoicePrint(args.get("voiceprint"));
		try {
			this.storeModelService.processEvent(event);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Creates a check account balance event and passes the event to the Store Model Service API.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void checkAccountBalanceEvent(Map<String, String> args)
			throws CommandProcessorException {
		if (this.ledger == null) {
			throw new CommandProcessorException("", "check account balance event", "No ledger found", 0);
		}

		Event event = new Event(EventType.CHECK_ACCOUNT_BALANCE, this.storeModelService,
				args.get("check_account_balance_event"));
		event.setCustomerId(args.get("customer"));
		event.setLedger(this.ledger);
		event.setVoicePrint(args.get("voiceprint"));
		try {
			this.storeModelService.processEvent(event);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Creates an enter store event and passes the event to the Store Model Service API.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void enterStoreEvent(Map<String, String> args)
			throws CommandProcessorException {
		if (this.ledger == null) {
			throw new CommandProcessorException("", "enter store event", "No ledger found", 0);
		}

		Event event = new Event(EventType.ENTER_STORE, this.storeModelService,
				args.get("enter_store_event"));
		event.setCustomerId(args.get("customer"));
		event.setLedger(this.ledger);
		event.setFacePrint(args.get("faceprint"));

		try {
			this.storeModelService.processEvent(event);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Creates a checkout event and passes the event to the Store Model Service API.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void checkoutEvent(Map<String, String> args)
			throws CommandProcessorException {
		if (this.ledger == null) {
			throw new CommandProcessorException("", "checkout event", "No ledger found", 0);
		}

		Event event = new Event(EventType.CHECKOUT, this.storeModelService,
				args.get("checkout_event"));
		event.setCustomerId(args.get("customer"));
		event.setLedger(this.ledger);
		event.setFacePrint(args.get("faceprint"));
		try {
			this.storeModelService.processEvent(event);
		} catch (StoreModelServiceException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if the authentication service exists and creates a permission for the given parameters.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void definePermission(Map<String, String> args) throws CommandProcessorException {
		// Check if the authentication service exists
		if (this.authenticationService == null) {
			throw new CommandProcessorException("", "define permission", "No authentication service found", 0);
		}

		try {
			// Create the permission
			this.authenticationService.definePermission(args.get("define-permission"), args.get("name"),
					args.get("description"));
		} catch (AuthenticationException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if the authentication service exists and creates a user for the given parameters.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void createUser(Map<String, String> args) throws CommandProcessorException {
		// Check if the authentication service exists
		if (this.authenticationService == null) {
			throw new CommandProcessorException("", "create user", "No authentication service found", 0);
		}

		try {
			// Create the user
			this.authenticationService.defineUser(args.get("create-user"), args.get("name"));
		} catch (AuthenticationException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if the authentication service exists and creates a role for the given parameters.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void defineRole(Map<String, String> args) throws CommandProcessorException {
		// Check if the authentication service exists
		if (this.authenticationService == null) {
			throw new CommandProcessorException("", "define role", "No authentication service found", 0);
		}

		try {
			// Create the role
			this.authenticationService.defineRole(args.get("define-role"), args.get("name"),
					args.get("description"));
		} catch (AuthenticationException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if the authentication service exists and adds an entitlement to a role.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void addEntitlementToRole(Map<String, String> args) throws CommandProcessorException {
		// Check if the authentication service exists
		if (this.authenticationService == null) {
			throw new CommandProcessorException("", "add permission to role", "No authentication service found", 0);
		}

		try {
			// Add the entitlement to the role
			this.authenticationService.addEntitlementToRole(args.get("entitlement"), args.get("role"));
		} catch (AuthenticationException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if the authentication service exists and adds a credential to user.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void addUserCredential(Map<String, String> args) throws CommandProcessorException {
		// Check if the authentication service exists
		if (this.authenticationService == null) {
			throw new CommandProcessorException("", "add user credential", "No authentication service found", 0);
		}

		try {
			// Add user credential
			this.authenticationService.addUserCredential(args.get("add-user-credential"), args.get("type"),
					args.get("value"));
		} catch (AuthenticationException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if the authentication service exists and logs in the given user.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void login(Map<String, String> args) throws CommandProcessorException {
		// Check if the authentication service exists
		if (this.authenticationService == null) {
			throw new CommandProcessorException("", "login", "No authentication service found", 0);
		}

		try {
			// login user
			this.authenticationService.login(args.get("login"), args.get("password"));
		} catch (AuthenticationException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if the authentication service exists and logs out the current user.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void logout(Map<String, String> args) throws CommandProcessorException {
		// Check if the authentication service exists
		if (this.authenticationService == null) {
			throw new CommandProcessorException("", "logout", "No authentication service found", 0);
		}

		try {
			// Logout the current user
			this.authenticationService.logout();
		} catch (AuthenticationException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if the authentication service exists and adds a role to a user.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void addRoleToUser(Map<String, String> args) throws CommandProcessorException {
		// Check if the authentication service exists
		if (this.authenticationService == null) {
			throw new CommandProcessorException("", "add role to user", "No authentication service found", 0);
		}

		try {
			// Add the role to the user
			this.authenticationService.addUserEntitlement(args.get("user"), args.get("role"));
		} catch (AuthenticationException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if the authentication service exists and creates a resource role.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void createResourceRole(Map<String, String> args) throws CommandProcessorException {
		// Check if the authentication service exists
		if (this.authenticationService == null) {
			throw new CommandProcessorException("", "create resource role", "No authentication service found", 0);
		}

		try {
			// Create the resource role
			this.authenticationService.defineResourceRole(args.get("create-resource-role"), args.get("name"),
					args.get("description"), args.get("role"), args.get("resource"));
		} catch (AuthenticationException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if the authentication service exists and adds a resource role to a user.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void addResourceRoleToUser(Map<String, String> args) throws CommandProcessorException {
		// Check if the authentication service exists
		if (this.authenticationService == null) {
			throw new CommandProcessorException("", "add resource role to user", "No authentication service found", 0);
		}

		try {
			// Add the resource role to the user
			this.authenticationService.addUserEntitlement(args.get("user"), args.get("resource-role"));
		} catch (AuthenticationException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Checks if the authentication service exists and displays the inventory of Authentication Service objects.
	 *
	 * @param args
	 * @throws CommandProcessorException
	 */
	public void getInventory(Map<String, String> args) throws CommandProcessorException {
		// Check if the authentication service exists
		if (this.authenticationService == null) {
			throw new CommandProcessorException("", "get inventory", "No authentication service found", 0);
		}

		try {
			// Display the inventory of Authentication Service objects
			this.authenticationService.getInventory();
		} catch (AuthenticationException e) {
			throw new CommandProcessorException("", e.getAction(), e.getReason(), 0);
		}
	}

	/**
	 * Processes a set of commands provided within the given command file.
	 * Prints out FileNotFoundException and CommandProcessorExceptions.
	 *
	 * @param fName  command file path
	 */
	public void processCommandFile(String fName) {
		int lineNum = 0;

		// Read the command file line by line
		try (Scanner cmdFile = new Scanner(new FileReader(fName))) {
			while (cmdFile.hasNextLine()) {
				lineNum++;
				String cmd = cmdFile.nextLine();

				if (cmd.length() != 0) {
					System.out.println(cmd);
				}

				if (lineNum == 13) {
					int x = 5;
				}

				// Skip zero length lines or comments
				if (cmd.strip().length() == 0 || cmd.startsWith("#")) {
					continue;
				}

				// Process command lines and catch CommandProcessorExceptions
				try {
					this.processCommand(cmd);
				} catch (CommandProcessorException e) {
					e.setLineNum(lineNum);
					e.setCommand(cmd);
					System.out.println(e.getMessage());
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println(e);
		}
	}

	/**
	 * Parses commands and uses the methods specified in methodsMap to process the commands.
	 *
	 * @param cmd  Command to be processed
	 * @throws CommandProcessorException  if errors occur while processing the commands
	 */
	public void processCommand(String cmd) throws CommandProcessorException {

		// Split by whitespace without splitting double-quoted strings
		List<String> cmdElementsTemp = Utility.splitString(cmd);
		List<String> cmdElements;

		// Reformat the list
		try {
			cmdElements = this.reformatCmdElements(cmdElementsTemp);
		} catch (IllegalArgumentException e) {
			throw new CommandProcessorException("", "reformat commands", e.getMessage(), 0);
		}

		// Map of arguments and their values
		Map<String, String> args;

		// Get the command arguments
		try {
			args = this.getArguments(cmdElements);
		} catch (NoSuchElementException e) {
			throw new CommandProcessorException("", "get command arguments", e.getMessage(), 0);
		}

		String identifier = cmdElements.get(0);

		if (methodsMap.containsKey(identifier)) {
			String methodName = methodsMap.get(identifier);
			Method method;
			try {
				method = CommandProcessor.class.getMethod(methodName, Map.class);
			} catch (NoSuchMethodException e) {
				throw new CommandProcessorException("", "get method", e.getMessage(), 0);
			}
			try {
				method.invoke(this, args);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw (CommandProcessorException) e.getCause();
			}
		} else {
			throw new CommandProcessorException("", "find method", "Invalid command", 0);
		}
		System.out.println();
	}

	/**
	 * Reformats the commands list to ensure that the first element in the list corresponds to a valid command.
	 *
	 * @param cmdElements  List of commands
	 * @return  Modified list of commands
	 */
	private List<String> reformatCmdElements(List<String> cmdElements) {
		// Convert the first element to lowercase
		cmdElements.set(0, cmdElements.get(0).toLowerCase());

		// Check if this is an event
		if (cmdElements.get(0).equals("create-event")) {
			for (String cmdElement : cmdElements) {
				if (this.EVENTS_SET.contains(cmdElement.toUpperCase())) {
					cmdElements.set(0, cmdElement.toLowerCase() + "_event");
					return cmdElements;
				}
			}
		}

		if (!CMD_PATTERNS_MAP.containsKey(cmdElements.get(0))) {
			throw new IllegalArgumentException("invalid command format");
		}

		return cmdElements;
	}

	/**
	 * Extracts arguments from a list of commands.
	 *
	 * @param cmdElements  List of commands
	 * @return  Map of arguments
	 */
	private Map<String, String> getArguments(List<String> cmdElements) {

		Map<String, String> argsMap = getCopyOfArgsMap(cmdElements.get(0));

		// Extract the arguments
		for (Map.Entry<String, String> entry : argsMap.entrySet()) {
			for (int i = 0; i < cmdElements.size() - 1; i++) {
				if (cmdElements.get(i).equals(entry.getKey())) {
					// If the argument is not a credential, store in lower case. Otherwise, store the argument as is.
					if (!(entry.getKey().equals("value") || entry.getKey().equals("password"))) {
						argsMap.put(entry.getKey(), cmdElements.get(i + 1).toLowerCase());
					} else {
						argsMap.put(entry.getKey(), cmdElements.get(i + 1));
					}

					break;
				}
			}
		}

		// Check if all values are populated in argsMap
		for (Map.Entry<String, String> entry : argsMap.entrySet()) {
			if (entry.getValue().equals("")) {
				throw new NoSuchElementException("Missing argument: " + entry.getKey());
			}
		}

		return argsMap;
	}

	/**
	 * Deep copies a Map in CMD_PATTERNS_MAP.
	 *
	 * @param identifier  String that identifies the command in CMD_PATTERNS_MAP
	 * @return  Copy of a Map in CMD_PATTERNS_MAP
	 */
	private static Map<String, String> getCopyOfArgsMap(String identifier) {
		Map<String, String> copyOfArgsMap = new HashMap<>();
		for (Map.Entry<String, String> entry : CMD_PATTERNS_MAP.get(identifier).entrySet()) {
			copyOfArgsMap.put(entry.getKey(), entry.getValue());
		}
		return copyOfArgsMap;
	}
}
