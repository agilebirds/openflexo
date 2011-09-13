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
import org.openflexo.foundation.ie.dm.ComponentDeleted;
import org.openflexo.foundation.ie.dm.ComponentNameChanged2;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.generator.ie.PopupComponentGenerator;
import org.openflexo.logging.FlexoLogger;

/**
 * @author sylvain
 *
 */
public class PopupComponentAPIFileResource extends ComponentAPIFileResource<PopupComponentGenerator> implements PopupComponentFileResource
{
    protected static final Logger logger = FlexoLogger.getLogger(PopupComponentAPIFileResource.class.getPackage().getName());

    /**
     * @param builder
     */
    public PopupComponentAPIFileResource(FlexoProjectBuilder builder)
    {
        super(builder);
    }

    /**
     * @param aProject
     */
    public PopupComponentAPIFileResource(FlexoProject aProject)
    {
        super(aProject);
    }

	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		if (observable == getComponentDefinition()) {
			if (dataModification instanceof ComponentNameChanged2) {
				logger.info("Building new resource after renaming");
				PopupComponentGenerator generator = getGenerator();
				setGenerator(null);
				getCGFile().setMarkedForDeletion(true);
				generator.refreshConcernedResources();
				generator.generate(true);
				generator.getRepository().refresh();
				observable.deleteObserver(this);
				isObserverRegistered = false;
			}
			else if (dataModification instanceof ComponentDeleted) {
				logger.info("Handle component has been deleted");
				setGenerator(null);
				getCGFile().setMarkedForDeletion(true);
				getCGFile().getRepository().refresh();
				observable.deleteObserver(this);
				isObserverRegistered = false;
			}
		}
	}


}
