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

import org.openflexo.foundation.bpel.BPELModelException;
import org.openflexo.foundation.bpel.BPELWriter;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.generator.utils.MetaFileGenerator;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.ToolBox;


public class BPELFileGenerator extends MetaFileGenerator
{
     private static final String IDENTIFIER = "BPEL_FILE";

    public BPELFileGenerator(ProjectGenerator projectGenerator)
    {
        super(projectGenerator, FileFormat.BPEL, ResourceType.BPEL,ToolBox.cleanStringForJava(projectGenerator.getProject().getProjectName())+".bpel",IDENTIFIER+"_"+ToolBox.cleanStringForJava(projectGenerator.getProject().getProjectName()));
    }

	@Override
	public Logger getGeneratorLogger()
	{
		return getProjectGenerator().getGeneratorLogger();
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
    		//if (logger.isLoggable(Level.INFO))
    			//logger.info("Generating "+getFileName());
    		if (this.getProject().getRootFlexoProcess()==null) {
    			throw new BPELModelException("No process defined for project");
    		}
    		
    		FlexoProcess toTranslate=null;
    		for (FlexoProcess p: this.getProject().getAllLocalFlexoProcesses()) {
    			if ((p.getPortRegistery().getAllPorts().size()>=1) && ! p.getIsWebService()) {
    				toTranslate=p;
    			}
    		}
    		
    		
    		BPELWriter writer=getProjectGenerator().getInstance(toTranslate,true);
    		String leCodeBPELQuOnDoitGenerer=writer.write();
    	    generatedCode = new GeneratedTextResource(getFileName(), leCodeBPELQuOnDoitGenerer);
    		stopGeneration();
    		getGeneratorLogger().info("The generation of the BPEL process "+ this.getProject().getRootFlexoProcess().getName() +" has been successfully performed.");
    	} catch (BPELModelException e) {
    		setGenerationException(new GenerationException(e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();
			setGenerationException(new UnexpectedExceptionOccuredException(e,getProjectGenerator()));
    	}
    }

    private BPELFileResource bpelFileResource;
    
    @Override
    public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources)
	{
    	bpelFileResource = (BPELFileResource)resourceForKeyWithCGFile(ResourceType.BPEL, BPELFileResource.nameForRepositoryAndIdentifier(repository, getIdentifier()));
		if (bpelFileResource == null) {
			bpelFileResource = GeneratedFileResourceFactory.createBPELFileResource(repository,this);
			//logger.info("Created BPEL resource "+bpelFileResource.getName());
		}
		else {
			bpelFileResource.setGenerator(this);
			//logger.info("Successfully retrieved BPEL resource "+bpelFileResource.getName());
		}
		bpelFileResource.setFileType(ResourceType.BPEL);
		bpelFileResource.registerObserverWhenRequired();
		resources.add(bpelFileResource);
		textResource = bpelFileResource;
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
