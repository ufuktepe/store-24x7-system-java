package com.cscie97.authentication;

/**
 * Provides method signatures for visiting the AuthenticationService, Users, Entitlements, and Resources.
 *
 * @author Burak Ufuktepe
 */
public interface Visitor {

	/**
	 * Method signature for visiting the AuthenticationService.
	 *
	 * @param service  AuthenticationService object
	 */
	public abstract void visit(AuthenticationService service);

	/**
	 * Method signature for visiting a User.
	 *
	 * @param user  User object
	 */
	public abstract void visit(User user);

	/**
	 * Method signature for visiting an Entitlement.
	 *
	 * @param entitlement  Entitlement object
	 */
	public abstract void visit(Entitlement entitlement);

	/**
	 * Method signature for visiting a Resource.
	 *
	 * @param resource  Resource object
	 */
	public abstract void visit(Resource resource);
}
