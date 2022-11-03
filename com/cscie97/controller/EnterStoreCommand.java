package com.cscie97.controller;

import com.cscie97.authentication.AuthToken;
import com.cscie97.authentication.AuthenticationException;
import com.cscie97.authentication.AuthenticationService;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.store.*;

/**
 * Provides implementation details for checking a customer's account balance, granting/denying access to the store,
 * opening the turnstile, announcing a welcome message, assigning a basket to the customer, and closing the turnstile.
 *
 * @author Burak Ufuktepe
 */
public class EnterStoreCommand implements Command {

    private AuthenticationService authenticationService;

    public EnterStoreCommand(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Retrieves a customer's account balance and grants access to the store if the customer has a positive balance.
     * Otherwise, denies access. After granting access, opens the turnstile, announces a welcome message, assigns a
     * basket to the customer if the customer is registered. Then closes the turnstile.
     *
     * @param event  event emitted by the turnstile of the Store Model Service
     * @throws LedgerException  if no block has been committed yet or if the account address does not exist
     * @throws StoreModelServiceException  if the given customer ID in the event object does not exist
     */
    public void execute(Event event) throws LedgerException, StoreModelServiceException, AuthenticationException {
        StoreModelService storeModelService = event.getStoreModelService();
        Ledger ledger = event.getLedger();

        String storeName = storeModelService.getStoreFromDeviceId(event.getSourceDeviceId(), event.getAuthToken()).getName();
        String storeId = storeModelService.getStoreFromDeviceId(event.getSourceDeviceId(), event.getAuthToken()).getId();

        // Get an auth token for the customer
        String customerAuthToken = this.authenticationService.getAuthToken(event.getFacePrint()).getId();

        // Check if the consumer has permission to enter stores
        storeModelService.checkPermission("enter_store", storeId, customerAuthToken);

        // Retrieve customer details
        boolean registered = storeModelService.getCustomer(event.getCustomerId(), event.getAuthToken()).isRegistered();
        String accountAddress = storeModelService.getCustomer(event.getCustomerId(), event.getAuthToken()).getAccountAddress();
        String firstName = storeModelService.getCustomer(event.getCustomerId(), event.getAuthToken()).getFirstName();

        // Get the customer's account balance
        int accountBalance;
        try {
            accountBalance = ledger.getAccountBalance(accountAddress);
        } catch (LedgerException e) {
            // Account has not been committed to a block yet, set the balance to zero
            accountBalance = 0;
        }

        // Deny access if the customer does not have a positive account balance. Otherwise, grant access.
        if (accountBalance == 0) {
            System.out.println("Access denied. " + event.getCustomerId() + " has an account balance of "
                    + accountBalance + ".");
            return;
        } else {
            System.out.println("Access granted. " + event.getCustomerId() + " has an account balance of "
                    + accountBalance + ".");
        }

        // Open the turnstile
        storeModelService.setTurnstileState(event.getSourceDeviceId(), null, false, true, event.getAuthToken());

        // Announce message
        String message = "Hello " + firstName + ", welcome to " + storeName + "!";
        storeModelService.announceMessage(event.getSourceDeviceId(), false, message, event.getAuthToken());

        // Assign a basket to the customer if registered
        if (registered) {
            storeModelService.getCustomerBasket(event.getCustomerId(), event.getAuthToken());
        }
        // Update the location of the customer
        Address address = storeModelService.getDevice(event.getSourceDeviceId(), event.getAuthToken()).getAddress();
        event.getStoreModelService().updateCustomer(event.getCustomerId(), address, event.getAuthToken());

        // Close the turnstile
        storeModelService.setTurnstileState(event.getSourceDeviceId(), null, false, false, event.getAuthToken());
    }
}
