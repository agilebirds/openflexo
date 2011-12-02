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
import java.io.IOException;

import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;

/**
 * Indicates that a file has been successfully operated on, e.g. checked in, added etc. is the same as Checked-in but operates on modified
 * files..
 * 
 * @author Milos Kleint
 */
class NewEntryResponse implements Response {
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
			// System.err.println("Pathname is: " + localPath);
			String repositoryPath = dis.readLine();
			// System.err.println("Repository path is: " + repositoryPath);
			String entriesLine = dis.readLine();
			// System.err.println("New entries line is: " + entriesLine);
			// we set the date the file was last modified in the Entry line
			// so that we can easily determine whether the file has been
			// untouched
			final File theFile = new File(services.convertPathname(localPath, repositoryPath));
			// final Date d = new Date(theFile.lastModified());
			final Entry entry = new Entry(entriesLine);
			entry.setConflict(Entry.DUMMY_TIMESTAMP);

			services.setEntry(theFile, entry);
		} catch (IOException e) {
			throw new ResponseException((Exception) e.fillInStackTrace(), e.getLocalizedMessage());
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
