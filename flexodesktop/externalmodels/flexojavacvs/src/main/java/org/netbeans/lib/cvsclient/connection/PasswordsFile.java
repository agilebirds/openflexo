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

package org.netbeans.lib.cvsclient.connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.netbeans.lib.cvsclient.file.FileUtils;

/**
 * Represents .cvspass passwords file.
 * 
 * @author Petr Kuzel
 */
public final class PasswordsFile {

	/**
	 * Locates scrambled password for given CVS Root.
	 * 
	 * @param cvsRootString
	 *            identifies repository session [:method:][[user][:password]@][hostname[:[port]]]/path/to/repository
	 * @return scrambled password or <code>null</code>
	 */
	public static String findPassword(String cvsRootString) {
		File passFile = new File(System.getProperty("cvs.passfile", System.getProperty("user.home") + "/.cvspass"));
		BufferedReader reader = null;
		String password = null;

		try {
			reader = new BufferedReader(new FileReader(passFile));
			String line;
			while ((line = reader.readLine()) != null) {
				line = normalize(line);
				if (line.startsWith(cvsRootString + " ")) {
					password = line.substring(cvsRootString.length() + 1);
					break;
				}
			}
		} catch (IOException e) {
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return password;

	}

	/**
	 * List roots matching given prefix e.g. <tt>":pserver:"</tt>.
	 */
	public static Collection listRoots(String prefix) {

		List roots = new ArrayList();

		File passFile = new File(System.getProperty("cvs.passfile", System.getProperty("user.home") + "/.cvspass"));
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(passFile));
			String line;
			while ((line = reader.readLine()) != null) {
				line = normalize(line);
				String elements[] = line.split(" "); // NOI18N
				if (elements[0].startsWith(prefix)) {
					roots.add(elements[0]);
				}
			}
		} catch (IOException e) {
			return Collections.EMPTY_SET;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return roots;
	}

	/**
	 * Writes scrambled password for given CVS root. Eliminates all previous values and possible duplicities.
	 * 
	 * @param cvsRootString
	 *            identifies repository session [:method:][[user][:password]@][hostname[:[port]]]/path/to/repository
	 * @param encodedPassword
	 * @throws IOException
	 *             on write failure
	 */
	public static void storePassword(String cvsRootString, String encodedPassword) throws IOException {
		File passFile = new File(System.getProperty("cvs.passfile", System.getProperty("user.home") + File.separatorChar + ".cvspass"));
		BufferedWriter writer = null;
		BufferedReader reader = null;
		try {
			final String LF = System.getProperty("line.separator"); // NOI18N
			if (passFile.createNewFile()) {
				writer = new BufferedWriter(new FileWriter(passFile));
				writer.write(cvsRootString + " " + encodedPassword + LF);
				writer.close();
			} else {
				File tempFile = File.createTempFile("cvs", "tmp");
				reader = new BufferedReader(new FileReader(passFile));
				writer = new BufferedWriter(new FileWriter(tempFile));
				String line;
				boolean stored = false;
				while ((line = reader.readLine()) != null) {
					if (normalize(line).startsWith(cvsRootString + " ")) {
						if (stored == false) {
							writer.write(cvsRootString + " " + encodedPassword + LF);
							stored = true;
						}
					} else {
						writer.write(line + LF);
					}
				}
				if (stored == false) {
					writer.write(cvsRootString + " " + encodedPassword + LF);
				}
				reader.close();
				writer.flush();
				writer.close();

				// copyFile needs less permissions than File.renameTo
				FileUtils.copyFile(tempFile, passFile);
				tempFile.delete();
			}
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}

	/**
	 * Normalizes several possible line formats into 'normal' one that allows to apply dumb string operations.
	 */
	private static String normalize(String line) {
		if (line.startsWith("/1 ")) { // NOI18N
			line = line.substring("/1 ".length()); // NOI18N
		}
		return line;
	}
}
