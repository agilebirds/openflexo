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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMObject;

public class CreateDMMethod extends FlexoAction<CreateDMMethod, DMEntity, DMObject> {

	private static final Logger logger = Logger.getLogger(CreateDMMethod.class.getPackage().getName());

	public static FlexoActionType<CreateDMMethod, DMEntity, DMObject> actionType = new FlexoActionType<CreateDMMethod, DMEntity, DMObject>(
			"add_method", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateDMMethod makeNewAction(DMEntity entity, Vector<DMObject> globalSelection, FlexoEditor editor) {
			return new CreateDMMethod(entity, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DMEntity entity, Vector<DMObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DMEntity entity, Vector<DMObject> globalSelection) {
			return ((entity != null) && (!entity.getIsReadOnly()));
		}

	};

	private DMEntity _entity;
	private String _newMethodName;
	private DMMethod _newMethod;

	CreateDMMethod(DMEntity entity, Vector<DMObject> globalSelection, FlexoEditor editor) {
		super(actionType, entity, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("CreateDMMethod");
		}
		if (getEntity() != null) {
			if (_newMethodName == null || getEntity().getMethod(_newMethodName + "()") != null) {
				_newMethodName = getEntity().getDMModel().getNextDefautMethodName(getEntity());
			}
			_newMethod = new DMMethod(getEntity().getDMModel(),/* getEntity(),*/_newMethodName);
			_newMethod.setEntity(getEntity());
			getEntity().registerMethod(_newMethod);
		}
	}

	public String getNewMethodName() {
		return _newMethodName;
	}

	public void setNewMethodName(String name) {
		this._newMethodName = name;
	}

	public DMEntity getEntity() {
		if (_entity == null) {
			if (getFocusedObject() != null) {
				_entity = getFocusedObject();
			}
		}
		return _entity;
	}

	public DMMethod getNewMethod() {
		return _newMethod;
	}

}
