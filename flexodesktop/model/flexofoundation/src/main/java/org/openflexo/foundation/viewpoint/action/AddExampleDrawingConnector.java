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

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.viewpoint.ExampleDrawingConnector;
import org.openflexo.foundation.viewpoint.ExampleDrawingObject;
import org.openflexo.foundation.viewpoint.ExampleDrawingShape;

public class AddExampleDrawingConnector extends FlexoAction<AddExampleDrawingConnector, ExampleDrawingShape, ExampleDrawingObject> {

	private static final Logger logger = Logger.getLogger(AddExampleDrawingConnector.class.getPackage().getName());

	public static FlexoActionType<AddExampleDrawingConnector, ExampleDrawingShape, ExampleDrawingObject> actionType = new FlexoActionType<AddExampleDrawingConnector, ExampleDrawingShape, ExampleDrawingObject>(
			"add_connector", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddExampleDrawingConnector makeNewAction(ExampleDrawingShape focusedObject, Vector<ExampleDrawingObject> globalSelection,
				FlexoEditor editor) {
			return new AddExampleDrawingConnector(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ExampleDrawingShape shape, Vector<ExampleDrawingObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(ExampleDrawingShape shape, Vector<ExampleDrawingObject> globalSelection) {
			return shape != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(AddExampleDrawingConnector.actionType, ExampleDrawingShape.class);
	}

	private ExampleDrawingShape _fromShape;
	public ExampleDrawingShape toShape;
	public String newConnectorName;
	public String annotation;
	public ConnectorGraphicalRepresentation<?> graphicalRepresentation;

	private ExampleDrawingConnector _newConnector;

	AddExampleDrawingConnector(ExampleDrawingShape focusedObject, Vector<ExampleDrawingObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Add connector");
		if (getFocusedObject() != null && getFromShape() != null && toShape != null) {
			ExampleDrawingObject parent = ExampleDrawingObject.getFirstCommonAncestor(getFromShape(), toShape);
			logger.info("Parent=" + parent);
			if (parent == null) {
				throw new IllegalArgumentException("No common ancestor");
			}
			_newConnector = new ExampleDrawingConnector(getFromShape(), toShape);
			if (graphicalRepresentation != null) {
				_newConnector.setGraphicalRepresentation(graphicalRepresentation);
			}
			_newConnector.setName(newConnectorName);
			_newConnector.setDescription(annotation);
			parent.addToChilds(_newConnector);
		} else {
			logger.warning("Focused shape is null !");
		}
	}

	public ExampleDrawingShape getFromShape() {
		if (_fromShape == null) {
			return getFocusedObject();
		}
		return _fromShape;
	}

	public void setFromShape(ExampleDrawingShape fromShape) {
		_fromShape = fromShape;
	}

	public ExampleDrawingConnector getNewConnector() {
		return _newConnector;
	}

}
