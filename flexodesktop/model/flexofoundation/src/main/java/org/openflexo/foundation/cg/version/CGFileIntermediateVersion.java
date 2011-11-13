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
package org.openflexo.foundation.cg.version;

import java.io.File;
import java.util.Date;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.toolbox.FileUtils;

public class CGFileIntermediateVersion extends AbstractCGFileVersion {

	/**
	 * Default constructor
	 */
	public CGFileIntermediateVersion(CGFile cgFile, CGVersionIdentifier versionId, File file) {
		super(cgFile, versionId, file);
	}

	@Override
	public String getClassNameKey() {
		return "file_intermediate_version";
	}

	@Override
	public String getInspectorName() {
		return Inspectors.GENERATORS.FILE_INTERMEDIATE_VERSION_INSPECTOR;
	}

	@Override
	public boolean isContainedIn(CGObject obj) {
		if (obj == getCGFile())
			return true;
		return getCGFile().isContainedIn(obj);
	}

	@Override
	public String getName() {
		return getVersionId().toString();
	}

	@Override
	public String getDescription() {
		return "-";
	}

	private Date date = null;

	@Override
	public Date getDate() {
		if (date == null) {
			date = FileUtils.getDiskLastModifiedDate(getFile());
		}
		return date;
	}

	@Override
	public String getUserId() {
		return "-";
	}

	@Override
	public String getStringRepresentation() {
		return getVersionId().major + "." + getVersionId().minor + "." + getVersionId().patch;
	}

}
