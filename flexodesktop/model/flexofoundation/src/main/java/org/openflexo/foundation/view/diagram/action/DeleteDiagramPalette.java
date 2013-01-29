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
package org.openflexo.foundation.view.diagram.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.viewpoint.ViewPointObject;

public class DeleteDiagramPalette extends FlexoAction<DeleteDiagramPalette, DiagramPalette, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(DeleteDiagramPalette.class.getPackage().getName());

	public static FlexoActionType<DeleteDiagramPalette, DiagramPalette, ViewPointObject> actionType = new FlexoActionType<DeleteDiagramPalette, DiagramPalette, ViewPointObject>(
			"delete_calc_palette", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteDiagramPalette makeNewAction(DiagramPalette focusedObject, Vector<ViewPointObject> globalSelection,
				FlexoEditor editor) {
			return new DeleteDiagramPalette(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DiagramPalette object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(DiagramPalette object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeleteDiagramPalette.actionType, DiagramPalette.class);
	}

	DeleteDiagramPalette(DiagramPalette focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Delete calc palette");

		getFocusedObject().delete();
	}

}