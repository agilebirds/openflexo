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

import java.io.File;

/**
 * Simple utility class to instanciate existing file from a relative path name, in the context of Flexo. Directories search order is defined
 * here.
 * 
 * @author sguerin
 * 
 */
public class FileResource extends File {

	private String internalPath;

	public FileResource(String relativePathName) {
		super(ResourceLocator.locateFile(relativePathName).getAbsolutePath());
		internalPath = relativePathName;
	}

	public String getInternalPath() {
		return internalPath;
	}

}
