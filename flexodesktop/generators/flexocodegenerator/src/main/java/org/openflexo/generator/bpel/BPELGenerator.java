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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.MetaGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.utils.ResourceGenerator;
import org.openflexo.logging.FlexoLogger;


/**
 * @author denadmin
 * 
 */
public class BPELGenerator extends MetaGenerator<FlexoModelObject, CGRepository>
{
    private static final Logger logger = FlexoLogger.getLogger(ResourceGenerator.class.getPackage().getName());

    private BPELFileGenerator bpelFileGenerator;
    private BPELXSDFileGenerator xsdFileGenerator;
    private BPELWSDLFilesGenerator wsdlFilesGenerator;
    
	@Override
	public Logger getGeneratorLogger()
	{
		return logger;
	}

	public BPELGenerator(ProjectGenerator projectGenerator)
    {
    	super(projectGenerator,null);

    	bpelFileGenerator = new BPELFileGenerator(projectGenerator);
    	xsdFileGenerator = new BPELXSDFileGenerator(projectGenerator);
    	wsdlFilesGenerator = new BPELWSDLFilesGenerator(projectGenerator);
    }

	@Override
	public void generate(boolean forceRegenerate) throws GenerationException
	{
		startGeneration();
		bpelFileGenerator.generate(forceRegenerate);
		xsdFileGenerator.generate(forceRegenerate);
		wsdlFilesGenerator.generate(forceRegenerate);
		stopGeneration();
	}

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) 
	{
		resetSecondaryProgressWindow(24);
    	bpelFileGenerator.buildResourcesAndSetGenerators(repository,resources); // OK    	
    	xsdFileGenerator.buildResourcesAndSetGenerators(repository,resources); // OK    	
    	wsdlFilesGenerator.buildResourcesAndSetGenerators(repository,resources); // OK    	
	}

    public BPELFileGenerator getBpelFileGenerator()
    {
        return bpelFileGenerator;
    }

}
