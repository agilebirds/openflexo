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

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;

/**
 * This Template response allows the server to send a template file that is used when committing changes. The client tools can read the
 * Template file which is stored in CVS/Template and display it to the user to be used as a prompt for commit comments.
 * 
 * @author Robert Greig
 */
class TemplateResponse implements Response {
	/**
	 * A reference to an uncompressed file handler
	 */
	/*
	    // TODO: Should this be taken from ResponseServices???
	    protected static FileHandler uncompressedFileHandler;
	*/

	/**
	 * The local path of the new file
	 */
	protected String localPath;

	/**
	 * The full repository path of the file
	 */
	protected String repositoryPath;

	/**
	 * Creates new TemplateResponse
	 */
	public TemplateResponse() {
	}

	/*
	    // TODO: replace this with a call to ResponseSerivices::getUncompr....ler?
	    protected static FileHandler getUncompressedFileHandler()
	    {
	        if (uncompressedFileHandler == null) {
	            uncompressedFileHandler = new DefaultFileHandler();
	        }
	        return uncompressedFileHandler;
	    }
	*/

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
			localPath = dis.readLine();
			repositoryPath = dis.readLine();

			int length = Integer.parseInt(dis.readLine());

			// now read in the file
			final String filePath = services.convertPathname(localPath, repositoryPath) + "CVS/Template"; // NOI18N

			// #69639 write metadata directly
			// XXX possibly add a method to AdminHandler
			OutputStream out = null;
			File file = new File(filePath);
			file.getParentFile().mkdirs();
			try {
				out = new FileOutputStream(file);
				out = new BufferedOutputStream(out);
				byte[] lineSeparator = System.getProperty("line.separator").getBytes(); // NOI18N
				byte[] data = dis.readBytes(length);
				for (int i = 0; i < data.length; i++) {
					byte ch = data[i];
					if (ch == '\n') {
						out.write(lineSeparator);
					} else {
						out.write(ch);
					}
				}
			} catch (EOFException eof) {
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException alreadyClosed) {
					}
				}
			}
		} catch (EOFException ex) {
			String localMessage = CommandException.getLocalMessage("CommandException.EndOfFile"); // NOI18N
			throw new ResponseException(ex, localMessage);
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
