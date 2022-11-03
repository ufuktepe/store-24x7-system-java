package com.cscie97.authentication;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user of the store system. A user has a unique ID, name, and a set of entitlements. Additionally, a
 * user may have a face print, a voice print, and a password.
 *
 * @author Burak Ufuktepe
 */
public class User implements Visitable {

	/**
	 * Unique ID of the user.
	 */
	private String id;

	/**
	 * Name of the user.
	 */
	private String name;

	/**
	 * Face print of the user.
	 */
	private Credential facePrint;

	/**
	 * Voice print of the user.
	 */
	private Credential voicePrint;

	/**
	 * Password print of the user.
	 */
	private Credential password;

	/**
	 * Authorization token of the user.
	 */
	private AuthToken authToken;

	/**
	 * Set of entitlements associated with the user.
	 */
	private Set<Entitlement> entitlements;

	/**
	 * Class constructor that sets the user ID and name.
	 *
	 * @param anId  Unique ID of the user
	 * @param aName  Name of the user
	 */
	public User(String anId, String aName) {
		this.id = anId.toLowerCase();
		this.name = aName.toLowerCase();
		this.entitlements = new HashSet<>();
	}

	/**
	 * Checks if the given biometric ID matches the user's face print or voice print.
	 *
	 * @param biometricId  Face print or voice print
	 * @return  True if the given biometric ID matches the user's face print or voice print. Otherwise, returns false.
	 */
	public boolean validateBiometricId(String biometricId) {
		return (this.facePrint != null && this.facePrint.equals(biometricId)) ||
				(this.voicePrint != null && this.voicePrint.equals(biometricId));
	}

	/**
	 * If the user does not have a password, returns false. Otherwise, checks if the given username/password
	 * combination matches the user's username/password combination.
	 *
	 * @param username  Username of the user
	 * @param password  Password of the user
	 * @return  True if the given username/password combination matches the user's username/password combination.
	 * Otherwise, return false.
	 */
	public boolean login(String username, String password) {
		if (this.password == null) {
			return false;
		}

		return this.id.equals(username.toLowerCase()) && this.password.equals(password);
	}

	/**
	 * Sets the user's authToken to null.
	 * Throws an IllegalArgumentException if the user is already logged out.
	 */
	public void logout() {
		if (this.authToken == null) {
			throw new IllegalArgumentException(this.id + " is already logged out");
		}
		this.authToken = null;
	}

	/**
	 * Adds the given entitlement to the user's set of entitlements.
	 * Throws an IllegalArgumentException if the given entitlement is already added.
	 *
	 * @param anEntitlement  Entitlement to be added.
	 */
	public void addEntitlement(Entitlement anEntitlement) {
		if (this.hasEntitlement(anEntitlement)) {
			throw new IllegalArgumentException(this.id + " already has " + anEntitlement.getId());
		}

		this.entitlements.add(anEntitlement);
	}

	/**
	 * Iterates over the user's entitlements and checks to see if the given entitlement already exists.
	 *
	 * @param anEntitlement  Entitlement that is searched for
	 * @return  True if the entitlement already exists. Otherwise, returns false.
	 */
	public boolean hasEntitlement(Entitlement anEntitlement) {
		for (Entitlement entitlement : this.entitlements) {
			if (entitlement.hasEntitlement(anEntitlement)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Accepts a visitor and calls the visit method of the Visitor per the Visitor Pattern.
	 *
	 * @param visitor  Visitor instance
	 */
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Returns a valid (unexpired) AuthToken.
	 *
	 * @return  a new AuthToken if the user does not have an unepired AuthToken. Otherwise, returns the existing
	 * AuthToken.
	 */
	public AuthToken getValidAuthToken() {
		if (this.authToken == null || this.authToken.isExpired()) {
			this.authToken = new AuthToken();
		}
		return this.authToken;
	}

	/**
	 * Retrieves the existing AuthToken of the user.
	 *
	 * @return  User's existing AuthToken
	 */
	public AuthToken getExistingAuthToken() {
		return this.authToken;
	}

	/**
	 * Retrieves the user's unique ID.
	 *
	 * @return  Unique ID of the user
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Retrieves the user's set of entitlements.
	 *
	 * @return  Entitlements of the user
	 */
	public Set<Entitlement> getEntitlements() {
		return this.entitlements;
	}

	/**
	 * Creates a new Credential instance using the given biometric print and assigns it to facePrint.
	 *
	 * @param biometricPrint  Face print of the user.
	 */
	public void setFacePrint(String biometricPrint) {
		this.facePrint = new Credential(biometricPrint);
		System.out.println(biometricPrint + " assigned to " + this.id);
	}

	/**
	 * Creates a new Credential instance using the given biometric print and assigns it to voicePrint.
	 *
	 * @param biometricPrint  Voice print of the user.
	 */
	public void setVoicePrint(String biometricPrint) {
		this.voicePrint = new Credential(biometricPrint);
		System.out.println(biometricPrint + " assigned to " + this.id);
	}

	/**
	 * Validates the given password. Creates a new Credential instance using the given password and assigns it to
	 * password.
	 *
	 * @param password  Password of the user.
	 */
	public void setPassword(String password) {
		Credential.validatePassword(password);
		this.password = new Credential(password);
		System.out.println("Password assigned to " + this.id);
	}

	/**
	 * Returns a string representation of the user including user's ID, name, AuthToken, face print, and voice print.
	 *
	 * @return  User's string representation.
	 */
	public String toString() {
		String authToken = "N/A";
		String facePrint = "N/A";
		String voicePrint = "N/A";
		if (this.authToken != null) {
			authToken = this.authToken.toString();
		}

		if (this.facePrint != null) {
			facePrint = this.facePrint.toString();
		}

		if (this.voicePrint != null) {
			voicePrint = this.voicePrint.toString();
		}

		return String.format("USER ID       : %-24sNAME: %-30sAUTHTOKEN  : %-46sFACEPRINT: %-18sVOICEPRINT: "
						+ "%-18s%n",
				this.id, this.name, authToken, facePrint, voicePrint);

	}
}
