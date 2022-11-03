package com.cscie97.store;

import com.cscie97.controller.Event;

/**
 * The observers of the Store Model Service implement the Observer interface. The update method is called whenever
 * sensors and appliances emit events.
 *
 * @author Burak Ufuktepe
 */
public interface Observer {

    /**
     * Provides a method signature for the method that will be called by the Subject to update the observers.
     *
     * @param event  event emitted by the sensors of the Store Model Service
     * @throws StoreModelServiceException  if error conditions occur in the Store Model Service API methods
     */
    void update(Event event) throws StoreModelServiceException;
}
