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
package org.netbeans.lib.cvsclient.file;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.request.Request;
import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;
import org.netbeans.lib.cvsclient.util.LoggedDataOutputStream;

/**
 * Handles the reading and writing of files to and from the server. Different implementations of this interface can use different formats
 * for sending or receiving the files, for example gzipped format.
 * 
 * @author Robert Greig
 */
public interface FileHandler {
	/**
	 * Transmit a text file to the server, using the standard CVS protocol conventions. CR/LFs are converted to the Unix format.
	 * 
	 * @param file
	 *            the file to transmit
	 * @param dos
	 *            the data outputstream on which to transmit the file
	 */
	void transmitTextFile(File file, LoggedDataOutputStream dos) throws IOException;

	/**
	 * Transmit a binary file to the server, using the standard CVS protocol conventions.
	 * 
	 * @param file
	 *            the file to transmit
	 * @param dos
	 *            the data outputstream on which to transmit the file
	 */
	void transmitBinaryFile(File file, LoggedDataOutputStream dos) throws IOException;

	/**
	 * Write (either create or replace) a text file on the local machine with one read from the server.
	 * 
	 * @param path
	 *            the absolute path of the file, (including the file name).
	 * @param mode
	 *            the mode of the file
	 * @param dis
	 *            the stream to read the file from, as bytes
	 * @param length
	 *            the number of bytes to read
	 */
	void writeTextFile(String path, String mode, LoggedDataInputStream dis, int length) throws IOException;

	/**
	 * Merge a text file on the local machine with the diff from the server. (it uses the RcsDiff response format - see cvsclient.ps for
	 * details)
	 * 
	 * @param path
	 *            the absolute path of the file, (including the file name).
	 * @param mode
	 *            the mode of the file
	 * @param dis
	 *            the stream to read the file from, as bytes
	 * @param length
	 *            the number of bytes to read
	 */
	void writeRcsDiffFile(String path, String mode, LoggedDataInputStream dis, int length) throws IOException;

	/**
	 * Write (either create or replace) a text file on the local machine with one read from the server.
	 * 
	 * @param path
	 *            the absolute path of the file, (including the file name).
	 * @param mode
	 *            the mode of the file
	 * @param dis
	 *            the stream to read the file from, as bytes
	 * @param length
	 *            the number of bytes to read
	 */
	void writeBinaryFile(String path, String mode, LoggedDataInputStream dis, int length) throws IOException;

	/**
	 * Remove the specified file from the local disk. If the file does not exist, the operation does nothing.
	 * 
	 * @param pathname
	 *            the full path to the file to remove
	 * @throws IOException
	 *             if an IO error occurs while removing the file
	 */
	void removeLocalFile(String pathname) throws IOException;

	/**
	 * Rename the local file If the destination file exists, the operation does nothing.
	 * 
	 * @param pathname
	 *            the full path to the file to rename
	 * @param newName
	 *            the new name of the file (not the full path)
	 * @throws IOException
	 *             if an IO error occurs while renaming the file
	 */
	void renameLocalFile(String pathname, String newName) throws IOException;

	/**
	 * Set the modified date of the next file to be written. The next call to writeFile will use this date.
	 * 
	 * @param modifiedDate
	 *            the date the file should be marked as modified
	 */
	void setNextFileDate(Date modifiedDate);

	/**
	 * Get any requests that must be sent before commands are sent, to init this file handler.
	 * 
	 * @return an array of Requests that must be sent
	 */
	Request[] getInitialisationRequests();

	/**
	 * Sets the global options. This can be useful to detect, whether local files should be made read-only.
	 */
	void setGlobalOptions(GlobalOptions globalOptions);
}
