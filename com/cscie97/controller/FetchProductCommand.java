package com.cscie97.controller;

import com.cscie97.authentication.AuthenticationException;
import com.cscie97.authentication.AuthenticationService;
import com.cscie97.store.*;

/**
 * Provides implementation details for fetching one or more products to a customer.
 *
 * @author Burak Ufuktepe
 */
public class FetchProductCommand implements Command {

    private AuthenticationService authenticationService;

    public FetchProductCommand(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Checks if the customer is registered and then calls the Store Model Service API to fetch the given product.
     * Announces a message in the following two conditions:
     *  - If the customer who is making the request is not registered
     *  - If the given product does not exist in the store
     *
     * @param event  event emitted by the microphone of the Store Model Service
     * @throws StoreModelServiceException  if the given customer ID in the event object does not exist
     */
    public void execute(Event event) throws StoreModelServiceException, AuthenticationException {
        StoreModelService storeModelService = event.getStoreModelService();

        // Get the store ID
        String storeId = storeModelService.getStoreFromDeviceId(event.getSourceDeviceId(), event.getAuthToken()).getId();

        // Get an auth token for the customer
        String customerAuthToken = this.authenticationService.getAuthToken(event.getVoicePrint()).getId();

        // Check if the customer has permission to control robots in store
        try {
            storeModelService.checkPermission("control_robot", storeId, customerAuthToken);
        } catch (StoreModelServiceException e) {
            String message = "Access denied! You do not have the required permission to control robots.";
            storeModelService.announceMessage(event.getSourceDeviceId(), false, message, event.getAuthToken());
            return;
        }

        // Get customer's location
        Address toAddress = storeModelService.getCustomer(event.getCustomerId(), event.getAuthToken()).getLocation();

        // Bring the product to the given location. Checks if the requester has permission to control robots.
        int fetchCount = storeModelService.fetchProduct(toAddress, event.getProductId(), event.getCount(),
                customerAuthToken);

        // If no product is found, announce a message to the customer using the speaker at the location.
        if (fetchCount == 0) {
            String message = "No " + event.getProductId() + " found in " + toAddress.getStoreId();
            storeModelService.announceMessage(event.getSourceDeviceId(), false, message, event.getAuthToken());
        } else {
            // Get the basket ID
            String basketId = storeModelService.getCustomerBasket(event.getCustomerId(), event.getAuthToken()).getId();

            // Add the product to the customer's basket
            storeModelService.addToBasket(basketId, event.getProductId(), fetchCount, event.getAuthToken());
        }
    }
}
