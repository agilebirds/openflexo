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
 * This class encapsulates the wrapper-sendme-rcsOptions request in the CVS client-server protocol. This request is used by the client to
 * get the wrapper settings on the server.
 * 
 * @author Sriram Seshan
 */
public class WrapperSendRequest extends Request {

	/**
	 * Get the request String that will be passed to the server
	 * 
	 * @return the request String
	 * @throws UnconfiguredRequestException
	 *             if the request has not been properly configured
	 * 
	 */
	@Override
	public String getRequestString() throws UnconfiguredRequestException {
		return "wrapper-sendme-rcsOptions \n";
	}

	/**
	 * Is a response expected from the server?
	 * 
	 * @return true if a response is expected, false if no response if expected
	 * 
	 */
	@Override
	public boolean isResponseExpected() {
		return true;
	}

}
