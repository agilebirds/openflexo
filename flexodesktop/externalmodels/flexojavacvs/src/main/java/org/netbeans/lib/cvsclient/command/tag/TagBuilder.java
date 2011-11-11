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
package org.netbeans.lib.cvsclient.command.tag;

import java.io.File;

import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.command.DefaultFileInfoContainer;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;

/**
 * @author Thomas Singer
 */
public class TagBuilder implements Builder {

	public static final String STATES = "T D ? "; // NOI18N
	public static final String CVS_SERVER = "server: "; // NOI18N
	public static final String EXAM_DIR = "server: "; // NOI18N

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
	private String localPath;

	public TagBuilder(EventManager eventManager, String localPath) {
		this.eventManager = eventManager;
		this.localPath = localPath;
	}

	@Override
	public void outputDone() {
		if (fileInfoContainer != null) {
			eventManager.fireCVSEvent(new FileInfoEvent(this, fileInfoContainer));
			fileInfoContainer = null;
		}
	}

	@Override
	public void parseLine(String line, boolean isErrorMessage) {
		if (isErrorMessage) {
			return;
		}

		if (line.indexOf(CVS_SERVER) < 0) {
			if (line.length() < 3) {
				return;
			}

			String firstChar = line.substring(0, 2);
			if (STATES.indexOf(firstChar) >= 0) {
				processFile(line);
			}
		}
	}

	private void processFile(String line) {
		if (fileInfoContainer == null) {
			fileInfoContainer = new DefaultFileInfoContainer();
		}
		fileInfoContainer.setType(line.substring(0, 1));

		String fileName = line.substring(2).trim();
		if (fileName.startsWith("no file")) { // NOI18N
			fileName = fileName.substring(8);
		}
		fileInfoContainer.setFile(createFile(fileName));
		eventManager.fireCVSEvent(new FileInfoEvent(this, fileInfoContainer));
		fileInfoContainer = null;
	}

	private File createFile(String fileName) {
		return new File(localPath, fileName);
	}

	@Override
	public void parseEnhancedMessage(String key, Object value) {
	}
}
