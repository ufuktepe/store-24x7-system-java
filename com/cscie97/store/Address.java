package com.cscie97.store;

/**
 * Represents a location within a store.
 */
public class Address {

    /**
     * Unique ID of the store
     */
    private String storeId;

    /**
     * Unique ID of the aisle
     */
    private String aisleId;

    /**
     * Unique ID of the shelf
     */
    private String shelfId;

    /**
     * Class constructor that accepts a storeId, aisleId, and shelfId as parameters.
     *
     * @param storeId  Unique ID of the store
     * @param aisleId  Unique ID of the aisle
     * @param shelfId  Unique ID of the shelf
     */
    public Address(String storeId, String aisleId, String shelfId) {
        this.storeId = storeId;
        this.aisleId = aisleId;
        this.shelfId = shelfId;
    }

    /**
     * Class constructor that accepts a storeId and aisleId as parameters. Sets the shelfId to null.
     *
     * @param storeId  Unique ID of the store
     * @param aisleId  Unique ID of the aisle
     */
    public Address(String storeId, String aisleId) {
        this.storeId = storeId;
        this.aisleId = aisleId;
        this.shelfId = null;
    }

    /**
     * Constructs an Address object from a string in the form of storeId:aisleId.
     *
     * @param aisleAddress  Aisle address in the form of storeId:aisleId
     * @return  The Address object
     */
    public static Address constructFromAisleAddress(String aisleAddress) {
        String[] addressComponents;
        addressComponents = extractAisleAddress(aisleAddress);

        return new Address(addressComponents[0], addressComponents[1]);
    }

    /**
     * Constructs an Address object from a string in the form of storeId:aisleId:shelfId.
     *
     * @param shelfAddress  Shelf address in the form of storeId:aisleId:shelfId
     * @return  The Address object
     */
    public static Address constructFromShelfAddress(String shelfAddress) {
        String[] addressComponents;
        addressComponents = extractShelfAddress(shelfAddress);

        return new Address(addressComponents[0], addressComponents[1], addressComponents[2]);
    }

    /**
     * Takes and address string in the form of storeId:aisleId and returns a String array that includes the
     * storeId and aisleId.
     * @param addressString  String in the form of storeId:aisleId
     * @return  String array that includes the storeId and aisleId
     */
    public static String[] extractAisleAddress(String addressString) {
        String[] addressArray = addressString.split(":");
        if (addressArray.length != 2 && addressArray.length != 3) {
            throw new IllegalArgumentException("Address " + addressString + " is invalid");
        }
        return addressArray;
    }

    /**
     * Takes and address string in the form of storeId:aisleId:shelfId and returns a String array that includes the
     * storeId, aisleId, and shelfId.
     *
     * @param addressString  String in the form of storeId:aisleId:shelfId
     * @return  String array that includes the storeId, aisleId, and shelfId
     */
    public static String[] extractShelfAddress(String addressString) {
        String[] addressArray = addressString.split(":");
        if (addressArray.length != 3) {
            throw new IllegalArgumentException("Address " + addressString + " is invalid");
        }
        return addressArray;
    }

    /**
     * Checks if the address matches the given address.
     *
     * @param anotherAddress  Address to be checked against
     * @return  True if the addresses match. Otherwise, returns false.
     */
    public boolean equals(Address anotherAddress) {
        if (this.shelfId != null && anotherAddress.getShelfId() != null) {
            return this.toString().equals(anotherAddress.toString());
        }

        return this.storeId.equals(anotherAddress.storeId) && this.aisleId.equals(anotherAddress.aisleId);
    }

    /**
     * Returns a string representation of the address.
     *
     * @return  String in the form of storeId:aisleId or storeId:aisleId:shelfId
     */
    public String toString() {
        String output = this.storeId + ":" + this.aisleId;

        if (this.shelfId != null) {
            output += ":" + this.shelfId;
        }

        return output;
    }

    /**
     * Below are getters.
     */

    public String getStoreId() {
        return this.storeId;
    }

    public String getAisleId() {
        return this.aisleId;
    }

    public String getShelfId() {
        return this.shelfId;
    }


}
