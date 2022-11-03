package com.cscie97.authentication;

import com.cscie97.Utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Credential class contains a hash string of a face print, a voice print or a password. Biometric prints and
 * passwords are considered sensitive information. Therefore, each Credential object includes only the hashed string
 * of a biometric print or a password.
 *
 * @author Burak Ufuktepe
 */
public class Credential {

	/**
	 * Hashed string of the credential.
	 */
	private String value;

	/**
	 * Class constructor that hashes the given string and assigns it to the value property.
	 *
	 * @param aValue  Hashed string of the credential
	 */
	public Credential(String aValue) {
		this.value = Utility.hashString(aValue);
	}

	/**
	 * Determines whether the hash of the given string is equal to the Credential's value.
	 *
	 * @param value  String value to be compared to
	 * @return  True if the hash of the given value is equal to the Credential's value. Otherwise, returns false.
	 */
	public boolean equals(String value) {
		return this.value.equals(Utility.hashString(value));
	}

	/**
	 * Validates the given password.
	 * Checks if the given password has:
	 * at least one digit
	 * at least one lowercase letter
	 * at least one uppercase letter
	 * at least one special character
	 * no whitespaces
	 * at least 8 characters
	 * Throws an IllegalArgumentException if the given password is invalid.
	 *
	 * @param password  Password string to be checked
	 */
	public static void validatePassword(String password) {
		if (password == null) {
			throw new IllegalArgumentException("Please provide a password.");
		}

		// Build the regex
		String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.])(?=\\S+$).{8,}$";

		// Compile the regex and generate a matcher
		Matcher matcher = Pattern.compile(regex).matcher(password);

		// Throw an IllegalArgumentException if the password is invalid
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Password must contain at least one digit, one lower case letter, one "
					+ "upper case letter, one special character, no whitespaces, and at least 8 characters.");
		}
	}

	/**
	 * Retrieves the value of the Credential.
	 *
	 * @return  Credential's value.
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Returns the first 16 characters of the Credential's value.
	 *
	 * @return  The string representation of the Credential.
	 */
	public String toString() {
		// For security reasons only the first 16 characters are displayed
		return this.value.substring(0, 16);
	}
}
