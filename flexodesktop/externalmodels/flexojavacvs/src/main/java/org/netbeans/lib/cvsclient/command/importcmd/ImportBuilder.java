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

package org.netbeans.lib.cvsclient.command.importcmd;

import java.io.File;

import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.command.DefaultFileInfoContainer;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;

/**
 * @author Thomas Singer
 */
public class ImportBuilder implements Builder {

	private static final String NO_CONFLICTS = "No conflicts created by this import"; // NOI18N
	private static final String FILE_INFOS = "NUCIL?"; // NOI18N

	private final EventManager eventManager;
	private final String localPath;
	private final String module;

	private DefaultFileInfoContainer fileInfoContainer;

	public ImportBuilder(EventManager eventManager, ImportCommand importCommand) {
		this.eventManager = eventManager;

		this.localPath = importCommand.getLocalDirectory();
		this.module = importCommand.getModule();
	}

	@Override
	public void outputDone() {
		if (fileInfoContainer == null) {
			return;
		}

		FileInfoEvent event = new FileInfoEvent(this, fileInfoContainer);
		eventManager.fireCVSEvent(event);

		fileInfoContainer = null;
	}

	@Override
	public void parseLine(String line, boolean isErrorMessage) {
		if (line.length() > 2 && line.charAt(1) == ' ') {
			String firstChar = line.substring(0, 1);
			if (FILE_INFOS.indexOf(firstChar) >= 0) {
				String filename = line.substring(2).trim();
				processFile(firstChar, filename);
			} else {
				error(line);
			}
		} else if (line.startsWith(NO_CONFLICTS)) {
			outputDone();
		}
	}

	@Override
	public void parseEnhancedMessage(String key, Object value) {
	}

	private void error(String line) {
		System.err.println("Don't know anything about: " + line);
	}

	private void processFile(String type, String filename) {
		outputDone();

		filename = filename.substring(module.length());
		File file = new File(localPath, filename);

		fileInfoContainer = new DefaultFileInfoContainer();
		fileInfoContainer.setType(type);
		fileInfoContainer.setFile(file);

		outputDone();
	}
}
