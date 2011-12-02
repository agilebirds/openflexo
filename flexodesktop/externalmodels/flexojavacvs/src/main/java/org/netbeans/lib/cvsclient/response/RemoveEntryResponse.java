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

import java.io.File;

import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;

/**
 * Tells the client to remove an entry from the Entries file
 * 
 * @author Robert Greig
 */
class RemoveEntryResponse implements Response {

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
			// System.err.println("LocalPath is: " + localPath);
			final String repositoryPath = dis.readLine();
			// System.err.println("Repository path is: " + repositoryPath);

			String filePath = services.convertPathname(localPath, repositoryPath);
			File toRemove = new File(filePath);
			if (services.getGlobalOptions().isExcluded(toRemove)) {
				return;
			}

			services.removeEntry(toRemove);
		} catch (Exception e) {
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
