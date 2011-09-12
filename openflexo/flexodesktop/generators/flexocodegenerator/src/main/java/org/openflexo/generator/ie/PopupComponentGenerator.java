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
package org.openflexo.generator.ie;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 *
 */
public class PopupComponentGenerator extends ComponentGenerator
{
    
	protected Logger logger = FlexoLogger.getLogger(PopupComponentGenerator.class.getPackage().getName());

	/**
     * @param projectGenerator
     * @param componentDefinition
     * @param componentGeneratedName
     */
    public PopupComponentGenerator(ProjectGenerator projectGenerator, PopupComponentDefinition componentDefinition)
    {
        super(projectGenerator, componentDefinition, nameForComponentDefinition(componentDefinition));
    }

	@Override
	public Logger getGeneratorLogger()
	{
		return logger;
	}

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) 
	{
		// Java
		javaResource = GeneratedFileResourceFactory.createNewPopupComponentJavaFileResource(repository, this);
		resources.add(javaResource);

		// WO
		woResource = GeneratedFileResourceFactory.createNewPopupComponentWOFileResource(repository, this);
		resources.add(woResource);

		// API
		apiResource = GeneratedFileResourceFactory.createNewPopupComponentAPIFileResource(repository, this);
		resources.add(apiResource);
	}
	
	@Override
	public PopupComponentDefinition getComponentDefinition()
	{
		return (PopupComponentDefinition)super.getComponentDefinition();
	}

}
