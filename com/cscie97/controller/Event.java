package com.cscie97.controller;

import com.cscie97.ledger.Ledger;
import com.cscie97.store.*;

/**
 * Stores properties related to events emitted by the sensors of the Store Model Service.
 */
public class Event {

    /**
     * Identifies the type of event.
     */
    private EventType eventType;

    /**
     * StoreModelService instance that is used to access the Store Model Service API.
     */
    private StoreModelService storeModelService;

    /**
     * Ledger instance that is used to access the Ledger Service API.
     */
    private Ledger ledger;

    /**
     * Unique identifier of the source device which emitted the event.
     */
    private String sourceDeviceId;

    /**
     * Provides location information about the event.
     */
    private Address address;

    /**
     * Unique identifier of the customer that is related to the event.
     */
    private String customerId;

    /**
     * Unique identifier of the product that is related to the event.
     */
    private String productId;

    /**
     * Represents a location in the form of storeId:aisleId, aisleId:shelfId or aisleId.
     */
    private String addressString;

    /**
     * Represents the quantity for a product.
     */
    private int count;

    /**
     * Identifies the type of emergency.
     */
    private EmergencyType emergencyType;

    /**
     * Represents the item on the floor that needs to be cleaned.
     */
    private String item;

    /**
     * Device object that is related to the event.
     */
    private Device device;

    /**
     * Face print of the person who is associated with the event
     */
    private String facePrint;

    /**
     * Voice print of the user who is associated with the event
     */
    private String voicePrint;

    /**
     * Authentication token of the controller service
     */
    private String authToken;

    /**
     * Class constructor that sets the event type, storeModelService, sourceDeviceId, authToken, and state.
     *
     * @param eventType  The type of event
     * @param storeModelService  Store Model Service instance
     * @param sourceDeviceId  Unique identifier of the source device that emitted the event
     */
    public Event(EventType eventType, StoreModelService storeModelService, String sourceDeviceId) {
        this.eventType = eventType;
        this.storeModelService = storeModelService;
        this.sourceDeviceId = sourceDeviceId;
    }

    /**
     * Below are getters and setters
     */

    public void setLedger(Ledger ledger) {
        this.ledger = ledger;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setAddressString(String addressString) {
        this.addressString = addressString;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setEmergencyType(EmergencyType emergencyType) {
        this.emergencyType = emergencyType;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public void setFacePrint(String facePrint) {
        this.facePrint = facePrint;
    }

    public void setVoicePrint(String voicePrint) {
        this.voicePrint = voicePrint;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public StoreModelService getStoreModelService() {
        return this.storeModelService;
    }

    public Ledger getLedger() {
        return this.ledger;
    }

    public String getSourceDeviceId() {
        return this.sourceDeviceId;
    }

    public Address getAddress() {
        return this.address;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public String getProductId() {
        return this.productId;
    }

    public String getAddressString() {
        return this.addressString;
    }

    public int getCount() {
        return this.count;
    }

    public EmergencyType getEmergencyType() {
        return this.emergencyType;
    }

    public String getItem() {
        return this.item;
    }

    public Device getDevice() {
        return this.device;
    }

    public String getFacePrint() {
        return this.facePrint;
    }

    public String getVoicePrint() {
        return this.voicePrint;
    }

    public String getAuthToken() {
        return this.authToken;
    }
}
