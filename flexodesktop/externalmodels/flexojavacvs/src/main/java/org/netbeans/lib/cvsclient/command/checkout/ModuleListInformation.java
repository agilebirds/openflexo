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

package org.netbeans.lib.cvsclient.command.checkout;

import java.io.File;

import org.netbeans.lib.cvsclient.command.FileInfoContainer;

/**
 * Object containing information about various modules defined in the repository. Is parsed from the output of cvs checkout -c and cvs
 * checkout -s.
 * 
 * @author Milos Kleint
 */
public class ModuleListInformation extends FileInfoContainer {

	private String moduleName;

	private String moduleStatus;

	private final StringBuffer paths = new StringBuffer();

	private String type;

	public ModuleListInformation() {
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleStatus() {
		return moduleStatus;
	}

	public void setModuleStatus(String moduleStatus) {
		this.moduleStatus = moduleStatus;
	}

	public String getPaths() {
		return paths.toString();
	}

	public void addPath(String path) {
		if (paths.length() > 0) {
			paths.append(' ');
		}
		paths.append(path);
	}

	@Override
	public File getFile() {
		return null;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
