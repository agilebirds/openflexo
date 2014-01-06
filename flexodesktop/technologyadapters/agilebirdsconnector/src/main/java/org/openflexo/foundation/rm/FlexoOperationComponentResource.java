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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.ie.IEOperationComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.resource.InvalidFileNameException;
import org.openflexo.foundation.utils.FlexoProjectFile;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FlexoOperationComponentResource extends FlexoComponentResource {

	private static final Logger logger = Logger.getLogger(FlexoOperationComponentResource.class.getPackage().getName());

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public FlexoOperationComponentResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoOperationComponentResource(FlexoProject aProject) {
		super(aProject);
	}

	public FlexoOperationComponentResource(FlexoProject aProject, String aName, FlexoComponentLibraryResource libResource,
			FlexoProjectFile componentFile) throws InvalidFileNameException {
		super(aProject, aName, libResource, componentFile);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.OPERATION_COMPONENT;
	}

	public IEOperationComponent getIEOperationComponent() {
		return (IEOperationComponent) getResourceData();
	}

	private OperationComponentDefinition _componentDefinition;

	@Override
	public OperationComponentDefinition getComponentDefinition() {
		if (_componentDefinition == null) {
			// FlexoProcess process = getProcessResource().getFlexoProcess();
			ComponentDefinition cd = getProject().getFlexoComponentLibrary().getComponentNamed(getName());
			if (cd instanceof OperationComponentDefinition) {
				_componentDefinition = (OperationComponentDefinition) cd;
			}
			if (_componentDefinition == null) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("cant find an OperationComponentDefinition for operation: " + getName() + " in library");
				}
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Trying to repair...");
				}
				FlexoComponentFolder rootFolder = getProject().getFlexoComponentLibrary().getRootFolder();
				try {
					_componentDefinition = new OperationComponentDefinition(getName(), getProject().getFlexoComponentLibrary(), rootFolder,
							getProject(), false);
				} catch (DuplicateResourceException e) {
				}
			}
		}

		return _componentDefinition;
	}

	public static String resourceIdentifierForName(String aComponentName) {
		return ResourceType.OPERATION_COMPONENT.getName() + "." + aComponentName;
	}
}
