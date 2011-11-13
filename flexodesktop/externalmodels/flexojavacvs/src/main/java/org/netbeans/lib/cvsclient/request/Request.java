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

import java.io.IOException;

import org.netbeans.lib.cvsclient.connection.Connection;
import org.netbeans.lib.cvsclient.file.FileDetails;

/**
 * The superclass of all requests made to the CVS server
 * 
 * @author Robert Greig
 */
public abstract class Request {
	/**
	 * Get the request String that will be passed to the server
	 * 
	 * @return the request String
	 * @throws UnconfiguredRequestException
	 *             if the request has not been properly configured
	 */
	public abstract String getRequestString() throws UnconfiguredRequestException;

	/**
	 * Is a response expected from the server?
	 * 
	 * @return true if a response is expected, false if no response if expected
	 */
	public abstract boolean isResponseExpected();

	/**
	 * If a file transmission is required, get the file object representing the file to transmit after the request string. The default
	 * implementation returns null, indicating no file is to be transmitted
	 * 
	 * @return the file object, if one should be transmitted, or null if no file object is to be transmitted.
	 */
	public FileDetails getFileForTransmission() {
		return null;
	}

	/**
	 * Modify streams on the connection if necessary
	 */
	public void modifyOutputStream(Connection connection) throws IOException {
		// DO NOTHING
	}

	/**
	 * Modify streams on the connection if necessary
	 */
	public void modifyInputStream(Connection connection) throws IOException {
		// DO NOTHING
	}

	/**
	 * Does this request modify the input stream?
	 * 
	 * @return true if it does, false otherwise
	 */
	public boolean modifiesInputStream() {
		return false;
	}
}