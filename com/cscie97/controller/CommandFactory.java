package com.cscie97.controller;

import com.cscie97.authentication.AuthenticationService;
import com.cscie97.store.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static java.util.Map.entry;

/**
 * Factory class for generating concrete command objects for the given event names.
 */
public class CommandFactory {

    /**
     * Map of event names and command class names.
     */
    private static final Map<String, String> eventCommandMap = Map.ofEntries(
            entry("EMERGENCY", "EmergencyCommand"),
            entry("BASKET", "BasketCommand"),
            entry("CUSTOMER_SEEN", "UpdateCustomerLocationCommand"),
            entry("CLEANING", "CleanUpAisleCommand"),
            entry("MISSING_PERSON", "LocateCustomerCommand"),
            entry("FETCH_PRODUCT", "FetchProductCommand"),
            entry("CHECK_ACCOUNT_BALANCE", "CheckAccountBalanceCommand"),
            entry("ENTER_STORE", "EnterStoreCommand"),
            entry("CHECKOUT", "CheckoutCommand")
    );

    /**
     * Validates the event name and then generates a Command object for the given event name.
     *
     * @param eventName  Name of the event emitted by the sensors of the Store Model Service
     * @return  The Command object
     * @throws StoreModelServiceException  if the command class does not exist
     */
    public static Command generateCommand(String eventName, AuthenticationService authenticationService)
            throws StoreModelServiceException {
        // Validate the eventName
        if (!eventCommandMap.containsKey(eventName)) {
            throw new StoreModelServiceException("generate command for event " + eventName, "invalid event name");
        }

        // Get the command object name
        String commandName = "com.cscie97.controller." + eventCommandMap.get(eventName);

        // Construct the command object and return it. If the command class does not exist, throw a
        // StoreModelServiceException
        try {
            Class<Command> c = (Class<Command>) Class.forName(commandName);
            return c.getDeclaredConstructor(AuthenticationService.class).newInstance(authenticationService);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new StoreModelServiceException("generate command for " + commandName, "invalid command name");
        }
    }
}
