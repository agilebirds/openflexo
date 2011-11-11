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

import java.util.List;

/**
 * @author Thomas Singer
 */
public class CommandUtils {
	/**
	 * Returns the directory relative to local path from the specified message. This method returns null, if the specified message isn't a
	 * EXAM_DIR- message.
	 */
	public static String getExaminedDirectory(String message, String examDirPattern) {
		final int index = message.indexOf(examDirPattern);
		final int startIndex = index + examDirPattern.length() + 1;
		if (index < 0 || message.length() < startIndex + 1) {
			return null;
		}

		return message.substring(startIndex);
	}

	/**
	 * for a list of string will return the string that equals the name parameter. To be used everywhere you need to have only one string
	 * occupying teh memory space, eg. in Builders to have the revision number strings not repeatedly in memory.
	 */
	public static String findUniqueString(String name, List list) {
		if (name == null) {
			return null;
		}
		int index = list.indexOf(name);
		if (index >= 0) {
			return (String) list.get(index);
		} else {
			String newName = new String(name);
			list.add(newName);
			return newName;
		}
	}
}
