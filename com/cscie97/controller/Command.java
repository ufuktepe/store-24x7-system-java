package com.cscie97.controller;

import com.cscie97.authentication.AuthenticationException;
import com.cscie97.ledger.LedgerException;
import com.cscie97.store.StoreModelServiceException;

/**
 * The Command interface declares an interface for all commands. It provides a method signature for a single method
 * called execute which is called by the StoreControllerService to perform actions in response to events.
 *
 * @author Burak Ufuktepe
 */
public interface Command {

    /**
     * Provides a method signature for invoking a command. Takes an Event object as an argument that holds a
     * reference to a StoreModelService instance and Ledger instance and includes properties related to the event.
     *
     * @param event  event emitted by the sensors of the Store Model Service
     * @throws LedgerException  if error conditions occur in the Ledger Service API methods
     * @throws StoreModelServiceException  if error conditions occur in the Store Model Service API methods
     */
    void execute(Event event) throws LedgerException, StoreModelServiceException, AuthenticationException;
}
