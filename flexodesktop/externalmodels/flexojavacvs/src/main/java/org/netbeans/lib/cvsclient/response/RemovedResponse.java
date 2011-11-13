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
import java.io.File;
import java.io.IOException;

import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.event.FileRemovedEvent;
import org.netbeans.lib.cvsclient.event.FileToRemoveEvent;
import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;

/**
 * Indicates that a file has been removed from the repository.
 * 
 * @author Robert Greig
 */
class RemovedResponse implements Response {

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
			String localPath = dis.readLine();
			String repositoryPath = dis.readLine();

			String filePath = services.convertPathname(localPath, repositoryPath);
			filePath = new File(filePath).getAbsolutePath();
			if (services.getGlobalOptions().isExcluded(new File(filePath))) {
				return;
			}

			FileToRemoveEvent e1 = new FileToRemoveEvent(this, filePath);
			FileRemovedEvent e2 = new FileRemovedEvent(this, filePath);

			// Fire the event before removing the local file. This is done
			// so that event listeners have a chance to access the file one
			// last time before the file is removed.
			services.getEventManager().fireCVSEvent(e1);
			services.removeLocalFile(localPath, repositoryPath);
			services.getEventManager().fireCVSEvent(e2);
		} catch (EOFException ex) {
			throw new ResponseException(ex, CommandException.getLocalMessage("CommandException.EndOfFile", // NOI18N
					null));
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
