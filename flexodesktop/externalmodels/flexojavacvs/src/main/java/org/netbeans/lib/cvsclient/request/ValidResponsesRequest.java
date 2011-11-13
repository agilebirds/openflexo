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
 * Sends a request telling the server which responses this client understands. This request should be sent before any commands are executed.
 * This is done automatically by the Client class.
 * 
 * @see org.netbeans.lib.cvsclient.Client
 * @author Robert Greig
 */
public class ValidResponsesRequest extends Request {
	/**
	 * Get the request String that will be passed to the server
	 * 
	 * @return the request String
	 * @throws UnconfiguredRequestException
	 *             if the request has not been properly configured
	 */
	@Override
	public String getRequestString() throws UnconfiguredRequestException {
		return "Valid-responses E M MT Mbinary Updated Created Update-existing Rcs-diff Checked-in ok error " + // NOI18N
				"Clear-static-directory Valid-requests Merged Removed " + // NOI18N
				"Copy-file Mod-time Template Set-static-directory " + // NOI18N
				"Module-expansion Clear-sticky Set-sticky New-entry\n"; // NOI18N
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
