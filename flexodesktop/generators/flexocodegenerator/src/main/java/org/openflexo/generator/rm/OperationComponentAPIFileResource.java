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

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.OperationComponentInstance;
import org.openflexo.foundation.ie.dm.ComponentDeleted;
import org.openflexo.foundation.ie.dm.ComponentNameChanged2;
import org.openflexo.foundation.rm.FlexoProcessResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.generator.ie.PageComponentGenerator;
import org.openflexo.logging.FlexoLogger;

/**
 * @author sylvain
 *
 */
public class OperationComponentAPIFileResource extends ComponentAPIFileResource<PageComponentGenerator> implements OperationComponentFileResource
{
    protected static final Logger logger = FlexoLogger.getLogger(OperationComponentAPIFileResource.class.getPackage().getName());

    /**
     * Rebuild resource dependancies for this resource
     */
    @Override
	public void rebuildDependancies()
    {
        super.rebuildDependancies();
    	if (getGenerator() != null) {
    		for (ComponentInstance ci : getComponentDefinition().getComponentInstances()) {
    			if (ci instanceof OperationComponentInstance)
    				addToDependantResources(((OperationComponentInstance)ci).getOperationNode().getProcess().getFlexoResource());
    		}
    	}
    }

    /**
     * @param builder
     */
    public OperationComponentAPIFileResource(FlexoProjectBuilder builder)
    {
        super(builder);
    }

    /**
     * @param aProject
     */
    public OperationComponentAPIFileResource(FlexoProject aProject)
    {
        super(aProject);
    }

	/**
     * Return dependancy computing between this resource, and an other resource,
     * asserting that this resource is contained in this resource's dependant resources
     *
     * @param resource
	 * @param dependancyScheme
     * @return
     */
	@Override
	public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate)
	{
		if (resource instanceof FlexoProcessResource) {
			FlexoProcessResource processRes = (FlexoProcessResource)resource;
			if (processRes.isLoaded()) {
				FlexoProcess concernedProcess = processRes.getResourceData();
				if (getGenerator() != null) {
					boolean iCanBeOptimistic = true;
					for (ComponentInstance ci : getComponentDefinition().getComponentInstances()) {
						if (ci instanceof OperationComponentInstance) {
							OperationNode operationNode = ((OperationComponentInstance)ci).getOperationNode();
							if (operationNode.getProcess() == concernedProcess) {
								if (!operationNode.getLastUpdate().before(requestDate)) {
									iCanBeOptimistic = false;
								}
							}
						}
		    		}
					if (iCanBeOptimistic) return false;
		    	}
			}
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		if (observable == getComponentDefinition()) {
			if (dataModification instanceof ComponentNameChanged2) {
				logger.info("Building new resource after renaming");
				PageComponentGenerator generator = getGenerator();
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
