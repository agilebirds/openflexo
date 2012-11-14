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
package org.openflexo.foundation.param;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.TabModel;
import org.openflexo.xmlcode.XMLMapping;

public class ParametersModel extends TemporaryFlexoModelObject implements InspectableObject {

	static final Logger logger = Logger.getLogger(ParametersModel.class.getPackage().getName());

	// Hashtable of parameter definitions
	private Hashtable _parameters;

	private FlexoProject _project;

	public ParametersModel(FlexoProject project, ParameterDefinition[] params) {
		super();
		_project = project;
		_parameters = new Hashtable();
		_tabModel = new TabModel();
		_tabModel.name = "Parameters";
		for (int i = 0; i < params.length; i++) {
			ParameterDefinition next = params[i];
			_parameters.put(params[i].getName(), params[i]);
			next.getPropertyModel().constraint = i;
			_tabModel.setPropertyForKey(next.getPropertyModel(), next.getName());
		}
		_params = new ParamsModel();
		_specialObjects = new Hashtable<String, Object>();
	}

	public ParameterDefinition parameterForKey(String aKey) {
		return (ParameterDefinition) _parameters.get(aKey);
	}

	private ParamsModel _params;

	public ParamsModel getParams() {
		return _params;
	}

	public void setParams(ParamsModel model) {
		// interface
	}

	protected class ParamsModel extends FlexoObject {
		@Override
		public Object objectForKey(String key) {
			return parameterForKey(key);
		}
	}

	private Hashtable<String, Object> _specialObjects;

	/**
	 * Adds an objects to "special" objects in order to be access through KV-coding in inspector definitions
	 * 
	 * @param anObject
	 * @param aKey
	 */
	public void addObjectForKey(Object anObject, String aKey) {
		_specialObjects.put(aKey, anObject);
	}

	// Objects interface

	@Override
	public Object objectForKey(String key) {
		ParameterDefinition param = parameterForKey(key);
		if (param != null) {
			return param.getValue();
		} else if (_specialObjects.get(key) != null) {
			return _specialObjects.get(key);
		} else {
			return super.objectForKey(key);
		}
	}

	@Override
	public void setObjectForKey(Object value, String key) {
		ParameterDefinition param = parameterForKey(key);
		if (param != null) {
			param.setValue(value);
		} else {
			super.setObjectForKey(value, key);
		}
	}

	@Override
	public boolean booleanValueForKey(String key) {
		ParameterDefinition param = parameterForKey(key);
		if (param != null) {
			return param.getBooleanValue();
		} else {
			return super.booleanValueForKey(key);
		}
	}

	@Override
	public void setBooleanValueForKey(boolean value, String key) {
		ParameterDefinition param = parameterForKey(key);
		if (param != null) {
			param.setBooleanValue(value);
		} else {
			super.setBooleanValueForKey(value, key);
		}
	}

	@Override
	public int integerValueForKey(String key) {
		ParameterDefinition param = parameterForKey(key);
		if (param != null) {
			return param.getIntegerValue();
		} else {
			return super.integerValueForKey(key);
		}

	}

	@Override
	public void setIntegerValueForKey(int value, String key) {
		ParameterDefinition param = parameterForKey(key);
		if (param != null) {
			param.setIntegerValue(value);
		} else {
			super.setIntegerValueForKey(value, key);
		}

	}

	// Retrieving type

	@Override
	public Class getTypeForKey(String key) {
		ParameterDefinition param = parameterForKey(key);
		if (param != null && param.getValue() != null) {
			return param.getValue().getClass();
		}
		return null;

	}

	@Override
	public String getInspectorName() {
		return null;
	}

	private TabModel _tabModel = null;

	public TabModel getTabModel() {
		return _tabModel;
	}

	@Override
	public FlexoProject getProject() {
		return _project;
	}

	@Override
	public String getFullyQualifiedName() {
		return null;
	}

	@Override
	public XMLMapping getXMLMapping() {
		return null;
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return null;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "parameter_model";
	}

}
