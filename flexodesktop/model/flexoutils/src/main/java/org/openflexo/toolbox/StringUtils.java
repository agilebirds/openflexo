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
package org.openflexo.toolbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	public static String getString(InputStream is, String encoding) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
		StringWriter sw = new StringWriter();
		String line = null;
		while ((line = br.readLine()) != null) {
			sw.write(line);
			sw.write(LINE_SEPARATOR);
		}
		return sw.toString();
	}

	public static String reverse(String s) {
		if (s == null) {
			return s;
		}
		StringBuilder sb = new StringBuilder(s.length());
		for (int i = s.length(); i > 0; i--) {
			sb.append(s.charAt(i - 1));
		}
		return sb.toString();
	}

	public static String circularOffset(String s, int offset) {
		if (offset == 0) {
			return s;
		}
		if (s.length() != 0) {
			offset = offset % s.length();
		}
		StringBuilder sb = new StringBuilder(s.length());
		for (int i = 0; i < s.length(); i++) {
			int location = (i + offset) % s.length();
			if (location < 0) {
				location += s.length();
			}
			sb.append(s.charAt(location));
		}
		return sb.toString();
	}

	public static String convertAccents(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case 'Á':
			case 'À':
			case 'Â':
			case 'Ä':
			case 'Ã':
			case 'Å':
				sb.append('A');
				break;
			case 'É':
			case 'È':
			case 'Ê':
			case 'Ë':
				sb.append('E');
				break;
			case 'Í':
			case 'Ì':
			case 'Î':
			case 'Ï':
				sb.append('I');
				break;
			case 'Ó':
			case 'Ò':
			case 'Ô':
			case 'Ö':
			case 'Õ':
			case 'Ø':
				sb.append('O');
				break;
			case 'Ú':
			case 'Ù':
			case 'Û':
			case 'Ü':
				sb.append('U');
				break;
			case 'Ç':
				sb.append('C');
				break;
			case 'Ñ':
				sb.append('N');
				break;
			case 'á':
			case 'à':
			case 'â':
			case 'ä':
			case 'ã':
			case 'å':
				sb.append('a');
				break;
			case 'é':
			case 'è':
			case 'ê':
			case 'ë':
				sb.append('e');
				break;
			case 'í':
			case 'ì':
			case 'î':
			case 'ï':
				sb.append('i');
				break;
			case 'ó':
			case 'ò':
			case 'ô':
			case 'ö':
			case 'õ':
			case 'ø':
				sb.append('o');
				break;
			case 'ú':
			case 'ù':
			case 'û':
			case 'ü':
				sb.append('u');
				break;
			case 'ý':
			case 'ÿ':
				sb.append('y');
				break;
			case 'ç':
				sb.append('c');
				break;
			case 'ñ':
				sb.append('n');
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String replaceNonMatchingPatterns(String string, String regexp, String replacement) {
		return replaceNonMatchingPatterns(string, regexp, replacement, false);
	}

	public static String replaceNonMatchingPatterns(String string, String regexp, String replacement, boolean replaceEachCharacter) {
		if (string == null || string.length() == 0) {
			return string;
		}
		StringBuilder sb = new StringBuilder();
		Matcher m = Pattern.compile(regexp).matcher(string);
		int last = 0;
		while (m.find()) {
			if (replaceEachCharacter) {
				for (int i = last; i < m.start(); i++) {
					sb.append(replacement);
				}
			} else if (last != m.start()) {
				sb.append(replacement);
			}
			sb.append(m.group());
			last = m.end();
		}
		if (replaceEachCharacter) {
			for (int i = last; i < string.length(); i++) {
				sb.append(replacement);
			}
		} else if (last != string.length()) {
			sb.append(replacement);
		}
		return sb.toString();
	}

	public static Hashtable<String, String> getQueryFromURL(URL url) {
		if (url == null || url.getQuery() == null) {
			return new Hashtable<String, String>();
		}
		Hashtable<String, String> returned = new Hashtable<String, String>();
		StringTokenizer st = new StringTokenizer(url.getQuery(), "&");
		while (st.hasMoreTokens()) {
			StringTokenizer subSt = new StringTokenizer(st.nextToken(), "=");
			String key = null, value = null;
			if (subSt.hasMoreTokens()) {
				key = subSt.nextToken();
			}
			if (subSt.hasMoreTokens()) {
				value = subSt.nextToken();
			}
			if (key != null && value != null) {
				returned.put(key, value);
			}
		}
		return returned;
	}

	public static int countMatches(String str, String sub) {
		if (isEmpty(str) || isEmpty(sub)) {
			return 0;
		}
		int count = 0;
		int idx = 0;
		while ((idx = str.indexOf(sub, idx)) != -1) {
			count++;
			idx += sub.length();
		}
		return count;
	}

	public static boolean isSame(String str1, String str2) {
		if (str1 == null) {
			return str2 == null;
		} else {
			return str1.equals(str2);
		}
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isNotEmpty(String str) {
		return str != null && str.length() > 0;
	}

	/**
	 * Compute number of lines of a string (use \n)
	 */
	public static int linesNb(String aString) {
		int returned = 0;
		BufferedReader rdr = new BufferedReader(new StringReader(aString));
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
			returned++;
		}
		return returned;
	}

	public static final String LINE_SEPARATOR = "\n";/*System.getProperty("line.separator");*/

	public static String extractStringFromLine(String aString, int lineNb) {
		StringBuilder sb = new StringBuilder();
		int n = 0;
		for (int i = 0; i < aString.length(); i++) {
			char c = aString.charAt(i);
			if (n >= lineNb) {
				sb.append(c);
			}
			if (c == '\n') {
				n++;
			}
			if (c == '\r' && i + 1 < aString.length() && aString.charAt(i + 1) != '\n') {
				n++;
			}
		}
		return sb.toString();
	}

	public static String extractStringFromLineOld(String aString, int lineNb) {
		StringBuffer returned = new StringBuffer();
		int n = 0;
		BufferedReader rdr = new BufferedReader(new StringReader(aString));
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
			if (n >= lineNb) {
				returned.append((n > lineNb ? LINE_SEPARATOR : "") + line);
			}
			n++;
		}
		return returned.toString();
	}

	public static void main(String[] args) {
		String s = "12345";
		System.err.println(reverse(s));
		System.err.println(circularOffset(s, 2));
		System.err.println(circularOffset(circularOffset(s, -9), 9));
		String s1 = "12";
		System.err.println(circularOffset(circularOffset(s1, -2), 2));
		System.err.println(circularOffset(circularOffset("", -2), 2));
		System.err.println(circularOffset(circularOffset("1", -2), 2));
	}

	public static String extractStringAtLine(String aString, int lineNb) {
		int n = 0;
		BufferedReader rdr = new BufferedReader(new StringReader(aString));
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
			if (n == lineNb) {
				return line;
			}
			n++;
		}
		return null;
	}

	public static String extractWhiteSpace(String aString) {
		if (aString == null) {
			return null;
		}
		int index = 0;
		while (index < aString.length() && aString.charAt(index) < ' ') {
			index++;
		}
		return aString.substring(0, index);
	}

	public static String buildString(char c, int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(c);
		}
		return sb.toString();
	}

	public static String buildWhiteSpaceIndentation(int length) {
		return buildString(' ', length);
	}

	public static int indexOfEscapingJava(char searchedChar, String someJavaCode) {
		int parentLevel = 0; /* () */
		int bracketLevel = 0; /* [] */
		int curlyLevel = 0; /* {} */

		int index = 0;

		while (index < someJavaCode.length()) {
			char current = someJavaCode.charAt(index);
			if (current == '(') {
				parentLevel++;
			}
			if (current == ')') {
				parentLevel--;
			}
			if (current == '[') {
				bracketLevel++;
			}
			if (current == ']') {
				bracketLevel--;
			}
			if (current == '{') {
				curlyLevel++;
			}
			if (current == '}') {
				curlyLevel--;
			}
			if (parentLevel == 0 && bracketLevel == 0 && curlyLevel == 0 && current == searchedChar) {
				return index;
			}
			index++;
		}

		return -1;
	}

	public static String replaceBreakLinesBy(String value, String replacement) {
		return value.replaceAll("(\r\n|\r|\n|\n\r)", replacement);
	}

	/**
	 * Returns the specified string into "camel case" : each word are appended without white-spaces, but with a capital letter.<br/>
	 * Note that, except for the first word, if the whole word is uppurcase, it will be converted into lowercase.
	 * 
	 * Example : "Todo list" => "TodoList"; "DAO controller" => "daoController".
	 * 
	 * @param firstUpper
	 *            <code>true</code> if the first letter has to be uppercase.
	 * @param string
	 *            the string to transform into camel case
	 * @return the camel case string
	 */
	public static String camelCase(String string, boolean firstUpper) {
		if (string == null) {
			return null;
		}
		String value = string.trim().replace('_', ' ');
		if (value.trim().length() == 0) {
			return value;
		}
		if (value.equals(value.toUpperCase())) {
			value = value.toLowerCase();
		}
		StringBuilder result = new StringBuilder(value.length());

		String[] words = value.split(" ");

		// First word
		if (words[0].equals(words[0].toUpperCase())) {
			if (firstUpper) {
				result.append(words[0]);// If the first word is upper case, and first letter must be uppercase, we keep all the word
										// uppercase.
			} else {
				result.append(words[0].toLowerCase());// If the first word is upper case, and first letter must be lowercase, we set all the
														// word lowercase.
			}
		} else {
			if (firstUpper) {
				result.append(firstUpper(words[0]));
			} else {
				result.append(firstsLower(words[0]));
			}
		}

		// Other words
		for (int i = 1; i < words.length; i++) {
			if (words[i].equals(words[i].toUpperCase())) {
				result.append(words[i]);
			} else {
				result.append(firstUpper(words[i]));
			}
		}

		return result.toString();
	}

	/**
	 * Sets the first char into upper case.
	 * 
	 * @param value
	 *            the string to transform.
	 * @return the same string with the first char upper case.
	 */
	public static String firstUpper(String value) {
		if (value == null) {
			return null;
		}
		if (value.length() < 2) {
			return value.toUpperCase();
		}
		return value.substring(0, 1).toUpperCase() + value.substring(1);
	}

	/**
	 * Sets the first char into lower case.<br/>
	 * If the word has more than its first letter in upper case, the consecutive next upper case letters are also converted into lower case.
	 * 
	 * @param value
	 *            the string to convert.
	 * @return the same string, with its first upper case letters converted into lower case.
	 */
	public static String firstsLower(String value) {
		if (value == null) {
			return null;
		}

		if (value.length() == 0) {
			return value;
		}

		int indexOfUpperCase = -1;
		for (char c : value.toCharArray()) {
			if (!Character.isUpperCase(c)) {
				break;
			}
			indexOfUpperCase++;
		}

		if (indexOfUpperCase <= 0) {
			indexOfUpperCase = 1;
		}

		return value.substring(0, indexOfUpperCase).toLowerCase()
				+ (value.length() > indexOfUpperCase ? value.substring(indexOfUpperCase) : "");
	}

}
