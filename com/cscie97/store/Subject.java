package com.cscie97.store;

import com.cscie97.controller.Event;

/**
 * The Subject interface provides method signatures for registering, removing, and updating observers. The Store
 * Model Service implements the Subject interface.
 *
 * @author Burak Ufuktepe
 */
public interface Subject {

    /**
     * Provides a method signature for registering observers.
     *
     * @param observer  object that wants to listen for events emitted by the sensors of the Store Model Service
     */
    public void registerObserver(Observer observer);

    /**
     * Provides a method signature for removing objects from being observers.
     *
     * @param observer  object that wants to stop being an observer
     */
    public void removeObserver(Object observer);

    /**
     * Provides a method signature for notifying observers when the Subject’s state changes.
     *
     * @param event  event emitted by the sensors of the Store Model Service
     * @throws StoreModelServiceException  if error conditions occur in the Store Model Service API methods
     */
    public void notifyObservers(Event event) throws StoreModelServiceException;
}
