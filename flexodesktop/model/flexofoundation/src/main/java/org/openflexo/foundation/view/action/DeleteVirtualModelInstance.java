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
package org.openflexo.foundation.view.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.view.VirtualModelInstance;

public class DeleteVirtualModelInstance extends FlexoAction<DeleteVirtualModelInstance, VirtualModelInstance, FlexoObject> {

	private static final Logger logger = Logger.getLogger(DeleteVirtualModelInstance.class.getPackage().getName());

	public static FlexoActionType<DeleteVirtualModelInstance, VirtualModelInstance, FlexoObject> actionType = new FlexoActionType<DeleteVirtualModelInstance, VirtualModelInstance, FlexoObject>(
			"delete_virtual_model_instance", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteVirtualModelInstance makeNewAction(VirtualModelInstance focusedObject, Vector<FlexoObject> globalSelection,
				FlexoEditor editor) {
			return new DeleteVirtualModelInstance(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(VirtualModelInstance vmi, Vector<FlexoObject> globalSelection) {
			return vmi != null && vmi.getClass().equals(VirtualModelInstance.class); // Only if class is not specialized
		}

		@Override
		public boolean isEnabledForSelection(VirtualModelInstance vmi, Vector<FlexoObject> globalSelection) {
			return isVisibleForSelection(vmi, globalSelection);
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(DeleteVirtualModelInstance.actionType, VirtualModelInstance.class);
	}

	DeleteVirtualModelInstance(VirtualModelInstance focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Delete virtual model instance");

		if (getFocusedObject().getResource() != null) {
			getFocusedObject().getResource().delete();
		}
	}
}