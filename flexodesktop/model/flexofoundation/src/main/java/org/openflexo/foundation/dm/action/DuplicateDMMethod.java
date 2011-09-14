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
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DuplicateMethodSignatureException;
import org.openflexo.foundation.dm.DMMethod.DMMethodParameter;


public class DuplicateDMMethod extends FlexoAction<DuplicateDMMethod, DMMethod, DMObject> {

	private static final Logger logger = Logger.getLogger(DuplicateDMMethod.class.getPackage().getName());

	public static FlexoActionType<DuplicateDMMethod, DMMethod, DMObject> actionType = new FlexoActionType<DuplicateDMMethod, DMMethod, DMObject>(
			"duplicate_method", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DuplicateDMMethod makeNewAction(DMMethod method, Vector<DMObject> globalSelection, FlexoEditor editor) {
			return new DuplicateDMMethod(method, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(DMMethod method, Vector<DMObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(DMMethod method, Vector<DMObject> globalSelection) {
			return method != null && method.getEntity() != null && !method.getEntity().getIsReadOnly();
		}

	};

	private DMMethod _methodToDuplicate;
	private String _newMethodName;
	private DMMethod _newMethod;

	DuplicateDMMethod(DMMethod methodToDuplicate, Vector<DMObject> globalSelection, FlexoEditor editor) {
		super(actionType, methodToDuplicate, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("DuplicateDMMethod");
		if (getMethodToDuplicate() != null) {
			_newMethodName = getMethodToDuplicate().getEntity().getDMModel().getNextDefautMethodName(getMethodToDuplicate().getEntity());
			_newMethod = new DMMethod(getMethodToDuplicate().getEntity().getDMModel(),/* getEntity(), */_newMethodName);
			_newMethod.setEntity(getMethodToDuplicate().getEntity());
			getMethodToDuplicate().getEntity().registerMethod(_newMethod);
			_newMethod.setReturnType(getMethodToDuplicate().getReturnType(), true);
			for (int i = 0; i < getMethodToDuplicate().getParameters().size(); i++) {
				DMMethodParameter param = getMethodToDuplicate().getParameters().get(i);
				DMMethodParameter newParam;
				try {
					newParam = _newMethod.createNewParameter();
					newParam.setName(param.getName());
					newParam.setType(param.getType());
				} catch (DuplicateMethodSignatureException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public String getNewPropertyName() {
		return _newMethodName;
	}

	public DMMethod getMethodToDuplicate() {
		if (_methodToDuplicate == null) {
			if (getFocusedObject() != null) {
				_methodToDuplicate = getFocusedObject();
			}
		}
		return _methodToDuplicate;
	}

	public DMMethod getNewMethod() {
		return _newMethod;
	}

}
