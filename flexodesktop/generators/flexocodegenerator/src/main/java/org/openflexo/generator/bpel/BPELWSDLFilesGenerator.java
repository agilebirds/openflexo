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
package org.openflexo.generator.bpel;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.generator.MetaGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.utils.ResourceGenerator;
import org.openflexo.logging.FlexoLogger;


/**
 * @author denadmin
 * 
 */
public class BPELWSDLFilesGenerator extends MetaGenerator<FlexoModelObject, CGRepository>
{
    private static final Logger logger = FlexoLogger.getLogger(ResourceGenerator.class.getPackage().getName());

    private Hashtable<FlexoProcess,BPELWSDLFileGenerator> generators;
    
	@Override
	public Logger getGeneratorLogger()
	{
		return logger;
	}

	public BPELWSDLFilesGenerator(ProjectGenerator projectGenerator)
    {
    	super(projectGenerator,null);
    	generators = new Hashtable<FlexoProcess, BPELWSDLFileGenerator>();
    }
	
	@Override
	public ProjectGenerator getProjectGenerator() {
		return (ProjectGenerator) super.getProjectGenerator();
	}

	@Override
	public void generate(boolean forceRegenerate) throws GenerationException
	{
		startGeneration();
		for (BPELWSDLFileGenerator g : generators.values()) {
			g.generate(forceRegenerate);
		}
		stopGeneration();
	}

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) 
	{
		for (FlexoProcess process : getProject().getAllLocalFlexoProcesses()) {
			if (process.getIsWebService()) {
				BPELWSDLFileGenerator generator = getGenerator(process);
				generator.buildResourcesAndSetGenerators(repository, resources);
			}
		}
	}
	
	private BPELWSDLFileGenerator getGenerator(FlexoProcess p){
		if(generators.get(p)==null){
			BPELWSDLFileGenerator generator = new BPELWSDLFileGenerator(getProjectGenerator(),p);
			generators.put(p,generator);
		}
		return generators.get(p);
	}

}
