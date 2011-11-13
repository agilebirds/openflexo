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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Thomas Singer
 * @version Sep 26, 2001
 */
public class DefaultTransmitTextFilePreprocessor implements TransmitTextFilePreprocessor {

	private static final int CHUNK_SIZE = 32768;

	private File tempDir;

	@Override
	public void setTempDir(File tempDir) {
		this.tempDir = tempDir;
	}

	@Override
	public File getPreprocessedTextFile(File originalTextFile) throws IOException {
		// must write file to temp location first because size might change
		// due to CR/LF changes
		File preprocessedTextFile = File.createTempFile("cvs", null, tempDir); // NOI18N

		byte[] newLine = System.getProperty("line.separator").getBytes();
		boolean doConversion = newLine.length != 1 || newLine[0] != '\n';

		OutputStream out = null;
		InputStream in = null;

		try {
			in = new BufferedInputStream(new FileInputStream(originalTextFile));
			out = new BufferedOutputStream(new FileOutputStream(preprocessedTextFile));

			byte[] fileChunk = new byte[CHUNK_SIZE];
			byte[] fileWriteChunk = new byte[CHUNK_SIZE];

			for (int readLength = in.read(fileChunk); readLength > 0; readLength = in.read(fileChunk)) {

				if (doConversion) {
					int writeLength = 0;
					for (int i = 0; i < readLength;) {
						int pos = findIndexOf(fileChunk, newLine, i);
						if (pos >= i && pos < readLength) {
							System.arraycopy(fileChunk, i, fileWriteChunk, writeLength, pos - i);
							writeLength += pos - i;
							i = pos + newLine.length;
							fileWriteChunk[writeLength++] = '\n';
						} else {
							System.arraycopy(fileChunk, i, fileWriteChunk, writeLength, readLength - i);
							writeLength += readLength - i;
							i = readLength;
						}
					}
					out.write(fileWriteChunk, 0, writeLength);
				} else {
					out.write(fileChunk, 0, readLength);
				}
			}
			return preprocessedTextFile;
		} catch (IOException ex) {
			if (preprocessedTextFile != null) {
				cleanup(preprocessedTextFile);
			}
			throw ex;
		} finally {
			if (in != null) {
				try {
					in.close();
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

	private static int findIndexOf(byte[] array, byte[] pattern, int start) {
		int subPosition = 0;
		for (int i = start; i < array.length; i++) {
			if (array[i] == pattern[subPosition]) {
				if (++subPosition == pattern.length) {
					return i - subPosition + 1;
				}
			} else {
				subPosition = 0;
			}
		}
		return -1;
	}

	@Override
	public void cleanup(File preprocessedTextFile) {
		if (preprocessedTextFile != null) {
			preprocessedTextFile.delete();
		}
	}

}
