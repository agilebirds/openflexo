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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Thomas Singer
 * @version Sep 26, 2001
 */
public class DefaultWriteTextFilePreprocessor implements WriteTextFilePreprocessor {

	private static final int CHUNK_SIZE = 32768;

	@Override
	public void copyTextFileToLocation(InputStream processedInputStream, File fileToWrite, OutputStreamProvider customOutput)
			throws IOException {
		// Here we read the temp file in again, doing any processing required
		// (for example, unzipping). We must not convert the bytes to characters
		// because the file may not be written in the current encoding.
		// We would corrupt it's content when characters would be written!
		InputStream tempInput = null;
		OutputStream out = null;
		byte[] newLine = System.getProperty("line.separator").getBytes();
		try {
			tempInput = new BufferedInputStream(processedInputStream);
			out = new BufferedOutputStream(customOutput.createOutputStream());
			// this chunk is directly read from the temp file
			byte[] cchunk = new byte[CHUNK_SIZE];
			for (int readLength = tempInput.read(cchunk); readLength > 0; readLength = tempInput.read(cchunk)) {

				// we must perform our own newline conversion. The file will
				// definitely have unix style CRLF conventions, so if we have
				// a \n this code will write out a \n or \r\n as appropriate for
				// the platform we are running on
				for (int i = 0; i < readLength; i++) {
					if (cchunk[i] == '\n') {
						out.write(newLine);
					} else {
						out.write(cchunk[i]);
					}
				}
			}
		} finally {
			if (tempInput != null) {
				try {
					tempInput.close();
				} catch (IOException ex) {
					// ignore
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
					// ignore
				}
			}
		}
	}
}
