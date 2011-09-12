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
package org.openflexo.foundation.ontology.action;

import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.ontology.shema.OEShape;
import org.openflexo.foundation.ontology.shema.OEShema;
import org.openflexo.foundation.ontology.shema.OEShemaObject;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;


public class AddShape extends FlexoAction<AddShape,OEShemaObject,OEShemaObject> 
{

	private static final Logger logger = Logger.getLogger(AddShape.class.getPackage().getName());

	public static FlexoActionType<AddShape,OEShemaObject,OEShemaObject> actionType 
	= new FlexoActionType<AddShape,OEShemaObject,OEShemaObject> (
			"add_new_shape",
			FlexoActionType.newMenu,
			FlexoActionType.defaultGroup,
			FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddShape makeNewAction(OEShemaObject focusedObject, Vector<OEShemaObject> globalSelection, FlexoEditor editor) 
		{
			return new AddShape(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(OEShemaObject object, Vector<OEShemaObject> globalSelection) 
		{
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(OEShemaObject object, Vector<OEShemaObject> globalSelection) 
		{
			return (object instanceof OEShema 
					|| object instanceof OEShape);
		}

	};

	static {
		FlexoModelObject.addActionForClass (AddShape.actionType, OEShema.class);
		FlexoModelObject.addActionForClass (AddShape.actionType, OEShape.class);
	}



	private OEShape _newShape;
	private String _newShapeName;
	private OEShemaObject _parent;
	private Object _graphicalRepresentation;
	private boolean nameSetToNull = false;

	AddShape (OEShemaObject focusedObject, Vector<OEShemaObject> globalSelection, FlexoEditor editor)
	{
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException,NotImplementedException,InvalidParameterException
	{
		logger.info ("Add shape");  	

		if (getParent() == null) {
			throw new InvalidParameterException("folder is undefined");
		}
		if (getNewShapeName() == null && !isNameSetToNull()) {
			throw new InvalidParameterException("shema name is undefined");
		}

		_newShape = new OEShape(getParent().getShema());
		if (getGraphicalRepresentation() != null) _newShape.setGraphicalRepresentation(getGraphicalRepresentation());

		_newShape.setName(getNewShapeName());
		getParent().addToChilds(_newShape);   

		logger.info("Added shape "+_newShape+" under "+getParent());
	}

	public FlexoProject getProject()
	{
		if (getFocusedObject() != null) return getFocusedObject().getProject();
		return null;
	}

	public OEShape getNewShape() 
	{
		return _newShape;
	}

	public OEShemaObject getParent()
	{
		if (_parent == null) {
			if (getFocusedObject() instanceof OEShape) {
				_parent = getFocusedObject();
			} else if (getFocusedObject() instanceof OEShema) {
				_parent = getFocusedObject();
			} 
		}
		return _parent;
	}

	public void setParent(OEShemaObject parent) 
	{
		_parent = parent;
	}

	public String getNewShapeName() 
	{
		return _newShapeName;
	}

	public void setNewShapeName(String newShapeName) 
	{
		_newShapeName = newShapeName;
	}

	public Object getGraphicalRepresentation() 
	{
		return _graphicalRepresentation;
	}

	public void setGraphicalRepresentation(Object graphicalRepresentation) 
	{
		_graphicalRepresentation = graphicalRepresentation;
	}

	public boolean isNameSetToNull() 
	{
		return nameSetToNull;
	}

	public void setNameSetToNull(boolean nameSetToNull)
	{
		this.nameSetToNull = nameSetToNull;
	}

}