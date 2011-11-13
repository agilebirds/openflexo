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
 * Appends \n followed by the text specified in the request to the previous argument sent.
 * 
 * @author Robert Greig
 */
public class ArgumentxRequest extends Request {
	/**
	 * The argument to pass in
	 */
	private String argument;

	/**
	 * Create a new request
	 * 
	 * @param theArgument
	 *            the argument to use
	 */
	public ArgumentxRequest(String theArgument) {
		argument = theArgument;
	}

	/**
	 * Set the argument
	 */
	public void setArgument(String theArgument) {
		argument = theArgument;
	}

	/**
	 * Get the request String that will be passed to the server
	 * 
	 * @return the request String
	 */
	@Override
	public String getRequestString() throws UnconfiguredRequestException {
		if (argument == null) {
			throw new UnconfiguredRequestException("Argument has not been set");
		}

		return "Argumentx " + argument + "\n"; // NOI18N
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