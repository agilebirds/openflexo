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
package org.netbeans.lib.cvsclient.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Milos Kleint, Thomas Singer
 */
public class DefaultIgnoreFileFilter implements IgnoreFileFilter {
	private final List patterns = new LinkedList();

	private final List localPatterns = new LinkedList();
	private boolean processGlobalPatterns = true;
	private boolean processLocalPatterns = false;
	private File lastDirectory = null;

	public DefaultIgnoreFileFilter() {
	}

	/**
	 * Creates new DefaultIgnoreFileFilter and fills in patterns.
	 * 
	 * @param patternList
	 *            - list of objects, patterns are retrieved via the Object.toString() method.
	 */
	public DefaultIgnoreFileFilter(List patternList) {
		for (Iterator it = patternList.iterator(); it.hasNext();) {
			String patternString = it.next().toString();
			SimpleStringPattern pattern = new SimpleStringPattern(patternString);
			addPattern(pattern);
		}
	}

	/**
	 * Adds a StringPattern to the list of ignore file patters.
	 */
	public void addPattern(StringPattern pattern) {
		if (pattern.toString().equals("!")) { // NOI18N
			clearPatterns();
		} else {
			patterns.add(pattern);
		}
	}

	/**
	 * Adds a string to the list of ignore file patters using the SimpleStringPattern.
	 */
	public void addPattern(String pattern) {
		if (pattern.equals("!")) { // NOI18N
			clearPatterns();
		} else {
			patterns.add(new SimpleStringPattern(pattern));
		}
	}

	/**
	 * Clears the list of patters. To be used when the "!" character is used in any of the .cvsignore lists.
	 */
	public void clearPatterns() {
		if (patterns.size() > 0) {
			patterns.clear();
		}
	}

	/**
	 * A file is checked against the patterns in the filter. If any of these matches, the file should be ignored. A file will also be
	 * ignored, if its name matches any local <code>.cvsignore</code> file entry.
	 * 
	 * @param directory
	 *            is a file object that refers to the directory the file resides in.
	 * @param noneCvsFile
	 *            is the name of the file to be checked.
	 */
	@Override
	public boolean shouldBeIgnored(File directory, String noneCvsFile) {
		// current implementation ignores the directory parameter.
		// in future or different implementations can add the directory dependant .cvsignore lists
		if (lastDirectory != directory) {
			lastDirectory = directory;
			processGlobalPatterns = true;
			processLocalPatterns = false;
			if (localPatterns.size() > 0) {
				localPatterns.clear();
			}
			String filename = directory.getPath() + File.separator + ".cvsignore"; // NOI18N
			File cvsIgnoreFile = new File(filename);
			if (cvsIgnoreFile.exists()) {
				try {
					List list = parseCvsIgnoreFile(cvsIgnoreFile);
					for (Iterator it = list.iterator(); it.hasNext();) {
						String s = it.next().toString();
						if (s.equals("!")) { // NOI18N
							processGlobalPatterns = false;
							if (localPatterns.size() > 0) {
								localPatterns.clear();
							}
						} else {
							localPatterns.add(new SimpleStringPattern(s));
						}
					}
				} catch (IOException ex) {
					// ignore exception
				}
			}
			processLocalPatterns = localPatterns.size() > 0;
		}
		if (processGlobalPatterns) {
			for (Iterator it = patterns.iterator(); it.hasNext();) {
				StringPattern pattern = (StringPattern) it.next();
				if (pattern.doesMatch(noneCvsFile)) {
					return true;
				}
			}
		}
		if (processLocalPatterns) {
			for (Iterator it = localPatterns.iterator(); it.hasNext();) {
				StringPattern pattern = (StringPattern) it.next();
				if (pattern.doesMatch(noneCvsFile)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Utility method that reads the .cvsignore file and returns a list of Strings. These strings represent the patterns read from the file.
	 */
	public static List parseCvsIgnoreFile(File cvsIgnoreFile) throws IOException, FileNotFoundException {
		BufferedReader reader = null;
		List toReturn = new LinkedList();
		try {
			reader = new BufferedReader(new FileReader(cvsIgnoreFile));
			String line;
			while ((line = reader.readLine()) != null) {
				StringTokenizer token = new StringTokenizer(line, " ", false); // NOI18N
				while (token.hasMoreTokens()) {
					String tok = token.nextToken();
					toReturn.add(tok);
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return toReturn;
	}
}
