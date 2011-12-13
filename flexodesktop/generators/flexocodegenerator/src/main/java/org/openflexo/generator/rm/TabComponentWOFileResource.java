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
import java.util.logging.Logger;

import org.openflexo.foundation.ie.OperationComponentInstance;
import org.openflexo.foundation.rm.FlexoProcessResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.generator.ie.TabComponentGenerator;
import org.openflexo.logging.FlexoLogger;

/**
 * @author sylvain
 * 
 */
public class TabComponentWOFileResource extends ComponentWOFileResource<TabComponentGenerator> implements TabComponentFileResource {
	protected static final Logger logger = FlexoLogger.getLogger(TabComponentWOFileResource.class.getPackage().getName());

	public void registerObserverWhenRequired() {
		if ((!isObserverRegistered) && (getComponentDefinition() != null)) {
			isObserverRegistered = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("*** addObserver " + getFileName() + " for " + getComponentDefinition());
			}
			getComponentDefinition().addObserver(this);
		}
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		if (getGenerator() != null) {
			for (OperationComponentInstance ci : getGenerator().getComponentDefinition().getAllOperationComponentInstances()) {
				addToDependentResources(ci.getOperationNode().getProcess().getFlexoResource());
			}
		}
	}

	/**
	 * @param builder
	 */
	public TabComponentWOFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	/**
	 * @param aProject
	 */
	public TabComponentWOFileResource(FlexoProject aProject) {
		super(aProject);
	}

	/**
	 * Return dependancy computing between this resource, and an other resource, asserting that this resource is contained in this
	 * resource's dependant resources
	 * 
	 * @param resource
	 * @param dependancyScheme
	 * @return
	 */
	@Override
	public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate) {
		if (resource instanceof FlexoProcessResource) {
			FlexoProcessResource processRes = (FlexoProcessResource) resource;
			if (processRes.isLoaded()) {
				FlexoProcess concernedProcess = processRes.getResourceData();
				if (getGenerator() != null) {
					boolean iCanBeOptimistic = true;
					for (OperationComponentInstance ci : getGenerator().getComponentDefinition().getAllOperationComponentInstances()) {
						OperationNode operationNode = ci.getOperationNode();
						if (operationNode.getProcess() == concernedProcess) {
							if (!operationNode.getLastUpdate().before(requestDate)) {
								iCanBeOptimistic = false;
							}
						}
					}
					if (iCanBeOptimistic) {
						return false;
					}
				}
			}
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}

}
