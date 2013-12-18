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

import org.flexo.model.TestModelObject;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.view.View;

public class DeleteView extends FlexoAction<DeleteView, View, FlexoObject> {

	private static final Logger logger = Logger.getLogger(DeleteView.class.getPackage().getName());

	public static FlexoActionType<DeleteView, View, FlexoObject> actionType = new FlexoActionType<DeleteView, View, FlexoObject>(
			"delete_view", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteView makeNewAction(View focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
			return new DeleteView(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(View view, Vector<FlexoObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(View view, Vector<FlexoObject> globalSelection) {
			return view != null;
		}

	};

	static {
		TestModelObject.addActionForClass(DeleteView.actionType, View.class);
	}

	DeleteView(View focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Delete view");

		if (getFocusedObject().getResource() != null) {
			getFocusedObject().getResource().delete();
		}
	}

	public FlexoProject getProject() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getProject();
		}
		return null;
	}

}