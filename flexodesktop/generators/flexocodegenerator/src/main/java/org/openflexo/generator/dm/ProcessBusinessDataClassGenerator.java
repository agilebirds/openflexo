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

import java.util.Vector;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;

import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.ProcessBusinessDataRepository;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.JavaFileResource;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.generator.rm.UtilJavaFileResource;
import org.openflexo.generator.utils.JavaClassGenerator;
import org.openflexo.logging.FlexoLogger;

public class ProcessBusinessDataClassGenerator extends JavaClassGenerator
{

    protected static final String TEMPLATE_NAME = "ProcessBusinessDataGeneratedClass.java.vm";
    protected static final String PROCESSBUSINESSDATA_BASE_TEMPLATE_NAME = "ProcessBusinessDataBaseClass.java.vm";
    private static final Logger logger = FlexoLogger.getLogger(ProcessBusinessDataClassGenerator.class.getPackage().getName());
    
    public ProcessBusinessDataClassGenerator(ProjectGenerator projectGenerator, DMEntity entity)
    {
        super(projectGenerator, entity);
    }

	@Override
    public Logger getGeneratorLogger()
	{
		return logger;
	}

    @Override
	public VelocityContext defaultContext() 
    {
        VelocityContext context = super.defaultContext();
        context.put("entity", getEntity());

        return context;
    }

    @SuppressWarnings("unchecked")
	@Override
    public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources)
	{
		// Java file
    	javaResource  = (UtilJavaFileResource)resourceForKeyWithCGFile(ResourceType.JAVA_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, getIdentifier()));
		if (javaResource == null) {
			javaResource = GeneratedFileResourceFactory.createNewUtilJavaFileResource(repository,this);
			logger.info("Created DMENTITY JAVA resource "+javaResource.getName());
		}
		else {
			javaResource.setGenerator(this);
			logger.info("Successfully retrieved DMENTITY JAVA resource "+javaResource.getName());
		}
		javaResource.registerObserverWhenRequired();
		resources.add(javaResource);
	}


	@Override
	public void rebuildDependanciesForResource(JavaFileResource resource) {
		
	}

	@Override
	public String getTemplateName() {
		if(getEntity() == ((ProcessBusinessDataRepository)getEntity().getRepository()).getProcessBusinessDataEntity())
			return PROCESSBUSINESSDATA_BASE_TEMPLATE_NAME;
		return TEMPLATE_NAME;
	}


}
