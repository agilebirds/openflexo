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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.ontology.calc.AddShape;
import org.openflexo.foundation.ontology.calc.CalcPaletteElement;
import org.openflexo.foundation.ontology.calc.DropScheme;
import org.openflexo.foundation.ontology.calc.EditionScheme;
import org.openflexo.foundation.ontology.shema.OEShape;
import org.openflexo.foundation.ontology.shema.OEShema;
import org.openflexo.foundation.ontology.shema.OEShemaObject;
import org.openflexo.foundation.rm.DuplicateResourceException;


public class DropSchemeAction extends EditionSchemeAction<DropSchemeAction> 
{

	private static final Logger logger = Logger.getLogger(DropSchemeAction.class.getPackage().getName());

	public static FlexoActionType<DropSchemeAction,FlexoModelObject,FlexoModelObject> actionType 
	= new FlexoActionType<DropSchemeAction,FlexoModelObject,FlexoModelObject> (
			"drop_palette_element",
			FlexoActionType.newMenu,
			FlexoActionType.defaultGroup,
			FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DropSchemeAction makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) 
		{
			return new DropSchemeAction(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) 
		{
			return false;
		}

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) 
		{
			return (object instanceof OEShemaObject);
		}

	};

	private OEShemaObject _parent;
	private CalcPaletteElement _paletteElement;
	private DropScheme _dropScheme;
	private OEShape _newShape;
	
	public boolean escapeParameterRetrievingWhenValid = false;

	private Object _graphicalRepresentation;

	DropSchemeAction (FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor)
	{
		super(actionType, focusedObject, globalSelection, editor);
	}

	//private Hashtable<EditionAction,FlexoModelObject> createdObjects;

	private EditionPatternInstance editionPatternInstance;
	
	@Override
	protected void doAction(Object context) throws DuplicateResourceException,NotImplementedException,InvalidParametersException
	{
		logger.info ("Drop palette element");  	

		getEditionPattern().getCalc().getCalcOntology().loadWhenUnloaded();
		
		editionPatternInstance = getProject().makeNewEditionPatternInstance(getEditionPattern());
			
		applyEditionActions();
		
	}

	public OEShemaObject getParent()
	{
		if (_parent == null) {
			if (getFocusedObject() instanceof OEShape) {
				_parent = (OEShape)getFocusedObject();
			} else if (getFocusedObject() instanceof OEShema) {
				_parent = (OEShema)getFocusedObject();
			} 
		}
		return _parent;
	}

	public void setParent(OEShemaObject parent) 
	{
		_parent = parent;
	}
	
	public DropScheme getDropScheme() 
	{
		return _dropScheme;
	}

	public void setDropScheme(DropScheme dropScheme) 
	{
		_dropScheme = dropScheme;
	}

	@Override
	public EditionScheme getEditionScheme()
	{
		return getDropScheme();
	}

	public CalcPaletteElement getPaletteElement() 
	{
		return _paletteElement;
	}

	public void setPaletteElement(CalcPaletteElement paletteElement)
	{
		_paletteElement = paletteElement;
	}

	@Override
	public Object getOverridenGraphicalRepresentation() 
	{
		return _graphicalRepresentation;
	}

	public void setOverridenGraphicalRepresentation(Object graphicalRepresentation) 
	{
		_graphicalRepresentation = graphicalRepresentation;
	}

	public OEShape getNewShape()
	{
		return _newShape;
	}
	
	@Override
	public EditionPatternInstance getEditionPatternInstance()
	{
		return editionPatternInstance;
	}

	@Override
	protected OEShema retrieveOEShema()
	{
		if (getParent() != null) return getParent().getShema();
		if (getFocusedObject() instanceof OEShemaObject) return ((OEShemaObject)getFocusedObject()).getShema();
		return null;
	}

	@Override
	protected OEShape performAddShape(AddShape action)
	{
		_newShape = super.performAddShape(action);
		return _newShape;
	}
}