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
import java.util.Date;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;


/**
 * This class represents a Flexo resource. A FlexoResource represent an object
 * handled by Flexo Application Suite (all concerned modules), which is simply
 * stored in memory.
 * 
 * @author sguerin
 */
public class FlexoMemoryResource extends FlexoResource
{

	
    /**
     * Constructor used for XML Serialization: never try to instanciate resource
     * from this constructor
     * 
     * @param builder
     */
    public FlexoMemoryResource(FlexoProjectBuilder builder)
    {
        this(builder.project);
        builder.notifyResourceLoading(this);
   }

    public FlexoMemoryResource(FlexoProject aProject)
    {
        super(aProject);
    }

    public boolean needsSaving()
    {
        return false;
    }

	/**
	 * Override this
	 */
     @Override
	public boolean isToBeSerialized()
    {
        return false;
    }

     /**
      * This date is VERY IMPORTANT and CRITICAL since this is the date used by ResourceManager
      * to compute dependancies between resources. This method returns the date that must be considered
      * as last known update for this resource
      * 
      * Please override this method
      * 
      * @return a Date object
      */
    @Override
	public synchronized Date getLastUpdate()
    {
        return new Date();
    }

	/**
	 * Override this
	 */
	@Override
	public String getName() 
	{
		return null;
	}

	/**
	 * Override this
	 */
    @Override
	public void setName(String aName)
    {
    }
    
	/**
	 * Override this
	 */
	@Override
	public ResourceType getResourceType() 
	{
		return null;
	}

	private String _resourceClassName;
	
	public String _getResourceClassName()
	{
		if (_resourceClassName != null) return _resourceClassName;
		else return this.getClass().getName();
	}

	public void _setResourceClassName(String aClassName)
	{
		_resourceClassName = aClassName;
	}

    /**
     * Overrides performUpdating
     * @see org.openflexo.foundation.rm.FlexoResource#performUpdating(org.openflexo.foundation.rm.FlexoResourceTree)
     */
    @Override
    protected void performUpdating(FlexoResourceTree updatedResources) throws ResourceDependancyLoopException, LoadResourceException, FileNotFoundException, ProjectLoadingCancelledException, FlexoException
    {
        
    }

    @Override
    public void rebuildDependancies() {
    	// We cannot put in any resource list, otherwise it will be serialized!
    }
    
}
