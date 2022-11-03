package com.cscie97.controller;

import com.cscie97.authentication.AuthenticationService;
import com.cscie97.store.Address;
import com.cscie97.store.StoreModelServiceException;

/**
 * Provides implementation details for updating a customer's location.
 *
 * @author Burak Ufuktepe
 */
public class UpdateCustomerLocationCommand implements Command {

    private AuthenticationService authenticationService;

    public UpdateCustomerLocationCommand(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Create an address for the customer's new location and then call the Store Model Service API to update the
     * customer's location.
     *
     * @param event  event emitted by the camera of the Store Model Service
     * @throws StoreModelServiceException  if the given address or the customer ID in the event object is invalid
     */
    public void execute(Event event) throws StoreModelServiceException {
        // Create the Address
        Address address;
        try {
            address = Address.constructFromAisleAddress(event.getAddressString());
        } catch (IllegalArgumentException e) {
            throw new StoreModelServiceException("update customer location command", e.getMessage());
        }

        // Update the location of the customer
        event.getStoreModelService().updateCustomer(event.getCustomerId(), address, event.getAuthToken());
    }
}
