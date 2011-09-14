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

import java.io.File;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.generator.rm.BuildPropertiesResource;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;


public class BuildPropertiesGenerator extends MetaFileGenerator
{
    private final Logger logger = FlexoLogger.getLogger(BuildPropertiesGenerator.class.getPackage().getName());

    private static final String FILE_NAME = "build.properties";
    public static final String IDENTIFIER = "BUILD_PROPERTIES";

    public BuildPropertiesGenerator(ProjectGenerator projectGenerator)
    {
        super(projectGenerator, FileFormat.TEXT, ResourceType.ANT_FILE, FILE_NAME, IDENTIFIER);
    }

	@Override
    public Logger getGeneratorLogger()
	{
		return logger;
	}

    @Override
    public void generate(boolean forceRegenerate)
    {
       	if (!forceRegenerate && !needsGeneration()) {
			return;
		}
    	try {
    		refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating")+ " "+getIdentifier(),false);
    		startGeneration();
    		if (logger.isLoggable(Level.INFO)) {
				logger.info("Generating "+FILE_NAME);
			}
    		VelocityContext velocityContext = defaultContext();
    		String javaCode = merge("build.properties.vm", velocityContext);
    		generatedCode = new GeneratedTextResource(FILE_NAME, javaCode);
    		stopGeneration();
      	} catch (GenerationException e) {
    		setGenerationException(e);
		} catch (Exception e) {
			setGenerationException(new UnexpectedExceptionOccuredException(e,getProjectGenerator()));
    	}
    }

    /**
     * Overrides defaultContext
     * @see org.openflexo.generator.CGGenerator#defaultContext()
     */
    @Override
    protected VelocityContext defaultContext()
    {
        VelocityContext vc = super.defaultContext();
        vc.put("applicationConfFileName","Application.conf.PROD");
        return vc;
    }

    public File getFile(){
    	return new File(getProjectGenerator().getRootOutputDirectory(),FILE_NAME);
    }

	@Override
    public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources)
	{
		// PList file
    	textResource = (BuildPropertiesResource) resourceForKeyWithCGFile(ResourceType.TEXT_FILE,
    			GeneratorUtils.nameForRepositoryAndIdentifier(repository, getIdentifier()));
        if (textResource == null) {
            textResource = GeneratedFileResourceFactory.createBuildPropertiesFileResource(repository, this);
            textResource.setGenerator(this);
            logger.info("Created HELP resource " + textResource.getName());
        } else {
            textResource.setGenerator(this);
            logger.info("Successfully retrieved HELP FILE resource " + textResource.getName());
        }
        ((BuildPropertiesResource)textResource).registerObserverWhenRequired();
        resources.add(textResource);
    }

	
	@Override
	public String getRelativePath()
	{
		return "";
	}

	@Override
	public CGSymbolicDirectory getSymbolicDirectory(CGRepository repository)
	{
		return repository.getProjectSymbolicDirectory();
	}

}
