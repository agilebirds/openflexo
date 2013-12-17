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

import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.inspector.InspectableObject;

public abstract class ImplModelObject extends AgileBirdsObject implements InspectableObject {

	private ImplementationModel implementationModel;
	private String name;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Never use this constructor except for ComponentLibrary
	 */
	public ImplModelObject(FlexoProject project) {
		super(project);
	}

	/**
	 * Default constructor
	 */
	public ImplModelObject(ImplementationModel implementationModel) {
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
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) throws DuplicateResourceException, InvalidNameException {
		if (requireChange(this.name, name)) {
			String oldName = this.name;
			this.name = name;
			setChanged();
			notifyObservers(new NameChanged(oldName, name));
		}
	}
}
