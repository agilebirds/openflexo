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
package org.netbeans.lib.cvsclient.command.editors;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.command.FileInfoContainer;
import org.netbeans.lib.cvsclient.event.CVSEvent;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;

/**
 * @author Thomas Singer
 * @version Nov 11, 2001
 */
public class EditorsBuilder implements Builder {
	// Constants ==============================================================

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd hh:mm:ss yyyy");
	// private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd hh:mm:ss yyyy zzz");

	// Fields =================================================================

	private final EventManager eventManager;

	private String editorsFileName;

	// Setup ==================================================================

	EditorsBuilder(EventManager eventManager) {
		this.editorsFileName = null;
		this.eventManager = eventManager;
	}

	// Implemented ============================================================

	@Override
	public void parseLine(String line, boolean isErrorMessage) {
		if (!isErrorMessage) {
			parseLine(line);
		}
	}

	@Override
	public void parseEnhancedMessage(String key, Object value) {
	}

	@Override
	public void outputDone() {
	}

	// Utils ==================================================================

	private boolean parseLine(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, "\t");
		if (!tokenizer.hasMoreTokens()) {
			return false;
		}

		// check whether line is the first editors line for this file.
		// persist for later lines.
		if (!line.startsWith("\t")) {
			editorsFileName = tokenizer.nextToken();
			if (!tokenizer.hasMoreTokens()) {
				return false;
			}
		}
		// must have a filename associated with the line,
		// either from this line or a previous one
		else if (editorsFileName == null) {
			return false;
		}

		final String user = tokenizer.nextToken();
		if (!tokenizer.hasMoreTokens()) {
			return false;
		}

		final String dateString = tokenizer.nextToken();
		if (!tokenizer.hasMoreTokens()) {
			return false;
		}

		final String clientName = tokenizer.nextToken();
		if (!tokenizer.hasMoreTokens()) {
			return false;
		}

		final String localDirectory = tokenizer.nextToken();

		try {
			FileInfoContainer fileInfoContainer = parseEntries(localDirectory, editorsFileName, user, dateString, clientName);
			final CVSEvent event = new FileInfoEvent(this, fileInfoContainer);
			eventManager.fireCVSEvent(event);
			return true;
		} catch (ParseException ex) {
			return false;
		}
	}

	private EditorsFileInfoContainer parseEntries(String localDirectory, String fileName, String user, String dateString, String clientName)
			throws ParseException {
		int lastSlashIndex = fileName.lastIndexOf('/');
		if (lastSlashIndex >= 0) {
			fileName = fileName.substring(lastSlashIndex + 1);
		}

		final Date date = parseDate(dateString);
		final File file = new File(localDirectory, fileName);
		return new EditorsFileInfoContainer(file, user, date, clientName);
	}

	private Date parseDate(String dateString) throws ParseException {
		int firstSpaceIndex = Math.max(dateString.indexOf(' '), 0);
		int lastSpaceIndex = Math.min(dateString.lastIndexOf(' '), dateString.length());

		// dateString = dateString.substring(0, lastSpaceIndex).trim();
		dateString = dateString.substring(firstSpaceIndex, lastSpaceIndex).trim();

		return DATE_FORMAT.parse(dateString);
	}
}
