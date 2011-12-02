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
package org.netbeans.lib.cvsclient.response;

import java.io.IOException;

import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.ModuleExpansionEvent;
import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;

/**
 * Return a file or directory which is included in a particular module.
 * 
 * @author Robert Greig
 */
class ModuleExpansionResponse implements Response {

	/**
	 * Creates new ModuleExpansionResponse
	 */
	public ModuleExpansionResponse() {
	}

	/**
	 * Process the data for the response.
	 * 
	 * @param dis
	 *            the data inputstream allowing the client to read the server's response. Note that the actual response name has already
	 *            been read and the input stream is positioned just before the first argument, if any.
	 */
	@Override
	public void process(LoggedDataInputStream dis, ResponseServices services) throws ResponseException {
		try {
			final String localPath = dis.readLine();
			EventManager em = services.getEventManager();
			em.fireCVSEvent(new ModuleExpansionEvent(this, localPath));
		} catch (IOException e) {
			throw new ResponseException(e);
		}
	}

	/**
	 * Is this a terminal response, i.e. should reading of responses stop after this response. This is true for responses such as OK or an
	 * error response
	 */
	@Override
	public boolean isTerminalResponse() {
		return false;
	}
}
