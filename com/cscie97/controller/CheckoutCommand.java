package com.cscie97.controller;

import com.cscie97.authentication.AuthenticationException;
import com.cscie97.authentication.AuthenticationService;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.ledger.Transaction;
import com.cscie97.store.*;

import java.sql.Timestamp;

/**
 * Provides implementation details for the following actions:
 *  - Computing the basket total of a customer
 *  - Retrieving the customer's account balance
 *  - Creating and processing a transaction
 *  - Computing the total weight of items in the basket
 *  - Opening the turnstile
 *  - Announcing a goodbye message
 *  - Closing the turnstile
 *  - Having a robot assist a customer to his/her car if the basket items weigh more than 10 lbs
 *
 * @author Burak Ufuktepe
 */
public class CheckoutCommand implements Command {

    private AuthenticationService authenticationService;

    public CheckoutCommand(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Checks if the customer is registered and has an assigned basket. Then computes the basket total. If the basket
     * total is positive, retrieves the customer's account balance and checks if the customer has enough funds.
     * Announces a message if the customer does not have enough balance. Otherwise, creates and processes the
     * transaction. Opens the turnstile, announces a goodbye message, and closes the turnstile. Also, computes the
     * total weight of basket items. If the weight is over 10 lbs, a robot will assist the customer to his/her car.
     *
     * @param event  event emitted by the turnstile of the Store Model Service
     * @throws LedgerException  if no block has been committed yet or if the account address does not exist, if the
     * min fee requirement is not satisfied, if the amount is negative, if the note is longer than the allowed note
     * length, if the payer or receiver account does not exist, if the payer account does not have enough funds
     * @throws StoreModelServiceException  if the given customer ID in the event object does not exist, if the
     * customer does not have an assigned basket or if the customer is not located in the store
     */
    public void execute(Event event) throws LedgerException, StoreModelServiceException, AuthenticationException {
        StoreModelService storeModelService = event.getStoreModelService();
        Ledger ledger = event.getLedger();

        // Get the store
        Store store = storeModelService.getStoreFromDeviceId(event.getSourceDeviceId(), event.getAuthToken());

        // Get the customer's name
        String firstName = storeModelService.getCustomer(event.getCustomerId(), event.getAuthToken()).getFirstName();

        // Get an auth token for the customer
        String customerAuthToken = this.authenticationService.getAuthToken(event.getFacePrint()).getId();

        // Check if the customer has permission for checking out
        storeModelService.checkPermission("checkout", store.getId(), customerAuthToken);

        // Check if customer is registered
        boolean registered = storeModelService.getCustomer(event.getCustomerId(), event.getAuthToken()).isRegistered();
        if (!registered) {
            // Announce message
            String message = "Goodbye " + firstName + ", thanks for visiting " + store.getName() + "!";
            storeModelService.announceMessage(event.getSourceDeviceId(), false, message, event.getAuthToken());
            return;
        }

        // Check if the customer has an assigned basket
        if (storeModelService.getCustomer(event.getCustomerId(), event.getAuthToken()).getBasket() == null) {
            throw new StoreModelServiceException("get basket", event.getCustomerId() + " does not have an assigned basket");
        }

        // Retrieve customer details
        String accountAddress = storeModelService.getCustomer(event.getCustomerId(), event.getAuthToken()).getAccountAddress();
        String basketId = storeModelService.getCustomer(event.getCustomerId(), event.getAuthToken()).getBasket().getId();

        // Compute basket total
        int basketTotal = storeModelService.computeBasketTotal(basketId, event.getAuthToken());

        // Process the transaction if the basket total is positive
        if (basketTotal > 0) {
            // Get the customer's account balance
            int accountBalance = ledger.getAccountBalance(accountAddress);

            // Print the account balance
            System.out.println(event.getCustomerId() + " has an account balance of " + accountBalance + ".");

            // Check if enough funds exist
            if (accountBalance < basketTotal) {
                String message = "You do not have enough funds to cover your basket total.";
                storeModelService.announceMessage(event.getSourceDeviceId(), false, message, event.getAuthToken());
                return;
            }

            String timeStamp = new Timestamp(System.currentTimeMillis()).toString();
            String note = "Transaction for " + event.getCustomerId() + " at " + timeStamp;
            Transaction txn = ledger.createTransaction(String.valueOf(basketTotal), String.valueOf(Transaction.MIN_FEE),
                    note, accountAddress, store.getId());

            // Process the transaction
            ledger.processTransaction(txn);
            System.out.println(txn);
        }

        // Open the turnstile
        storeModelService.setTurnstileState(event.getSourceDeviceId(), null, false, true, event.getAuthToken());

        // Announce message
        String message = "Goodbye " + firstName + ", thanks for shopping at " + store.getName() + "!";
        storeModelService.announceMessage(event.getSourceDeviceId(), false, message, event.getAuthToken());

        // Close the turnstile
        storeModelService.setTurnstileState(event.getSourceDeviceId(), null, false, false, event.getAuthToken());

        // Compute the weight of items in the basket
        double basketWeight = storeModelService.computeBasketWeight(basketId, event.getAuthToken());

        // Check if the basket weight exceed the threshold (10 lbs). If it does, then assist customer to car
        if (basketWeight > Basket.WEIGHT_THRESHOLD) {
            System.out.println("Robot assistance required.");
            storeModelService.assistCustomerToCar(event.getCustomerId(), event.getAuthToken());
        } else {
            System.out.println("No robot assistance required.");
            // Update the location of the customer
            event.getStoreModelService().updateCustomer(event.getCustomerId(), null, event.getAuthToken());
        }
    }
}
