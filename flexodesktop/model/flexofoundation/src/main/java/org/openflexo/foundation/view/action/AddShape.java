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

import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;

public class AddShape extends FlexoAction<AddShape, ViewObject, ViewObject> {

	private static final Logger logger = Logger.getLogger(AddShape.class.getPackage().getName());

	public static FlexoActionType<AddShape, ViewObject, ViewObject> actionType = new FlexoActionType<AddShape, ViewObject, ViewObject>(
			"add_new_shape", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddShape makeNewAction(ViewObject focusedObject, Vector<ViewObject> globalSelection, FlexoEditor editor) {
			return new AddShape(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ViewObject object, Vector<ViewObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(ViewObject object, Vector<ViewObject> globalSelection) {
			return object instanceof View || object instanceof ViewShape;
		}

	};

	static {
		FlexoModelObject.addActionForClass(AddShape.actionType, View.class);
		FlexoModelObject.addActionForClass(AddShape.actionType, ViewShape.class);
	}

	private ViewShape _newShape;
	private String _newShapeName;
	private ViewObject _parent;
	private ShapeGraphicalRepresentation<?> _graphicalRepresentation;
	private boolean nameSetToNull = false;

	AddShape(ViewObject focusedObject, Vector<ViewObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException {
		logger.info("Add shape");

		if (getParent() == null) {
			throw new InvalidParameterException("folder is undefined");
		}
		if (getNewShapeName() == null && !isNameSetToNull()) {
			throw new InvalidParameterException("shape name is undefined");
		}

		_newShape = new ViewShape(getParent().getShema());
		if (getGraphicalRepresentation() != null) {
			_newShape.setGraphicalRepresentation(getGraphicalRepresentation());
		}

		try {
			_newShape.setName(getNewShapeName());
		} catch (InvalidNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getParent().addToChilds(_newShape);

		logger.info("Added shape " + _newShape + " under " + getParent());
	}

	public FlexoProject getProject() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getProject();
		}
		return null;
	}

	public ViewShape getNewShape() {
		return _newShape;
	}

	public ViewObject getParent() {
		if (_parent == null) {
			if (getFocusedObject() instanceof ViewShape) {
				_parent = getFocusedObject();
			} else if (getFocusedObject() instanceof View) {
				_parent = getFocusedObject();
			}
		}
		return _parent;
	}

	public void setParent(ViewObject parent) {
		_parent = parent;
	}

	public String getNewShapeName() {
		return _newShapeName;
	}

	public void setNewShapeName(String newShapeName) {
		_newShapeName = newShapeName;
	}

	public ShapeGraphicalRepresentation<?> getGraphicalRepresentation() {
		return _graphicalRepresentation;
	}

	public void setGraphicalRepresentation(ShapeGraphicalRepresentation<?> graphicalRepresentation) {
		_graphicalRepresentation = graphicalRepresentation;
	}

	public boolean isNameSetToNull() {
		return nameSetToNull;
	}

	public void setNameSetToNull(boolean nameSetToNull) {
		this.nameSetToNull = nameSetToNull;
	}

}