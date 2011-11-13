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
package org.netbeans.lib.cvsclient.file;

import java.io.File;
import java.io.IOException;

/**
 * Preprocesses the text file before transmitting to the server.
 * 
 * @author Thomas Singer
 */
public interface TransmitTextFilePreprocessor {
	/**
	 * Generates the preprocessed text file from the original text file.
	 */
	File getPreprocessedTextFile(File originalTextFile) throws IOException;

	/**
	 * Cleans up the preprocessed text file after sending it.
	 */
	void cleanup(File preprocessedTextFile);

	/**
	 * Set the temporary directory that is to be used to create temporary files.
	 */
	void setTempDir(File tempDir);
}
