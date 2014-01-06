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

import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramContainerElement;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;
import org.openflexo.technologyadapter.diagram.model.DiagramShape;

public class AddShape extends FlexoAction<AddShape, DiagramContainerElement<?>, DiagramElement<?>> {

	private static final Logger logger = Logger.getLogger(AddShape.class.getPackage().getName());

	public static FlexoActionType<AddShape, DiagramContainerElement<?>, DiagramElement<?>> actionType = new FlexoActionType<AddShape, DiagramContainerElement<?>, DiagramElement<?>>(
			"add_new_shape", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddShape makeNewAction(DiagramContainerElement<?> focusedObject, Vector<DiagramElement<?>> globalSelection,
				FlexoEditor editor) {
			return new AddShape(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DiagramContainerElement<?> object, Vector<DiagramElement<?>> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DiagramContainerElement<?> object, Vector<DiagramElement<?>> globalSelection) {
			return object instanceof Diagram || object instanceof DiagramShape;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(AddShape.actionType, Diagram.class);
		FlexoObjectImpl.addActionForClass(AddShape.actionType, DiagramShape.class);
	}

	private DiagramShape newShape;
	private String newShapeName;
	private DiagramContainerElement<?> parent;
	private ShapeGraphicalRepresentation graphicalRepresentation;
	private boolean nameSetToNull = false;

	AddShape(DiagramContainerElement<?> focusedObject, Vector<DiagramElement<?>> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws NotImplementedException, InvalidParameterException {
		logger.info("Add shape");

		if (getParent() == null) {
			throw new InvalidParameterException("folder is undefined");
		}
		if (getNewShapeName() == null && !isNameSetToNull()) {
			throw new InvalidParameterException("shape name is undefined");
		}

		newShape = getFocusedObject().getDiagram().getDiagramFactory().makeNewShape(getNewShapeName(), getParent());

		if (getGraphicalRepresentation() != null) {
			newShape.setGraphicalRepresentation(getGraphicalRepresentation());
		}

		getParent().addToShapes(newShape);

		logger.info("Added shape " + newShape + " under " + getParent());
	}

	public DiagramShape getNewShape() {
		return newShape;
	}

	public DiagramContainerElement<?> getParent() {
		if (parent == null) {
			parent = getFocusedObject();
		}
		return parent;
	}

	public void setParent(DiagramContainerElement<?> parent) {
		this.parent = parent;
	}

	public String getNewShapeName() {
		return newShapeName;
	}

	public void setNewShapeName(String newShapeName) {
		this.newShapeName = newShapeName;
	}

	public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(ShapeGraphicalRepresentation graphicalRepresentation) {
		this.graphicalRepresentation = graphicalRepresentation;
	}

	public boolean isNameSetToNull() {
		return nameSetToNull;
	}

	public void setNameSetToNull(boolean nameSetToNull) {
		this.nameSetToNull = nameSetToNull;
	}

}
