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
package org.openflexo.foundation.rm;

import org.openflexo.foundation.FlexoException;

/**
 * Thrown when an exception was raised during resource loading
 * 
 * @author sguerin
 * 
 */
public abstract class LoadResourceException extends FlexoException {

	@Deprecated
	protected FlexoFileResource<?> deprecatedFileResource;
	protected org.openflexo.foundation.resource.FlexoFileResource<?> fileResource;

	@Deprecated
	public LoadResourceException(FlexoFileResource<?> fileResource, String message) {
		super(message);
		deprecatedFileResource = fileResource;
	}

	public LoadResourceException(org.openflexo.foundation.resource.FlexoFileResource<?> fileResource, String message) {
		super(message);
		this.fileResource = fileResource;
	}

	@Override
	public String getMessage() {
		String msg = super.getMessage();
		return "LoadResourceException: resource "
				+ (deprecatedFileResource != null ? deprecatedFileResource.getResourceIdentifier() : fileResource.getURI())
				+ (msg != null ? "\n" + msg : "");
	}

	@Deprecated
	public FlexoFileResource<?> getDeprecatedFileResource() {
		return deprecatedFileResource;
	}

	public org.openflexo.foundation.resource.FlexoFileResource<?> getFileResource() {
		return fileResource;
	}
}
