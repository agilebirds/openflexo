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
import org.netbeans.lib.cvsclient.connection.GzipModifier;

/**
 * This class implements the Gzip-Stream request that is used to indicate that all further communication with the server is to be gzipped.
 * 
 * @author Robert Greig
 */
public class GzipStreamRequest extends Request {

	/**
	 * The level of gzipping to specify
	 */
	private int level = 6;

	/**
	 * Creates new GzipStreamRequest with gzip level 6
	 */
	public GzipStreamRequest() {
	}

	/**
	 * Creates new GzipStreamRequest
	 * 
	 * @param level
	 *            the level of zipping to use (between 1 and 9)
	 */
	public GzipStreamRequest(int level) {
		this.level = level;
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
		return "Gzip-stream " + level + "\n"; // NOI18N
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

	/**
	 * Modify streams on the connection if necessary
	 */
	@Override
	public void modifyOutputStream(Connection connection) throws IOException {
		connection.modifyOutputStream(new GzipModifier());
	}

	/**
	 * Modify streams on the connection if necessary
	 */
	@Override
	public void modifyInputStream(Connection connection) throws IOException {
		connection.modifyInputStream(new GzipModifier());
	}

	/**
	 * Does this request modify the input stream?
	 * 
	 * @return true if it does, false otherwise
	 */
	@Override
	public boolean modifiesInputStream() {
		return true;
	}
}
