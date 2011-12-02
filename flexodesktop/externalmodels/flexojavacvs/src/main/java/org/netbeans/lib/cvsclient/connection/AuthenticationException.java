/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.netbeans.lib.cvsclient.connection;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.netbeans.lib.cvsclient.util.BundleUtilities;

/**
 * This exception is thrown when a connection with the server cannot be made, for whatever reason. It may be that the username and/or
 * password are incorrect or it could be that the port number is incorrect. Note that authentication is not restricted here to mean
 * security.
 * 
 * @author Robert Greig
 */
public class AuthenticationException extends Exception {
	/**
	 * The underlying cause of this exception, if any.
	 */
	private Throwable underlyingThrowable;

	private String message;

	private String localizedMessage;

	/**
	 * Construct an AuthenticationException with a message giving more details of what went wrong.
	 * 
	 * @param message
	 *            the message describing the error
	 **/
	public AuthenticationException(String message, String localizedMessage) {
		super(message);
		this.message = message;
		this.localizedMessage = localizedMessage;
	}

	/**
	 * Construct an AuthenticationException with a message and an underlying exception.
	 * 
	 * @param message
	 *            the message describing what went wrong
	 * @param e
	 *            the underlying exception
	 */
	public AuthenticationException(String message, Throwable underlyingThrowable, String localizedMessage) {
		this(message, localizedMessage);
		initCause(underlyingThrowable);
	}

	/**
	 * Construct an AuthenticationException with an underlying exception.
	 * 
	 * @param t
	 *            the underlying throwable that caused this exception
	 */
	public AuthenticationException(Throwable underlyingThrowable, String localizedMessage) {
		this.localizedMessage = localizedMessage;
		initCause(underlyingThrowable);
	}

	/**
	 * Get the underlying throwable that is responsible for this exception.
	 * 
	 * @return the underlying throwable, if any (may be null).
	 */
	public Throwable getUnderlyingThrowable() {
		return getCause();
	}

	@Override
	public String getLocalizedMessage() {
		if (localizedMessage == null) {
			return message;
		}
		return localizedMessage;
	}

	@Override
	public String getMessage() {
		return message;
	}

	protected static String getBundleString(String key) {
		String value = null;
		try {
			ResourceBundle bundle = BundleUtilities.getResourceBundle(AuthenticationException.class, "Bundle"); // NOI18N
			if (bundle != null) {
				value = bundle.getString(key);
			}
		} catch (MissingResourceException exc) {
		}
		return value;
	}
}
