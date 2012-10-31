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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.EOAccessException;

public class CreateDMEOAttribute extends FlexoAction<CreateDMEOAttribute, DMEOEntity, DMObject> {

	private static final Logger logger = Logger.getLogger(CreateDMEOAttribute.class.getPackage().getName());

	public static FlexoActionType<CreateDMEOAttribute, DMEOEntity, DMObject> actionType = new FlexoActionType<CreateDMEOAttribute, DMEOEntity, DMObject>(
			"add_eo_attribute", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateDMEOAttribute makeNewAction(DMEOEntity focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
			return new CreateDMEOAttribute(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(DMEOEntity object, Vector<DMObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(DMEOEntity object, Vector<DMObject> globalSelection) {
			return object != null && !object.getIsReadOnly();
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, DMEOEntity.class);
	}

	private DMEOEntity _entity;
	private String _newAttributeName;
	private DMEOAttribute _newEOAttribute;

	CreateDMEOAttribute(DMEOEntity focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws EOAccessException {
		logger.info("CreateDMEOAttribute");
		if (getEntity() != null) {
			_newAttributeName = getEntity().getDMModel().getNextDefautAttributeName(getEntity());
			_newEOAttribute = DMEOAttribute.createsNewDMEOAttribute(getEntity().getDMModel(), getEntity(), _newAttributeName, false, true,
					DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD);
			getEntity().registerProperty(_newEOAttribute);
		}
	}

	public String getNewAttributeName() {
		return _newAttributeName;
	}

	public DMEOEntity getEntity() {
		if (_entity == null) {
			if (getFocusedObject() != null) {
				_entity = getFocusedObject();
			}
		}
		return _entity;
	}

	public DMEOAttribute getNewEOAttribute() {
		return _newEOAttribute;
	}

}
