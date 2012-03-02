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
package org.openflexo.foundation.sg.implmodel;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.xmlcode.XMLMapping;

public abstract class TechnologyModelObject extends FlexoModelObject implements InspectableObject {

	private ImplementationModel implementationModel;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Never use this constructor except for ComponentLibrary
	 */
	public TechnologyModelObject(FlexoProject project) {
		super(project);
	}

	/**
	 * Default constructor
	 */
	public TechnologyModelObject(ImplementationModel implementationModel) {
		this(implementationModel.getProject());
		setImplementationModel(implementationModel);
	}

	public ImplementationModel getImplementationModel() {
		return implementationModel;
	}

	public void setImplementationModel(ImplementationModel implementationModel) {
		this.implementationModel = implementationModel;
	}

	/**
	 * Returns reference to the main object in which this XML-serializable object is contained relating to storing scheme: here it's the
	 * implementation model
	 * 
	 * @return the component library
	 */
	@Override
	public ImplementationModel getXMLResourceData() {
		return getImplementationModel();
	}

	@Override
	public XMLMapping getXMLMapping() {
		return getImplementationModel().getXMLMapping();
	}

	@Override
	public void setName(String name) {
		if (requireChange(getName(), name)) {
			try {
				super.setName(name);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getInspectorName() {
		if (getHasInspector()) {
			return this.getClass().getSimpleName() + ".inspector";
		}
		return null;
	}

	/**
	 * Return the TechnologyModuleImplementation which this object belongs. <br>
	 * TODO: store the TechnologyModuleImplementation here instead of ImplementationModel
	 * 
	 * @return the TechnologyModuleImplementation which this object belongs.
	 */
	public abstract TechnologyModuleImplementation getTechnologyModuleImplementation();

	/**
	 * Specify if this object has a specific inspector, in this case return true. Otherwise return false. <br>
	 * Used in the default implementation of getInspectorName. If this method is overrided, it may not be used.
	 * 
	 * @return true if this object has a specific inspector.
	 */
	public abstract boolean getHasInspector();
}
