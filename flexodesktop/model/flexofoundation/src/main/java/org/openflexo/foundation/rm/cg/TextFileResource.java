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
package org.openflexo.foundation.rm.cg;

import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.generator.GeneratedCodeResult;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.toolbox.FileFormat;

/**
 * @author sylvain
 * 
 */
public class TextFileResource<G extends IFlexoResourceGenerator, F extends CGFile> extends ASCIIFileResource<G, F> {

	private ResourceType _fileType;
	private FileFormat _format;

	/**
	 * @param builder
	 */
	public TextFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	/**
	 * @param aProject
	 */
	public TextFileResource(FlexoProject aProject) {
		super(aProject);
	}

	/**
	 * Overrides getResourceType
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#getResourceType()
	 */
	@Override
	public ResourceType getResourceType() {
		return ResourceType.TEXT_FILE;
	}

	@Override
	protected TextFile createGeneratedResourceData() {
		return new TextFile(getFile());
	}

	public TextFile getTextFile() {
		return (TextFile) getGeneratedResourceData();
	}

	public ResourceType getFileType() {
		return _fileType;
	}

	public void setFileType(ResourceType fileType) {
		_fileType = fileType;
	}

	@Override
	public FileFormat getResourceFormat() {
		return _format;
	}

	public void setResourceFormat(FileFormat format) {
		_format = format;
	}

	@Override
	public String getGenerationResultKey() {
		return GeneratedCodeResult.DEFAULT_KEY;
	}

}
