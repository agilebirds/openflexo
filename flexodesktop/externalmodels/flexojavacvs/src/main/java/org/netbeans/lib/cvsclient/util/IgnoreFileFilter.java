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
package org.netbeans.lib.cvsclient.util;

import java.io.File;

/**
 * interface for recognizing if the local files are to be ignored. Implements the functionality of the .cvsignore files..
 * 
 * @author Milos Kleint
 */
public interface IgnoreFileFilter {

	/**
	 * A file is checked against the patterns in the filter. If any of these matches, the file should be ignored.
	 * 
	 * @param directory
	 *            is a file object that refers to the directory the file resides in.
	 * @param noneCvsFile
	 *            is the name of the file to be checked.
	 */
	boolean shouldBeIgnored(File directory, String noneCvsFile);
}
