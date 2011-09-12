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
package org.openflexo.generator.wkf;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.MetaGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.logging.FlexoLogger;


public class WorkflowContextGenerator extends MetaGenerator<FlexoModelObject, CGRepository>
{
    private static final Logger logger = FlexoLogger.getLogger(WorkflowContextGenerator.class.getPackage().getName());

    public static final String PACKAGENAME = "org.openflexo.workflowcontext";
    
	private WorkflowComponentInstanceGenerator workflowComponentInstanceGenerator;
	private WorkflowProcessGenerator workflowProcessGenerator;
	private BusinessDataStorageGenerator businessDataStorageGenerator;
    
	@Override
	public Logger getGeneratorLogger()
	{
		return logger;
	}

	public WorkflowContextGenerator(ProjectGenerator projectGenerator)
    {
    	super(projectGenerator,null);
    }
    
    @Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) 
    {
		resetSecondaryProgressWindow(25);
		workflowComponentInstanceGenerator = new WorkflowComponentInstanceGenerator((ProjectGenerator) getProjectGenerator(), PACKAGENAME);
    	workflowProcessGenerator = new WorkflowProcessGenerator((ProjectGenerator) getProjectGenerator(), PACKAGENAME);
    	businessDataStorageGenerator = new BusinessDataStorageGenerator((ProjectGenerator) getProjectGenerator(), PACKAGENAME);
		workflowComponentInstanceGenerator.buildResourcesAndSetGenerators(repository, resources);
		workflowProcessGenerator.buildResourcesAndSetGenerators(repository, resources);
		businessDataStorageGenerator.buildResourcesAndSetGenerators(repository, resources);
    }
}
