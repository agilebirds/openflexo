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

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramObject;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramShape;

public class AddExampleDrawingShape extends FlexoAction<AddExampleDrawingShape, ExampleDiagramObject, ExampleDiagramObject> {

	private static final Logger logger = Logger.getLogger(AddExampleDrawingShape.class.getPackage().getName());

	public static FlexoActionType<AddExampleDrawingShape, ExampleDiagramObject, ExampleDiagramObject> actionType = new FlexoActionType<AddExampleDrawingShape, ExampleDiagramObject, ExampleDiagramObject>(
			"add_new_shape", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddExampleDrawingShape makeNewAction(ExampleDiagramObject focusedObject, Vector<ExampleDiagramObject> globalSelection,
				FlexoEditor editor) {
			return new AddExampleDrawingShape(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ExampleDiagramObject object, Vector<ExampleDiagramObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ExampleDiagramObject object, Vector<ExampleDiagramObject> globalSelection) {
			return object instanceof ExampleDiagram || object instanceof ExampleDiagramShape;
		}

	};

	static {
		FlexoModelObject.addActionForClass(AddExampleDrawingShape.actionType, ExampleDiagram.class);
		FlexoModelObject.addActionForClass(AddExampleDrawingShape.actionType, ExampleDiagramShape.class);
	}

	private ExampleDiagramShape _newShape;
	public String newShapeName;
	private ExampleDiagramObject _parent;
	public ShapeGraphicalRepresentation<?> graphicalRepresentation;
	public boolean nameSetToNull = false;

	AddExampleDrawingShape(ExampleDiagramObject focusedObject, Vector<ExampleDiagramObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws NotImplementedException, InvalidParametersException {
		logger.info("Add shape");

		if (getParent() == null) {
			throw new InvalidParametersException("folder is undefined");
		}

		_newShape = new ExampleDiagramShape(null);
		if (graphicalRepresentation != null) {
			_newShape.setGraphicalRepresentation(graphicalRepresentation);
		}

		_newShape.setName(newShapeName);
		getParent().addToChilds(_newShape);

		logger.info("Added shape " + _newShape + " under " + getParent());
	}

	public ExampleDiagramShape getNewShape() {
		return _newShape;
	}

	public ExampleDiagramObject getParent() {
		if (_parent == null) {
			if (getFocusedObject() instanceof ExampleDiagramShape) {
				_parent = getFocusedObject();
			} else if (getFocusedObject() instanceof ExampleDiagram) {
				_parent = getFocusedObject();
			}
		}
		return _parent;
	}

	public void setParent(ExampleDiagramObject parent) {
		_parent = parent;
	}

}
