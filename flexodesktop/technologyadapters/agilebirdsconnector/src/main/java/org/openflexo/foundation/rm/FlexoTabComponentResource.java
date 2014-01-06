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

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.ie.IETabComponent;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.resource.InvalidFileNameException;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.FlexoComponentBuilder;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FlexoTabComponentResource extends FlexoComponentResource {

	private static final Logger logger = Logger.getLogger(FlexoTabComponentResource.class.getPackage().getName());

	private TabComponentDefinition _componentDefinition;

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public FlexoTabComponentResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoTabComponentResource(FlexoProject aProject) {
		super(aProject);
	}

	public FlexoTabComponentResource(FlexoProject aProject, String aName, FlexoComponentLibraryResource clResource,
			FlexoProjectFile componentFile) throws InvalidFileNameException {
		super(aProject, aName, clResource, componentFile);
	}

	@Override
	public String getName() {
		return name;
	}

	public String getLastName() {
		return name;
	}

	@Override
	public void setName(String aName) {
		StringTokenizer st = new StringTokenizer(aName, ".");
		while (st.hasMoreTokens()) {
			name = st.nextToken();
		}
		setChanged();
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.TAB_COMPONENT;
	}

	public IETabComponent getTabComponent() {
		return (IETabComponent) getResourceData();
	}

	@Override
	public TabComponentDefinition getComponentDefinition() {
		if (_componentDefinition == null) {
			_componentDefinition = (TabComponentDefinition) getProject().getFlexoComponentLibrary().getComponentNamed(name);
			if (_componentDefinition == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("cant find an TabComponentDefinition for thumbnail: " + getName() + " in library");
				}
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Trying to repair...");
				}
				FlexoComponentFolder rootFolder = getProject().getFlexoComponentLibrary().getRootFolder();
				try {
					_componentDefinition = new TabComponentDefinition(getName(), getProject().getFlexoComponentLibrary(), rootFolder,
							getProject(), false);
				} catch (DuplicateResourceException e) {
				}
			}
		}
		return _componentDefinition;
	}

	public static String resourceIdentifierForName(String aComponentName) {
		return ResourceType.TAB_COMPONENT.getName() + "." + aComponentName;
	}

	@Override
	public Object instanciateNewBuilder() {
		Object returned = super.instanciateNewBuilder();
		((FlexoComponentBuilder) returned).componentDefinition = getComponentDefinition();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("instanciation of thumbnail builder:"
					+ (((FlexoComponentBuilder) returned).componentDefinition == null ? "WITH NULL DEF" : "with valid def"));
		}
		return returned;
	}
}
