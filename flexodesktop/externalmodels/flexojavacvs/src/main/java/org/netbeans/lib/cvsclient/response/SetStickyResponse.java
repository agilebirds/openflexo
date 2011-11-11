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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;

/**
 * Handles the Set-sticky response.
 * 
 * @author Milos Kleint
 */

class SetStickyResponse implements Response {

	/**
	 * Process the data for the response.
	 * 
	 * @param dis
	 *            the data inputstream allowing the client to read the server's response. Note that the actual response name has already
	 *            been read and the input stream is positioned just before the first argument, if any.
	 */
	@Override
	public void process(LoggedDataInputStream dis, ResponseServices services) throws ResponseException {
		PrintWriter w = null;
		try {
			String localPath = dis.readLine();

			String repositoryPath = dis.readLine();
			String tag = dis.readLine();
			// System.err.println("Repository path is: " + repositoryPath);
			// services.updateAdminData(localPath, repositoryPath, null);
			String absPath = services.convertPathname(localPath, repositoryPath);

			if (services.getGlobalOptions().isExcluded(new File(absPath))) {
				return;
			}

			absPath = absPath + "/CVS"; // NOI18N
			File absFile = new File(absPath);
			if (absFile.exists()) {
				File tagFile = new File(absPath, "Tag"); // NOI18N
				if ("THEAD".equals(tag) | "NHEAD".equals(tag)) { // NOI18N
					tagFile.delete();
				} else {
					w = new PrintWriter(new BufferedWriter(new FileWriter(tagFile)));
					// we write out the sticky tag/date
					w.println(tag);
				}
			}
		} catch (IOException e) {
			throw new ResponseException(e);
		} finally {
			if (w != null) {
				w.close();
			}
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
