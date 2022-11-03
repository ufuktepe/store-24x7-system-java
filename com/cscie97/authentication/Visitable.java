package com.cscie97.authentication;

/**
 * The Visitable interface provides a method signature for the accept method which enables objects to be visitable
 *
 * @author Burak Ufuktepe
 */
public interface Visitable {

	/**
	 * Method signature that enables objects to be visitable.
	 *
	 * @param visitor  Visitor object
	 */
	public abstract void accept(Visitor visitor);
}
