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
import java.util.Date;

import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.KeywordSubstitutionOptions;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.file.FileHandler;
import org.netbeans.lib.cvsclient.util.StringPattern;

/**
 * Services that are provided to response handlers.
 * 
 * @author Robert Greig
 */
public interface ResponseServices {
	/**
	 * Set the modified date of the next file to be written. The next call to writeFile will use this date.
	 * 
	 * @param modifiedDate
	 *            the date the file should be marked as modified
	 */
	void setNextFileDate(Date modifiedDate);

	/**
	 * Get the modified date of the next file to be written. This will also null any stored date so that future calls will not retrieve a
	 * date that was meant for a previous file.
	 * 
	 * @return the date the next file should be marked as having been modified on.
	 */
	Date getNextFileDate();

	/**
	 * Convert a <i>pathname</i> in the CVS sense (see 5.10 in the protocol document) into a local pathname for the file
	 * 
	 * @param localDirectory
	 *            the name of the local directory, relative to the directory in which the command was given
	 * @param repository
	 *            the full repository name for the file
	 */
	String convertPathname(String localDirectory, String repository);

	/**
	 * Create or update the administration files for a particular file This will create the CVS directory if necessary, and the Root and
	 * Repository files if necessary. It will also update the Entries file with the new entry
	 * 
	 * @param localDirectory
	 *            the local directory, relative to the directory in which the command was given, where the file in question lives
	 * @param entry
	 *            the entry object for that file
	 * @throws IOException
	 *             if there is an error writing the files
	 */
	void updateAdminData(String localDirectory, String repositoryPath, Entry entry) throws IOException;

	/**
	 * Set the Entry for the specified file
	 * 
	 * @param f
	 *            the file
	 * @param e
	 *            the new entry
	 * @throws IOException
	 *             if an error occurs writing the details
	 */
	void setEntry(File f, Entry e) throws IOException;

	/**
	 * Remove the Entry for the specified file
	 * 
	 * @param f
	 *            the file whose entry is to be removed
	 * @throws IOException
	 *             if an error occurs writing the Entries file
	 */
	void removeEntry(File f) throws IOException;

	/**
	 * Remove the specified file from the local disk If the file does not exist, the operation does nothing.
	 * 
	 * @param pathname
	 *            the full path to the file to remove
	 * @throws IOException
	 *             if an IO error occurs while removing the file
	 */
	void removeLocalFile(String pathname) throws IOException;

	/**
	 * Remove the specified file from the local disk.
	 * 
	 * @throws IOException
	 *             if an IO error occurs while removing the file
	 */
	void removeLocalFile(String localPath, String repositoryFileName) throws IOException;

	/**
	 * Rename the local file
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
	 * Get the CVS event manager. This is generally called by response handlers that want to fire events.
	 * 
	 * @return the eventManager
	 */
	EventManager getEventManager();

	/**
	 * Obtain from the underlying implementation the file handler for handling uncompressed data.
	 * 
	 * @return file handler for uncompressed data.
	 */
	FileHandler getUncompressedFileHandler();

	/**
	 * Obtain the file handler for Gzip compressed data.
	 * 
	 * @return file handler for Gzip compressed data.
	 */
	FileHandler getGzipFileHandler();

	/**
	 * ReSet the filehandler for Gzip compressed data. Makes sure the requests for sending gzipped data are not sent..
	 */
	void dontUseGzipFileHandler();

	/**
	 * This method is called when a response for the ValidRequests request is received.
	 * 
	 * @param requests
	 *            A List of requests that is valid for this CVS server separated by spaces.
	 */
	void setValidRequests(String requests);

	/**
	 * This method is called by WrapperSendResponse for each wrapper setting sent back by the CVS server
	 * 
	 * @param pattern
	 *            A StringPattern indicating the pattern for which the wrapper applies
	 * @param option
	 *            A KeywordSubstituionOption corresponding to the setting
	 */
	void addWrapper(StringPattern pattern, KeywordSubstitutionOptions option);

	/**
	 * Get the global options that are set to this client. Individual commands can get the global options via this method.
	 */
	GlobalOptions getGlobalOptions();
}
