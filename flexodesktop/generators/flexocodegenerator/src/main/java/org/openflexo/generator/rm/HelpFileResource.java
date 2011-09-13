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
import java.util.Enumeration;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.TextFileResource;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.generator.TemplateLocator;
import org.openflexo.generator.cg.CGTextFile;
import org.openflexo.generator.utils.HelpGenerator;
import org.openflexo.toolbox.FileFormat;


public class HelpFileResource extends TextFileResource<HelpGenerator, CGTextFile> implements GenerationAvailableFileResource, FlexoObserver{

	private final boolean isObserverRegistered = false;

	public HelpFileResource(FlexoProjectBuilder builder) {
		super(builder);
		setResourceFormat(FileFormat.TEXT);
	}

	public HelpFileResource(FlexoProject aProject) {
		super(aProject);
		setResourceFormat(FileFormat.TEXT);
	}
    
    @Override
	protected HelpFile createGeneratedResourceData()
    {
        return new HelpFile(getFile(),this);
    }
    
    
    public void registerObserverWhenRequired()
    {
    }

    /**
     * Overrides update
     * 
     * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
     *      org.openflexo.foundation.DataModification)
     */
    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
        
    }
    
    @Override
	public HelpFile getGeneratedResourceData()
    {
    	return (HelpFile)super.getGeneratedResourceData();
    }
    
    /**
     * Return dependancy computing between this resource, and an other resource,
     * asserting that this resource is contained in this resource's dependant
     * resources
     * 
     * @param resource
     * @param dependancyScheme
     * @return
     */
    @Override
	public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate)
    {
    	if (resource instanceof TemplateLocator) {
			return ((TemplateLocator)resource).needsUpdateForResource(this);
		}
    	if(resource.getResourceType()!=ResourceType.PROCESS) {
			return false;
		}
    	if(resource.getLastUpdate().before(getLastGenerationDate())) {
			return false;
		}
        return super.optimisticallyDependsOf(resource, requestDate);
    }

    /**
     * Rebuild resource dependancies for this resource
     */
    @Override
	public void rebuildDependancies()
    {
        super.rebuildDependancies();
        Enumeration<FlexoProcess> en  = getProject().getAllLocalFlexoProcesses().elements();
        while(en.hasMoreElements()){
        	addToDependantResources(en.nextElement().getFlexoResource());
        }
    }

    
    static String getDefaultFileName()
    {
        return "help.properties";
    }


}
