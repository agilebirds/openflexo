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
package org.netbeans.lib.cvsclient.command.annotate;

import java.io.File;
import java.io.IOException;

import org.netbeans.lib.cvsclient.command.BasicCommand;
import org.netbeans.lib.cvsclient.command.Builder;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;

/**
 * Handles the building of a annotate information object and the firing of events when complete objects are built.
 * 
 * @author Milos Kleint
 */
public class AnnotateBuilder implements Builder {
	private static final String UNKNOWN = ": nothing known about"; // NOI18N
	private static final String ANNOTATING = "Annotations for "; // NOI18N
	private static final String STARS = "***************"; // NOI18N

	/**
	 * The Annotate object that is currently being built.
	 */
	private AnnotateInformation annotateInformation;

	/**
	 * The event manager to use.
	 */
	private final EventManager eventManager;

	private final String localPath;
	private String relativeDirectory;
	private int lineNum;
	private File tempDir;

	public AnnotateBuilder(EventManager eventManager, BasicCommand annotateCommand) {
		this.eventManager = eventManager;
		this.localPath = annotateCommand.getLocalDirectory();
		tempDir = annotateCommand.getGlobalOptions().getTempDir();
	}

	@Override
	public void outputDone() {
		if (annotateInformation == null) {
			return;
		}

		try {
			annotateInformation.closeTempFile();
		} catch (IOException exc) {
			// ignore
		}
		eventManager.fireCVSEvent(new FileInfoEvent(this, annotateInformation));
		annotateInformation = null;
	}

	@Override
	public void parseLine(String line, boolean isErrorMessage) {
		if (isErrorMessage && line.startsWith(ANNOTATING)) {
			outputDone();
			annotateInformation = new AnnotateInformation(tempDir);
			annotateInformation.setFile(createFile(line.substring(ANNOTATING.length())));
			lineNum = 0;
			return;
		}

		if (isErrorMessage && line.startsWith(STARS)) {
			// skip
			return;
		}

		if (!isErrorMessage) {
			processLines(line);
		}
	}

	private File createFile(String fileName) {
		return new File(localPath, fileName);
	}

	@Override
	public void parseEnhancedMessage(String key, Object value) {
	}

	private void processLines(String line) {
		if (annotateInformation != null) {
			try {
				annotateInformation.addToTempFile(line);
			} catch (IOException exc) {
				// just ignore, should not happen.. if it does the worst thing that happens is a annotate info without data..
			}
		}
		/*
		        AnnotateLine annLine = processLine(line);
		        if (annotateInformation != null && annLine != null) {
		            annLine.setLineNum(lineNum);
		            annotateInformation.addLine(annLine);
		            lineNum++;
		        }
		 */
	}

	public static AnnotateLine processLine(String line) {
		int indexOpeningBracket = line.indexOf('(');
		int indexClosingBracket = line.indexOf(')');
		AnnotateLine annLine = null;
		if (indexOpeningBracket > 0 && indexClosingBracket > indexOpeningBracket) {
			String revision = line.substring(0, indexOpeningBracket).trim();
			String userDate = line.substring(indexOpeningBracket + 1, indexClosingBracket);
			String contents = line.substring(indexClosingBracket + 3);
			int lastSpace = userDate.lastIndexOf(' ');
			String user = userDate;
			String date = userDate;
			if (lastSpace > 0) {
				user = userDate.substring(0, lastSpace).trim();
				date = userDate.substring(lastSpace).trim();
			}
			annLine = new AnnotateLine();
			annLine.setContent(contents);
			annLine.setAuthor(user);
			annLine.setDateString(date);
			annLine.setRevision(revision);
		}
		return annLine;
	}
}
