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

import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.ontology.calc.CalcObject;
import org.openflexo.foundation.ontology.calc.CalcPalette;
import org.openflexo.foundation.ontology.calc.CalcPaletteElement;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;


public class AddCalcPaletteElement extends FlexoAction<AddCalcPaletteElement,CalcPalette,CalcObject> 
{

	private static final Logger logger = Logger.getLogger(AddCalcPaletteElement.class.getPackage().getName());

	public static FlexoActionType<AddCalcPaletteElement,CalcPalette,CalcObject> actionType 
	= new FlexoActionType<AddCalcPaletteElement,CalcPalette,CalcObject> (
			"add_new_palette_element",
			FlexoActionType.newMenu,
			FlexoActionType.defaultGroup,
			FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddCalcPaletteElement makeNewAction(CalcPalette focusedObject, Vector<CalcObject> globalSelection, FlexoEditor editor) 
		{
			return new AddCalcPaletteElement(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CalcPalette object, Vector<CalcObject> globalSelection) 
		{
			return object != null;
		}

		@Override
		protected boolean isEnabledForSelection(CalcPalette object, Vector<CalcObject> globalSelection) 
		{
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass (AddCalcPaletteElement.actionType, CalcPalette.class);
	}


	private String _newElementName;
	private CalcPaletteElement _newElement;
	private Object _graphicalRepresentation;

	AddCalcPaletteElement (CalcPalette focusedObject, Vector<CalcObject> globalSelection, FlexoEditor editor)
	{
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException,NotImplementedException,InvalidParameterException
	{
		logger.info ("Add calc palette element, gr = "+getGraphicalRepresentation());  	

		_newElement = getFocusedObject().addPaletteElement(getNewElementName(), getGraphicalRepresentation());
		
	}

	public FlexoProject getProject()
	{
		if (getFocusedObject() != null) return getFocusedObject().getProject();
		return null;
	}

	public CalcPaletteElement getNewElement() 
	{
		return _newElement;
	}

	public String getNewElementName() 
	{
		return _newElementName;
	}

	public void setNewElementName(String newElementName) 
	{
		_newElementName = newElementName;
	}


	public Object getGraphicalRepresentation() 
	{
		return _graphicalRepresentation;
	}

	public void setGraphicalRepresentation(Object graphicalRepresentation) 
	{
		_graphicalRepresentation = graphicalRepresentation;
	}


}