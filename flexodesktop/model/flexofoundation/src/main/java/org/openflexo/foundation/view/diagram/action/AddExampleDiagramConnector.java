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

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramConnector;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramObject;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramShape;

public class AddExampleDiagramConnector extends FlexoAction<AddExampleDiagramConnector, ExampleDiagramShape, ExampleDiagramObject> {

	private static final Logger logger = Logger.getLogger(AddExampleDiagramConnector.class.getPackage().getName());

	public static FlexoActionType<AddExampleDiagramConnector, ExampleDiagramShape, ExampleDiagramObject> actionType = new FlexoActionType<AddExampleDiagramConnector, ExampleDiagramShape, ExampleDiagramObject>(
			"add_connector", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddExampleDiagramConnector makeNewAction(ExampleDiagramShape focusedObject, Vector<ExampleDiagramObject> globalSelection,
				FlexoEditor editor) {
			return new AddExampleDiagramConnector(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ExampleDiagramShape shape, Vector<ExampleDiagramObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ExampleDiagramShape shape, Vector<ExampleDiagramObject> globalSelection) {
			return shape != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(AddExampleDiagramConnector.actionType, ExampleDiagramShape.class);
	}

	private ExampleDiagramShape _fromShape;
	public ExampleDiagramShape toShape;
	public String newConnectorName;
	public String annotation;
	public ConnectorGraphicalRepresentation<?> graphicalRepresentation;

	private ExampleDiagramConnector _newConnector;

	AddExampleDiagramConnector(ExampleDiagramShape focusedObject, Vector<ExampleDiagramObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Add connector");
		if (getFocusedObject() != null && getFromShape() != null && toShape != null) {
			ExampleDiagramObject parent = ExampleDiagramObject.getFirstCommonAncestor(getFromShape(), toShape);
			logger.info("Parent=" + parent);
			if (parent == null) {
				throw new IllegalArgumentException("No common ancestor");
			}
			_newConnector = new ExampleDiagramConnector(getFromShape(), toShape);
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

	public ExampleDiagramShape getFromShape() {
		if (_fromShape == null) {
			return getFocusedObject();
		}
		return _fromShape;
	}

	public void setFromShape(ExampleDiagramShape fromShape) {
		_fromShape = fromShape;
	}

	public ExampleDiagramConnector getNewConnector() {
		return _newConnector;
	}

}
