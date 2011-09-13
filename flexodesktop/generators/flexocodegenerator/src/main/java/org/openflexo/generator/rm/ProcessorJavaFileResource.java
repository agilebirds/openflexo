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
package org.openflexo.generator.rm;

import java.util.Date;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.rm.FlexoProcessResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.cg.JavaFileResource;
import org.openflexo.foundation.wkf.dm.ProcessRemoved;
import org.openflexo.generator.TemplateLocator;
import org.openflexo.generator.cg.CGJavaFile;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.generator.wkf.ControlGraphGenerator;
import org.openflexo.logging.FlexoLogger;


/**
 * @author sylvain
 * 
 */
public class ProcessorJavaFileResource extends JavaFileResource<ControlGraphGenerator, CGJavaFile> implements GenerationAvailableFileResource, FlexoObserver
{
    protected static final Logger logger = FlexoLogger.getLogger(ProcessorJavaFileResource.class.getPackage().getName());

    /**
     * @param builder
     */
    public ProcessorJavaFileResource(FlexoProjectBuilder builder)
    {
        super(builder);
    }

    /**
     * @param aProject
     */
    public ProcessorJavaFileResource(FlexoProject aProject)
    {
        super(aProject);
    }

    public DMEntity getEntity() 
    {
    	return null;
    }

    @Override
	public void registerObserverWhenRequired()
    {
    }
    
    @Override
	protected ProcessorJavaFile createGeneratedResourceData()
    {
        return new ProcessorJavaFile(getFile(),this);
    }
    
    /**
     * Rebuild resource dependancies for this resource
     */
    @Override
	public void rebuildDependancies()
    {
        //super.rebuildDependancies();
        if(getGenerator()!=null)
        	addToDependantResources(getGenerator().getProcess().getFlexoResource());
   }
    
    @Override
	public ProcessorJavaFile getGeneratedResourceData()
    {
    	return (ProcessorJavaFile)super.getGeneratedResourceData();
    }

	@Override
	public void update(FlexoObservable observable,
			DataModification dataModification) {
		if(getGenerator()!=null && getGenerator().getProcess()==observable){
			if(dataModification instanceof ProcessRemoved || dataModification instanceof NameChanged){
				ControlGraphGenerator generator = getGenerator();
				getCGFile().setMarkedForDeletion(true);
				setGenerator(null);
				generator.refreshConcernedResources();
				generator.getRepository().refresh();
			}
		}
	}
	
	@Override
	public CGRepository getRepository()
    {
        return getGenerator().getProjectGenerator().getRepository();
    }
	
	@Override
	public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate)
	{
		if (resource instanceof TemplateLocator) {
			return false;
		}
		if(resource instanceof FlexoProcessResource) {
			return ((FlexoProcessResource)resource).isModified();
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}

}
