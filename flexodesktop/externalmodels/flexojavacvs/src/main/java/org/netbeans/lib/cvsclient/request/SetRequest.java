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

import org.netbeans.lib.cvsclient.util.BugLog;

/**
 * Tells the server about the eviroment variables to set for CVSROOT/*info executions.
 * 
 * @author Milos Kleint
 */
public class SetRequest extends Request {

	private String keyValue;

	/**
	 * Creates a new SetResponse with the key-value pair for one enviroment variable.
	 */
	public SetRequest(String keyValue) {
		BugLog.getInstance().assertTrue(keyValue.indexOf('=') > 0, "Wrong SetRequest=" + keyValue);
		this.keyValue = keyValue;
	}

	/**
	 * Get the request String that will be passed to the server
	 * 
	 * @return the request String
	 * @throws UnconfiguredRequestException
	 *             if the request has not been properly configured
	 */
	@Override
	public String getRequestString() throws UnconfiguredRequestException {
		String toReturn = "Set " + keyValue + "\n"; // NOI18N
		return toReturn; // NOI18N
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