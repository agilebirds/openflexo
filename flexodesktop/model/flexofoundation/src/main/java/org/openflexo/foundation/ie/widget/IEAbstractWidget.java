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
package org.openflexo.foundation.ie.widget;

import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Abstract class defining the common behaviour of a widget. Subclassed in IEDSWidget (a widget in the palette) and IEWidget (a widget
 * embedded in a WOComponent)
 * 
 * @author bmangez
 */
public abstract class IEAbstractWidget extends IEObject {

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	protected String _name;

	public IEAbstractWidget(FlexoProject project) {
		super(project);
	}

	// ==========================================================================
	// ============================= Accessors
	// ==================================
	// ==========================================================================

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String name) {
		_name = name;
		setChanged();
		notifyObservers(new IEDataModification("name", null, name));
	}

}
