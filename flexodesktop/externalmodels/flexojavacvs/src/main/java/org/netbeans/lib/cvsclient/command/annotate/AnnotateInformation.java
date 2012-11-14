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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.netbeans.lib.cvsclient.command.FileInfoContainer;

/**
 * Describes annotate information for a file. This is the result of doing a cvs annotate command. The fields in instances of this object are
 * populated by response handlers.
 * 
 * @author Milos Kleint
 */
public class AnnotateInformation extends FileInfoContainer {
	/**
	 * The file, associated with thiz.
	 */
	private File file;

	/**
	 * List of lines stored here.
	 */
	private List linesList;

	private Iterator iterator;

	private File tempFile;

	private File tempDir;

	private BufferedOutputStream tempOutStream;

	public AnnotateInformation() {
		this.tempDir = null;
	}

	public AnnotateInformation(File tempDir) {
		this.tempDir = tempDir;
	}

	/**
	 * Getter for property file.
	 * 
	 * @return Value of property file.
	 */
	@Override
	public File getFile() {
		return file;
	}

	/**
	 * Setter for property file.
	 * 
	 * @param file
	 *            New value of property file.
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Return a string representation of this object. Useful for debugging.
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(30);
		buf.append("\nFile: " + (file != null ? file.getAbsolutePath() : "null")); // NOI18N
		return buf.toString();
	}

	public AnnotateLine createAnnotateLine() {
		return new AnnotateLine();
	}

	public void addLine(AnnotateLine line) {
		linesList.add(line);
	}

	public AnnotateLine getFirstLine() {
		if (linesList == null) {
			linesList = createLinesList();
		}
		iterator = linesList.iterator();
		return getNextLine();
	}

	public AnnotateLine getNextLine() {
		if (iterator == null) {
			return null;
		}
		if (!iterator.hasNext()) {
			return null;
		}
		return (AnnotateLine) iterator.next();
	}

	/**
	 * Adds the specified line to the temporary file.
	 */
	protected void addToTempFile(String line) throws IOException {
		if (tempOutStream == null) {
			try {
				tempFile = File.createTempFile("ann", ".cvs", tempDir); // NOI18N
				tempFile.deleteOnExit();
				tempOutStream = new BufferedOutputStream(new FileOutputStream(tempFile));
			} catch (IOException ex) {
				// TODO
			}
		}
		tempOutStream.write(line.getBytes());
		tempOutStream.write('\n');
	}

	protected void closeTempFile() throws IOException {
		if (tempOutStream == null) {
			return;
		}
		try {
			tempOutStream.flush();
		} finally {
			tempOutStream.close();
		}
	}

	public File getTempFile() {
		return tempFile;
	}

	private List createLinesList() {
		List toReturn = new LinkedList();
		BufferedReader reader = null;
		if (tempFile == null) {
			return toReturn;
		}
		try {
			reader = new BufferedReader(new FileReader(tempFile));
			String line = reader.readLine();
			int lineNum = 1;
			while (line != null) {
				AnnotateLine annLine = AnnotateBuilder.processLine(line);
				if (annLine != null) {
					annLine.setLineNum(lineNum);
					toReturn.add(annLine);
					lineNum++;
				}
				line = reader.readLine();
			}
		} catch (IOException exc) {
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex2) {
			}
		}
		return toReturn;
	}

}
