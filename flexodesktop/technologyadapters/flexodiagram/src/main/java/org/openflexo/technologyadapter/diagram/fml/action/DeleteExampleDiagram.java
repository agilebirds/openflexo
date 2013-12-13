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
package org.openflexo.technologyadapter.diagram.fml.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.technologyadapter.diagram.fml.ExampleDiagram;

public class DeleteExampleDiagram extends FlexoAction<DeleteExampleDiagram, ExampleDiagram, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(DeleteExampleDiagram.class.getPackage().getName());

	public static FlexoActionType<DeleteExampleDiagram, ExampleDiagram, ViewPointObject> actionType = new FlexoActionType<DeleteExampleDiagram, ExampleDiagram, ViewPointObject>(
			"delete_example_diagram", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteExampleDiagram makeNewAction(ExampleDiagram focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
			return new DeleteExampleDiagram(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ExampleDiagram object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(ExampleDiagram object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeleteExampleDiagram.actionType, ExampleDiagram.class);
	}

	DeleteExampleDiagram(ExampleDiagram focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Delete example diagram");
		if (getFocusedObject().getResource() != null) {
			getFocusedObject().getResource().delete();
		}
	}

}