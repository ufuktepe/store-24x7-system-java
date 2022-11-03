package com.cscie97.controller;

/**
 * Identifies the type of event.
 *
 * @author Burak Ufuktepe
 */
public enum EventType {
    /**
     * Emergency event
     */
    EMERGENCY,

    /**
     * Basket event.
     */
    BASKET,

    /**
     * Customer seen event.
     */
    CUSTOMER_SEEN,

    /**
     * Cleaning event.
     */
    CLEANING,

    /**
     * Missing person event.
     */
    MISSING_PERSON,

    /**
     * Fetch product event.
     */
    FETCH_PRODUCT,

    /**
     * Check account balance event.
     */
    CHECK_ACCOUNT_BALANCE,

    /**
     * Enter store event.
     */
    ENTER_STORE,

    /**
     * Checkout event.
     */
    CHECKOUT
}
