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

import java.awt.Desktop;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * @author bmangez <B>Class Description</B>
 */
public class ToolBox {

	private static String PLATFORM;

	public static final String WINDOWS = "WINDOWS";

	public static final String LINUX = "LINUX";

	public static final String MACOS = "MACOS";

	public static final String OTHER = "OTHER";

	static {
		String osName = System.getProperty("os.name");
		if (osName.indexOf("Mac OS") > -1) {
			PLATFORM = MACOS;
		} else if (osName.indexOf("Windows") > -1) {
			PLATFORM = WINDOWS;
		} else if (osName.indexOf("Linux") > -1) {
			PLATFORM = LINUX;
		} else {
			PLATFORM = OTHER;
		}
	}

	public static boolean isMacOS() {
		return PLATFORM == MACOS;
	}

	public static boolean isWindows() {
		return PLATFORM == WINDOWS;
	}

	public static boolean isLinux() {
		return PLATFORM == LINUX;
	}

	public static boolean isOther() {
		return PLATFORM == OTHER;
	}

	/**
     *
     */
	public ToolBox() {
		super();
	}

	public static boolean isEmpty(String s) {
		return s == null || s.trim().equals("");
	}

	public static String replaceStringByStringInString(String replacedString, String aNewString, String message) {
		if (message == null || message.equals("")) {
			return "";
		}
		if (replacedString == null || replacedString.equals("")) {
			return message;
		}
		if (aNewString == null || aNewString.equals("")) {
			aNewString = "";
		}

		// String newString = "";
		// int replacedStringLength = replacedString.length();
		// int indexOfTag = message.indexOf(replacedString);
		// while (indexOfTag != -1) {
		// newString = newString + message.substring(0, indexOfTag) + aNewString;
		// message = message.substring(indexOfTag + replacedStringLength);
		// indexOfTag = message.indexOf(replacedString);
		// }
		// return newString + message;

		StringBuffer newString = new StringBuffer("");
		int replacedStringLength = replacedString.length();
		int indexOfTag = message.indexOf(replacedString);
		while (indexOfTag != -1) {
			newString.append(message.substring(0, indexOfTag)).append(aNewString);
			message = message.substring(indexOfTag + replacedStringLength);
			indexOfTag = message.indexOf(replacedString);
		}
		return newString.append(message).toString();
	}

	public static String replaceStringByStringInStringOld(String replacedString, String aNewString, String message) {
		if (message == null || message.equals("")) {
			return "";
		}
		if (replacedString == null || replacedString.equals("")) {
			return message;
		}
		if (aNewString == null || aNewString.equals("")) {
			aNewString = "";
		}

		String newString = "";
		int replacedStringLength = replacedString.length();
		int indexOfTag = message.indexOf(replacedString);
		while (indexOfTag != -1) {
			newString = newString + message.substring(0, indexOfTag) + aNewString;
			message = message.substring(indexOfTag + replacedStringLength);
			indexOfTag = message.indexOf(replacedString);
		}
		return newString + message;

	}

	public static String capitalize(String s, boolean removeStartingUnderscore) {
		if (s == null) {
			return null;
		}
		if (s.length() == 0) {
			return s;
		}
		if (s.startsWith("_") && removeStartingUnderscore) {
			s = s.substring(1);
		}
		if (s.length() == 0) {
			return s;
		}
		if (s.length() == 1) {
			return s.toUpperCase();
		}
		return s.substring(0, 1).toUpperCase() + s.substring(1);

	}

	public static String capitalize(String s) {
		return capitalize(s, false);
	}

	public static String uncapitalize(String s) {
		if (s == null) {
			return null;
		}
		if (s.length() > 0 && Character.isUpperCase(s.charAt(0))) {
			s = Character.toLowerCase(s.charAt(0)) + s.substring(1);
		}
		return s;
	}

	/**
	 * @deprecated use methods from JavaUtils
	 * @param s
	 * @return
	 */
	@Deprecated
	public static String cleanStringForJava(String s) {
		StringBuilder sb = new StringBuilder();
		s = StringUtils.convertAccents(s);
		Matcher m = JavaUtils.JAVA_VARIABLE_ACCEPTABLE_PATTERN.matcher(s);
		while (m.find()) {
			if (sb.length() == 0) {
				sb.append(m.group());
			} else {
				sb.append(capitalize(m.group()));
			}
		}
		String ret = sb.toString();
		if (!ret.matches(JavaUtils.JAVA_BEGIN_VARIABLE_NAME_REGEXP)) {
			return "_" + ret;
		}
		return ret;
	}

	public static String cleanStringForProcessDictionaryKey(String s) {
		String cleanedString = getJavaName(s, false, true);

		if ("_".equals(cleanedString)) {
			return null;
		}
		return cleanedString;
	}

	/**
	 * Replace ",',\n,\r by blank
	 * 
	 * @param comment
	 * @return a String to use in a javascript
	 */
	public static String getJavascriptComment(String comment) {
		if (comment == null) {
			return null;
		}
		return ToolBox.replaceStringByStringInString(
				"\r",
				" ",
				ToolBox.replaceStringByStringInString("\n", " ",
						ToolBox.replaceStringByStringInString("\"", " ", ToolBox.replaceStringByStringInString("'", " ", comment))));
	}

	/**
	 * @deprecated use methods from JavaUtils
	 * @param s
	 * @return
	 */
	@Deprecated
	public static String getJavaName(String name, boolean keepCase) {
		return getJavaName(name, keepCase, true);
	}

	/**
	 * @deprecated use methods from JavaUtils
	 * @param name
	 * @return a java name ( starts with a minuscule, and no blanks, dot,..., convert accentuated characters)
	 */
	@Deprecated
	public static String getJavaName(String name, boolean keepCase, boolean lowerFirstChar) {
		if (name == null) {
			return null;
		}
		if (name.equals("")) {
			return name;
		}
		name = StringUtils.convertAccents(name);
		StringBuffer sb = new StringBuffer();
		Matcher m = JavaUtils.JAVA_VARIABLE_ACCEPTABLE_PATTERN.matcher(name);
		while (m.find()) {
			String group = m.group();
			if (sb.length() == 0 && !group.matches(JavaUtils.JAVA_BEGIN_VARIABLE_NAME_REGEXP)) {
				sb.append('_');
			}
			if (keepCase) {
				sb.append(group);
			} else {
				if (sb.length() > 0 || !lowerFirstChar) {
					sb.append(capitalize(group));
				} else {
					sb.append(group.substring(0, 1).toLowerCase()).append(group.substring(1, group.length()));
				}
			}
		}
		name = sb.toString();
		if (name.equals("")) {
			return "_";
		}
		if (ReservedKeyword.contains(name)) {
			return "_" + name;
		}
		return name;
	}

	/**
	 * @deprecated use methods from JavaUtils
	 * @param s
	 * @return
	 */
	@Deprecated
	public static String getJavaClassName(String name) {
		return getJavaName(name, false, false);
	}

	/**
	 * 
	 * @param name
	 * @return a java name ( starts with a minuscule, and no blanks, dot,..., convert accentuated characters)
	 */
	public static String getWarName(String name) {
		if (name == null) {
			return null;
		}
		if (name.equals("")) {
			return name;
		}
		name = StringUtils.convertAccents(name);
		StringBuffer sb = new StringBuffer();
		Matcher m = WAR_NAME_ACCEPTABLE_PATTERN.matcher(name);
		while (m.find()) {
			String group = m.group();
			if (sb.length() == 0 && !group.matches(JavaUtils.JAVA_BEGIN_VARIABLE_NAME_REGEXP)) {
				sb.append('_');
			}
			sb.append(group);
		}
		name = sb.toString();
		if (name.equals("")) {
			return "_";
		}
		return name;
	}

	/**
	 * @deprecated use methods from JavaUtils
	 * @param s
	 * @return
	 */
	@Deprecated
	public static String getJavaName(StringBuffer name) {
		return getJavaName(name.toString(), false);
	}

	/**
	 * @deprecated use methods from JavaUtils
	 * @param s
	 * @return
	 */
	@Deprecated
	public static String getJavaName(String name) {
		return getJavaName(name, false);
	}

	/**
	 * @deprecated use methods from JavaUtils
	 * @param s
	 * @return
	 */
	@Deprecated
	public static String convertStringToJavaString(String stringToConvert) {
		if (stringToConvert == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		Matcher m = JavaUtils.JAVA_CHAR_TO_ESCAPE_IN_STRINGS_PATTERN.matcher(stringToConvert);
		while (m.find()) {
			m.appendReplacement(sb, "\\\\$0");
		}
		m.appendTail(sb);
		int index;
		while ((index = sb.indexOf("\r")) > -1) {
			sb.deleteCharAt(index);
		}
		return sb.toString().replaceAll("[\n]", "\\\\n");
	}

	public static String convertStringToJavascriptString(String stringToConvert) {
		StringBuffer sb = new StringBuffer();
		Matcher m = JAVASCRIPT_CHAR_TO_ESCAPE_IN_STRINGS_PATTERN.matcher(stringToConvert);
		while (m.find()) {
			m.appendReplacement(sb, "\\\\$0");
		}
		m.appendTail(sb);
		return sb.toString().replaceAll("[\n\r]", "\\\\n");
	}

	public static String convertJavaStringToDBName(String javaString) {
		int index = 0;
		boolean lastCharIsUpperCase = true;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < javaString.length(); i++) {
			char c = javaString.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i != 0) {
					if (!lastCharIsUpperCase) {
						sb.append(javaString.substring(index, i).toUpperCase());
						sb.append("_");
						index = i;
					} else {
						if (i + 1 < javaString.length()) {
							if (!Character.isUpperCase(javaString.charAt(i + 1))) {
								sb.append(javaString.substring(index, i).toUpperCase());
								sb.append("_");
								index = i;
							}
						} else {
							sb.append(javaString.substring(index, i + 1).toUpperCase());
							index = i;
						}
					}
				}
				lastCharIsUpperCase = true;
			} else {
				if (i + 1 == javaString.length()) {
					sb.append(javaString.substring(index, i + 1).toUpperCase());
				}
				lastCharIsUpperCase = false;
			}
		}
		return sb.toString();
	}

	public static final String JAVASCRIPT_CHAR_TO_ESCAPE_IN_STRINGS_REG_EXP = "['\\\\]";

	public static final Pattern JAVASCRIPT_CHAR_TO_ESCAPE_IN_STRINGS_PATTERN = Pattern
			.compile(JAVASCRIPT_CHAR_TO_ESCAPE_IN_STRINGS_REG_EXP);

	public static final String WAR_NAME_ACCEPTABLE_CHARS = "[_A-Za-z0-9.]+";

	public static final Pattern WAR_NAME_ACCEPTABLE_PATTERN = Pattern.compile(WAR_NAME_ACCEPTABLE_CHARS);

	/**
	 * Getter method for the attribute pLATFORM
	 * 
	 * @return Returns the pLATFORM.
	 */
	public static String getPLATFORM() {
		return PLATFORM;
	}

	public static class RequestResponse {
		public int status;
		public String response;
	}

	public static RequestResponse getRequest(Hashtable param, String url) throws IOException {
		StringBuffer paramsAsString = new StringBuffer("");
		if (param != null && param.size() > 0) {
			// paramsAsString.append("?");
			Enumeration en = param.keys();
			String key = null;
			String value = null;
			while (en.hasMoreElements()) {
				key = (String) en.nextElement();
				value = (String) param.get(key);
				try {
					paramsAsString.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if (en.hasMoreElements()) {
					paramsAsString.append("&");
				}
			}

		}

		// Create a URL for the desired page
		URL local_url = new URL(url);
		URLConnection conn = local_url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(paramsAsString.toString());
		wr.flush();
		// Read all the text returned by the server
		int httpStatus = 200;
		if (conn instanceof HttpURLConnection) {
			httpStatus = ((HttpURLConnection) conn).getResponseCode();
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String str;
		StringBuffer reply = new StringBuffer();
		while ((str = in.readLine()) != null) {
			reply.append(str).append("\n");
		}
		wr.close();
		in.close();
		RequestResponse response = new RequestResponse();
		response.response = reply.toString();
		response.status = httpStatus;
		return response;

	}

	public static RequestResponse postRequest(Hashtable parameters, String url) {
		try {
			// Construct data
			StringBuffer data = new StringBuffer();
			if (parameters != null && parameters.size() > 0) {
				Enumeration en = parameters.keys();
				String key = null;
				String value = null;
				while (en.hasMoreElements()) {
					key = (String) en.nextElement();
					value = (String) parameters.get(key);
					data.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8"));
					if (en.hasMoreElements()) {
						data.append("&");
					}
				}

			}

			// Send data
			URL local_url = new URL(url);
			URLConnection conn = local_url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data.toString());
			wr.flush();

			// Get the response
			int httpStatus = 200;
			if (conn instanceof HttpURLConnection) {
				httpStatus = ((HttpURLConnection) conn).getResponseCode();
			}
			StringBuffer reply = new StringBuffer();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				reply.append(line).append("\n");
			}
			wr.close();
			rd.close();
			RequestResponse response = new RequestResponse();
			response.response = reply.toString();
			response.status = httpStatus;
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			RequestResponse response = new RequestResponse();
			response.response = e.getMessage();
			response.status = -1;
			return response;
		}

	}

	public static <E> Enumeration<E> getEnumeration(E[] o) {
		return new ArrayEnumeration<E>(o);
	}

	private static class ArrayEnumeration<E> implements Enumeration<E> {
		private E[] array;

		private int index = 0;

		protected ArrayEnumeration(E[] array) {
			this.array = array;
		}

		/**
		 * Overrides hasMoreElements
		 * 
		 * @see java.util.Enumeration#hasMoreElements()
		 */
		@Override
		public boolean hasMoreElements() {
			return array != null && array.length > index;
		}

		/**
		 * Overrides nextElement
		 * 
		 * @see java.util.Enumeration#nextElement()
		 */
		@Override
		public E nextElement() {
			return array[index++];
		}

	}

	public static String serializeHashtable(Hashtable<String, String> params) {
		StringBuffer buf = new StringBuffer();
		String key = null;
		Enumeration<String> en = params.keys();
		while (en.hasMoreElements()) {
			key = en.nextElement();
			buf.append(key).append("=").append(params.get(key));
			if (en.hasMoreElements()) {
				buf.append("&");
			}
		}
		return buf.toString();
	}

	/**
	 * @deprecated use methods from JavaUtils
	 * @param s
	 * @return
	 */
	@Deprecated
	public static String getJavaDocString(String s) {
		return getJavaDocString(s, "    ");
	}

	/**
	 * @deprecated use methods from JavaUtils
	 * @param s
	 * @return
	 */
	@Deprecated
	public static String getJavaDocString(String s, String prefix) {
		if (s == null) {
			return "";
		}
		s = ToolBox.replaceStringByStringInString("*/", "* /", s);
		StringTokenizer st = new StringTokenizer(s, StringUtils.LINE_SEPARATOR, false);
		StringBuilder sb = new StringBuilder();
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			if (str.length() > 0) {
				sb.append(str.trim());
				sb.append(StringUtils.LINE_SEPARATOR);
				if (st.hasMoreTokens()) {
					sb.append(prefix + "* ");
				}
			}
		}
		return sb.toString();
	}

	public static String getWodKeyPath(String s) {
		if (s == null) {
			return null;
		}
		s = ToolBox.replaceStringByStringInString("()", "", s);
		return s;
	}

	public static String stackTraceAsAString(Throwable e) {
		StringBuilder sb = new StringBuilder("Exception " + e.getClass().getName() + ":\n" + (e.getMessage() != null ? e.getMessage() : ""));
		stackTraceAsAString(e, sb);
		return sb.toString();
	}

	private static void stackTraceAsAString(Throwable e, StringBuilder sb) {
		StackTraceElement[] stackTrace = e.getStackTrace();
		if (stackTrace != null) {
			for (int i = 0; i < stackTrace.length; i++) {
				sb.append("\tat " + stackTrace[i] + "\n");
			}
			if (e.getCause() != null) {
				sb.append("Caused by ").append(e.getCause().getClass().getName()).append('\n');
				stackTraceAsAString(e.getCause(), sb);
			}
		} else {
			sb.append("StackTrace not available\n");
		}
	}

	public static void openURL(String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void showFileInExplorer(File fileToOpen) throws IOException {
		String[] command;
		if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
			command = new String[3];
			command[0] = "explorer";
			command[1] = "/select,";
			command[2] = fileToOpen.getAbsolutePath();
		} else {
			command = new String[2];
			command[0] = "open";
			command[1] = fileToOpen.isDirectory() ? fileToOpen.getCanonicalPath() : fileToOpen.getParentFile().getCanonicalPath();
		}
		Runtime.getRuntime().exec(command);
	}

	public static boolean openFile(File fileToOpen) {
		try {
			Desktop.getDesktop().open(fileToOpen);
			return true;
		} catch (IOException e2) {
			e2.printStackTrace();
			return false;
		}
	}

	/**
	 * @param newNalme
	 */
	public static String getDBTableNameFromPropertyName(String name) {
		StringBuffer sb = new StringBuffer();
		boolean previousCharIsUpperCase = false;
		for (int i = 0; i < name.length(); i++) {
			if (Character.isUpperCase(name.charAt(i))) {
				if (i == 0) {
					sb.append(name.charAt(i));
				} else if (i + 1 == name.length()) {
					sb.append(name.charAt(i));
				} else if (previousCharIsUpperCase) {
					if (Character.isUpperCase(name.charAt(i + 1))) {
						sb.append(name.charAt(i));
					} else {
						sb.append('_').append(name.charAt(i));
					}
				} else {
					sb.append('_').append(name.charAt(i));
				}
				previousCharIsUpperCase = true;
			} else {
				sb.append(Character.toUpperCase(name.charAt(i)));
				previousCharIsUpperCase = false;
			}
		}
		return sb.toString();
	}

	public static Document parseXMLData(StringReader xmlStream) throws IOException, JDOMException {
		SAXBuilder parser = new SAXBuilder();
		return parser.build(xmlStream);
	}

	private static Boolean fileChooserRequiresFix;

	public static boolean fileChooserRequiresFix() {
		if (fileChooserRequiresFix == null) {
			if (getPLATFORM() == WINDOWS) {
				String javaVersion = System.getProperty("java.version");
				String version;
				String release = null;
				if (javaVersion.indexOf('_') > 0) {
					version = javaVersion.substring(0, javaVersion.indexOf('_'));
					release = javaVersion.substring(javaVersion.indexOf('_') + 1);
				} else {
					version = javaVersion;
				}
				try {
					fileChooserRequiresFix = version.startsWith("1.6.0") && (release == null || Integer.valueOf(release) < 10);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					fileChooserRequiresFix = true;
				}
			} else {
				fileChooserRequiresFix = false;
			}
		}
		return fileChooserRequiresFix;
	}

	public static void fixFileChooser() {
		String[] cmd = new String[] { "regsvr32", "/u", "/s", System.getenv("windir") + "\\system32\\zipfldr.dll" };
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void undoFixFileChooser() {
		String[] cmd = new String[] { "regsvr32", "/s", System.getenv("windir") + "\\system32\\zipfldr.dll" };
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param s
	 * @return a string to be inserted between single quote in js.
	 */
	public static String escapeStringForJS(String s) {
		if (s == null) {
			return null;
		}
		return s.replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'").replaceAll("\n", "\\\\n").replaceAll("\r", "");
	}

	public static String escapeStringForProperties(String s) {
		if (s == null) {
			return null;
		}
		return s.replaceAll("#", "\\\\#").replaceAll("!", "\\\\!").replaceAll("=", "\\\\=").replaceAll(":", "\\\\:");
	}

	public static String escapeStringForCsv(String s) {
		if (s == null) {
			return null;
		}
		return s.replaceAll("\"", "\"\"");
	}

	public static String getCsvLine(List<String> list) {
		List<List<String>> tmp = new ArrayList<List<String>>();
		tmp.add(list);
		return getCsv(tmp);
	}

	public static String getCsv(List<List<String>> list) {
		StringBuilder sb = new StringBuilder();

		boolean isFirstLine = true;
		for (List<String> line : list) {
			if (!isFirstLine) {
				sb.append("\n");
			}

			boolean isFirstValue = true;
			for (String value : line) {
				if (!isFirstValue) {
					sb.append(";");
				}
				if (!StringUtils.isEmpty(value)) {
					sb.append("\"" + escapeStringForCsv(value) + "\"");
				}
				isFirstValue = false;
			}

			isFirstLine = false;
		}

		return sb.toString();
	}

	public static List<String> parseCsvLine(String csvLine) {
		List<List<String>> result = parseCsv(csvLine);

		if (result.size() > 0) {
			return result.get(0);
		}

		return new ArrayList<String>();
	}

	public static List<List<String>> parseCsv(String csvString) {
		csvString = csvString != null ? csvString.trim() : null;
		List<List<String>> result = new ArrayList<List<String>>();

		if (StringUtils.isEmpty(csvString)) {
			return result;
		}

		char separator;
		if (csvString.indexOf(';') == -1 && csvString.indexOf(',') > -1) {
			separator = ',';
		} else {
			separator = ';';
		}

		List<String> line = new ArrayList<String>();
		StringBuilder currentValue = new StringBuilder();
		boolean isInsideQuote = false;
		boolean wasInsideQuote = false;
		for (int i = 0; i < csvString.length(); i++) {
			if (!wasInsideQuote && csvString.charAt(i) == '"' && (isInsideQuote || currentValue.toString().trim().length() == 0)) {
				if (i + 1 < csvString.length() && csvString.charAt(i + 1) == '"') { // Double quote, escape
					i++;
				} else {
					if (isInsideQuote) {
						wasInsideQuote = true;
					} else {
						currentValue = new StringBuilder();
					}
					isInsideQuote = !isInsideQuote;
					continue;
				}
			} else if ((csvString.charAt(i) == separator || csvString.charAt(i) == '\n') && !isInsideQuote) {
				line.add(currentValue.toString());
				currentValue = new StringBuilder();
				wasInsideQuote = false;
				if (csvString.charAt(i) == '\n') {
					result.add(line);
					line = new ArrayList<String>();
				}
				continue;
			}

			if (!wasInsideQuote) {
				currentValue.append(csvString.charAt(i));
			}
		}

		if (result.size() > 0 || currentValue.length() > 0 || line.size() > 0) {
			line.add(currentValue.toString());
			result.add(line);
		}

		return result;
	}

	/**
	 * Returns the owner frame if not null, or the hidden frame otherwise.
	 * 
	 * @param owner
	 * @return
	 */
	public static Frame getFrame(Frame owner) {
		return owner == null ? Frame.getFrames().length > 0 ? Frame.getFrames()[0] : JOptionPane.getRootFrame() : owner;
	}

	public static String getMd5Hash(String toHash) throws NoSuchAlgorithmException {
		if (toHash == null) {
			return null;
		}
		java.security.MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");
		byte dataBytes[] = toHash.getBytes();
		md5.update(dataBytes);
		byte digest[] = md5.digest();

		StringBuffer hashString = new StringBuffer();

		for (int i = 0; i < digest.length; ++i) {
			String hex = Integer.toHexString(digest[i]);

			if (hex.length() == 1) {
				hashString.append('0');
				hashString.append(hex.charAt(hex.length() - 1));
			} else {
				hashString.append(hex.substring(hex.length() - 2));
			}
		}
		return hashString.toString();
	}

	public static String[] getHostPortFromString(String hostPort, int defaultPort) {
		int hostPortSepIndex = hostPort.indexOf(":");
		if (hostPortSepIndex > -1) {
			return new String[] { hostPort.substring(0, hostPortSepIndex), hostPort.substring(hostPortSepIndex + 1) };
		} else {
			return new String[] { hostPort, String.valueOf(defaultPort) };
		}
	}

	public static String getContentAtURL(URL url) throws UnsupportedEncodingException, IOException {
		if (url.getProtocol().toLowerCase().startsWith("http")) {
			return getContentAtHTTPURL(url);
		} else if (url.getProtocol().toLowerCase().startsWith("file")) {
			return getContentAtFileURL(url);
		} else {
			System.err.println("Can't handle prototcol: " + url.getProtocol());
			return null;
		}
	}

	public static String getContentAtHTTPURL(URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		int httpStatus = conn.getResponseCode();
		if (httpStatus > 199 && httpStatus < 300) {
			InputStream is = conn.getInputStream();
			return getContentFromInputStream(is);
		} else if (httpStatus > 299 && httpStatus < 400) {
			return getContentAtHTTPURL(new URL(conn.getHeaderField("Location")));
		}
		return null;
	}

	public static String getContentAtFileURL(URL url) throws UnsupportedEncodingException, IOException {
		return getContentFromInputStream(url.openStream());
	}

	public static String getContentFromInputStream(InputStream is) throws IOException, UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		byte[] b = new byte[1024];
		while (is.available() > 0) {
			int read = is.read(b);
			sb.append(new String(b, 0, read, "UTF-8"));
		}
		return sb.toString();
	}

	public static String getSystemProperties() {
		return getSystemProperties(false);
	}

	public static String getSystemProperties(boolean replaceBackslashInClasspath) {
		StringBuilder sb = new StringBuilder();
		for (Entry<Object, Object> e : new TreeMap<Object, Object>(System.getProperties()).entrySet()) {
			String key = (String) e.getKey();
			if ("line.separator".equals(key)) {
				String nl = (String) e.getValue();
				nl = nl.replace("\r", "\\r");
				nl = nl.replace("\n", "\\n");
				sb.append(key).append(" = ").append(nl).append('\n');
			} else if ("java.class.path".equals(key)) {
				String nl = (String) e.getValue();
				nl = nl.replace('\\', '/');
				sb.append(key).append(" = ").append(nl).append('\n');
			} else {
				sb.append(key).append(" = ").append(e.getValue()).append('\n');
			}
		}
		return sb.toString();
	}

	public static String getStackTraceAsString(Throwable t) {
		if (t == null) {
			return null;
		}
		StringBuilder returned = new StringBuilder();
		if (t.getStackTrace() != null) {
			for (StackTraceElement ste : t.getStackTrace()) {
				returned.append("\tat ").append(ste).append("\n");
			}
		}
		return returned.toString();
	}

	public static List<?> getListFromIterable(Object iterable) {
		if (iterable instanceof List) {
			return (List<?>) iterable;
		}
		if (iterable instanceof Collection) {
			return new ArrayList<Object>((Collection<?>) iterable);
		}
		if (iterable instanceof Iterable) {
			List<Object> list = new ArrayList<Object>();
			for (Object o : (Iterable<?>) iterable) {
				list.add(o);
			}
			return list;
		}
		if (iterable instanceof Enumeration) {
			List<Object> list = new ArrayList<Object>();
			for (Enumeration<?> en = (Enumeration<?>) iterable; en.hasMoreElements();) {
				list.add(en.nextElement());
			}
			return list;
		}
		return null;
	}

	public static boolean isMacOSLaf() {
		return UIManager.getLookAndFeel().getName().equals("Mac OS X");
	}

	public static boolean isWindowsLaf() {
		return UIManager.getLookAndFeel().getName().startsWith("Windows");
	}

	public static boolean isNimbusLaf() {
		return UIManager.getLookAndFeel().getName().equals("Nimbus");
	}
}