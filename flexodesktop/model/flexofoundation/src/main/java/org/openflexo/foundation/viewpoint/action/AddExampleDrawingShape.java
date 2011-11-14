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
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.viewpoint.ExampleDrawingObject;
import org.openflexo.foundation.viewpoint.ExampleDrawingShape;
import org.openflexo.foundation.viewpoint.ExampleDrawingShema;

public class AddExampleDrawingShape extends FlexoAction<AddExampleDrawingShape, ExampleDrawingObject, ExampleDrawingObject> {

	private static final Logger logger = Logger.getLogger(AddExampleDrawingShape.class.getPackage().getName());

	public static FlexoActionType<AddExampleDrawingShape, ExampleDrawingObject, ExampleDrawingObject> actionType = new FlexoActionType<AddExampleDrawingShape, ExampleDrawingObject, ExampleDrawingObject>(
			"add_new_shape", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddExampleDrawingShape makeNewAction(ExampleDrawingObject focusedObject, Vector<ExampleDrawingObject> globalSelection,
				FlexoEditor editor) {
			return new AddExampleDrawingShape(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ExampleDrawingObject object, Vector<ExampleDrawingObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(ExampleDrawingObject object, Vector<ExampleDrawingObject> globalSelection) {
			return (object instanceof ExampleDrawingShema || object instanceof ExampleDrawingShape);
		}

	};

	static {
		FlexoModelObject.addActionForClass(AddExampleDrawingShape.actionType, ExampleDrawingShema.class);
		FlexoModelObject.addActionForClass(AddExampleDrawingShape.actionType, ExampleDrawingShape.class);
	}

	private ExampleDrawingShape _newShape;
	public String newShapeName;
	private ExampleDrawingObject _parent;
	public Object graphicalRepresentation;
	public boolean nameSetToNull = false;

	AddExampleDrawingShape(ExampleDrawingObject focusedObject, Vector<ExampleDrawingObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws NotImplementedException, InvalidParametersException {
		logger.info("Add shape");

		if (getParent() == null) {
			throw new InvalidParametersException("folder is undefined");
		}
		if (newShapeName == null && !nameSetToNull) {
			throw new InvalidParametersException("shape name is undefined");
		}

		_newShape = new ExampleDrawingShape();
		if (graphicalRepresentation != null) {
			_newShape.setGraphicalRepresentation(graphicalRepresentation);
		}

		_newShape.setName(newShapeName);
		getParent().addToChilds(_newShape);

		logger.info("Added shape " + _newShape + " under " + getParent());
	}

	public ExampleDrawingShape getNewShape() {
		return _newShape;
	}

	public ExampleDrawingObject getParent() {
		if (_parent == null) {
			if (getFocusedObject() instanceof ExampleDrawingShape) {
				_parent = getFocusedObject();
			} else if (getFocusedObject() instanceof ExampleDrawingShema) {
				_parent = getFocusedObject();
			}
		}
		return _parent;
	}

	public void setParent(ExampleDrawingObject parent) {
		_parent = parent;
	}

}