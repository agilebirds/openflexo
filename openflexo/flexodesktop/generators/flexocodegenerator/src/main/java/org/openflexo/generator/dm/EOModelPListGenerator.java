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
import org.openflexo.foundation.dm.eo.DMEOModel;
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
public class EOModelPListGenerator extends MetaFileGenerator
{

    private static final Logger logger = FlexoLogger.getLogger(EOModelPListGenerator.class.getPackage().getName());
    public String pListCache = "";

    private final DMEOModel _dmeoModel;
    /**
     * @param projectGenerator
     * @param object
     */
    public EOModelPListGenerator(ProjectGenerator projectGenerator, DMEOModel object)
    {
        super(projectGenerator, FileFormat.PLIST, ResourceType.EOMODEL,"index.eomodeld","INDEX_PLIST." +object.getName());
        
        _dmeoModel = object;
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
    	textResource = GeneratedFileResourceFactory.createNewModelPlistFileResource(repository, this);
        resources.add(textResource);
    }

    /**
     * @return
     */
    public DMEOModel getModel()
    {
        return _dmeoModel;
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
        	pListCache = generatePList();
            generatedCode = new GeneratedTextResource(getModel().getName(), pListCache);
        } catch (CayenneRuntimeException cre) {
            cre.printStackTrace();
            try {
            	generatedCode = new GeneratedTextResource(getModel().getName(), FileUtils
                        .fileContents(getModel().getEOModel().getIndexFile()));
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
    
    public String generatePList() throws CayenneRuntimeException{
    	return getModel().getEOModel().getPListRepresentation();
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
		return "";
	}

	@Override
	public CGSymbolicDirectory getSymbolicDirectory(CGRepository repository) {
		return repository.getResourcesSymbolicDirectory();
	}

}
