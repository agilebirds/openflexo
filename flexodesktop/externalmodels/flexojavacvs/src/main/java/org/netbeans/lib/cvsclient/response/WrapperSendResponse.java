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

package org.netbeans.lib.cvsclient.response;

import java.io.EOFException;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.KeywordSubstitutionOptions;
import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;
import org.netbeans.lib.cvsclient.util.SimpleStringPattern;
import org.netbeans.lib.cvsclient.util.StringPattern;

/**
 * This class handles the response from the server to a wrapper-sendme-rcsOptions request
 * 
 * @author Sriram Seshan
 */
public class WrapperSendResponse implements Response {

	public static Map parseWrappers(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line);

		// the first token is the pattern
		SimpleStringPattern pattern = new SimpleStringPattern(tokenizer.nextToken());

		// it is followed by option value pairs
		String option, value;

		Map wrappersMap = null;

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
				if (wrappersMap == null) {
					if (!tokenizer.hasMoreTokens()) {
						wrappersMap = Collections.singletonMap(pattern, keywordOption);
					} else {
						wrappersMap = new LinkedHashMap();
						wrappersMap.put(pattern, keywordOption);
					}
				} else {
					wrappersMap.put(pattern, keywordOption);
				}
			}
		}
		return wrappersMap;
	}

	/**
	 * Process the data for the response.
	 * 
	 * @param dis
	 *            the data inputstream allowing the client to read the server's response. Note that the actual response name has already
	 *            been read and the input stream is positioned just before the first argument, if any.
	 */
	@Override
	public void process(LoggedDataInputStream dis, ResponseServices services) throws ResponseException {
		try {

			String wrapperSettings = dis.readLine();
			Map wrappers = parseWrappers(wrapperSettings);
			for (Iterator it = wrappers.keySet().iterator(); it.hasNext();) {
				StringPattern pattern = (StringPattern) it.next();
				KeywordSubstitutionOptions keywordOption = (KeywordSubstitutionOptions) wrappers.get(pattern);
				services.addWrapper(pattern, keywordOption);
			}
		} catch (EOFException ex) {
			throw new ResponseException(ex, CommandException.getLocalMessage("CommandException.EndOfFile", null)); // NOI18N
		} catch (IOException ex) {
			throw new ResponseException(ex);
		} catch (NoSuchElementException nse) {
			throw new ResponseException(nse);
		}
	}

	/**
	 * Is this a terminal response, i.e. should reading of responses stop after this response. This is true for responses such as OK or an
	 * error response
	 */
	@Override
	public boolean isTerminalResponse() {
		return false;
	}

}
