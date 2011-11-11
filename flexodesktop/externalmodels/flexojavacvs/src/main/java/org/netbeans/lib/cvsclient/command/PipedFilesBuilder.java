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
package org.netbeans.lib.cvsclient.command;

import java.io.File;
import java.io.IOException;

import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;

/**
 * Handles the building of "checkout with -p switch" information object and storing of the checked out file to the temporary file and the
 * firing of events when complete objects are built.
 * 
 * @author Milos Kleint
 */
public class PipedFilesBuilder implements Builder, BinaryBuilder {

	private static final String ERR_START = "======="; // NOI18N
	private static final String ERR_CHECK = "Checking out "; // NOI18N
	private static final String ERR_RCS = "RCS:  "; // NOI18N
	private static final String ERR_VERS = "VERS: "; // NOI18N
	private static final String EXAM_DIR = ": Updating"; // NOI18N

	private static final byte[] lineSeparator = System.getProperty("line.separator").getBytes();

	/**
	 * The module object that is currently being built.
	 */
	private PipedFileInformation fileInformation;

	/**
	 * The event manager to use.
	 */
	private EventManager eventManager;

	/**
	 * The directory in which the file being processed lives. This is relative to the local directory.
	 */
	private String fileDirectory;

	private BuildableCommand command;

	private TemporaryFileCreator tempFileCreator;

	/**
	 * Creates a new Builder for the PipeFileResponse.
	 */
	public PipedFilesBuilder(EventManager eventManager, BuildableCommand command, TemporaryFileCreator tempFileCreator) {
		this.eventManager = eventManager;
		this.command = command;
		this.tempFileCreator = tempFileCreator;
	}

	@Override
	public void outputDone() {
		if (fileInformation == null) {
			return;
		}

		try {
			fileInformation.closeTempFile();
		} catch (IOException exc) {
			// TODO
		}
		eventManager.fireCVSEvent(new FileInfoEvent(this, fileInformation));
		fileInformation = null;
	}

	@Override
	public void parseBytes(byte[] bytes, int len) {
		if (fileInformation == null) {
			// HOTFIX there is no header for :local: repositories (thereare two copies in this source)
			// XXX it might be dangerous because PipedFileInformation stays partialy unitialized
			try {
				fileInformation = new PipedFileInformation(File.createTempFile("checkout", null));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			fileInformation.addToTempFile(bytes, len);
		} catch (IOException exc) {
			outputDone();
		}
	}

	@Override
	public void parseLine(String line, boolean isErrorMessage) {
		if (isErrorMessage) {
			if (line.indexOf(EXAM_DIR) >= 0) {
				fileDirectory = line.substring(line.indexOf(EXAM_DIR) + EXAM_DIR.length()).trim();
			} else if (line.startsWith(ERR_CHECK)) {
				processFile(line);
			} else if (line.startsWith(ERR_RCS)) {
				if (fileInformation != null) {
					String repositoryName = line.substring(ERR_RCS.length()).trim();
					fileInformation.setRepositoryFileName(repositoryName);
				}
			} else if (line.startsWith(ERR_VERS)) {
				if (fileInformation != null) {
					String repositoryRevision = line.substring(ERR_RCS.length()).trim();
					fileInformation.setRepositoryRevision(repositoryRevision);
				}
			}
			// header stuff..
		} else {
			if (fileInformation == null) {
				// HOTFIX there is no header for :local: repositories (thereare two copies in this source)
				// XXX it might be dangerous because PipedFileInformation stays partialy unitialized
				try {
					fileInformation = new PipedFileInformation(File.createTempFile("checkout", null));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fileInformation != null) {
				try {
					fileInformation.addToTempFile(line.getBytes("ISO-8859-1")); // see BuildableCommand
					fileInformation.addToTempFile(lineSeparator);
				} catch (IOException exc) {
					outputDone();
				}
			}
		}
	}

	private void processFile(String line) {
		outputDone();
		String filename = line.substring(ERR_CHECK.length());
		try {
			File temporaryFile = tempFileCreator.createTempFile(filename);
			fileInformation = new PipedFileInformation(temporaryFile);
		} catch (IOException ex) {
			fileInformation = null;
			return;
		}
		fileInformation.setFile(createFile(filename));
	}

	private File createFile(String fileName) {
		File file = new File(command.getLocalDirectory(), fileName);
		return file;
	}

	@Override
	public void parseEnhancedMessage(String key, Object value) {
	}

	public BuildableCommand getCommand() {
		return command;
	}
}
