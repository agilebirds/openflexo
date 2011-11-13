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
package org.netbeans.lib.cvsclient;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.file.FileHandler;
import org.netbeans.lib.cvsclient.request.UnconfiguredRequestException;
import org.netbeans.lib.cvsclient.response.ResponseException;
import org.netbeans.lib.cvsclient.util.IgnoreFileFilter;

/**
 * Clients that provide the ability to execute commands must implement this interface. All commands use this interface to get details about
 * the environment in which it is being run, and to perform administrative functions such as obtaining Entry lines for specified files.
 * 
 * @author Robert Greig
 */
public interface ClientServices {
	/**
	 * Process all the requests.
	 * 
	 * @param requests
	 *            the requets to process
	 */
	void processRequests(List requests) throws IOException, UnconfiguredRequestException, ResponseException, CommandAbortedException;

	/**
	 * Get the repository used for this connection.
	 * 
	 * @return the repository, for example /home/bob/cvs
	 */
	String getRepository();

	/**
	 * Get the repository path for a given directory, for example in the directory /home/project/foo/bar, the repository directory might be
	 * /usr/cvs/foo/bar. The repository directory is commonly stored in the file
	 * 
	 * <pre>
	 * Repository
	 * </pre>
	 * 
	 * in the CVS directory on the client. (This is the case in the standard CVS command-line tool)
	 * 
	 * @param directory
	 *            the directory
	 */
	String getRepositoryForDirectory(String directory) throws IOException;

	/**
	 * Semantically equivalent to {@link #getRepositoryForDirectory(String)} but does not try to recover from missing CVS/Repository file.
	 * 
	 * @param directory
	 *            the directory to get repository for
	 * @return repository path that corresponds to the given local working directory or null if local directory is not versioned or does not
	 *         exist
	 * @throws IOException
	 *             if the repository cannot be determined by reading CVS/Repository file
	 */
	String getRepositoryForDirectory(File directory) throws IOException;

	/**
	 * Get the local path that the command is executing in.
	 * 
	 * @return the local path
	 */
	String getLocalPath();

	/**
	 * Get the Entry for the specified file, if one exists.
	 * 
	 * @param file
	 *            the file
	 * 
	 * @throws IOException
	 *             if the Entries file cannot be read
	 */
	Entry getEntry(File file) throws IOException;

	/**
	 * Get the entries for a specified directory.
	 * 
	 * @param directory
	 *            the directory for which to get the entries
	 * 
	 * @return an iterator of Entry objects
	 */
	Iterator getEntries(File directory) throws IOException;

	/**
	 * Create or update the administration files for a particular file This will create the CVS directory if necessary, and the Root and
	 * Repository files if necessary. It will also update the Entries file with the new entry
	 * 
	 * @param localDirectory
	 *            the local directory, relative to the directory in which the command was given, where the file in question lives
	 * @param entry
	 *            the entry object for that file
	 * 
	 * @throws IOException
	 *             if there is an error writing the files
	 */
	void updateAdminData(String localDirectory, String repositoryPath, Entry entry) throws IOException;

	/**
	 * Get all the files contained within a given directory that are <b>known to CVS</b>.
	 * 
	 * @param directory
	 *            the directory to look in
	 * 
	 * @return a set of all files.
	 */
	Set getAllFiles(File directory) throws IOException;

	/**
	 * Returns true if no command was sent before. This is used, because the server rejects some doubled commands.
	 */
	boolean isFirstCommand();

	/**
	 * Set whether this is the first command. Normally you do not need to set this yourself - after execution the first command will have
	 * set this to false.
	 */
	void setIsFirstCommand(boolean first);

	/**
	 * Removes the Entry for the specified file.
	 */
	void removeEntry(File file) throws IOException;

	/**
	 * Sets the specified IgnoreFileFilter to use to ignore non-cvs files. TS, 2001-11-23: really needed in the interface (it's never used)?
	 */
	void setIgnoreFileFilter(IgnoreFileFilter filter);

	/**
	 * Returns the IgnoreFileFilter used to ignore non-cvs files. TS, 2001-11-23: really needed in the interface (it's never used)?
	 */
	IgnoreFileFilter getIgnoreFileFilter();

	/**
	 * Returnes true to indicate, that the file specified by directory and nonCvsFile should be ignored.
	 */
	boolean shouldBeIgnored(File directory, String nonCvsFile);

	//
	// allow the user of the Client to define the FileHandlers
	//

	/**
	 * Set the uncompressed file handler.
	 */
	void setUncompressedFileHandler(FileHandler handler);

	/**
	 * Set the handler for Gzip data.
	 */
	void setGzipFileHandler(FileHandler handler);

	/**
	 * Checks for presence of CVS/Tag file and returns it's value.
	 * 
	 * @return the value of CVS/Tag file for the specified directory null if file doesn't exist
	 */
	String getStickyTagForDirectory(File directory);

	/**
	 * Ensures, that the connection is open.
	 * 
	 * @throws AuthenticationException
	 *             if it wasn't possible to connect
	 */
	void ensureConnection() throws AuthenticationException;

	/**
	 * Returns the wrappers map associated with the CVS server The map is valid only after the connection is established
	 */
	Map getWrappersMap() throws CommandException;

	/**
	 * Get the global options that are set to this client. Individual commands can get the global options via this method.
	 */
	GlobalOptions getGlobalOptions();

	/**
	 * Tests for existence of the given file. Normally this method delegates to File.exists() but it may also return true for files that
	 * exists only virtually (in memory). Is such case the file/directory will not exist on disk but its metadata will be available via
	 * getEntries() methods.
	 * 
	 * @param file
	 *            file to test for existence
	 * @return true if the file exists, false otherwise
	 */
	boolean exists(File file);

	/**
	 * Tests whether command execution should be aborted. Commands are encouraged to regulary poll this value if they expect to block for a
	 * long time in their code.
	 * 
	 * @return true if currently running command should abort, false otherwise
	 */
	boolean isAborted();
}
