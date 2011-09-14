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

import org.apache.velocity.VelocityContext;

import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.JavaFileResource;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.generator.utils.JavaClassGenerator;
import org.openflexo.logging.FlexoLogger;

public class ControlGraphGenerator extends JavaClassGenerator{

	protected Logger logger = FlexoLogger.getLogger(ControlGraphGenerator.class.getPackage().getName());

    private static final String TEMLPATE_NAME = "ControlGraph.java.vm";
    
    private FlexoProcess _process;

	public ControlGraphGenerator(ProjectGenerator projectGenerator,FlexoProcess process) {
		super(projectGenerator,process.getExecutionClassName(),process.getExecutionGroupName());
		_process = process;
	}
	
	public FlexoProcess getProcess(){
		return _process;
	}
	
    @Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources)
	{
		// Java file
    	javaResource  = (JavaFileResource)resourceForKeyWithCGFile(ResourceType.JAVA_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, getIdentifier()));
		if (javaResource == null) {
			javaResource = GeneratedFileResourceFactory.createNewProcessorJavaFileResourceForProcess(repository,this);
		}
		else {
			javaResource.setGenerator(this);
		}
		javaResource.registerObserverWhenRequired();
		resources.add(javaResource);
	}

    @Override
    protected VelocityContext defaultContext() {
    	VelocityContext vc = super.defaultContext();
    	vc.put("code", getRefreshedCode());
    	return vc;
    }
    
	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	public String getRefreshedCode(){
		getProcess().getExecution().refresh();
		return getProcess().getExecution().getFormattedCode();
	}

	public String getClassName(){
		return getProcess().getExecutionClassName();
	}
	
	public String getPackageName(){
		return getProcess().getExecutionGroupName();
	}

	@Override
	public void rebuildDependanciesForResource(JavaFileResource resource) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getTemplateName() {
		return TEMLPATE_NAME;
	}
}
