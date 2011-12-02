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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.viewpoint.ExampleDrawingShema;
import org.openflexo.foundation.viewpoint.ViewPointObject;

public class DeleteExampleDrawing extends FlexoAction<DeleteExampleDrawing, ExampleDrawingShema, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(DeleteExampleDrawing.class.getPackage().getName());

	public static FlexoActionType<DeleteExampleDrawing, ExampleDrawingShema, ViewPointObject> actionType = new FlexoActionType<DeleteExampleDrawing, ExampleDrawingShema, ViewPointObject>(
			"delete_calc_drawing_shema", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteExampleDrawing makeNewAction(ExampleDrawingShema focusedObject, Vector<ViewPointObject> globalSelection,
				FlexoEditor editor) {
			return new DeleteExampleDrawing(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ExampleDrawingShema object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

		@Override
		protected boolean isEnabledForSelection(ExampleDrawingShema object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeleteExampleDrawing.actionType, ExampleDrawingShema.class);
	}

	DeleteExampleDrawing(ExampleDrawingShema focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Delete calc drawing shema");

		getFocusedObject().delete();
	}

}