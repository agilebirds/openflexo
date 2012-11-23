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
package org.openflexo.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import org.openflexo.toolbox.ToolBox;

/**
 * Utility class used to format logs of Flexo
 * 
 * @author sguerin
 */
public class FlexoLoggingFormatter extends Formatter {

	private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss,SSS");

	public static boolean logDate = true;

	@Override
	public String format(LogRecord log) {
		StringBuffer sb = new StringBuffer();
		sb.append(formatString(logDate ? 30 : 10, log.getLevel().toString()
				+ (logDate ? " " + dateFormat.format(new Date(log.getMillis())) : "")));
		sb.append(formatString(100, log.getMessage()));
		sb.append(formatString(50, "[" + log.getSourceClassName() + "." + log.getSourceMethodName() + "]"));
		sb.append("\n");
		if (log.getThrown() != null) {
			sb.append(ToolBox.stackTraceAsAString(log.getThrown()));
		}
		return sb.toString();
	}

	public static String formatString(int cols, String aString) {
		char[] blank;
		if (aString == null) {
			aString = "null";
		}
		if (cols > aString.length()) {
			blank = new char[cols - aString.length()];
			for (int i = 0; i < cols - aString.length(); i++) {
				blank[i] = ' ';
			}
			return aString + new String(blank);
		} else {
			return aString;
		}
	}

}
