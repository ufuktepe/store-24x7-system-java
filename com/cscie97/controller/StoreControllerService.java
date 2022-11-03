package com.cscie97.controller;

import com.cscie97.authentication.AuthenticationException;
import com.cscie97.authentication.AuthenticationService;
import com.cscie97.ledger.LedgerException;
import com.cscie97.store.Observer;
import com.cscie97.store.StoreModelServiceException;
import com.cscie97.store.StoreModelService;

/**
 * Provides the overall management for the stores and acts as a medium for communication between the Store Model
 * Service and the Ledger Service. Monitors store events utilizing the Observer Pattern. Actions that are generated in
 * response to status updates from the Store Model Service are implemented using the Command Pattern. To decouple the
 * Store Controller Service from the implementation details of creating Commands, a Command Factory is used.
 *
 * @author Burak Ufuktepe
 */
public class StoreControllerService implements Observer {

    private static final String controllerAccountId = "controller";

    private static final String controllerAccountPassword = "Default.4321";

    /**
     * StoreModelService instance that can be used to un-register the Store Controller Service.
     */
    private StoreModelService storeModelService;

    /**
     * Authentication Service instance used for obtaining auth tokens
     */
    private AuthenticationService authenticationService;

    /**
     * Class constructor that sets the storeModelService property and registers itself as an observer to the
     * storeModelService.
     *
     * @param storeModelService  StoreModelService instance to be monitored
     */
    public StoreControllerService(StoreModelService storeModelService, AuthenticationService authenticationService) {
        this.storeModelService = storeModelService;
        this.authenticationService = authenticationService;
        this.storeModelService.registerObserver(this);
    }

    /**
     * Utilizes the CommandFactory class to generate commands for the given event type. Then invokes the execute
     * method of the command.
     *
     * @param event  represents the event emitted by the sensors of the Store Model Service
     * @throws StoreModelServiceException
     */
    public void update(Event event) throws StoreModelServiceException {
        // Generate the command for the given event type
        Command command = CommandFactory.generateCommand(event.getEventType().toString(), this.authenticationService);

        // Login as the Controller Service and set the authToken. Call the execute method of the command. Throw a
        // StoreModelServiceException for error conditions.
        try {
            String authToken = this.authenticationService.login(controllerAccountId, controllerAccountPassword).getId();
            event.setAuthToken(authToken);
            command.execute(event);
        } catch (LedgerException e) {
            throw new StoreModelServiceException(e.getAction(), e.getReason());
        } catch (AuthenticationException e) {
            throw new StoreModelServiceException(e.getAction(), e.getReason());
        }
    }
}
