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
package org.openflexo.foundation.ontology.calc.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.ontology.calc.CalcDrawingObject;
import org.openflexo.foundation.ontology.calc.CalcDrawingShape;
import org.openflexo.foundation.ontology.calc.CalcDrawingShema;


public class AddCalcDrawingShape extends FlexoAction<AddCalcDrawingShape,CalcDrawingObject,CalcDrawingObject> 
{

	private static final Logger logger = Logger.getLogger(AddCalcDrawingShape.class.getPackage().getName());

	public static FlexoActionType<AddCalcDrawingShape,CalcDrawingObject,CalcDrawingObject> actionType 
	= new FlexoActionType<AddCalcDrawingShape,CalcDrawingObject,CalcDrawingObject> (
			"add_new_shape",
			FlexoActionType.newMenu,
			FlexoActionType.defaultGroup,
			FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddCalcDrawingShape makeNewAction(CalcDrawingObject focusedObject, Vector<CalcDrawingObject> globalSelection, FlexoEditor editor) 
		{
			return new AddCalcDrawingShape(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CalcDrawingObject object, Vector<CalcDrawingObject> globalSelection) 
		{
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(CalcDrawingObject object, Vector<CalcDrawingObject> globalSelection) 
		{
			return (object instanceof CalcDrawingShema 
					|| object instanceof CalcDrawingShape);
		}

	};

	static {
		FlexoModelObject.addActionForClass (AddCalcDrawingShape.actionType, CalcDrawingShema.class);
		FlexoModelObject.addActionForClass (AddCalcDrawingShape.actionType, CalcDrawingShape.class);
	}



	private CalcDrawingShape _newShape;
	public String newShapeName;
	private CalcDrawingObject _parent;
	public Object graphicalRepresentation;
	public boolean nameSetToNull = false;

	AddCalcDrawingShape (CalcDrawingObject focusedObject, Vector<CalcDrawingObject> globalSelection, FlexoEditor editor)
	{
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws NotImplementedException,InvalidParametersException
	{
		logger.info ("Add shape");  	

		if (getParent() == null) {
			throw new InvalidParametersException("folder is undefined");
		}
		if (newShapeName == null && !nameSetToNull) {
			throw new InvalidParametersException("shape name is undefined");
		}

		_newShape = new CalcDrawingShape();
		if (graphicalRepresentation != null) _newShape.setGraphicalRepresentation(graphicalRepresentation);

		_newShape.setName(newShapeName);
		getParent().addToChilds(_newShape);   

		logger.info("Added shape "+_newShape+" under "+getParent());
	}

	public CalcDrawingShape getNewShape() 
	{
		return _newShape;
	}

	public CalcDrawingObject getParent()
	{
		if (_parent == null) {
			if (getFocusedObject() instanceof CalcDrawingShape) {
				_parent = getFocusedObject();
			} else if (getFocusedObject() instanceof CalcDrawingShema) {
				_parent = getFocusedObject();
			} 
		}
		return _parent;
	}

	public void setParent(CalcDrawingObject parent) 
	{
		_parent = parent;
	}

}