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
package org.openflexo.technologyadapter.diagram.model.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.technologyadapter.diagram.model.Diagram;

public class DeleteDiagram extends FlexoAction<DeleteDiagram, Diagram, FlexoObject> {

	private static final Logger logger = Logger.getLogger(DeleteDiagram.class.getPackage().getName());

	public static FlexoActionType<DeleteDiagram, Diagram, FlexoObject> actionType = new FlexoActionType<DeleteDiagram, Diagram, FlexoObject>(
			"delete_diagram", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteDiagram makeNewAction(Diagram focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
			return new DeleteDiagram(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(Diagram diagram, Vector<FlexoObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(Diagram diagram, Vector<FlexoObject> globalSelection) {
			return diagram != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(DeleteDiagram.actionType, Diagram.class);
	}

	DeleteDiagram(Diagram focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Delete diagram");

		if (getFocusedObject().getResource() != null) {
			getFocusedObject().getResource().delete();
		}
	}

}