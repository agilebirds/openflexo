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
 * A utility class for file based operations.
 * 
 * @author Thomas Singer
 * @version Nov 23, 2001
 */
public class FileUtils {
	private static FileReadOnlyHandler fileReadOnlyHandler;

	/**
	 * Returns the current FileReadOnlyHandler used by setFileReadOnly().
	 */
	public static FileReadOnlyHandler getFileReadOnlyHandler() {
		return fileReadOnlyHandler;
	}

	/**
	 * Sets the specified fileReadOnlyHandler to be used with setFileReadOnly().
	 */
	public static void setFileReadOnlyHandler(FileReadOnlyHandler fileReadOnlyHandler) {
		FileUtils.fileReadOnlyHandler = fileReadOnlyHandler;
	}

	/**
	 * Sets the specified file read-only (readOnly == true) or writable (readOnly == false). If no fileReadOnlyHandler is set, nothing
	 * happens.
	 * 
	 * @throws IOException
	 *             if the operation failed
	 */
	public static void setFileReadOnly(File file, boolean readOnly) throws IOException {
		if (getFileReadOnlyHandler() == null) {
			return;
		}

		getFileReadOnlyHandler().setFileReadOnly(file, readOnly);
	}

	/**
	 * Copies the specified sourceFile to the specified targetFile.
	 */
	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		if (sourceFile == null || targetFile == null) {
			throw new NullPointerException("sourceFile and targetFile must not be null"); // NOI18N
		}

		// ensure existing parent directories
		File directory = targetFile.getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Could not create directory '" + directory + "'"); // NOI18N
		}

		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
			outputStream = new BufferedOutputStream(new FileOutputStream(targetFile));

			try {
				byte[] buffer = new byte[32768];
				for (int readBytes = inputStream.read(buffer); readBytes > 0; readBytes = inputStream.read(buffer)) {
					outputStream.write(buffer, 0, readBytes);
				}
			} catch (IOException ex) {
				targetFile.delete();
				throw ex;
			}
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ex) {
					// ignore
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException ex) {
					// ignore
				}
			}
		}
	}

	/**
	 * Do the best to rename the file.
	 * 
	 * @param orig
	 *            regular file
	 * @param dest
	 *            regular file (if exists it's rewritten)
	 */
	public static void renameFile(File orig, File dest) throws IOException {
		boolean destExists = dest.exists();
		if (destExists) {
			for (int i = 0; i < 3; i++) {
				if (dest.delete()) {
					destExists = false;
					break;
				}
				try {
					Thread.sleep(71);
				} catch (InterruptedException e) {
				}
			}
		}

		if (destExists == false) {
			for (int i = 0; i < 3; i++) {
				if (orig.renameTo(dest)) {
					return;
				}
				try {
					Thread.sleep(71);
				} catch (InterruptedException e) {
				}
			}
		}

		// requires less permisions than renameTo
		FileUtils.copyFile(orig, dest);

		for (int i = 0; i < 3; i++) {
			if (orig.delete()) {
				return;
			}
			try {
				Thread.sleep(71);
			} catch (InterruptedException e) {
			}
		}
		throw new IOException("Can not delete: " + orig.getAbsolutePath()); // NOI18N
	}

	/**
	 * This utility class needs not to be instantiated anywhere.
	 */
	private FileUtils() {
	}
}
