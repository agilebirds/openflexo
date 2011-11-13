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
package org.netbeans.lib.cvsclient.command.watchers;

import java.io.File;

import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;
import org.netbeans.lib.cvsclient.util.BugLog;

/**
 * Handles the building of a watchers information object and the firing of events when complete objects are built.
 * 
 * @author Milos Kleint
 */
public class WatchersBuilder implements Builder {

	private static final String UNKNOWN_FILE = "? "; // NOI18N

	/**
	 * The status object that is currently being built.
	 */
	private WatchersInformation watchersInfo;

	/**
	 * The event manager to use.
	 */
	private final EventManager eventManager;

	/**
	 * The directory where the command was executed. Used to compute absolute path to the file.
	 */
	private final String localPath;

	/**
	 * Creates a WatchersBuilder.
	 * 
	 * @param eventManager
	 *            the event manager that will fire events.
	 * @param localPath
	 *            absolute path to the directory where the command was executed.
	 */
	public WatchersBuilder(EventManager eventManager, String localPath) {
		this.eventManager = eventManager;
		this.localPath = localPath;
	}

	@Override
	public void outputDone() {
		if (watchersInfo != null) {
			eventManager.fireCVSEvent(new FileInfoEvent(this, watchersInfo));
			watchersInfo = null;
		}
	}

	@Override
	public void parseLine(String line, boolean isErrorMessage) {
		if (line.startsWith(UNKNOWN_FILE)) {
			File file = new File(localPath, line.substring(UNKNOWN_FILE.length()));
			watchersInfo = new WatchersInformation(file);
			outputDone();
			return;
		}

		if (isErrorMessage) {
			return;
		}

		if (line.startsWith(" ") || line.startsWith("\t")) { // NOI18N
			BugLog.getInstance().assertNotNull(watchersInfo);

			watchersInfo.addWatcher(line);
			return;
		}

		// the line starts with file..
		outputDone();
		String trimmedLine = line.trim().replace('\t', ' ');
		int spaceIndex = trimmedLine.indexOf(' ');

		BugLog.getInstance().assertTrue(spaceIndex > 0, "Wrong line = " + line);

		File file = new File(localPath, trimmedLine.substring(0, spaceIndex));
		String watcher = trimmedLine.substring(spaceIndex + 1);
		watchersInfo = new WatchersInformation(file);
		watchersInfo.addWatcher(watcher);
	}

	@Override
	public void parseEnhancedMessage(String key, Object value) {
	}
}
