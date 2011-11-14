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
package org.openflexo.foundation.cg.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.toolbox.ToolBox;

/**
 * Some utilities used in the context of Code Generator
 * 
 * @author bmangez
 */
public class GeneratorUtils {

	public static final String OUTPUT_FILES_ENCODING = "UTF-8";

	public static final String CONTENT_TAG = "<_CONTENT_>";

	public static final String GEN_IF = "GEN_IF";

	public static final String BAD_CHARS_IN_WOD_VALUE_REGEXP = "[ \"\\\\]+";

	public static final Pattern BAD_CHARS_IN_WOD_VALUE_PATTERN = Pattern.compile(BAD_CHARS_IN_WOD_VALUE_REGEXP);

	public static final String JAVA_CHAR_TO_ESCAPE_IN_STRINGS_REG_EXP = "[\"\\\\]";

	public static final Pattern JAVA_CHAR_TO_ESCAPE_IN_STRINGS_PATTERN = Pattern.compile(JAVA_CHAR_TO_ESCAPE_IN_STRINGS_REG_EXP);

	/**
	 * @deprecated use {@link ToolBox} convertStringToJavaString
	 * @param stringToConvert
	 * @return
	 */
	@Deprecated
	public static String convertStringToJavaString(String stringToConvert) {
		return ToolBox.convertStringToJavaString(stringToConvert);

	}

	public static String beforeContentHtml(String html) {
		if (html == null || html.equals("")) {
			return "";
		}
		int tagIndex = html.indexOf(CONTENT_TAG);
		if (tagIndex != -1) {
			return html.substring(0, tagIndex);
		} else {
			return html;
		}
	}

	public static String afterContentHtml(String html) {
		if (html == null || html.equals("")) {
			return "";
		}
		int tagIndex = html.indexOf(CONTENT_TAG);
		if (tagIndex != -1) {
			return html.substring(tagIndex + CONTENT_TAG.length());
		} else {
			return "";
		}
	}

	/**
	 * @deprecated use ToolBox.getJavaName(name)
	 * @param name
	 * @return a java name ( starts with a minuscule, and no blanks, dot,..., convert accentuated characters)
	 */
	@Deprecated
	public static String getJavaName(String name) {
		return ToolBox.getJavaName(name);
	}

	public static String getWodValueName(String s) {
		if (s == null) {
			return "";
		}
		Matcher m = BAD_CHARS_IN_WOD_VALUE_PATTERN.matcher(s);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
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

	public static String getJavascriptComment2(String comment) {
		if (comment == null) {
			return null;
		}
		return ToolBox.replaceStringByStringInString("\r", " ",
				ToolBox.replaceStringByStringInString("\n", " ", ToolBox.replaceStringByStringInString("\"", " ", comment)));
	}

	public static String evaluateConditions(String value, Map replacement) {
		if (value == null) {
			return null;
		}
		String condBeginString = "<" + GEN_IF + "(";
		int condBegin = value.indexOf(condBeginString);
		if (condBegin != -1) {
			boolean condition = false;
			String conditionKey = value.substring(condBegin + condBeginString.length(),
					value.indexOf(")", condBegin + condBeginString.length()));
			int contentBegin = condBegin + condBeginString.length() + conditionKey.length() + 2;
			int contentEnd = value.indexOf("</" + GEN_IF + "(" + conditionKey + ")>");
			int condEnd = contentEnd + 2 + GEN_IF.length() + 1 + conditionKey.length() + 2;

			StringTokenizer stk = new StringTokenizer(conditionKey, "|");
			while (stk.hasMoreTokens()) {
				String orToken = stk.nextToken();

				boolean negate = orToken.startsWith("!");
				Object conditionValue;
				if (negate) {
					conditionValue = replacement.get("<" + orToken.substring(1) + ">");
				} else {
					conditionValue = replacement.get("<" + orToken + ">");
				}

				boolean orCondition = conditionValue != null && !conditionValue.toString().equals("");
				if (negate) {
					orCondition = !orCondition;
				}

				if (orCondition) {
					condition = true;
					break;
				}
			}

			StringBuffer clean = new StringBuffer(value.length());
			String before = value.substring(0, condBegin);

			// 1. before condition
			// New line before <GEN_IF is removed
			if (before.endsWith("\n\r")) {
				before = before.substring(0, before.length() - 2);
			} else if (before.endsWith("\r\n")) {
				before = before.substring(0, before.length() - 2);
			} else if (before.endsWith("\r")) {
				before = before.substring(0, before.length() - 1);
			} else if (before.endsWith("\n")) {
				before = before.substring(0, before.length() - 1);
			}

			clean.append(before);

			// 2. between condition
			if (condition) {
				String content = value.substring(contentBegin, contentEnd);
				// New line before </GEN_IF is removed
				if (content.endsWith("\n\r")) {
					content = content.substring(0, content.length() - 2);
				} else if (content.endsWith("\r\n")) {
					content = content.substring(0, content.length() - 2);
				} else if (content.endsWith("\r")) {
					content = content.substring(0, content.length() - 1);
				} else if (content.endsWith("\n")) {
					content = content.substring(0, content.length() - 1);
				}

				clean.append(content);
			}

			// 3. after condition
			clean.append(value.substring(condEnd));

			return evaluateConditions(clean.toString(), replacement);
		} else {
			return value;
		}
	}

	public static String[] getStringBetweenTags(String string, String tag) {
		String resp = "";
		if (string != null) {
			String beginTag = "<" + tag + ">";
			String endTag = "</" + tag + ">";
			int begin = string.indexOf(beginTag);
			if (begin != -1) {
				int end = string.indexOf(endTag, begin);
				if (end != -1) {
					resp = string.substring(begin + beginTag.length(), end);

					String before = string.substring(0, begin);
					if (before.endsWith("\n\r") || before.endsWith("\r\n")) {
						before = before.substring(0, before.length() - 2);
					} else if (before.endsWith("\r") || before.endsWith("\n")) {
						before = before.substring(0, before.length() - 1);
					}

					String after = string.substring(end + endTag.length());
					if (after.endsWith("\n\r") || after.endsWith("\r\n")) {
						after = after.substring(0, after.length() - 2);
					} else if (after.endsWith("\r") || after.endsWith("\n")) {
						after = after.substring(0, after.length() - 1);
					}

					string = before + after;
				}
			}
		}
		return new String[] { string, resp };
	}

	/**
	 * 
	 * Remove the code generator comments. A code generator comment is a line starting with ##.
	 * 
	 * @param template
	 * @return the template, with no code generator comments
	 */
	public static String removeComments(String template) {
		int i = template.indexOf("##");
		while (i != -1) {
			int endOfLineN = template.indexOf("\n", i);
			int endOfLineR = template.indexOf("\r", i);
			int endOfLine;
			if (endOfLineN == -1 && endOfLineR == -1) {
				endOfLine = template.length();
			} else if (endOfLineN == -1 && endOfLineR != -1) {
				endOfLine = endOfLineR;
			} else if (endOfLineN != -1 && endOfLineR == -1) {
				endOfLine = endOfLineN;
			} else {
				endOfLine = Math.min(endOfLineR, endOfLineN);
				if (endOfLine + 1 == Math.max(endOfLineR, endOfLineN)) {
					endOfLine = endOfLine + 1;
				}
			}
			StringBuffer buf = new StringBuffer(template.length());
			buf.append(template.substring(0, i)).append(template.substring(endOfLine + 1));
			template = buf.toString();
			i = template.indexOf("##");
		}
		return template;
	}

	/*
	 * @deprecated public static String generateSimpleRepetition(Vector
	 *             list,String templateName,Properties replacement){ String
	 *             answer = ""; Enumeration en = list.elements();
	 *             while(en.hasMoreElements()){ replacement.put("<ITEM>",en.nextElement());
	 *             answer=answer+generateCode(templateName,replacement); }
	 *             return answer; }
	 */

	public static File saveToFile(String fileName, String fileCode, File dir, String fileExtention) {
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileOutputStream fos = null;
		try {
			File dest = new File(dir.getAbsolutePath() + File.separator + getFileName(fileName, fileExtention));
			dest.createNewFile();
			byte[] b;
			if (fileCode != null) {
				b = fileCode.getBytes(OUTPUT_FILES_ENCODING);
			} else {
				b = new byte[0];
			}
			fos = new FileOutputStream(dest);
			fos.write(b);
			return dest;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getFileName(String fileName, String fileExtention) {
		if (fileExtention != null) {
			return fileName + "." + fileExtention;
		} else {
			return fileName;
		}
	}

	public static String defaultWOO() {
		return "" + "{\n" + "        \"WebObjects Release\" = \"WebObjects 5.0\";\n" + "        encoding = NSUTF8StringEncoding;\n"
				+ "        variables = {};\n" + "}";
	}

	public static void writeWOComponentFiles(File woComponentDirectory, File javaSrcDirectory, String woComponentName, String javaCode,
			String apiCode, String htmlCode, String wodCode, String wooCode) {
		woComponentDirectory.mkdirs();
		File woDir = null;
		if (htmlCode != null || wodCode != null || wooCode != null) {
			woDir = new File(woComponentDirectory, woComponentName + ".wo");
		}
		if (woDir != null && !woDir.exists()) {
			woDir.mkdir();
		}
		try {
			if (javaCode != null) {
				saveToFile(woComponentName, javaCode, javaSrcDirectory == null ? woComponentDirectory : javaSrcDirectory, "java");
			}
			if (apiCode != null) {
				saveToFile(woComponentName, apiCode, woComponentDirectory, "api");
			}
			if (htmlCode != null) {
				saveToFile(woComponentName, htmlCode, woDir, "html");
			}
			if (wodCode != null) {
				saveToFile(woComponentName, wodCode, woDir, "wod");
			}
			if (wooCode != null) {
				saveToFile(woComponentName, wooCode, woDir, "woo");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String stringFromFile(File f) {
		try {
			FileInputStream fis = new FileInputStream(f);
			StringBuffer buf = new StringBuffer();
			while (fis.available() > 0) {
				byte[] b = new byte[fis.available()];
				fis.read(b);
				buf.append(new String(b));
			}
			fis.close();
			return buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns true if ch matches [A-Za-z] http://www.asciitable.com
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isAlpha(char ch) {
		// returns true if ch matches [A-Za-z]
		return ((ch > (char) 64 && ch < (char) 91) || (ch > (char) 96 && ch < (char) 123));
	}

	// converts the first character of the string to uppercase, or the second
	// one if the first is not alphabetic (eg an underscore)
	public static String capitalize(String str) {
		if (isAlpha(str.charAt(0))) {
			return str.substring(0, 1).toUpperCase() + str.substring(1);
		} else {
			// if starts with '_' for example, capitalize 2nd character
			return str.charAt(0) + str.substring(1, 2).toUpperCase() + str.substring(2);
		}
	}

	/**
	 * Replace any "." by "_" in the accessor.
	 * 
	 * @param accessorValue
	 * @return an accessor name, with no "."
	 */
	public static String accessorName(String accessorValue) {
		return ToolBox.replaceStringByStringInString(".", "_", accessorValue);
	}

	public static String dateString(Date timestamp) {
		if (timestamp == null) {
			return null;
		}

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(timestamp);
		int year = cal.get(Calendar.YEAR);
		int monthIndex = cal.get(Calendar.MONTH);
		// log("day of week: "+ dayOfWeek);
		int day = cal.get(Calendar.DAY_OF_MONTH);

		String dayString = String.valueOf(day);
		String twoCharsDayString = (day > 9 ? String.valueOf(dayString) : "0" + String.valueOf(dayString));
		String monthString = (monthIndex + 1 > 9 ? String.valueOf(monthIndex + 1) : "0" + String.valueOf(monthIndex + 1));
		String yearString = String.valueOf(year);
		String fourCharsYearString = (year < 1000 ? (year < 100 ? (year < 10 ? "000" + yearString : "00" + yearString) : "0" + yearString)
				: yearString);

		return twoCharsDayString + "/" + monthString + "/" + fourCharsYearString;
	}

	/**
	 * Returns a String with only characters [a-Z] or [0-9] or defaultChar.
	 * 
	 * @param string
	 * @param defaultChar
	 * @return a String with only characters [a-Z] or [0-9] or defaultChar.
	 */
	public static String stringToRestrictedCase(String string, char defaultChar) {
		char[] array = string.toCharArray();
		char[] newArray = new char[string.length()];
		for (int i = 0; i < string.length(); i++) {
			char c = array[i];
			String ch = ("" + c).toLowerCase();
			boolean isLower = ("" + c).equals(ch);

			if (ch.equals("a") || ch.equals("�") || ch.equals("�") || ch.equals("�") || ch.equals("�")) {
				newArray[i] = (isLower ? 'a' : 'A');
			} else if (ch.equals("b")) {
				newArray[i] = (isLower ? 'b' : 'B');
			} else if (ch.equals("c") || ch.equals("�")) {
				newArray[i] = (isLower ? 'c' : 'C');
			} else if (ch.equals("d")) {
				newArray[i] = (isLower ? 'd' : 'D');
			} else if (ch.equals("e") || ch.equals("�") || ch.equals("�") || ch.equals("�") || ch.equals("�")) {
				newArray[i] = (isLower ? 'e' : 'E');
			} else if (ch.equals("f")) {
				newArray[i] = (isLower ? 'f' : 'F');
			} else if (ch.equals("g")) {
				newArray[i] = (isLower ? 'g' : 'G');
			} else if (ch.equals("h")) {
				newArray[i] = (isLower ? 'h' : 'H');
			} else if (ch.equals("i") || ch.equals("�") || ch.equals("�") || ch.equals("�") || ch.equals("�")) {
				newArray[i] = (isLower ? 'i' : 'I');
			} else if (ch.equals("j")) {
				newArray[i] = (isLower ? 'j' : 'J');
			} else if (ch.equals("k")) {
				newArray[i] = (isLower ? 'k' : 'K');
			} else if (ch.equals("l")) {
				newArray[i] = (isLower ? 'l' : 'L');
			} else if (ch.equals("m")) {
				newArray[i] = (isLower ? 'm' : 'M');
			} else if (ch.equals("n")) {
				newArray[i] = (isLower ? 'n' : 'N');
			} else if (ch.equals("o") || ch.equals("�") || ch.equals("�") || ch.equals("�") || ch.equals("�") || ch.equals("�")
					|| ch.equals("�")) {
				newArray[i] = (isLower ? 'o' : 'O');
			} else if (ch.equals("p")) {
				newArray[i] = (isLower ? 'p' : 'P');
			} else if (ch.equals("q")) {
				newArray[i] = (isLower ? 'q' : 'Q');
			} else if (ch.equals("r")) {
				newArray[i] = (isLower ? 'r' : 'R');
			} else if (ch.equals("s")) {
				newArray[i] = (isLower ? 's' : 'S');
			} else if (ch.equals("t")) {
				newArray[i] = (isLower ? 't' : 'T');
			} else if (ch.equals("u") || ch.equals("�") || ch.equals("�") || ch.equals("�")) {
				newArray[i] = (isLower ? 'u' : 'U');
			} else if (ch.equals("v")) {
				newArray[i] = (isLower ? 'v' : 'V');
			} else if (ch.equals("w")) {
				newArray[i] = (isLower ? 'w' : 'W');
			} else if (ch.equals("x")) {
				newArray[i] = (isLower ? 'x' : 'X');
			} else if (ch.equals("y")) {
				newArray[i] = (isLower ? 'y' : 'Y');
			} else if (ch.equals("z")) {
				newArray[i] = (isLower ? 'z' : 'Z');
			} else if (ch.equals("0")) {
				newArray[i] = '0';
			} else if (ch.equals("1")) {
				newArray[i] = '1';
			} else if (ch.equals("2")) {
				newArray[i] = '2';
			} else if (ch.equals("3")) {
				newArray[i] = '3';
			} else if (ch.equals("4")) {
				newArray[i] = '4';
			} else if (ch.equals("5")) {
				newArray[i] = '5';
			} else if (ch.equals("6")) {
				newArray[i] = '6';
			} else if (ch.equals("7")) {
				newArray[i] = '7';
			} else if (ch.equals("8")) {
				newArray[i] = '8';
			} else if (ch.equals("9")) {
				newArray[i] = '9';
			} else {
				newArray[i] = defaultChar;
			}
		}
		StringBuffer newString = new StringBuffer();
		StringTokenizer tokens = new StringTokenizer(new String(newArray), new String(new char[] { defaultChar }));
		if (tokens.hasMoreTokens()) {
			newString.append(tokens.nextToken());
		}
		while (tokens.hasMoreTokens()) {
			newString.append(new String(new char[] { defaultChar })).append(tokens.nextToken());
		}
		return newString.toString();
	}

	public static String getHiddenValueFieldName(OperationNode targetNode, ActionNode startNode) {
		return getHiddenValueAccessorName(targetNode, startNode).toUpperCase();
	}

	public static String getHiddenValueAccessorName(OperationNode targetNode, ActionNode startNode) {
		return ToolBox.getJavaName(new StringBuffer(targetNode.getName()).append("_FROM_").append(startNode.getName()).append("_")
				.append(startNode.getFlexoID()));
	}

	public static String nameForRepositoryAndIdentifier(GenerationRepository repository, String identifier) {
		return repository.getName() + "." + identifier;
	}
}
