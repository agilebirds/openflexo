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
package org.openflexo.foundation.resource;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author gpolet
 * 
 */
@SuppressWarnings("serial")
public class InvalidFileNameException extends FlexoException {

	private String relativePath;

	private FlexoProjectFile file;

	/**
	 * @param relativePath
	 */
	public InvalidFileNameException(String relativePath) {
		this.relativePath = relativePath;
	}

	public InvalidFileNameException(FlexoProjectFile file) {
		this.file = file;
		if (file != null) {
			this.relativePath = file.getRelativePath();
		}
	}

	public String getRelativePath() {
		return relativePath;
	}

	public FlexoProjectFile getFile() {
		return file;
	}

	@Override
	public String getMessage() {
		return getLocalizedMessage();
	}

	/**
	 * Overrides getLocalizedMessage
	 * 
	 * @see org.openflexo.foundation.FlexoException#getLocalizedMessage()
	 */
	@Override
	public String getLocalizedMessage() {
		return FlexoLocalization.localizedForKey("invalid_file_name") + " " + getRelativePath() != null ? getRelativePath()
				: " null relative path";
	}
}
