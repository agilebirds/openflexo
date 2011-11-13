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
 * The global options request. Sends global switch to the server.
 * 
 * @author Milos Kleint
 */
public class GlobalOptionRequest extends Request {
	/**
	 * The option to pass in.
	 */
	private final String option;

	/**
	 * Create a new request
	 * 
	 * @param theOption
	 *            the option to use
	 */
	public GlobalOptionRequest(String option) {
		this.option = option;
	}

	/**
	 * Get the request String that will be passed to the server.
	 * 
	 * @return the request String
	 */
	@Override
	public String getRequestString() throws UnconfiguredRequestException {
		if (option == null) {
			throw new UnconfiguredRequestException("Global option has not been set");
		}

		return "Global_option " + option + "\n"; // NOI18N
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