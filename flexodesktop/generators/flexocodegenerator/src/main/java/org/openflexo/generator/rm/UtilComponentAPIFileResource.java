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

import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.generator.FlexoComponentResourceGenerator;
import org.openflexo.generator.utils.MetaWOGenerator;
import org.openflexo.logging.FlexoLogger;

/**
 * @author sylvain
 * 
 */
public class UtilComponentAPIFileResource extends ComponentAPIFileResource<FlexoComponentResourceGenerator>
{
    protected static final Logger logger = FlexoLogger.getLogger(UtilComponentAPIFileResource.class.getPackage().getName());

    /**
     * Rebuild resource dependancies for this resource
     */
    @Override
	public void rebuildDependancies()
    {
        super.rebuildDependancies();
        if (getGenerator() instanceof MetaWOGenerator)
            ((MetaWOGenerator)getGenerator()).rebuildDependanciesForResource(this);
    }

    /**
     * @param builder
     */
    public UtilComponentAPIFileResource(FlexoProjectBuilder builder)
    {
        super(builder);
    }

    /**
     * @param aProject
     */
    public UtilComponentAPIFileResource(FlexoProject aProject)
    {
        super(aProject);
    }

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// TODO Auto-generated method stub
		
	}

}
