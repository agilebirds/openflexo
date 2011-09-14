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
package org.openflexo.generator.utils;

import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.generator.GeneratedCopiedFile;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.rm.FlexoCopiedResource;
import org.openflexo.foundation.rm.FlexoGeneratedResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.FlexoResourceGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.logging.FlexoLogger;


/**
 * @author gpolet
 *
 */
public class CopiedResourceGenerator extends FlexoResourceGenerator<FlexoModelObject, GeneratedCopiedFile>
{
    private static final Logger logger = FlexoLogger.getLogger(CopiedResourceGenerator.class.getPackage().getName());
    
    private FlexoCopiedResource copiedResource;
    
    private Date lastMemoryGeneration = new Date(1);
    
    private GeneratedCopiedFile generatedCode;
    
    /**
     * 
     */
    public CopiedResourceGenerator(ProjectGenerator pg, FlexoCopiedResource copiedResource)
    {
    	super(pg);
        this.copiedResource = copiedResource;
    }
    
    /**
     * Overrides generate
     * @see org.openflexo.foundation.cg.generator.IFlexoResourceGenerator#generate(boolean)
     */
    @Override
	public void generate(boolean forceRegenerate)
    {
        if (forceRegenerate) {
            if (logger.isLoggable(Level.INFO))
                logger.info("Called force generate on copied resource");
            if (copiedResource.getResourceToCopy() instanceof FlexoGeneratedResource)
                try {
                    ((FlexoGeneratedResource)copiedResource.getResourceToCopy()).generate();
                } catch (SaveResourceException e) {
                    e.printStackTrace();
                } catch (FlexoException e) {
                    e.printStackTrace();
                }
        }
        generatedCode = new GeneratedCopiedFile(copiedResource.getResourceToCopy().getFile());
        lastMemoryGeneration = new Date();
    }

    /**
     * Overrides getGeneratedCode
     * @see org.openflexo.foundation.cg.generator.IFlexoResourceGenerator#getGeneratedCode()
     */
    @Override
	public GeneratedCopiedFile getGeneratedCode()
    {
        return generatedCode;
    }

    @Override
    public Logger getGeneratorLogger() {
    	return logger;
    }
    
    /**
     * Overrides getGenerationException
     * @see org.openflexo.foundation.cg.generator.IFlexoResourceGenerator#getGenerationException()
     */
    @Override
	public GenerationException getGenerationException()
    {
        return null;
    }

    /**
     * Overrides getIdentifier
     * @see org.openflexo.foundation.cg.generator.IFlexoResourceGenerator#getIdentifier()
     */
    @Override
	public String getIdentifier()
    {
        return "SCREENSHOT-"+copiedResource.getResourceIdentifier();
    }

    /**
     * Overrides getMemoryLastGenerationDate
     * @see org.openflexo.foundation.cg.generator.IFlexoResourceGenerator#getMemoryLastGenerationDate()
     */
    @Override
	public Date getMemoryLastGenerationDate()
    {
        //return copiedResource.getResourceToCopy().getDiskLastModifiedDate();
        return lastMemoryGeneration;
    }

    /**
     * Overrides getUsedTemplates
     * @see org.openflexo.foundation.cg.generator.IFlexoResourceGenerator#getUsedTemplates()
     */
	@Override
	public Vector<CGTemplate> getUsedTemplates()
    {
		return new Vector<CGTemplate>();
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasFormattingException() {
		return false;
	}

    /**
     * Overrides needsRegenerationBecauseOfTemplateUpdated
     * @see org.openflexo.foundation.cg.generator.IFlexoResourceGenerator#needsRegenerationBecauseOfTemplateUpdated()
     */
    @Override
	public boolean needsRegenerationBecauseOfTemplateUpdated()
    {
        return false;
    }

    /**
     * Overrides needsRegenerationBecauseOfTemplateUpdated
     * @see org.openflexo.foundation.cg.generator.IFlexoResourceGenerator#needsRegenerationBecauseOfTemplateUpdated(java.util.Date)
     */
    @Override
	public boolean needsRegenerationBecauseOfTemplateUpdated(Date diskLastGenerationDate)
    {
        return false;
    }

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		
	}

}
