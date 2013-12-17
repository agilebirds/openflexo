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
package org.openflexo.foundation.viewpoint.action;

import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.flexo.model.FlexoModelObject;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.VirtualModel;

public class DeleteVirtualModel extends FlexoAction<DeleteVirtualModel, VirtualModel, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(DeleteVirtualModel.class.getPackage().getName());

	public static FlexoActionType<DeleteVirtualModel, VirtualModel, ViewPointObject> actionType = new FlexoActionType<DeleteVirtualModel, VirtualModel, ViewPointObject>(
			"delete_virtual_model", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteVirtualModel makeNewAction(VirtualModel focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
			return new DeleteVirtualModel(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(VirtualModel object, Vector<ViewPointObject> globalSelection) {
			return object != null && object.getClass().equals(VirtualModel.class);
		}

		@Override
		public boolean isEnabledForSelection(VirtualModel object, Vector<ViewPointObject> globalSelection) {
			return isVisibleForSelection(object, globalSelection);
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeleteVirtualModel.actionType, VirtualModel.class);
	}

	DeleteVirtualModel(VirtualModel focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException {
		logger.info("Delete VirtualModel");
		if (getFocusedObject().getResource() != null) {
			getFocusedObject().getResource().delete();
		}
	}

}