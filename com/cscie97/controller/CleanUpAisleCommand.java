package com.cscie97.controller;

import com.cscie97.authentication.AuthenticationService;
import com.cscie97.store.Address;
import com.cscie97.store.StoreModelServiceException;

/**
 * Provides implementation details for cleaning up an aisle.
 *
 * @author Burak Ufuktepe
 */
public class CleanUpAisleCommand implements Command {

    private AuthenticationService authenticationService;

    public CleanUpAisleCommand(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Create an address for the given aisle and then call the Store Model Service API to send a robot to the given
     * location and clean up the aisle.
     *
     * @param event  event emitted by the microphone or camera of the Store Model Service
     * @throws StoreModelServiceException  if the given aisle ID in the event object does not exist in the store
     */
    public void execute(Event event) throws StoreModelServiceException {
        // Create the address
        Address address;
        try {
            address = Address.constructFromAisleAddress(event.getAddressString());
        } catch (IllegalArgumentException e) {
            throw new StoreModelServiceException("clean up aisle command", e.getMessage());
        }

        // Clean up the aisle
        event.getStoreModelService().cleanUpAisle(address, event.getItem(), event.getAuthToken());
    }
}
