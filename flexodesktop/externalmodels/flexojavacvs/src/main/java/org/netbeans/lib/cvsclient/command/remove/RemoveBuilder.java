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

package org.netbeans.lib.cvsclient.command.remove;

import java.io.File;

import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;

/**
 * Handles the building of remove information object and the firing of events when complete objects are built.
 * 
 * @author Milos Kleint
 */
public class RemoveBuilder implements Builder {
	private static final String UNKNOWN = ": nothing known about"; // NOI18N
	private static final String WARNING = ": warning: "; // NOI18N
	private static final String SCHEDULING = ": scheduling `"; // NOI18N
	private static final String USE_COMMIT = ": use 'cvs commit' "; // NOI18N
	private static final String DIRECTORY = ": Removing "; // NOI18N
	private static final String STILL_IN_WORKING = ": file `"; // NOI18N
	private static final String REMOVE_FIRST = "first"; // NOI18N
	private static final String UNKNOWN_FILE = "?"; // NOI18N

	/**
	 * The status object that is currently being built
	 */
	private RemoveInformation removeInformation;

	/**
	 * The directory in which the file being processed lives. This is relative to the local directory
	 */
	private String fileDirectory;

	/**
	 * The event manager to use
	 */
	private final EventManager eventManager;

	private final RemoveCommand removeCommand;

	public RemoveBuilder(EventManager eventManager, RemoveCommand removeCommand) {
		this.eventManager = eventManager;
		this.removeCommand = removeCommand;
	}

	@Override
	public void outputDone() {
		if (removeInformation != null) {
			eventManager.fireCVSEvent(new FileInfoEvent(this, removeInformation));
			removeInformation = null;
		}
	}

	@Override
	public void parseLine(String line, boolean isErrorMessage) {
		if (line.indexOf(SCHEDULING) >= 0) {
			int endingIndex = line.indexOf('\'');
			String fn = line.substring(line.indexOf(SCHEDULING) + SCHEDULING.length(), endingIndex).trim();
			addFile(fn);
			removeInformation.setRemoved(true);
			outputDone();
		}
		if (line.startsWith(UNKNOWN_FILE)) {
			addFile(line.substring(UNKNOWN_FILE.length()));
			removeInformation.setRemoved(false);
			outputDone();
		}
		if (line.indexOf(STILL_IN_WORKING) >= 0) {
			int endingIndex = line.indexOf('\'');
			String fn = line.substring(line.indexOf(STILL_IN_WORKING) + STILL_IN_WORKING.length(), endingIndex).trim();
			addFile(fn);
			removeInformation.setRemoved(false);
			outputDone();
		}
		// ignore the rest..
	}

	protected File createFile(String fileName) {
		StringBuffer path = new StringBuffer();
		path.append(removeCommand.getLocalDirectory());
		path.append(File.separator);
		if (fileDirectory == null) {
			// happens for single files only
			// (for directories, the dir name is always sent before the actual files)
			File locFile = removeCommand.getFileEndingWith(fileName);
			if (locFile == null) {
				path.append(fileName);
			} else {
				path = new StringBuffer(locFile.getAbsolutePath());
			}
		} else {
			// path.append(fileDirectory);
			// path.append(File.separator);
			path.append(fileName);
		}
		String toReturn = path.toString();
		toReturn = toReturn.replace('/', File.separatorChar);
		return new File(path.toString());
	}

	protected void addFile(String name) {
		removeInformation = new RemoveInformation();
		removeInformation.setFile(createFile(name));
	}

	@Override
	public void parseEnhancedMessage(String key, Object value) {
	}
}
