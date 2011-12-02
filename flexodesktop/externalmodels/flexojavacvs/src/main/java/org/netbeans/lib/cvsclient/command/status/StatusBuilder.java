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
package org.netbeans.lib.cvsclient.command.status;

import java.io.File;

import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;
import org.netbeans.lib.cvsclient.file.FileStatus;

/**
 * Handles the building of a status information object and the firing of events when complete objects are built.
 * 
 * @author Milos Kleint
 * @author Thomas Singer
 */
public class StatusBuilder implements Builder {
	private static final String UNKNOWN = ": nothing known about"; // NOI18N
	private static final String EXAM_DIR = ": Examining"; // NOI18N
	private static final String NOT_IN_REPOSITORY = "No revision control file"; // NOI18N

	private static final String FILE = "File: "; // NOI18N
	private static final String STATUS = "Status:"; // NOI18N
	private static final String NO_FILE_FILENAME = "no file"; // NOI18N
	private static final String WORK_REV = "   Working revision:"; // NOI18N
	private static final String REP_REV = "   Repository revision:"; // NOI18N
	private static final String TAG = "   Sticky Tag:"; // NOI18N
	private static final String DATE = "   Sticky Date:"; // NOI18N
	private static final String OPTIONS = "   Sticky Options:"; // NOI18N
	private static final String EXISTING_TAGS = "   Existing Tags:"; // NOI18N
	private static final String EMPTY_BEFORE_TAGS = "   "; // NOI18N
	private static final String NO_TAGS = "   No Tags Exist"; // NOI18N
	private static final String UNKNOWN_FILE = "? "; // NOI18N

	/**
	 * The status object that is currently being built.
	 */
	private StatusInformation statusInformation;

	/**
	 * The event manager to use.
	 */
	private EventManager eventManager;

	private final StatusCommand statusCommand;
	private String relativeDirectory;
	private final String localPath;

	private boolean beginning;

	private boolean readingTags;

	private final File[] fileArray;

	/**
	 * Creates a StatusBuilder.
	 */
	public StatusBuilder(EventManager eventManager, StatusCommand statusCommand) {
		this.eventManager = eventManager;
		this.statusCommand = statusCommand;

		File[] fileArray = statusCommand.getFiles();
		if (fileArray != null) {
			this.fileArray = new File[fileArray.length];
			System.arraycopy(fileArray, 0, this.fileArray, 0, fileArray.length);
		} else {
			this.fileArray = null;
		}

		this.localPath = statusCommand.getLocalDirectory();

		this.beginning = true;
	}

	@Override
	public void outputDone() {
		if (statusInformation != null) {
			eventManager.fireCVSEvent(new FileInfoEvent(this, statusInformation));
			statusInformation = null;
			readingTags = false;
		}
	}

	@Override
	public void parseLine(String line, boolean isErrorMessage) {
		if (readingTags) {
			if (line.startsWith(NO_TAGS)) {
				outputDone();
				return;
			}

			int bracket = line.indexOf("\t(");
			if (bracket > 0) {
				// it's another tag..
				String tag = line.substring(0, bracket).trim();
				String rev = line.substring(bracket + 2, line.length() - 1);

				if (statusInformation == null) {
					statusInformation = new StatusInformation();
				}
				statusInformation.addExistingTag(tag, rev);
			} else {
				outputDone();
				return;
			}
		}

		if (line.startsWith(UNKNOWN_FILE) && beginning) {
			File file = new File(localPath, line.substring(UNKNOWN_FILE.length()));
			statusInformation = new StatusInformation();
			statusInformation.setFile(file);
			statusInformation.setStatusString(FileStatus.UNKNOWN.toString());
			outputDone();
		}

		if (line.startsWith(UNKNOWN)) {
			outputDone();
			beginning = false;
		} else if (line.indexOf(EXAM_DIR) >= 0) {
			relativeDirectory = line.substring(line.indexOf(EXAM_DIR) + EXAM_DIR.length()).trim();
			beginning = false;
		} else if (line.startsWith(FILE)) {
			outputDone();
			statusInformation = new StatusInformation();
			processFileAndStatusLine(line.substring(FILE.length()));
			beginning = false;
		} else if (line.startsWith(WORK_REV)) {
			processWorkRev(line.substring(WORK_REV.length()));
		} else if (line.startsWith(REP_REV)) {
			processRepRev(line.substring(REP_REV.length()));
			/*            if (statusInformation.getRepositoryRevision().startsWith(NOT_IN_REPOSITORY))
			            {
			                outputDone();
			            }
			 */
		} else if (line.startsWith(TAG)) {
			processTag(line.substring(TAG.length()));
		} else if (line.startsWith(DATE)) {
			processDate(line.substring(DATE.length()));
		} else if (line.startsWith(OPTIONS)) {
			processOptions(line.substring(OPTIONS.length()));
			if (!statusCommand.isIncludeTags()) {
				outputDone();
			}
		} else if (line.startsWith(EXISTING_TAGS)) {
			readingTags = true;
		}
	}

	private File createFile(String fileName) {
		File file = null;

		if (relativeDirectory != null) {
			if (relativeDirectory.trim().equals(".")) { // NOI18N
				file = new File(localPath, fileName);
			} else {
				file = new File(localPath, relativeDirectory + '/' + fileName);
			}
		} else if (fileArray != null) {
			for (int i = 0; i < fileArray.length; i++) {
				File currentFile = fileArray[i];
				if (currentFile == null || currentFile.isDirectory()) {
					continue;
				}

				String currentFileName = currentFile.getName();
				if (fileName.equals(currentFileName)) {
					fileArray[i] = null;
					file = currentFile;
					break;
				}
			}
		}

		if (file == null) {
			System.err.println("JAVACVS ERROR!! wrong algorithm for assigning path to single files(1)!!");
		}

		return file;
	}

	private void processFileAndStatusLine(String line) {
		int statusIndex = line.lastIndexOf(STATUS);
		String fileName = line.substring(0, statusIndex).trim();
		if (fileName.startsWith(NO_FILE_FILENAME)) {
			fileName = fileName.substring(8);
		}

		statusInformation.setFile(createFile(fileName));

		String status = new String(line.substring(statusIndex + 8).trim());
		statusInformation.setStatusString(status);
	}

	private boolean assertNotNull() {
		if (statusInformation == null) {
			System.err.println("Bug: statusInformation must not be null!");
			return false;
		}

		return true;
	}

	private void processWorkRev(String line) {
		if (!assertNotNull()) {
			return;
		}
		statusInformation.setWorkingRevision(line.trim().intern());
	}

	private void processRepRev(String line) {
		if (!assertNotNull()) {
			return;
		}
		line = line.trim();
		if (line.startsWith(NOT_IN_REPOSITORY)) {
			statusInformation.setRepositoryRevision(line.trim().intern());
			return;
		}

		int firstSpace = line.indexOf('\t');
		if (firstSpace > 0) {
			statusInformation.setRepositoryRevision(line.substring(0, firstSpace).trim().intern());
			statusInformation.setRepositoryFileName(new String(line.substring(firstSpace).trim()));
		} else {
			statusInformation.setRepositoryRevision(""); // NOI18N
			statusInformation.setRepositoryFileName(""); // NOI18N
		}
	}

	private void processTag(String line) {
		if (!assertNotNull()) {
			return;
		}
		statusInformation.setStickyTag(line.trim().intern());
	}

	private void processDate(String line) {
		if (!assertNotNull()) {
			return;
		}
		statusInformation.setStickyDate(line.trim().intern());
	}

	private void processOptions(String line) {
		if (!assertNotNull()) {
			return;
		}
		statusInformation.setStickyOptions(line.trim().intern());
	}

	@Override
	public void parseEnhancedMessage(String key, Object value) {
	}

}
