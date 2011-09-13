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
package org.openflexo.generator.file;

import java.io.File;
import java.io.IOException;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoGeneratedResource;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.GeneratedResourceData;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.generator.rm.FlexoCopyOfFileResource;
import org.openflexo.generator.rm.GenerationAvailableFile;
import org.openflexo.toolbox.FileUtils;


public class CGPackagedResourceFile extends AbstractCGFile implements GenerationAvailableFile, GeneratedResourceData {

	public CGPackagedResourceFile(GeneratedCodeBuilder builder)
    {
        super(builder.generatedCode);
     }
    
    public CGPackagedResourceFile(GeneratedOutput generatedCode)
    {
        super(generatedCode);  
    }

    public CGPackagedResourceFile(GenerationRepository repository, CGRepositoryFileResource resource)
    {
        super(repository.getGeneratedCode());
        setResource(resource);
    }

	@Override
	public void generate() throws FlexoException {
		getGenerator().generate(false);
	}

	@Override
	public void regenerate() throws FlexoException {
		getGenerator().generate(true);
	}

	@Override
	public void writeToFile(File file) throws FlexoException {
		if (getFlexoResource() instanceof FlexoCopyOfFileResource) {
			try {
				FileUtils.copyFileToFile(((FlexoCopyOfFileResource) getFlexoResource()).getResourceToCopy(), file);
			} catch (IOException e) {
				throw new FlexoException(e.getMessage(), e);
			}
		}
	}

	@Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
		super.setResource((CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>) resource);
	}
	
	@Override
	public boolean hasGenerationErrors()
	{
		return false;
	}

	@Override
	public FlexoGeneratedResource getFlexoResource() {
		return super.getResource();
	}

}
