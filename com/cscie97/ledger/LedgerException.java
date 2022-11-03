package com.cscie97.ledger;

/**
 * Custom exception for error conditions in the Ledger API.
 * Captures the action that was attempted and the reason for the failure.
 *
 * @author Burak Ufuktepe
 */
public class LedgerException extends Exception {

    /**
     * Performed action.
     */
    private String action;

    /**
     * Reason for the exception.
     */
    private String reason;

    /**
     * Constructor.
     *
     * @param anAction  Action that was performed
     * @param aReason  Reason for the exception
     */
    public LedgerException(String anAction, String aReason) {
        super(String.format("Failed action: %s, Reason: %s.", anAction, aReason));
        this.action = anAction;
        this.reason = aReason;
    }

    /**
     * Retrieves the reason that caused the exception.
     *
     * @return  reason for the exception
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * Retrieves the action that was performed.
     *
     * @return  failed action
     */
    public String getAction() {
        return this.action;
    }
}
