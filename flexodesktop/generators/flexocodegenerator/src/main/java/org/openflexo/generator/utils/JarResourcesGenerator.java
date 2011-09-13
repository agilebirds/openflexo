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

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.rm.FlexoJarResource;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.MetaGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.localization.FlexoLocalization;


public class JarResourcesGenerator extends MetaGenerator<FlexoModelObject, CGRepository>
{
	private static final Logger logger = Logger.getLogger(WebServerResourcesGenerator.class.getPackage().getName());
	
    public JarResourcesGenerator(ProjectGenerator projectGenerator)
    {
        super(projectGenerator,null);
        _generators = new Hashtable<FlexoJarResource,ResourceToCopyGenerator>();
    }
    
    @Override
    public ProjectGenerator getProjectGenerator() {
    	return (ProjectGenerator) super.getProjectGenerator();
    }
    
	@Override
	public Logger getGeneratorLogger()
	{
		return logger;
	}

    @Override
	public void generate(boolean forceRegenerate) throws GenerationException
    {
    	if (logger.isLoggable(Level.FINE))
    		logger.fine("Called JarResourcesGenerator.generate(forceRegenerate)");
    	resetSecondaryProgressWindow(_generators.values().size());
    	startGeneration();
    	for (ResourceToCopyGenerator generator : _generators.values()) {
    		refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating")+ " "+generator.getIdentifier(),false);
    		generator.generate(forceRegenerate);
    	}
    	stopGeneration();
    }
    
	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) 
	{
		
		Vector<FlexoJarResource> allJars = getProject().getResourcesOfClass(FlexoJarResource.class);
		resetSecondaryProgressWindow(allJars.size());
		for (FlexoJarResource jar : allJars) {
			ResourceToCopyGenerator generator = getGenerator(jar);
			if(jar.getFile()==null || !jar.getFile().exists() || jar.getJarRepository()==null || !jar.getJarRepository().getIsImportedByUser())continue;
    		refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating")+ " "+jar.getName(),false);
			if (generator != null)
				generator.buildResourcesAndSetGenerators(repository,resources);
			else {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Could not instanciate JarResourceGenerator for "+jar);
			}
		}
		
	}

	private Hashtable<FlexoJarResource,ResourceToCopyGenerator> _generators;
	
	protected ResourceToCopyGenerator getGenerator(FlexoJarResource jarResource)
	{
		ResourceToCopyGenerator returned = _generators.get(jarResource);
		if (returned == null) {
			_generators.put(jarResource,returned=new ResourceToCopyGenerator(getProjectGenerator(),jarResource, getRepository().getLibSymbolicDirectory()));
		}
		return returned;
	}
}
