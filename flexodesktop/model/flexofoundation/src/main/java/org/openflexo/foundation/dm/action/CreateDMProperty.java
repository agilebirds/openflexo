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
package org.openflexo.foundation.dm.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMCardinality;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.ProcessDMEntity;

public class CreateDMProperty extends FlexoAction<CreateDMProperty, DMEntity, DMObject> {

	private static final Logger logger = Logger.getLogger(CreateDMProperty.class.getPackage().getName());

	public static FlexoActionType<CreateDMProperty, DMEntity, DMObject> actionType = new FlexoActionType<CreateDMProperty, DMEntity, DMObject>(
			"add_property", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateDMProperty makeNewAction(DMEntity focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
			return new CreateDMProperty(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(DMEntity object, Vector<DMObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(DMEntity object, Vector<DMObject> globalSelection) {
			return ((object != null) && (!object.getIsReadOnly()) && !(object instanceof ProcessDMEntity));
		}

	};

	private DMEntity _entity;
	private String _newPropertyName;
	private DMProperty _newProperty;
	private boolean _newPropertyIsBindable = false;

	CreateDMProperty(DMEntity focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("CreateDMProperty");
		if (getEntity() != null) {
			if (_newPropertyName == null)
				_newPropertyName = getEntity().getDMModel().getNextDefautPropertyName(getEntity());
			_newProperty = new DMProperty(getEntity().getDMModel(), /*getEntity(),*/_newPropertyName, null, DMCardinality.SINGLE,
					getEntity().getIsReadOnly(), true, getEntity().getPropertyDefaultImplementationType());
			getEntity().registerProperty(_newProperty, _newPropertyIsBindable);
		}
	}

	public String getNewPropertyName() {
		return _newPropertyName;
	}

	public void setNewPropertyName(String value) {
		_newPropertyName = value;
	}

	public DMEntity getEntity() {
		if (_entity == null) {
			if (getFocusedObject() != null) {
				_entity = getFocusedObject();
			}
		}
		return _entity;
	}

	public DMProperty getNewProperty() {
		return _newProperty;
	}

	public void setIsBindable(boolean v) {
		_newPropertyIsBindable = v;
	}

	public boolean getIsBindable() {
		return _newPropertyIsBindable;
	}

}
