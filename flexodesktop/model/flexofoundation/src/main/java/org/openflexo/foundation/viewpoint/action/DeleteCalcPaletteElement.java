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
import org.openflexo.foundation.viewpoint.CalcObject;
import org.openflexo.foundation.viewpoint.CalcPaletteElement;


public class DeleteCalcPaletteElement extends FlexoAction<DeleteCalcPaletteElement,CalcPaletteElement,CalcObject> 
{

	private static final Logger logger = Logger.getLogger(DeleteCalcPaletteElement.class.getPackage().getName());

	public static FlexoActionType<DeleteCalcPaletteElement,CalcPaletteElement,CalcObject> actionType 
	= new FlexoActionType<DeleteCalcPaletteElement,CalcPaletteElement,CalcObject> (
			"delete_palette_element",
			FlexoActionType.editGroup,
			FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteCalcPaletteElement makeNewAction(CalcPaletteElement focusedObject, Vector<CalcObject> globalSelection, FlexoEditor editor) 
		{
			return new DeleteCalcPaletteElement(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CalcPaletteElement object, Vector<CalcObject> globalSelection) 
		{
			return object != null;
		}

		@Override
		protected boolean isEnabledForSelection(CalcPaletteElement object, Vector<CalcObject> globalSelection) 
		{
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass (DeleteCalcPaletteElement.actionType, CalcPaletteElement.class);
	}


	DeleteCalcPaletteElement (CalcPaletteElement focusedObject, Vector<CalcObject> globalSelection, FlexoEditor editor)
	{
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) 
	{
		logger.info ("Delete palette element");  	

		getFocusedObject().delete();
	}


}