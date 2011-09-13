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
package org.openflexo.generator.dm;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.cayenne.CayenneRuntimeException;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.generator.utils.MetaFileGenerator;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.FileUtils;

/**
 * @author gpolet
 * 
 */
public class EOEntityPListGenerator extends MetaFileGenerator
{

    private static final Logger logger = FlexoLogger.getLogger(EOEntityPListGenerator.class.getPackage().getName());

    private final DMEOEntity _dmeoEntity;
    /**
     * @param projectGenerator
     * @param object
     */
    public EOEntityPListGenerator(ProjectGenerator projectGenerator, DMEOEntity eoEntity)
    {
        super(projectGenerator, FileFormat.PLIST, ResourceType.PLIST_FILE,eoEntity.getName()+".plist","PLIST." + eoEntity.getFullyQualifiedName());
        _dmeoEntity = eoEntity;
    }

    /**
     * Overrides buildResourcesAndSetGenerators
     * 
     * @see org.openflexo.generator.CGGenerator#buildResourcesAndSetGenerators(org.openflexo.foundation.cg.CGRepository,
     *      Vector)
     */
    @Override
    public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources)
    {
        // PList file
    	textResource = GeneratedFileResourceFactory.createNewEntityPlistFileResource(repository, this);
        resources.add(textResource);
    }

    /**
     * @return
     */
    public DMEOEntity getEntity()
    {
        return _dmeoEntity;
    }

    /**
     * Overrides generate
     * 
     * @see org.openflexo.generator.CGGenerator#generate(boolean)
     */
    @Override
    public void generate(boolean forceRegenerate)
    {
    	if (!forceRegenerate && !needsGeneration()) {
			return;
		}
        startGeneration();
        try {
            generatedCode = new GeneratedTextResource(getEntity().getName(), getEntity().getEOEntity().getPListRepresentation());
        } catch (CayenneRuntimeException cre) {
            cre.printStackTrace();
            try {
            	generatedCode = new GeneratedTextResource(getEntity().getName(), FileUtils
                        .fileContents(getEntity().getEOEntity().getFile()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        stopGeneration();
    }

    @Override
    public boolean needsRegenerationBecauseOfTemplateUpdated() {
    	return false;
    }
    
    @Override
    public boolean needsRegenerationBecauseOfTemplateUpdated(Date diskLastGenerationDate) {
    	return false;
    }
    
    /**
     * Overrides getGeneratorLogger
     * 
     * @see org.openflexo.generator.CGGenerator#getGeneratorLogger()
     */
    @Override
    public Logger getGeneratorLogger()
    {
        return logger;
    }

	@Override
	public String getRelativePath() {
		return getEntity().getEOEntity().getModel().getName()+".eomodeld";
	}

	@Override
	public CGSymbolicDirectory getSymbolicDirectory(CGRepository repository) {
		return repository.getResourcesSymbolicDirectory();
	}

}
