package com.cscie97.controller;

import com.cscie97.authentication.AuthenticationService;
import com.cscie97.store.Address;
import com.cscie97.store.Inventory;
import com.cscie97.store.StoreModelService;
import com.cscie97.store.StoreModelServiceException;

/**
 * Provides implementation details for adding products to a customer's basket or removing products from a customer's
 * basket.
 *
 * @author Burak Ufuktepe
 */
public class BasketCommand implements Command {

    private AuthenticationService authenticationService;

    public BasketCommand(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Adds/removes products to/from a customer's basket. Also updates the inventory, computes the inventory level
     * and initiates a restocking action if the inventory level falls below the INVENTORY_THRESHOLD.
     *
     * @param event  event emitted by the sensors of the Store Model Service
     * @throws StoreModelServiceException  if an error occurs in StoreModelService methods
     */
    public void execute(Event event) throws StoreModelServiceException {
        StoreModelService storeModelService = event.getStoreModelService();

        // Get the basket ID
        String basketId = storeModelService.getCustomerBasket(event.getCustomerId(), event.getAuthToken()).getId();

        // Find the store ID
        String storeId = storeModelService.getStoreFromDeviceId(event.getSourceDeviceId(), event.getAuthToken()).getId();

        // Create the Address
        String addressString = storeId + ":" + event.getAddressString();
        Address address;
        try {
            address = Address.constructFromShelfAddress(addressString);
        } catch (IllegalArgumentException e) {
            throw new StoreModelServiceException("construct address object from shelf address", e.getMessage());
        }

        // Find the inventory that the customer added/removed to/from basket
        Inventory inventory;
        try {
            inventory = storeModelService.findInventoryAtAddress(address, event.getProductId(), event.getAuthToken());
        } catch (IllegalArgumentException e) {
            throw new StoreModelServiceException("find inventory at address", e.getMessage());
        }

        // Validate the inventory update
        storeModelService.validateInventoryUpdate(inventory.getId(), String.valueOf(-1 * event.getCount()),
                event.getAuthToken());

        if (event.getCount() > 0) {
            // Update the inventory
            storeModelService.updateInventory(inventory.getId(), String.valueOf(-1 * event.getCount()), event.getAuthToken());

            // Add products to the basket
            storeModelService.addToBasket(basketId, event.getProductId(), event.getCount(), event.getAuthToken());

            // Compute the inventory level
            double inventoryLevel = (inventory.getCapacity() - event.getCount()) / (double) inventory.getCapacity();

            // Round to 2 decimals
            inventoryLevel = Math.round(inventoryLevel * 100.0) / 100.0;

            System.out.println("Inventory Level: " + inventoryLevel * 100 + "%");

            // Restock only if the inventory level is below INVENTORY_THRESHOLD
            if (inventoryLevel < Inventory.INVENTORY_THRESHOLD) {
                storeModelService.restockShelf(address, event.getProductId(), event.getAuthToken());
            }

        } else {
            // Remove products from the basket
            storeModelService.removeFromBasket(basketId, event.getProductId(), event.getCount() * -1,
                    event.getAuthToken());

            // Update the inventory
            storeModelService.updateInventory(inventory.getId(), String.valueOf(-1 * event.getCount()), event.getAuthToken());
        }
    }
}
