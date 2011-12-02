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
package org.netbeans.lib.cvsclient.file;

import java.io.File;

/**
 * Implements the CVS concept of File Modes (permissions).
 * 
 * @author Robert Greig
 */
public class FileMode {
	/**
	 * The underlying file
	 */
	private File file;

	/**
	 * Construct a new file mode from a file.
	 */
	public FileMode(File file) {
		this.file = file;
	}

	/**
	 * Returns a CVS-compatible file mode string
	 */
	@Override
	public String toString() {
		// TODO: really implement this!
		return "u=rw,g=r,o=r"; // NOI18N
	}
}