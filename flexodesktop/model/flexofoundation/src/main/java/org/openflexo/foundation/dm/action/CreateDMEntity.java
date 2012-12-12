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
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.ComponentRepository;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.ProcessInstanceRepository;
import org.openflexo.foundation.dm.WORepository;

public class CreateDMEntity extends FlexoAction {

	private static final Logger logger = Logger.getLogger(CreateDMEntity.class.getPackage().getName());

	public static FlexoActionType actionType = new FlexoActionType("add_entity", FlexoActionType.newMenu, FlexoActionType.defaultGroup,
			FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new CreateDMEntity(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoObject object, Vector globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoObject object, Vector globalSelection) {
			return object != null && object instanceof DMPackage && ((DMPackage) object).getRepository() != null
					&& !(((DMPackage) object).getRepository() instanceof ComponentRepository)
					&& !(((DMPackage) object).getRepository() instanceof WORepository)
					&& !(((DMPackage) object).getRepository() instanceof ProcessInstanceRepository)
					&& !((DMPackage) object).getRepository().isReadOnly();
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, DMPackage.class);
	}

	private DMPackage _package;
	private String _newEntityName;
	private DMEntity _newEntity;

	CreateDMEntity(FlexoObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("CreateDMEntity");
		if (getPackage() != null) {
			_newEntityName = _package.getDMModel().getNextDefautEntityName(_package);
			_newEntity = new DMEntity(_package.getDMModel(), _newEntityName, _package.getName(), _newEntityName, null);
			getRepository().registerEntity(_newEntity);
		}
	}

	public String getNewEntityName() {
		return _newEntityName;
	}

	public DMPackage getPackage() {
		if (_package == null) {
			if (getFocusedObject() != null && getFocusedObject() instanceof DMPackage) {
				_package = (DMPackage) getFocusedObject();
			}
		}
		return _package;
	}

	public DMRepository getRepository() {
		return getPackage().getRepository();
	}

	public DMEntity getNewEntity() {
		return _newEntity;
	}

}
