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

import java.io.EOFException;
import java.io.IOException;

import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.event.MessageEvent;
import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;

/**
 * Handles standard message responses
 * 
 * @author Milos Kleint
 */
class MessageTaggedResponse implements Response {
	/**
	 * Process the data for the response.
	 * 
	 * @param dis
	 *            the data inputstream allowing the client to read the server's response. Note that the actual response name has already
	 *            been read and the inputstream is positioned just before the first argument, if any.
	 */
	@Override
	public void process(LoggedDataInputStream dis, ResponseServices services) throws ResponseException {
		try {
			final String text = dis.readLine();
			MessageEvent event = new MessageEvent(this, text, false);
			event.setTagged(true);
			services.getEventManager().fireCVSEvent(event);
		} catch (EOFException ex) {
			throw new ResponseException(ex, CommandException.getLocalMessage("CommandException.EndOfFile", null)); // NOI18N
		} catch (IOException ex) {
			throw new ResponseException(ex);
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
