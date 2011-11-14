/**
 * 
 */
package org.openflexo.toolbox;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Nicolas Daniels
 */
public class JavaUtils {

	public static final String JAVA_CHAR_TO_ESCAPE_IN_STRINGS_REG_EXP = "[\"\\\\]";
	public static final Pattern JAVA_CHAR_TO_ESCAPE_IN_STRINGS_PATTERN = Pattern.compile(JAVA_CHAR_TO_ESCAPE_IN_STRINGS_REG_EXP);
	public static final String JAVA_BEGIN_VARIABLE_NAME_REGEXP = "^[_A-Za-z].*";
	public static final Pattern JAVA_BEGIN_VARIABLE_NAME_PATTERN = Pattern.compile(JAVA_BEGIN_VARIABLE_NAME_REGEXP);
	public static final String JAVA_VARIABLE_ACCEPTABLE_CHARS = "[_A-Za-z0-9]+";
	public static final Pattern JAVA_VARIABLE_ACCEPTABLE_PATTERN = Pattern.compile(JAVA_VARIABLE_ACCEPTABLE_CHARS);
	public static final String JAVA_CLASS_NAME_REGEXP = "[_A-Za-z][_A-Za-z0-9]*";
	public static final Pattern JAVA_CLASS_NAME_PATTERN = Pattern.compile(JAVA_CLASS_NAME_REGEXP);

	public static final Set<String> JAVA_RESERVED_KEYWORDS = new HashSet<String>(Arrays.asList("abstract", "continue", "for", "new",
			"switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double",
			"implements", "protected", "throw", "byte", "else", "import", "public", "throws", "catch", "extends", "int", "short", "try",
			"char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native",
			"super", "while"));

	/**
	 * Transform the specified <code>value</code> to be used as a java variable name. <br>
	 * Basically, it will remove all special/whiteSpace characters, ensure it starts with a lower case and ensure it doesn't start with a
	 * number.
	 * 
	 * @param value
	 *            the string to transform, can be null, in such case null is returned
	 * @return the transformed string
	 */
	public static String getVariableName(String value) {
		return getJavaName(value, true);
	}

	/**
	 * Transform the specified <code>value</code> to be used as a java constant name. <br>
	 * Basically, it will remove all special/whiteSpace characters, ensure it doesn't start with a number and transform it to upper case.
	 * 
	 * @param value
	 *            the string to transform, can be null, in such case null is returned
	 * @return the transformed string
	 */
	public static String getConstantName(String name) {
		return name == null ? name : getJavaName(name, false).toUpperCase();
	}

	/**
	 * Transform the specified <code>value</code> to be used as a java class name. <br>
	 * Basically, it will remove all special/whiteSpace characters, ensure it starts with a upper case and ensure it doesn't start with a
	 * number.
	 * 
	 * @param value
	 *            the string to transform, can be null, in such case null is returned
	 * @return the transformed string
	 */
	public static String getClassName(String value) {
		return getJavaName(value, false);
	}

	/**
	 * Transform the specified <code>value</code> to be used as a java package name. <br>
	 * Basically, it will remove all special/whiteSpace characters (except .), ensure it contains only lower case and ensure each part
	 * doesn't start with a number.
	 * 
	 * @param value
	 *            the string to transform, can be null, in such case null is returned
	 * @return the transformed string
	 */
	public static String getPackageName(String value) {
		if (value == null) {
			return null;
		}
		if (value.length() == 0) {
			return value;
		}

		StringBuilder sb = new StringBuilder();
		for (String s : value.split("\\.")) {
			String part = getVariableName(s).toLowerCase();
			if (sb.length() > 0 && part.length() > 0) {
				sb.append(".");
			}
			sb.append(part);
		}

		if (sb.length() == 0) {
			return "_";
		}
		return sb.toString();
	}

	/**
	 * Transform the specified <code>value</code> to be used as a java string. <br>
	 * Basically, it will replace all breaking line by \n and add a \ before all escapable characters (", \, ...).
	 * 
	 * @param value
	 *            the string to transform, can be null, in such case null is returned
	 * @return the transformed string
	 */
	public static String getJavaString(String value) {
		if (value == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		Matcher m = JAVA_CHAR_TO_ESCAPE_IN_STRINGS_PATTERN.matcher(value);
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

	/**
	 * Transform the specified <code>value</code> to be used as javadoc. <br>
	 * 
	 * @param value
	 * @return the transformed string
	 */
	public static String getJavaDocString(String value) {
		if (value == null) {
			return "";
		}
		value = ToolBox.replaceStringByStringInString("*/", "* /", value);
		StringTokenizer st = new StringTokenizer(value, StringUtils.LINE_SEPARATOR, false);
		StringBuilder sb = new StringBuilder();
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			if (str.length() > 0) {
				sb.append(str.trim());
				sb.append(StringUtils.LINE_SEPARATOR);
				if (st.hasMoreTokens()) {
					sb.append("    * ");
				}
			}
		}
		return sb.toString();
	}

	private static String getJavaName(String name, boolean lowerFirstChar) {
		if (name == null) {
			return null;
		}
		if (name.length() == 0) {
			return name;
		}
		name = StringUtils.camelCase(StringUtils.convertAccents(name), !lowerFirstChar);
		StringBuffer sb = new StringBuffer();
		Matcher m = JAVA_VARIABLE_ACCEPTABLE_PATTERN.matcher(name);
		while (m.find()) {
			String group = m.group();
			if (sb.length() == 0 && !group.matches(JAVA_BEGIN_VARIABLE_NAME_REGEXP)) {
				sb.append('_');
			}
			sb.append(group);
		}
		name = sb.toString();
		if (name.length() == 0) {
			return "_";
		}
		if (JAVA_RESERVED_KEYWORDS.contains(name)) {
			return "_" + name;
		}
		return name;
	}
}
