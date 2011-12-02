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

import java.io.File;

/**
 * Describes checkout/tag/update information for a file. The fields in instances of this object are populated by response handlers.
 * 
 * @author Thomas Singer
 */
public class DefaultFileInfoContainer extends FileInfoContainer {

	public static final String PERTINENT_STATE = "Y"; // NOI18N
	public static final String MERGED_FILE = "G"; // NOI18N
	private File file;

	private String type;

	public DefaultFileInfoContainer() {
	}

	/**
	 * Returns the associated file.
	 */
	@Override
	public File getFile() {
		// Bidouille en attendant
		if (file != null && file.getAbsolutePath().indexOf("ate: ") > 0) {
			System.out.println("Replace " + file.getAbsolutePath());
			file = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().indexOf("ate: "))
					+ file.getAbsolutePath().substring(file.getAbsolutePath().indexOf("ate: ") + 5));
			System.out.println("By " + file.getAbsolutePath());
		}
		return file;
	}

	/**
	 * Returns true if the associated file is a directory.
	 */
	public boolean isDirectory() {
		File file = getFile();
		if (file == null) {
			return false;
		}
		return file.isDirectory();
	}

	/**
	 * Sets the associated file.
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Returns the type. Mostly the type value equals to the states returned by update and tag command. see description in cvs manual. Some
	 * states are added: G - file was merged (when using the cvs update -j <rev> <file> command. D - file was deleted - no longer pertinent.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Return a string representation of this object. Useful for debugging.
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(type);
		buffer.append("  "); // NOI18N
		if (isDirectory()) {
			buffer.append("Directory "); // NOI18N
		}
		buffer.append(file != null ? file.getAbsolutePath() : "null"); // NOI18N
		return buffer.toString();
	}
}
