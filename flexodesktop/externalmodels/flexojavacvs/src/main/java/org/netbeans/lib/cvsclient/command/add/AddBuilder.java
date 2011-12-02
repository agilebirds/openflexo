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

package org.netbeans.lib.cvsclient.command.add;

import java.io.File;

import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;

/**
 * Handles the building of add information object and the firing of events when complete objects are built.
 * 
 * @author Milos Kleint
 */
public class AddBuilder implements Builder {
	private static final String UNKNOWN = ": nothing known about"; // NOI18N
	private static final String ADDED = " added to the repository"; // NOI18N
	private static final String WARNING = ": warning: "; // NOI18N
	private static final String ALREADY_ENTERED = " has already been entered"; // NOI18N
	private static final String SCHEDULING = ": scheduling file `"; // NOI18N
	private static final String USE_COMMIT = ": use 'cvs commit' "; // NOI18N
	private static final String DIRECTORY = "Directory "; // NOI18N
	private static final String READDING = ": re-adding file "; // NOI18N
	private static final String RESURRECTED = ", resurrected"; // NOI18N
	private static final String RESUR_VERSION = ", version "; // NOI18N

	private static final boolean DEBUG = false;

	/**
	 * The status object that is currently being built.
	 */
	private AddInformation addInformation;

	/**
	 * The event manager to use.
	 */
	private EventManager eventManager;

	/**
	 * The directory in which the file being processed lives. This is relative to the local directory
	 */
	private String fileDirectory;

	private AddCommand addCommand;

	private boolean readingTags;

	public AddBuilder(EventManager eventManager, AddCommand addCommand) {
		this.eventManager = eventManager;
		this.addCommand = addCommand;
	}

	@Override
	public void outputDone() {
		if (addInformation != null) {
			FileInfoEvent event = new FileInfoEvent(this, addInformation);
			eventManager.fireCVSEvent(event);
			addInformation = null;
		}
	}

	@Override
	public void parseLine(String line, boolean isErrorMessage) {
		if (line.endsWith(ADDED)) {
			String directory = line.substring(DIRECTORY.length(), line.indexOf(ADDED));
			addDirectory(directory);
		} else if (line.indexOf(SCHEDULING) >= 0) {
			String filename = line.substring(line.indexOf(SCHEDULING) + SCHEDULING.length(), line.indexOf('\'')).trim();
			addFile(filename);
		} else if (line.indexOf(READDING) >= 0) {
			String filename = line.substring(line.indexOf(READDING) + READDING.length(), line.indexOf('(')).trim();
			addFile(filename);
		} else if (line.endsWith(RESURRECTED)) {
			String filename = line.substring(0, line.length() - RESURRECTED.length());
			resurrectFile(filename);
		}
		// ignore the rest..
	}

	private File createFile(String fileName) {
		File locFile = addCommand.getFileEndingWith(fileName);
		if (locFile == null) {
			// in case the exact match was not achieved using the getFileEndingWith method
			// let's try to find the best match possible.
			// iterate from the back of the filename string and try to match the endings
			// of getFiles(). the best match is picked then.
			// Works ok for files and directories in add, should not probably be used
			// elsewhere where it's possible to have recursive commands and where resulting files
			// are not listed in getFiles()
			String name = fileName.replace('\\', '/');
			File[] files = addCommand.getFiles();
			int maxLevel = name.length();
			File bestMatch = null;
			String[] paths = new String[files.length];
			for (int index = 0; index < files.length; index++) {
				paths[index] = files[index].getAbsolutePath().replace('\\', '/');
			}
			int start = name.lastIndexOf('/');
			String part = null;
			if (start < 0) {
				part = name;
			} else {
				part = name.substring(start + 1);
			}
			while (start >= 0 || part != null) {
				boolean wasMatch = false;
				for (int index = 0; index < paths.length; index++) {
					if (paths[index].endsWith(part)) {
						bestMatch = files[index];
						wasMatch = true;
					}
				}
				start = name.lastIndexOf('/', start - 1);
				if (start < 0 || !wasMatch) {
					break;
				}
				part = name.substring(start + 1);
			}
			return bestMatch;
		}
		return locFile;
	}

	private void addDirectory(String name) {
		addInformation = new AddInformation();
		addInformation.setType(AddInformation.FILE_ADDED);
		String dirName = name.replace('\\', '/');
		/*        int index = dirName.lastIndexOf('/');
		        if (index > 0) {
		            dirName = dirName.substring(index + 1, dirName.length());
		        }
		 */
		addInformation.setFile(createFile(dirName));
		outputDone();
	}

	private void addFile(String name) {
		addInformation = new AddInformation();
		addInformation.setFile(createFile(name));
		addInformation.setType(AddInformation.FILE_ADDED);
		outputDone();
	}

	private void resurrectFile(String line) {
		int versionIndex = line.lastIndexOf(RESUR_VERSION);
		String version = line.substring(versionIndex + RESUR_VERSION.length()).trim();
		String cutLine = line.substring(0, versionIndex).trim();
		int fileIndex = cutLine.lastIndexOf(' ');
		String name = cutLine.substring(fileIndex).trim();

		if (DEBUG) {
			System.out.println("line1=" + line); // NOI18N
			System.out.println("versionIndex=" + versionIndex); // NOI18N
			System.out.println("version=" + version); // NOI18N
			System.out.println("fileindex=" + fileIndex); // NOI18N
			System.out.println("filename=" + name); // NOI18N
		}

		addInformation = new AddInformation();
		addInformation.setType(AddInformation.FILE_RESURRECTED);
		addInformation.setFile(createFile(name));
		outputDone();
	}

	@Override
	public void parseEnhancedMessage(String key, Object value) {
	}
}
