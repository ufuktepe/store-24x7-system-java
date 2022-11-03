package com.cscie97.authentication;

/**
 * The AuthenticationException is returned from the AuthenticationService methods in response to an error condition.
 * It captures the attempted action and the reason for failure.
 *
 * @author Burak Ufuktepe
 */
public class AuthenticationException extends Exception {

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
    public AuthenticationException(String anAction, String aReason) {
        super(String.format("Failed action: %s, Reason: %s.", anAction, aReason));
        this.action = anAction;
        this.reason = aReason;
    }

    /**
     * Retrieves the action that was attempted.
     *
     * @return  attempted action
     */
    public String getAction() {
        return this.action;
    }

    /**
     * Retrieves the reason that caused the exception.
     *
     * @return  reason for the exception
     */
    public String getReason() {
        return this.reason;
    }

}

