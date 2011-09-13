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
package org.openflexo.foundation.rm;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.ws.rm.WSDLResourceData;


/**
 * Represents a WSDL resource
 * 
 * @author sguerin
 * 
 */
public class FlexoWSDLResource extends FlexoImportedResource<WSDLResourceData>
{
	
	private static final Logger logger = Logger.getLogger(FlexoWSDLResource.class.getPackage().getName());	

    /**
     * Constructor used for XML Serialization: never try to instanciate resource
     * from this constructor
     * 
     * @param builder
     */
    public FlexoWSDLResource(FlexoProjectBuilder builder)
    {
        this(builder.project);
        builder.notifyResourceLoading(this);
   }

    public FlexoWSDLResource(FlexoProject aProject)
    {
        super(aProject);
    }

    public FlexoWSDLResource(FlexoProject aProject, FlexoDMResource dmResource, FlexoProjectFile wsdlFile) throws InvalidFileNameException
    {
        this(aProject);
        setResourceFile(wsdlFile);
        dmResource.addToDependantResources(this);
        if (logger.isLoggable(Level.INFO))
            logger.info("Build new FlexoWSDLResource");
    }

    public FlexoWSDLResource(FlexoProject aProject, WSDLResourceData anWSDLResourceData, FlexoDMResource dmResource, FlexoProjectFile wsdlFile) throws InvalidFileNameException
    {
        this(aProject, dmResource, wsdlFile);
        _resourceData = anWSDLResourceData;
        anWSDLResourceData.setFlexoResource(this);
    }

    @Override
	public ResourceType getResourceType()
    {
        return ResourceType.WSDL;
    }

    @Override
	public String getName()
    {
        return getFile().getName();
    }

    @Override
	public void setName(String aName)
    {
        // Not allowed
    }

    public Class getResourceDataClass()
    {
        return WSDLResourceData.class;
    }

    public WSDLResourceData getWSDLResourceData() throws FileNotFoundException, ProjectLoadingCancelledException, FlexoException
    {
        return getImportedData();
    }

    /**
     * Returns boolean indicating if custom disk update scheme is implemented
     * for this kind of resource. In this case, since a custom resource disk
     * update scheme is implemented in method #performDiskUpdate(), returns true
     * 
     * @return true
     */
    /*public boolean implementsResourceDiskUpdate()
    {
        return true;
    }*/

    /**
     * Perform update of specified resource data from data read from a updated
     * resource on disk NOTE 1: If no custom scheme is defined, disk update will
     * force the entire project to be reloaded NOTE 2: must be overriden in
     * subclasses if relevant
     */
    /*public synchronized void performDiskUpdate()
    {
        // here i should do something...
         logger.warning ("performDiskUpdate() not implemented for FlexoWSDLResource !");
    }*/

    @Override
	protected WSDLResourceData doImport() throws FlexoException
    {
    	_resourceData = new WSDLResourceData(project, this);
        notifyResourceStatusChanged();
        return _resourceData;
   }
    
    /**
     * Rebuild resource dependancies for this resource
     */
    @Override
	public void rebuildDependancies()
    {
        super.rebuildDependancies();
        getProject().getFlexoDMResource().addToDependantResources(this);
    }
    

}
