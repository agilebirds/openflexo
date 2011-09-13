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

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.cg.APIFile;
import org.openflexo.generator.FlexoResourceGenerator;
import org.openflexo.generator.rm.GenerationAvailableFile;
import org.openflexo.logging.FlexoLogger;


public class ComponentAPIFile extends APIFile implements GenerationAvailableFile
{

    protected static final Logger logger = FlexoLogger.getLogger(ComponentAPIFile.class.getPackage().getName());

    public ComponentAPIFile(File f, ComponentAPIFileResource resource)
	{
		super(f);
		try {
			setFlexoResource(resource);
		} catch (DuplicateResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
	public ComponentAPIFileResource getFlexoResource()
    {
        return (ComponentAPIFileResource)super.getFlexoResource();
    }

	public ComponentAPIFile()
	{
		super();
    }

	@Override
	public FlexoResourceGenerator getGenerator() 
	{
		return (FlexoResourceGenerator)getFlexoResource().getGenerator();
	}
	
}