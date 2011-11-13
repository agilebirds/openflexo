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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Contains intercepted infomation from command standard output. Actula data are held in temporary file.
 * 
 */
public class PipedFileInformation extends FileInfoContainer {
	private File file;

	private String repositoryRevision;

	private String repositoryFileName;

	private File tempFile;

	private OutputStream tmpStream;

	public PipedFileInformation(File tempFile) {
		this.tempFile = tempFile;
		// this.tempFile.deleteOnExit();
		try {
			tmpStream = new BufferedOutputStream(new FileOutputStream(tempFile));
		} catch (IOException ex) {
			// TODO
		}
	}

	/**
	 * Returns the original file. For piped content see {@link #getTempFile()}.
	 */
	@Override
	public File getFile() {
		return file;
	}

	/**
	 * Sets the original file.
	 */
	protected void setFile(File file) {
		this.file = file;
	}

	/**
	 * Returns the revision of the incoming file.
	 */
	public String getRepositoryRevision() {
		return repositoryRevision;
	}

	/**
	 * Sets the revision of the incoming file.
	 */
	protected void setRepositoryRevision(String repositoryRevision) {
		this.repositoryRevision = repositoryRevision;
	}

	/**
	 * Returns the filename in the repository.
	 */
	public String getRepositoryFileName() {
		return repositoryFileName;
	}

	/**
	 * Sets the repository filename.
	 */
	protected void setRepositoryFileName(String repositoryFileName) {
		this.repositoryFileName = repositoryFileName;
	}

	/**
	 * Adds the specified line to the temporary file.
	 */
	protected void addToTempFile(byte[] bytes) throws IOException {
		if (tmpStream != null) {
			tmpStream.write(bytes);
		}
	}

	/**
	 * Adds the specified line to the temporary file.
	 */
	public void addToTempFile(byte[] bytes, int len) throws IOException {
		if (tmpStream != null) {
			tmpStream.write(bytes, 0, len);
		}
	}

	protected void closeTempFile() throws IOException {
		if (tmpStream != null) {
			tmpStream.flush();
			tmpStream.close();
		}
	}

	public File getTempFile() {
		return tempFile;
	}

}
