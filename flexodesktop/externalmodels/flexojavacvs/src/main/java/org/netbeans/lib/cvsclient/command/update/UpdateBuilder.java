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
package org.netbeans.lib.cvsclient.command.update;

import java.io.File;

import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.command.DefaultFileInfoContainer;
import org.netbeans.lib.cvsclient.event.EnhancedMessageEvent;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;

/**
 * Handles the building of update information object and the firing of events when complete objects are built.
 * 
 * @author Milos Kleint, Thomas Singer
 */
public class UpdateBuilder implements Builder {

	/**
	 * Parsing constants..
	 */
	public static final String UNKNOWN = ": nothing known about"; // NOI18N
	public static final String EXAM_DIR = ": Updating"; // NOI18N
	public static final String TO_ADD = ": use `cvs add' to create an entry for"; // NOI18N
	public static final String STATES = "U P A R M C ? "; // NOI18N
	public static final String WARNING = ": warning: "; // NOI18N
	public static final String SERVER = "server: "; // NOI18N
	public static final String PERTINENT = "is not (any longer) pertinent"; // NOI18N
	public static final String REMOVAL = "for removal"; // NOI18N
	public static final String SERVER_SCHEDULING = "server: scheduling"; // NOI18N
	public static final String CONFLICTS = "rcsmerge: warning: conflicts during merge"; // NOI18N
	public static final String NOT_IN_REPOSITORY = "is no longer in the repository"; // NOI18N;
	// cotacao/src/client/net/riobranco/common/client/gui/BaseDialogThinlet.java already contains the differences between 1.17 and 1.18
	private static final String MERGE_SAME = " already contains the differences between";
	private static final String MERGED = "Merging differences between"; // NOI18N;

	/**
	 * The status object that is currently being built.
	 */
	private DefaultFileInfoContainer fileInfoContainer;

	/**
	 * The event manager to use.
	 */
	private EventManager eventManager;

	/**
	 * The local path the command run in.
	 */
	private final String localPath;

	private String diagnostics;

	/**
	 * Holds 'G' or 'C' if the current file was merged or conflicted, respectively.
	 */
	private String fileMergedOrConflict;

	public UpdateBuilder(EventManager eventManager, String localPath) {
		this.eventManager = eventManager;
		this.localPath = localPath;
	}

	@Override
	public void outputDone() {
		fileMergedOrConflict = null;
		if (fileInfoContainer != null) {
			if (fileInfoContainer.getFile() == null) {
				System.err.println("#65387 CVS: firing invalid event while processing: " + diagnostics);
			}
			eventManager.fireCVSEvent(new FileInfoEvent(this, fileInfoContainer));
			fileInfoContainer = null;
		}
	}

	@Override
	public void parseLine(String line, boolean isErrorMessage) {
		diagnostics = line;
		if (line.indexOf(UNKNOWN) >= 0) {
			processUnknownFile(line, line.indexOf(UNKNOWN) + UNKNOWN.length());
		} else if (line.indexOf(TO_ADD) >= 0) {
			processUnknownFile(line, line.indexOf(TO_ADD) + TO_ADD.length());
		} else if (line.indexOf(EXAM_DIR) >= 0) { // never comes with :local; connection method
			return;
		} else if (line.startsWith(CONFLICTS)) {
			if (fileInfoContainer != null) {
				fileInfoContainer.setType("C"); // NOI18N
				// fire from Merged response which follows
			}
			fileMergedOrConflict = "C";
		} else if (line.indexOf(WARNING) >= 0) {
			if (line.indexOf(PERTINENT) > 0) {
				String filename = line.substring(line.indexOf(WARNING) + WARNING.length(), line.indexOf(PERTINENT)).trim();
				processNotPertinent(filename);
			}
		} else if (line.indexOf(SERVER_SCHEDULING) >= 0) {
			if (line.indexOf(REMOVAL) > 0) {
				String filename = line.substring(line.indexOf(SERVER_SCHEDULING) + SERVER_SCHEDULING.length(), line.indexOf(REMOVAL))
						.trim();
				processNotPertinent(filename);
			}
		} else if (line.indexOf(MERGE_SAME) >= 0) { // not covered by parseEnhancedMessage
			ensureExistingFileInfoContainer();
			fileInfoContainer.setType(DefaultFileInfoContainer.MERGED_FILE);
			String path = line.substring(0, line.indexOf(MERGE_SAME));
			fileInfoContainer.setFile(createFile(path));
			outputDone();
		} else if (line.startsWith(MERGED)) { // not covered by parseEnhancedMessage
			outputDone();
			fileMergedOrConflict = "G";
		} else if (line.indexOf(NOT_IN_REPOSITORY) > 0) {
			String filename = line.substring(line.indexOf(SERVER) + SERVER.length(), line.indexOf(NOT_IN_REPOSITORY)).trim();
			processNotPertinent(filename);
			return;
		} else {
			// otherwise
			if (line.length() > 2) {
				String firstChar = line.substring(0, 2);
				if (STATES.indexOf(firstChar) >= 0) {
					processFile(line);
					return;
				}
			}
		}
	}

	private File createFile(String fileName) {
		return new File(localPath, fileName);
	}

	private void ensureExistingFileInfoContainer() {
		if (fileInfoContainer != null) {
			return;
		}
		fileInfoContainer = new DefaultFileInfoContainer();
	}

	private void processUnknownFile(String line, int index) {
		outputDone();
		fileInfoContainer = new DefaultFileInfoContainer();
		fileInfoContainer.setType("?"); // NOI18N
		String fileName = line.substring(index).trim();
		fileInfoContainer.setFile(createFile(fileName));
	}

	private void processFile(String line) {
		String fileName = line.substring(2).trim();

		if (fileName.startsWith("no file")) { // NOI18N
			fileName = fileName.substring(8);
		}

		if (fileName.startsWith("./")) { // NOI18N
			fileName = fileName.substring(2);
		}

		File file = createFile(fileName);
		if (fileInfoContainer != null) {
			// sometimes (when locally modified.. the merged response is followed by mesage M <file> or C <file>..
			// check the file.. if equals.. it's the same one.. don't send again.. the prior type has preference
			if (fileInfoContainer.getFile() == null) {
				// is null in case the global switch -n is used - then no Enhanced message is sent, and no
				// file is assigned the merged file..
				fileInfoContainer.setFile(file);
			}
			if (file.equals(fileInfoContainer.getFile())) {
				// if the previous information does not say anything, prefer newer one
				if (fileInfoContainer.getType().equals("?")) {
					fileInfoContainer = null;
				} else {
					outputDone();
					return;
				}
			}
		}

		if (fileMergedOrConflict != null && line.charAt(0) == 'M') {
			line = fileMergedOrConflict; // can be done this way, see below
		}

		outputDone();
		ensureExistingFileInfoContainer();

		fileInfoContainer.setType(line.substring(0, 1));
		fileInfoContainer.setFile(file);
	}

	private void processLog(String line) {
		ensureExistingFileInfoContainer();
	}

	private void processNotPertinent(String fileName) {
		outputDone();
		File fileToDelete = createFile(fileName);

		ensureExistingFileInfoContainer();

		// HACK - will create a non-cvs status in order to be able to have consistent info format
		fileInfoContainer.setType(DefaultFileInfoContainer.PERTINENT_STATE);
		fileInfoContainer.setFile(fileToDelete);
	}

	/** <tt>Merged</tt> response handler. */
	@Override
	public void parseEnhancedMessage(String key, Object value) {
		if (key.equals(EnhancedMessageEvent.MERGED_PATH)) {
			ensureExistingFileInfoContainer();
			String path = value.toString();
			File newFile = new File(path);
			// #70106 Merged responce must not rewrite CONFLICTS
			if (newFile.equals(fileInfoContainer.getFile()) == false) {
				fileInfoContainer.setFile(newFile);
				fileInfoContainer.setType(DefaultFileInfoContainer.MERGED_FILE);
				if (fileMergedOrConflict != null) {
					fileInfoContainer.setType(fileMergedOrConflict);
				}
			}
			outputDone();
		}
	}
}
