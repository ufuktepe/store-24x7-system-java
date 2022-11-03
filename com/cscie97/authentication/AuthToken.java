package com.cscie97.authentication;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

/**
 * Represents an authentication token which is used to access restricted Store Model methods.
 * Each AuthToken has a unique ID and expiration time. The state of an AuthToken is checked using the isExpired method.
 *
 * @author Burak Ufuktepe
 */
public class AuthToken {

	/**
	 * Time to live in minutes that determines the expiration time
	 */
	private static final int TTL_MINS = 15;

	/**
	 * Byte length for the AuthToken
	 */
	private static final int BYTE_LEN = 24;

	/**
	 * Unique ID of the AuthToken.
	 */
	private String id;

	/**
	 * Expiration time of the AuthToken.
	 */
	private Instant expirationTime;

	/**
	 * Class constructor that sets the expiration time.
	 */
	public AuthToken() {
		this.expirationTime = Instant.now().plus(TTL_MINS, ChronoUnit.MINUTES);

		// Generate token
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[BYTE_LEN];
		random.nextBytes(bytes);
		this.id = Base64.getUrlEncoder().encodeToString(bytes);
	}

	/**
	 * Determines whether the AuthToken is expired or not.
	 *
	 * @return  True if the AuthToken is expired. Otherwise, returns false.
	 */
	public boolean isExpired() {
		return this.expirationTime.isBefore(Instant.now());
	}

	/**
	 * Retrieves the unique ID of the AuthToken.
	 *
	 * @return  AuthToken's unique ID.
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Returns the first 16 characters of the AuthToken's ID.
	 *
	 * @return  The string representation of the AuthToken.
	 */
	public String toString() {
		// For security reasons only the first 16 characters are displayed
		return this.id.substring(0, 16);
	}
}
