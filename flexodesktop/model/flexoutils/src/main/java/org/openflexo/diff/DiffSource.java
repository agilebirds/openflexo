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
package org.openflexo.diff;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.StringUtils;

public class DiffSource {

	private MergeSourceType type;
	private String sourceString;
	private File sourceFile;
	private int ignoredCols = -1;
	private MergeToken[] textTokens;
	private String text;

	private int maxCols = 0;

	private DelimitingMethod _delimitingMethod = DelimitingMethod.LINES;

	public class MergeToken {
		private final int beginDelimStartIndex;
		private final int beginDelimEndIndex;
		private final int endDelimStartIndex;
		private final int endDelimEndIndex;

		protected MergeToken(int beginDelimStartIndex, int beginDelimEndIndex, int endDelimStartIndex, int endDelimEndIndex) {
			this.beginDelimStartIndex = beginDelimStartIndex;
			this.beginDelimEndIndex = beginDelimEndIndex;
			this.endDelimStartIndex = endDelimStartIndex;
			this.endDelimEndIndex = endDelimEndIndex;
		}

		public String getBeginDelim() {
			return text.substring(beginDelimStartIndex, beginDelimEndIndex);
		}

		public String getEndDelim() {
			return text.substring(endDelimStartIndex, endDelimEndIndex);
		}

		public String getToken() {
			return text.substring(beginDelimEndIndex, endDelimStartIndex);
		}

		public String getFullString() {
			return text.substring(beginDelimStartIndex, endDelimEndIndex);
		}

		@Override
		public String toString() {
			return getFullString();
		}

		public int getTokenStartIndex() {
			return beginDelimEndIndex;
		}

		public int getTokenEndIndex() {
			return endDelimStartIndex;
		}
	}

	public enum MergeSourceType {
		File, String
	}

	public DiffSource(String aString) {
		this(aString, DelimitingMethod.LINES);
	}

	public DiffSource(String aString, DelimitingMethod method) {
		this(aString, method, -1);
	}

	public DiffSource(String aString, int ignoredCols) {
		this(aString, DelimitingMethod.LINES, ignoredCols);
	}

	public DiffSource(String aString, DelimitingMethod method, int ignoredCols) {
		type = MergeSourceType.String;
		sourceString = aString;
		sourceFile = null;
		this.ignoredCols = ignoredCols;
		_delimitingMethod = method;
		if (aString == null) {
			textTokens = slurpString("", ignoredCols, method);
		} else {
			textTokens = slurpString(aString, ignoredCols, method);
		}
	}

	public DiffSource(File aFile) throws IOException {
		this(aFile, DelimitingMethod.LINES);
	}

	public DiffSource(File aFile, DelimitingMethod method) throws IOException {
		this(aFile, method, -1);
	}

	public DiffSource(File aFile, int ignoredCols) throws IOException {
		this(aFile, DelimitingMethod.LINES, ignoredCols);
	}

	public DiffSource(File aFile, DelimitingMethod method, int ignoredCols) throws IOException {
		type = MergeSourceType.File;
		sourceString = null;
		sourceFile = aFile;
		this.ignoredCols = ignoredCols;
		_delimitingMethod = method;
		textTokens = slurpFile(aFile, ignoredCols, method);
	}

	public void updateWith(String aString) {
		sourceString = aString;
		sourceFile = null;
		textTokens = slurpString(aString, 0, _delimitingMethod);
	}

	public void updateSourceString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tokensCount(); i++) {
			sb.append(tokenValueAt(i) + StringUtils.LINE_SEPARATOR);
		}
		sourceString = sb.toString();
	}

	/**
	 * Read a text file into an array of String. This provides basic diff functionality. A more advanced diff utility will use specialized
	 * objects to represent the text lines, with options to, for example, convert sequences of whitespace to a single space for comparison
	 * purposes.
	 */
	/*private String[] slurpFile(File file) throws IOException {
		BufferedReader rdr = new BufferedReader(new FileReader(file));
		Vector<String> s = new Vector<String>();
		StringBuffer sb = new StringBuffer();
		for (;;) {
			String line = rdr.readLine();
			if (line == null) break;
			s.addElement(line);
			//System.out.println("File: add line "+line);
			sb.append(line+"\n");
		}
		text = sb.toString();
		textLines = new String[s.size()];
		s.copyInto(textLines);
		return textLines;
	}*/

	/**
	 * Same as above, but ignore first ignoredCols cols
	 */
	/*private String[] slurpFile(File file, int ignoredCols) throws IOException
	{
		BufferedReader rdr = new BufferedReader(new FileReader(file));
		Vector<String> s = new Vector<String>();
		StringBuffer sb = new StringBuffer();
		for (;;) {
			String line = rdr.readLine();
			if (line == null) break;
			s.addElement(line.substring(ignoredCols));
			//System.out.println("File: add line "+line);
			sb.append(line.substring(ignoredCols)+"\n");
		}
		text = sb.toString();
		textLines = new String[s.size()];
		s.copyInto(textLines);
		return textLines;
	}*/

	/**
	 * Convert a String to an array of String (use \n)
	 */
	/*private String[] slurpString(String aString)
	{
		if (_delimitingMethod == DelimitingMethod.Lines) {
			BufferedReader rdr = new BufferedReader(new StringReader(aString));
			Vector<String> s = new Vector<String>();
			StringBuffer sb = new StringBuffer();
			for (;;) {
				String line = null;
				try {
					line = rdr.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (line == null) break;
				s.addElement(line);
				//System.out.println("String: add line "+line);
				sb.append(line+"\n");
			}
			text = sb.toString();
			textLines = new String[s.size()];
			s.copyInto(textLines);
			return textLines;
		}
		else {
			//String delims = "\t\n\r\f=<>?/"+'"';
			String delims = "\t\n\r\f= ";
			StringTokenizer st = new StringTokenizer(aString,delims,true);
			Vector<String> s = new Vector<String>();
			StringBuffer sb = new StringBuffer();
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				s.addElement(token);
				System.out.println("String: add token "+token);
				sb.append(token+"\n");
			}
			text = sb.toString();
			textLines = new String[s.size()];
			s.copyInto(textLines);
			return textLines;
		}
	}*/

	/**
	 * Delete first columns
	 */
	private static String deleteFirstColumns(String aString, int ignoredCols) {
		BufferedReader rdr = new BufferedReader(new StringReader(aString));
		Vector<String> s = new Vector<String>();
		StringBuffer sb = new StringBuffer();
		for (;;) {
			String line = null;
			try {
				line = rdr.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (line == null) {
				break;
			}
			s.addElement(line);
			// System.out.println("String: add line "+line);
			sb.append(line + StringUtils.LINE_SEPARATOR);
		}
		return sb.toString();
	}

	/**
	 * Internally tokenize a File given some ignored cols and a delimiting method
	 * 
	 * @throws IOException
	 */
	private MergeToken[] slurpFile(File aFile, int ignoredCols, DelimitingMethod delimitingMethod) throws IOException {
		return slurpString(FileUtils.fileContents(aFile), ignoredCols, delimitingMethod);
	}

	private static enum TokenLocation {
		BEGIN, TOKEN, END;
	}

	/**
	 * Internally tokenize a String given some ignored cols and a delimiting method
	 */
	private MergeToken[] slurpString(String aString, int ignoredCols, DelimitingMethod delimitingMethod) {
		if (ignoredCols > 0) {
			aString = deleteFirstColumns(aString, ignoredCols);
		}
		aString = aString.replace("\r", "");
		text = aString;

		_significativeTokens = null;
		maxCols = 0;

		String delims = delimitingMethod.getDelimiters();

		List<MergeToken> tokens = new ArrayList<MergeToken>();
		int index = 0;
		int beginDelimStartIndex = 0;
		int beginDelimEndIndex = 0;
		int endDelimStartIndex = 0;
		int endDelimEndIndex = 0;
		TokenLocation location = TokenLocation.BEGIN;
		while (index < aString.length()) {
			char c = aString.charAt(index);
			boolean isDelim = delims.indexOf(c) > -1;
			boolean isNewLine = c == '\n';
			if (isNewLine) {
				if (location == TokenLocation.BEGIN) {
					beginDelimEndIndex = index;
				}
				if (location == TokenLocation.BEGIN || location == TokenLocation.TOKEN) {
					endDelimStartIndex = index;
				}
				location = TokenLocation.END;
			}
			switch (location) {
			case BEGIN:
				if (!isDelim) {
					beginDelimEndIndex = index;
					location = TokenLocation.TOKEN;
				}
				break;
			case END:
				if (!isDelim || isNewLine) {
					endDelimEndIndex = isNewLine ? index + 1 : index;
					if (beginDelimStartIndex < endDelimEndIndex) {
						tokens.add(new MergeToken(beginDelimStartIndex, beginDelimEndIndex, endDelimStartIndex, endDelimEndIndex));
					}
					beginDelimStartIndex = endDelimEndIndex;
					if (isNewLine) {
						location = TokenLocation.BEGIN;
					} else {
						location = TokenLocation.TOKEN;
					}
				}
				break;
			case TOKEN:
				if (isDelim) {
					endDelimStartIndex = index;
					location = TokenLocation.END;
				}
				break;
			}
			index++;
		}
		switch (location) {
		case BEGIN:
			beginDelimEndIndex = index;
		case TOKEN:
			endDelimStartIndex = index;
		case END:
			endDelimEndIndex = index;
		}
		if (beginDelimStartIndex < endDelimEndIndex) {
			tokens.add(new MergeToken(beginDelimStartIndex, beginDelimEndIndex, endDelimStartIndex, endDelimEndIndex));
		}
		return textTokens = tokens.toArray(new MergeToken[tokens.size()]);
	}

	/**
	 * Same as above, but ignore first ignoredCols cols
	 */
	/*private String[] slurpString(String aString, int ignoredCols)
	{
		BufferedReader rdr = new BufferedReader(new StringReader(aString));
		Vector<String> s = new Vector<String>();
		StringBuffer sb = new StringBuffer();
		for (;;) {
			String line = null;
			try {
				line = rdr.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (line == null) break;
			s.addElement(line.substring(ignoredCols));
			sb.append(line.substring(ignoredCols)+"\n");
		}
		text = sb.toString();
		textLines = new String[s.size()];
		s.copyInto(textLines);
		return textLines;
	}*/

	public int getIgnoredCols() {
		return ignoredCols;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public String getSourceString() {
		return sourceString;
	}

	public MergeToken[] getTextTokens() {
		return textTokens;
	}

	private String[] _significativeTokens;

	public String[] getSignificativeTokens() {
		if (_significativeTokens == null) {
			_significativeTokens = new String[textTokens.length];
		}
		for (int i = 0; i < textTokens.length; i++) {
			_significativeTokens[i] = textTokens[i].getToken();
		}
		return _significativeTokens;
	}

	public MergeToken tokenAt(int index) {
		if (index < textTokens.length) {
			return textTokens[index];
		} else {
			return null;
		}
	}

	public String tokenValueAt(int index) {
		if (index < textTokens.length) {
			return textTokens[index].getToken();
		} else {
			return "???";
		}
	}

	public int tokensCount() {
		return textTokens.length;
	}

	public String getText() {
		return text;
	}

	public MergeSourceType getType() {
		return type;
	}

	public String extractText(int beginIndex, int endIndex) {
		StringBuffer returned = new StringBuffer();
		for (int i = beginIndex; i <= endIndex; i++) {
			if (i >= 0 && i < textTokens.length) {
				returned.append(textTokens[i].getFullString());
			}
		}
		return returned.toString();
	}

	public DelimitingMethod getDelimitingMethod() {
		return _delimitingMethod;
	}

	public int getMaxCols() {
		return maxCols;
	}

}
