package com.cscie97.controller;

import com.cscie97.authentication.AuthToken;
import com.cscie97.authentication.AuthenticationException;
import com.cscie97.authentication.AuthenticationService;
import com.cscie97.store.*;

/**
 * Provides implementation details for locating a customer's location.
 *
 * @author Burak Ufuktepe
 */
public class LocateCustomerCommand implements Command {

    private AuthenticationService authenticationService;

    public LocateCustomerCommand(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Retrieves the location of the customer and utilizes the AnnounceCommand to announce the message.
     *
     * @param event  event emitted by the microphone of the Store Model Service
     * @throws StoreModelServiceException  if the given customer ID in the event object is invalid
     */
    public void execute(Event event) throws StoreModelServiceException, AuthenticationException {
        StoreModelService storeModelService = event.getStoreModelService();

        // Get the store ID
        String storeId = storeModelService.getStoreFromDeviceId(event.getSourceDeviceId(), event.getAuthToken()).getId();

        // Get an auth token for the customer
        String customerAuthToken = this.authenticationService.getAuthToken(event.getVoicePrint()).getId();

        // Check if the customer has permission to control microphones in store
        try {
            storeModelService.checkPermission("control_microphone", storeId, customerAuthToken);
        } catch (StoreModelServiceException e) {
            String message = "Access denied! You do not have the required permission to control microphones.";
            storeModelService.announceMessage(event.getSourceDeviceId(), false, message, event.getAuthToken());
            return;
        }

        // Get the customer's location
        Address location = storeModelService.getCustomer(event.getCustomerId(), event.getAuthToken()).getLocation();

        // Create the message
        String message;
        if (location != null) {
            message = event.getCustomerId() + " is in " + location;
        } else {
            message = event.getCustomerId() + " was not found";
        }

        // Announce the message
        storeModelService.announceMessage(event.getSourceDeviceId(), false, message, event.getAuthToken());
    }
}
