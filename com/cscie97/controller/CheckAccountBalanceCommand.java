package com.cscie97.controller;

import com.cscie97.authentication.AuthenticationException;
import com.cscie97.authentication.AuthenticationService;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.store.*;

/**
 * Provides implementation details for checking a customer's account balance, computing the basket total, and
 * announcing a message to the customer.
 *
 * @author Burak Ufuktepe
 */
public class CheckAccountBalanceCommand implements Command {

    private AuthenticationService authenticationService;

    public CheckAccountBalanceCommand(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Computes the total value of items of a registered customer's basket. If the customer is not registered,
     * announces a message to the customer stating that he/she must be a registered customer to make a check account
     * balance request. Retrieves the customer's account balance and finally announces a message for the total value of
     * basket items and the customer's account balance.
     *
     * @param event  event emitted by the microphone of the Store Model Service
     * @throws LedgerException  if no block has been committed yet or if the account address does not exist
     * @throws StoreModelServiceException  if the given customer ID in the event object does not exist or if the
     * customer does not have an assigned basket
     */
    public void execute(Event event) throws LedgerException, StoreModelServiceException, AuthenticationException {
        StoreModelService storeModelService = event.getStoreModelService();
        Ledger ledger = event.getLedger();

        // Get the store ID
        String storeId = storeModelService.getStoreFromDeviceId(event.getSourceDeviceId(), event.getAuthToken()).getId();

        // Get the auth token of the customer
        String customerAuthToken = this.authenticationService.getAuthToken(event.getVoicePrint()).getId();

        // Check if the customer has permission to control microphones in store
        storeModelService.checkPermission("control_microphone", storeId, customerAuthToken);

        // Compute the basket total if the customer is registered. If the customer is not registered, announce a message
        // to inform the customer that registration is required to make a check account balance request.
        int basketTotal;
        try {
            basketTotal = storeModelService.computeCustomersBasketTotal(event.getCustomerId(), event.getAuthToken());
        } catch (IllegalArgumentException e) {
            // Customer is not registered, announce message
            String message = "You must be a registered customer to make a check account balance request.";
            storeModelService.announceMessage(event.getSourceDeviceId(), false, message, event.getAuthToken());
            return;
        }

        // Get customer's account address
        String accountAddress = storeModelService.getAccountAddress(event.getCustomerId(), event.getAuthToken());

        // Get customer's account balance
        int accountBalance = ledger.getAccountBalance(accountAddress);

        // Announce a message for the total value of basket items and the customer's account balance
        String message;
        if (accountBalance > basketTotal) {
            message = "Total value of basket items is " + basketTotal + " which is less than your "
                    + "account balance of " + accountBalance;
        } else if (accountBalance < basketTotal) {
            message = "Total value of basket items is " + basketTotal + " which is more than your "
                    + "account balance of " + accountBalance;
        } else {
            message = "Total value of basket items is " + basketTotal + " which is equal to your "
                    + "account balance of " + accountBalance;
        }
        storeModelService.announceMessage(event.getSourceDeviceId(), false, message, event.getAuthToken());
    }
}
