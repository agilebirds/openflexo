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

import org.openflexo.foundation.rm.FlexoCopiedResource;
import org.openflexo.foundation.rm.FlexoFileResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.cg.CopyOfFlexoResource;
import org.openflexo.generator.TemplateLocator;
import org.openflexo.generator.cg.CGWebServerImageFile;
import org.openflexo.generator.utils.ResourceToCopyGenerator;
import org.openflexo.logging.FlexoLogger;


/**
 * @author gpolet
 *
 */
public class FlexoCopyOfFlexoResource extends CopyOfFlexoResource<ResourceToCopyGenerator, CGWebServerImageFile> implements GenerationAvailableFileResource
{

    public static final Logger logger = FlexoLogger.getLogger(FlexoCopiedResource.class.getPackage().getName());

    public FlexoCopyOfFlexoResource(FlexoProjectBuilder builder)
    {
        super(builder);
    }
    
    /**
     * @param aProject
     */
    public FlexoCopyOfFlexoResource(FlexoProject aProject, FlexoFileResource resourceToCopy)
    {
        super(aProject,resourceToCopy);
    }

	@Override
	public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate) {
		if (resource instanceof TemplateLocator) {
			return false;
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}
	
    @Override
    public CopyOfFlexoResourceData getGeneratedResourceData() {
    	return (CopyOfFlexoResourceData)super.getGeneratedResourceData();
    }

	@Override
	protected CopyOfFlexoResourceData createGeneratedResourceData() {
		return new CopyOfFlexoResourceData(this);
	}
}
