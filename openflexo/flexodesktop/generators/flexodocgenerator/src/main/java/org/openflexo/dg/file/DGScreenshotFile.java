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
package org.openflexo.dg.file;

import java.util.Date;

import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.FlexoCopiedResource;
import org.openflexo.foundation.rm.ScreenshotResource;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.generator.file.AbstractCGFile;


/**
 * @author gpolet
 *
 */
public class DGScreenshotFile extends AbstractCGFile
{

    /**
     * @param repository
     * @param resource
     */
    public DGScreenshotFile(DGRepository repository, CGRepositoryFileResource resource)
    {
        super(repository, resource);
    }

    /**
     * 
     */
    public DGScreenshotFile(GeneratedOutput generatedDoc)
    {
        super(generatedDoc);
    }
    
    /**
     * 
     */
    public DGScreenshotFile(GeneratedCodeBuilder builder)
    {
        this(builder.generatedCode);
    }
    
    /**
     * Overrides getGenerator
     * @see org.openflexo.dg.file.AbstractDGFile#getGenerator()
     */
    @Override
    public IFlexoResourceGenerator getGenerator()
    {
        return getResource().getGenerator();
    }
    
    /**
     * Overrides needsMemoryGeneration
     * @see org.openflexo.dg.file.AbstractDGFile#needsMemoryGeneration()
     */
    @Override
    public boolean needsMemoryGeneration()
    {
        return false;
    }
    
    /**
     * Overrides getResource
     * @see org.openflexo.foundation.cg.CGFile#getResource()
     */
    @Override
    public FlexoCopiedResource getResource()
    {
        return (FlexoCopiedResource) super.getResource();
    }
    
    /**
     * Overrides getLastGenerationDate
     * @see org.openflexo.foundation.cg.CGFile#getLastGenerationDate()
     */
    @Override
    public Date getLastGenerationDate()
    {
        return new Date(((ScreenshotResource)getResource().getResourceToCopy()).getLastGenerationDate().getTime());
    }
    
    /**
     * Overrides isGenerationConflicting
     * @see org.openflexo.foundation.cg.CGFile#isGenerationConflicting()
     */
    @Override
    public boolean isGenerationConflicting()
    {
        // Screenshots are never in conflict
        return false;
    }
    
    /**
     * Overrides getGenerationStatus
     * @see org.openflexo.foundation.cg.CGFile#getGenerationStatus()
     */
    @Override
    public GenerationStatus getGenerationStatus()
    {
        GenerationStatus status = super.getGenerationStatus();
        if (status.isConflicting()) // Little hack to prevent screenshots from being in conflict
            return GenerationStatus.GenerationModified;
        return status;
    }
}
