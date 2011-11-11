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
import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;

/**
 * This means that the server wants the local machine to copy a file in the local file space. This is usually requested after a conflict (to
 * allow the user to keep the original file unaltered).
 * 
 * @author Robert Greig
 */
class CopyFileResponse implements Response {
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
			// System.err.println("Got copy file response.");
			String localPath = dis.readLine();
			// System.err.println("LocalPath is: " + localPath);
			String repositoryPath = dis.readLine();
			// System.err.println("Repository path is: " + repositoryPath);
			String newname = dis.readLine();
			// System.err.println("New name is: " + newname);
			final String absPath = services.convertPathname(localPath, repositoryPath);
			if (services.getGlobalOptions().isExcluded(new File(absPath))) {
				return;
			}
			if (!services.getGlobalOptions().isDoNoChanges()) {
				services.removeLocalFile(new File(new File(absPath).getParentFile(), newname).getAbsolutePath());
				services.renameLocalFile(absPath, newname);
			}
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
