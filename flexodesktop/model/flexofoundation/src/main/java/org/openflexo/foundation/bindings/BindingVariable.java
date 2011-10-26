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
package org.openflexo.foundation.bindings;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.Typed;
import org.openflexo.foundation.dm.dm.DMEntityClassNameChanged;
import org.openflexo.foundation.dm.dm.DMObjectDeleted;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.xmlcode.XMLMapping;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class BindingVariable extends FlexoModelObject implements Typed {

	private DMModel _dataModel;

	private Bindable _container;

	private String variableName;

	private DMType _type;

	public BindingVariable(Bindable container, DMModel dataModel, String description) {
		super(dataModel.getProject(), description);
		_dataModel = dataModel;
		_container = container;
	}

	public Bindable getContainer() {
		return _container;
	}

	@Override
	public FlexoProject getProject() {
		return _dataModel.getProject();
	}

	@Override
	public XMLMapping getXMLMapping() {
		return ((FlexoModelObject) _container).getXMLMapping();
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		if (_container != null)
			return ((FlexoModelObject) _container).getXMLResourceData();
		return null;
	}

	@Override
	public DMType getType() {
		return _type;
	}

	@Override
	public void setType(DMType type) {
		if ((type == null && _type != null) || (type != null && !type.equals(_type))) {
			DMType oldType = _type;
			if (oldType != null) {
				oldType.removeFromTypedWithThisType(this);
			}
			_type = type;
			if (type != null) {
				type.addToTypedWithThisType(this);
			}
			// setChanged();
		}
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String aVariableName) {
		this.variableName = aVariableName;
		// setChanged();
	}

	public String getTypeName() {
		if (getType() != null) {
			return getType().getStringRepresentation();
		}
		return null;
	}

	public void setTypeName(String aFullQualifiedName) {
		setType(DMType.makeResolvedDMType(_dataModel.getDMEntity(aFullQualifiedName)));
	}

	@Override
	public String getFullyQualifiedName() {
		return "BINDING_VARIABLE." + getVariableName() + "." + getTypeName();
	}

	@Override
	public String toString() {
		return getFullyQualifiedName();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "binding_variable";
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof DMEntityClassNameChanged && observable == getType().getBaseEntity()) {
			// do nothing : no cached code
		} else if (dataModification instanceof DMObjectDeleted && observable == getType().getBaseEntity()) {
			// do nothing : no cached code
		}
	}

	public String getJavaAccess() {
		return "get" + getVariableName().substring(0, 1).toUpperCase() + getVariableName().substring(1) + "()";
	}
}
