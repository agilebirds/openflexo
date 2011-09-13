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
import java.util.logging.Level;

import org.apache.cayenne.CayenneRuntimeException;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.rm.FlexoDMResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoRMResource;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.cg.PListFileResource;
import org.openflexo.generator.cg.CGPListFile;
import org.openflexo.generator.dm.EOModelPListGenerator;
import org.openflexo.generator.rm.GenerationAvailableFileResource;


public class EOModelPListFileResource extends PListFileResource<EOModelPListGenerator,CGPListFile> implements GenerationAvailableFileResource, FlexoObserver{

	public EOModelPListFileResource(FlexoProjectBuilder builder) {
		super(builder);
		// TODO Auto-generated constructor stub
	}

	public EOModelPListFileResource(FlexoProject aProject) {
		super(aProject);
		// TODO Auto-generated constructor stub
	}
	private boolean isObserverRegistered = false;
    
    @Override
	protected EOModelPListFile createGeneratedResourceData()
    {
        return new EOModelPListFile(getFile(),this);
    }
    
    public DMEOModel getDMEOModel()
    {
        if (getGenerator() != null)
            return getGenerator().getModel();
        return null;
    }
    
    public void registerObserverWhenRequired()
    {
        if (!isObserverRegistered && getDMEOModel() != null) {
            isObserverRegistered = true;
            getDMEOModel().addObserver(this);
        }
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
        if (observable == getDMEOModel()) {
            
        }
    }
    
    @Override
	public EOModelPListFile getGeneratedResourceData()
    {
    	return (EOModelPListFile)super.getGeneratedResourceData();
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
    	if(resource instanceof FlexoRMResource)return false;
        if (resource instanceof FlexoDMResource) {
            FlexoDMResource dmRes = (FlexoDMResource) resource;
            if (dmRes.isLoaded() && getDMEOModel() != null) {
                try{
                	if(getGenerator()!=null && getGenerator().pListCache!=null && getGenerator().pListCache.equals(getGenerator().generatePList()))
                		return false;
                }catch(CayenneRuntimeException e){
                	return true;
                }catch(NullPointerException e){
                	return true;
                }
            	if (getDMEOModel().getLastUpdate().before(requestDate) || getDMEOModel().getLastUpdate().equals(requestDate)) {
                    if (logger.isLoggable(Level.FINER))
                        logger.finer("OPTIMIST DEPENDANCY CHECKING for PLIST EOMODEL " + getDMEOModel().getName());
                    return false;
                }
            }
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

        if (getDMEOModel() != null) {
            addToDependantResources(getProject().getFlexoDMResource());
        }
    }

    
    static String getDefaultFileName()
    {
        return "index.eomodeld";
    }


}
