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
package org.openflexo.search;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.openflexo.search.TextQueryResult.Result;

public class TextQueryEngine {

	public static TextQueryResult performSearchOnDocument(Document document, TextQuery query) {
		try {
			return performSearchInText(document.getText(0, document.getLength()), query, document);
		} catch (BadLocationException e) {
			e.printStackTrace();// Should never happen
			return null;
		}
	}

	/**
	 * 
	 * @param text
	 * @param query
	 * @param original
	 *            the original document, if any. This argument can be null.
	 * @return
	 */
	public static TextQueryResult performSearchInText(String text, TextQuery query, Document original) {
		if (text == null) {
			return null;
		}
		TextQueryResult result = original != null ? new TextQueryResult(query, original) : new TextQueryResult(query, text);
		if (query.getSearchedText() == null || query.getSearchedText().length() == 0) {
			return result;
		}
		if (query.isRegularExpression()) {
			int flag = Pattern.DOTALL;
			if (!query.isCaseSensitive()) {
				flag |= Pattern.CASE_INSENSITIVE;
			}
			Pattern pattern = Pattern.compile(query.getSearchedText(), flag);
			Matcher m = pattern.matcher(text);
			while (m.find()) {
				Result r = result.new Result(m.start(), m.end());
				result.addToResults(r);
			}
			return result;
		} else {
			for (int i = 0; i < text.length(); i++) {
				if (text.regionMatches(!query.isCaseSensitive(), i, query.getSearchedText(), 0, query.getSearchedText().length())) {
					if (query.isWholeWord()
							&& (i == 0 || !(Character.isLetterOrDigit(text.charAt(i - 1)) || text.charAt(i - 1) == '_'))
							&& (i + query.getSearchedText().length() == text.length() || !(Character.isLetterOrDigit(text.charAt(i
									+ query.getSearchedText().length())) || text.charAt(i + query.getSearchedText().length()) == '_'))
							|| !query.isWholeWord()) {
						Result r = result.new Result(i, i + query.getSearchedText().length());
						result.addToResults(r);
					}
				}
			}
			return result;
		}
	}

}
