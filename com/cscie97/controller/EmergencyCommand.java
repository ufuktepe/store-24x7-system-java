package com.cscie97.controller;

import com.cscie97.authentication.AuthenticationException;
import com.cscie97.authentication.AuthenticationService;
import com.cscie97.store.*;

import java.util.List;

/**
 * Provides implementation details for opening all turnstiles, announcing an emergency message, and controlling
 * robots for addressing the emergency, assisting customers exit the store and finally closing all the turnstiles.
 *
 * @author Burak Ufuktepe
 */
public class EmergencyCommand implements Command {

    private AuthenticationService authenticationService;

    public EmergencyCommand(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Opens all turnstiles in the store, announces an emergency message on all speakers in the store. Then finds a
     * robot to address the emergency and instructs the rest of the robots to assist customers exit the store.
     * Finally, closes all the turnstiles.
     *
     * @param event  event emitted by the camera of the Store Model Service
     * @throws StoreModelServiceException  if the given emergency is not a recognized emergency or if the given aisle
     * ID in the event object is invalid.
     */
    public void execute(Event event) throws StoreModelServiceException, AuthenticationException {
        StoreModelService storeModelService = event.getStoreModelService();

        // Get the ID of the device that emitted the event
        String sourceDeviceId = event.getSourceDeviceId();

        // Find the store ID
        String storeId = storeModelService.getStoreFromDeviceId(sourceDeviceId, event.getAuthToken()).getId();

        // Validate the aisle ID
        String aisleId = event.getAddressString();
        storeModelService.getAisle(storeId + ":" + aisleId, event.getAuthToken());

        // Create the Address and add it to the event
        Address address = new Address(storeId, aisleId);
        event.setAddress(address);

        // Open turnstiles
        storeModelService.setTurnstileState(null, storeId, true, true, event.getAuthToken());

        // Announce message
        String message = "There is a " + event.getEmergencyType() + " in " + aisleId + ", please leave " + storeId
                + " immediately";
        storeModelService.announceMessage(event.getSourceDeviceId(), true, message, event.getAuthToken());

        // Find a robot that is located in the same aisle as the emergency. If no robot is found, then get any robot.
        Device robot;
        try {
            robot = storeModelService.getDeviceAtAddress(address, Robot.class, false, event.getAuthToken());
        } catch (IllegalArgumentException e) {
            throw new StoreModelServiceException("get device at address", e.getMessage());
        }

        // Address the emergency
        storeModelService.addressEmergency(robot.getId(), event.getEmergencyType(), event.getAddress(), event.getAuthToken());

        // Get all the robots in the store except the robot addressing the emergency
        List<Device> robots = storeModelService.getDevicesFromStore(storeId, Robot.class, event.getAuthToken());
        robots.remove(robot);

        // Check if the store has enough robots
        if (robots.size() == 0) {
            System.out.println(storeId + " does not have any robots to assist customers to exit the store.");
        } else {
            // Assist customers exit the store
            storeModelService.assistCustomersExit(robots, event.getAddress().getStoreId(), event.getAuthToken());
        }

        // Close the turnstiles
        storeModelService.setTurnstileState(null, storeId, true, false, event.getAuthToken());
    }
}
