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
package org.netbeans.lib.cvsclient.request;

/**
 * The Argument request. The server saves the specified argument for use in a future argument-using command
 * 
 * @author Robert Greig
 */
public class ArgumentRequest extends Request {
	/**
	 * The argument to pass in
	 */
	private final String argument;

	/**
	 * Create a new request
	 * 
	 * @param theArgument
	 *            the argument to use
	 */
	public ArgumentRequest(String argument) {
		if (argument == null) {
			throw new IllegalArgumentException("argument must not be null!"); // NOI18N
		}

		this.argument = argument;
	}

	/**
	 * Get the request String that will be passed to the server
	 * 
	 * @return the request String
	 */
	@Override
	public String getRequestString() {
		return "Argument " + argument + "\n"; // NOI18N
	}

	/**
	 * Is a response expected from the server?
	 * 
	 * @return true if a response is expected, false if no response if expected
	 */
	@Override
	public boolean isResponseExpected() {
		return false;
	}
}