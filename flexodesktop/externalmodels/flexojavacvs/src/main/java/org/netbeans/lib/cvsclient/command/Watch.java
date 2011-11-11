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

/**
 * @author Thomas Singer
 * @version Dec 2, 2001
 */
public class Watch {
	public static final Watch EDIT = new Watch("edit", "E", // NOI18N
			new String[] { "edit" }); // NOI18N

	public static final Watch UNEDIT = new Watch("unedit", "U", // NOI18N
			new String[] { "unedit" }); // NOI18N

	public static final Watch COMMIT = new Watch("commit", "C", // NOI18N
			new String[] { "commit" }); // NOI18N

	public static final Watch ALL = new Watch("all", "EUC", // NOI18N
			new String[] { "edit", "unedit", "commit" }); // NOI18N

	public static final Watch NONE = new Watch("none", "", // NOI18N
			new String[0]);

	/**
	 * Returns the temporary watch value used in the Notify request.
	 */
	public static String getWatchString(Watch watch) {
		if (watch == null) {
			return NONE.getValue();
		}
		return watch.getValue();
	}

	private final String name;
	private final String value;
	private final String[] arguments;

	private Watch(String name, String value, String[] arguments) {
		this.name = name;
		this.value = value;
		this.arguments = arguments;
	}

	public String[] getArguments() {
		return arguments;
	}

	@Override
	public String toString() {
		return name;
	}

	private String getValue() {
		return value;
	}
}
