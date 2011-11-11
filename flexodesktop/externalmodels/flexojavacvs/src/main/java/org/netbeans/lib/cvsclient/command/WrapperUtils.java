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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.StringTokenizer;

import org.netbeans.lib.cvsclient.ClientServices;
import org.netbeans.lib.cvsclient.util.SimpleStringPattern;

/**
 * Support for <tt>.cvswrappers</tt> parsing and merging.
 */
public class WrapperUtils {

	/**
	 * Reads the wrappers from the specified source and populates the specified map
	 * 
	 * @param reader
	 *            The source of wrappers which is being processed
	 * @param theMap
	 *            The map which is being updated
	 */
	private static void parseWrappers(BufferedReader reader, Map theMap) throws IOException {

		String line;
		while ((line = reader.readLine()) != null) {
			StringTokenizer tokenizer = new StringTokenizer(line);

			// the first token is the pattern
			SimpleStringPattern pattern = new SimpleStringPattern(tokenizer.nextToken());

			// it is followed by option value pairs
			String option, value;

			while (tokenizer.hasMoreTokens()) {
				option = tokenizer.nextToken();
				value = tokenizer.nextToken();

				// do not bother with the -m Options now
				if (option.equals("-k")) { // NOI18N

					// This is a keyword substitution option
					// strip the quotes
					int first = value.indexOf('\'');
					int last = value.lastIndexOf('\'');
					if (first >= 0 && last >= 0) {
						value = value.substring(first + 1, last);
					}

					KeywordSubstitutionOptions keywordOption = KeywordSubstitutionOptions.findKeywordSubstOption(value);
					if (!theMap.containsKey(pattern)) {
						theMap.put(pattern, keywordOption);
					}
				}
			}
		}
	}

	/**
	 * Reads the wrappers from the specified file and populates the specified map
	 * 
	 * @param file
	 *            The File object corresponding to the file which is being processed
	 * @param wrapperMap
	 *            The map which is being updated
	 */
	public static void readWrappersFromFile(File file, Map wrapperMap) throws IOException, FileNotFoundException {
		parseWrappers(new BufferedReader(new FileReader(file)), wrapperMap);
	}

	/**
	 * Reads the wrappers from the specified System property and populates the specified map. The map is unchanged if the property is not
	 * set.
	 * 
	 * @param envVar
	 *            The system variable name
	 * @param wrapperMap
	 *            The map which is being updated
	 */
	private static void readWrappersFromProperty(String envVar, Map wrapperMap) throws IOException {
		String propertyValue = System.getenv(envVar);
		if (propertyValue != null) {
			parseWrappers(new BufferedReader(new StringReader(propertyValue)), wrapperMap);
		}
	}

	/**
	 * This method consolidates the wrapper map so that it follows CVS prioritization rules for the wrappers. Both AddCommand and
	 * ImportCommand will be calling this.
	 */
	public static Map mergeWrapperMap(ClientServices client) throws CommandException {
		String wrapperSource = null;
		Map wrappersMap = new java.util.HashMap(client.getWrappersMap());
		try {
			File home = new File(System.getProperty("user.home")); // NOI18N
			File wrappers = new File(home, "./cvswrappers"); // NOI18N

			wrapperSource = CommandException.getLocalMessage("WrapperUtils.clientDotWrapper.text"); // NOI18N

			if (wrappers.exists()) {
				readWrappersFromFile(wrappers, wrappersMap);
			}

			wrapperSource = CommandException.getLocalMessage("WrapperUtils.environmentWrapper.text"); // NOI18N

			// process the Environment variable CVSWRAPPERS
			readWrappersFromProperty("CVSWRAPPERS", wrappersMap); // NOI18N
		} catch (FileNotFoundException fnex) {
			// should not happen as we check for file existence. Even if it does
			// it just means the .cvswrappers are not there and can be ignored
		} catch (Exception ioex) {
			Object[] parms = new Object[1];
			parms[0] = wrapperSource;
			String localizedMessage = CommandException.getLocalMessage("WrapperUtils.wrapperError.text", parms); // NOI18N
			throw new CommandException(ioex, localizedMessage);
		}

		return wrappersMap;
	}

}
