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
package org.openflexo.foundation.rm.cg;

import java.util.logging.Logger;

import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;

/**
 * @author sylvain
 * 
 */
public class PListFileResource<G extends IFlexoResourceGenerator, F extends CGFile> extends TextFileResource<G,F>
{
    protected static final Logger logger = FlexoLogger.getLogger(PListFileResource.class.getPackage().getName());

    /**
     * @param builder
     */
    public PListFileResource(FlexoProjectBuilder builder)
    {
        super(builder);
     }

    /**
     * @param aProject
     */
    public PListFileResource(FlexoProject aProject)
    {
        super(aProject);
    }

    /**
     * Overrides getResourceType
     * 
     * @see org.openflexo.foundation.rm.FlexoResource#getResourceType()
     */
    @Override
	public ResourceType getResourceType()
    {
        return ResourceType.PLIST_FILE;
    }


    @Override
	protected PListFile createGeneratedResourceData()
    {
        return new PListFile(getFile());
    }
    
     public PListFile getPListFile()
    {
        return (PListFile) getGeneratedResourceData();
    }

     @Override
	public FileFormat getResourceFormat(){
     	return FileFormat.PLIST;
     }
     
}
