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
package org.openflexo.generator.rm;

import java.io.File;
import java.util.Date;

import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.cg.generator.IGenerationException;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.GenerationAvailableFileResourceInterface;
import org.openflexo.toolbox.FileFormat;


public interface GenerationAvailableFileResource extends GenerationAvailableFileResourceInterface {

	public File getFile();
	
	public CGFile getCGFile(); 

    public GenerationAvailableFile getGeneratedResourceData();

	public abstract IFlexoResourceGenerator getGenerator();

	public boolean isCodeGenerationAvailable();

	public IGenerationException getGenerationException();
	
	public Date getLastUpdate();

	public Date getDiskLastModifiedDate();

	public ResourceType getResourceType();

	public FileFormat getResourceFormat();

}
