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
package cb.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import cb.petal.LogicalCategory;
import cb.petal.PetalNode;
import cb.petal.UseCaseCategory;

/**
 * Global constants and utility methods.
 * 
 * @version $Id: Constants.java,v 1.2 2011/09/12 11:47:29 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public abstract class Constants {
	public static final int TAB = 4; // Default number of indendation chars
	public static final String INDENT = "    ";

	private static boolean DOS_MODE = true;
	public static final String DOS = "\r\n";
	public static final String UNIX = "\n";

	public static final String VERSION = "1.0";

	public static final boolean isDOS() {
		return java.io.File.separatorChar == '\\'; // heuristic check
	}

	public static void setMode(boolean DOS) {
		Constants.DOS_MODE = DOS;
	}

	public static String getNewLine() {
		return DOS_MODE ? DOS : UNIX;
	}

	public static String makeName(String name) {
		return makeName(name, Collections.EMPTY_LIST, null);
	}

	/**
	 * Convert a Rose name (which may contains white space, e.g.) to a Java name.
	 */
	public static String makeName(String name, java.util.List params, PetalNode parent) {
		if (name == null) {
			return null;
		}

		char[] ch = name.toCharArray();
		StringBuffer buf = new StringBuffer();
		boolean underscore = true;

		for (int i = 0; i < ch.length; i++) {
			switch (ch[i]) {
			case '_':
			case ' ':
				underscore = true;
				break;

			default:
				if (underscore) {
					buf.append(Character.toUpperCase(ch[i]));
				} else {
					buf.append(ch[i]);
				}

				underscore = false;
				break;
			}
		}

		String ret = buf.toString();

		if (ret.equals("ClassCategory")) { // Find more specific class
			String obj = (String) params.get(0);

			if (obj.equals("Use Case View") || parent instanceof UseCaseCategory) {
				return "UseCaseCategory";
			} else if (obj.equals("Logical View") || parent instanceof LogicalCategory) {
				return "LogicalCategory";
			} else {
				return ret;
			}
		} else {
			return ret;
		}
	}

	private static HashSet primitive = new HashSet(
			Arrays.asList(new String[] { "int", "double", "long", "char", "byte", "float", "short" }));

	public static String getValueForType(String t) {
		if (primitive.contains(t)) {
			return "(" + t + ")0";
		} else if (t.equals("boolean")) {
			return "false";
		} else {
			return "null";
		}
	}
}
