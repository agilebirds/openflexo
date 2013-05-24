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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.event.FileUpdatedEvent;
import org.netbeans.lib.cvsclient.file.FileHandler;
import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;

/**
 * Sends a diff of a particular file, indicating that the file currently checked-out needs to be updated by the patch sent with this
 * response.
 * 
 * @author Milos Kleint
 */
class RcsDiffResponse implements Response {

	private static final boolean DEBUG = false;

	/**
	 * The local path of the new file.
	 */
	private String localPath;

	/**
	 * The full repository path of the file.
	 */
	private String repositoryPath;

	/**
	 * The entry line.
	 */
	private String entryLine;

	/**
	 * The file mode.
	 */
	private String mode;

	/**
	 * fullpath to the file being processed.
	 */
	protected String localFile;

	/**
	 * The date Formatter used to parse and format dates. Format is: "EEE MMM dd HH:mm:ss yyyy"
	 */
	private DateFormat dateFormatter;

	/**
	 * Process the data for the response.
	 * 
	 * @param r
	 *            the buffered reader allowing the client to read the server's response. Note that the actual response name has already been
	 *            read and the reader is positioned just before the first argument, if any.
	 * @param services
	 *            various services that are useful to response handlers
	 * @throws ResponseException
	 *             if something goes wrong handling this response
	 */
	@Override
	public void process(LoggedDataInputStream dis, ResponseServices services) throws ResponseException {
		try {
			localPath = dis.readLine();
			repositoryPath = dis.readLine();
			entryLine = dis.readLine();
			mode = dis.readLine();

			String nextLine = dis.readLine();

			boolean useGzip = nextLine.charAt(0) == 'z';

			int length = Integer.parseInt(useGzip ? nextLine.substring(1) : nextLine);

			if (DEBUG) {
				System.err.println("Got update response."); // NOI18N
				System.err.println("LocalPath is          : " + localPath); // NOI18N
				System.err.println("Repository path is    : " + repositoryPath); // NOI18N
				System.err.println("Entries line is       : " + entryLine); // NOI18N
				System.err.println("Mode is               : " + mode); // NOI18N
				System.err.println("Next line (length) is : " + nextLine); // NOI18N
				System.err.println("File length is        : " + length); // NOI18N
			}

			// now read in the file
			final String filePath = services.convertPathname(localPath, repositoryPath);

			final File newFile = new File(filePath);

			if (services.getGlobalOptions().isExcluded(newFile)) {
				while (length > 0) {
					length -= dis.skip(length);
				}
				return;
			}

			localFile = newFile.getAbsolutePath();
			final Entry entry = new Entry(entryLine);

			FileHandler fileHandler = useGzip ? services.getGzipFileHandler() : services.getUncompressedFileHandler();
			// FileHandler fileHandler = useGzip ? getGzippedFileHandler()
			// : getUncompressedFileHandler();
			fileHandler.setNextFileDate(services.getNextFileDate());

			// check if the file is binary
			if (entry.isBinary()) {
				// should actually not happen. it's possible to send pathces for text files only..
				// TODO add BugLog print here
			} else {
				fileHandler.writeRcsDiffFile(filePath, mode, dis, length);
			}

			// we set the date the file was last modified in the Entry line
			// so that we can easily determine whether the file has been
			// untouched
			// for files with conflicts skip the setting of the conflict field.
			// NOT SURE THIS IS NESSESARY HERE..
			String conflictString = null;
			if (entry.getConflict() != null && entry.getConflict().charAt(0) == Entry.HAD_CONFLICTS) {
				if (entry.getConflict().charAt(1) == Entry.TIMESTAMP_MATCHES_FILE) {
					final Date d = new Date(newFile.lastModified());
					conflictString = getEntryConflict(d, true);
				} else {
					conflictString = entry.getConflict().substring(1);
				}
			} else {
				final Date d = new Date(newFile.lastModified());
				conflictString = getEntryConflict(d, false);
			}
			entry.setConflict(conflictString);

			// update the admin files (i.e. within the CVS directory)
			services.updateAdminData(localPath, repositoryPath, entry);

			FileUpdatedEvent e = new FileUpdatedEvent(this, filePath);
			services.getEventManager().fireCVSEvent(e);
			// System.err.println("Finished writing file");
		} catch (IOException e) {
			throw new ResponseException(e);
		}
	}

	/**
	 * Returns the Conflict field for the file's entry. Can be overriden by subclasses. (For example the MergedResponse that sets the
	 * "result of merge" there.)
	 * 
	 * @param date
	 *            the date to put in
	 * @param hadConflicts
	 *            if there were conflicts (e.g after merge)
	 * @return the conflict field
	 */
	protected String getEntryConflict(Date date, boolean hadConflicts) {
		return getDateFormatter().format(date);
	}

	/**
	 * Is this a terminal response, i.e. should reading of responses stop after this response. This is true for responses such as OK or an
	 * error response
	 */
	@Override
	public boolean isTerminalResponse() {
		return false;
	}

	/**
	 * Returns the DateFormatter instance that parses and formats date Strings. The exact format matches the one in
	 * Entry.getLastModifiedDateFormatter() method.
	 * 
	 */
	protected DateFormat getDateFormatter() {
		if (dateFormatter == null) {
			dateFormatter = new SimpleDateFormat(Entry.getLastModifiedDateFormatter().toPattern(), Locale.US);
			dateFormatter.setTimeZone(Entry.getTimeZone());
		}
		return dateFormatter;
	}

}
