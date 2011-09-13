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
package org.openflexo.generator;

import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.generator.GeneratedCopiedFile;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.FlexoCopiedResource;
import org.openflexo.foundation.rm.FlexoGeneratedResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 *
 */
public class CopiedResourceGenerator<R extends GenerationRepository> extends Generator<FlexoModelObject, R> implements IFlexoResourceGenerator
{
    private static final Logger logger = FlexoLogger.getLogger(CopiedResourceGenerator.class.getPackage().getName());

    private FlexoCopiedResource copiedResource;

	private Generator<?, R> parent;

    /**
     *
     */
    public CopiedResourceGenerator(FlexoCopiedResource copiedResource, AbstractProjectGenerator<R> projectGenerator, Generator<?, R> parent)
    {
    	super(projectGenerator,null);
    	this.parent = parent;
        this.copiedResource = copiedResource;
        generatedCode = new GeneratedCopiedFile(copiedResource.getFile());
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
        return copiedResource.getDiskLastModifiedDate();
    }

    /**
     * Overrides isCodeAlreadyGenerated
     * @see org.openflexo.foundation.cg.generator.IFlexoResourceGenerator#isCodeAlreadyGenerated()
     */
    @Override
	public boolean isCodeAlreadyGenerated()
    {
        return true;
    }

    /**
     * Overrides needsGeneration
     * @see org.openflexo.foundation.cg.generator.IFlexoResourceGenerator#needsGeneration()
     */
    @Override
	public boolean needsGeneration()
    {
        return false;
    }

    //Just put this method to satisfy the Interface IFlexoResourceGenerator.
    //but, the TemplateLocator don't exist for copied resources and shouldn't be called.
    //so it returns null.
	@Override
	public TemplateLocator getTemplateLocator() {
		return null;
	}

	@Override
	public void addToGeneratedResourcesGeneratedByThisGenerator(CGRepositoryFileResource<?, ?, ? extends CGFile> resource) {

	}

	@Override
	public void removeFromGeneratedResourcesGeneratedByThisGenerator(CGRepositoryFileResource<?, ?, ? extends CGFile> resource) {

	}

	@Override
	public void silentlyGenerateCode() {
		if (logger.isLoggable(Level.SEVERE))
			logger.severe("This operation is not supported!");
	}

	@Override
	public void buildResourcesAndSetGenerators(R repository, Vector<CGRepositoryFileResource> resources) {
		if (parent!=null)
			parent.buildResourcesAndSetGenerators(repository, resources);
	}

	@Override
	public Logger getGeneratorLogger() {
		return null;
	}

}
