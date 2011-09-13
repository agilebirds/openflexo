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

import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class CustomTemplatesResource extends FlexoDirectoryResource
{

    /**
     * Constructor used for XML Serialization: never try to instanciate resource
     * from this constructor
     * 
     * @param builder
     */
    public CustomTemplatesResource(FlexoProjectBuilder builder)
    {
        super(builder.project);
        builder.notifyResourceLoading(this);
    }

    public CustomTemplatesResource(FlexoProject aProject, String name)
    {
        super(aProject);
        setName(name);
    }

    public CustomTemplatesResource(FlexoProject aProject, String name, FlexoProjectFile directory) throws InvalidFileNameException
    {
        this(aProject,name);
        setResourceFile(directory);
    }

    @Override
	public ResourceType getResourceType()
    {
        return ResourceType.CUSTOM_TEMPLATES;
    }

    @Override
	public String getName()
    {
        return _name;
    }

    @Override
	public void setName(String aName)
    {
    	_name = aName;
    }

    private String _name;

    /**
     * Overrides performUpdating
     * @see org.openflexo.foundation.rm.FlexoResource#performUpdating(org.openflexo.foundation.rm.FlexoResourceTree)
     */
    @Override
    protected void performUpdating(FlexoResourceTree updatedResources) throws ResourceDependancyLoopException, LoadResourceException, FileNotFoundException, ProjectLoadingCancelledException
    {
        // TODO Auto-generated method stub
        
    }
}
