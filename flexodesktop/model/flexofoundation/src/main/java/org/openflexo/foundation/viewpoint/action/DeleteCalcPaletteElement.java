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
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.ViewPointPaletteElement;


public class DeleteCalcPaletteElement extends FlexoAction<DeleteCalcPaletteElement,ViewPointPaletteElement,ViewPointObject> 
{

	private static final Logger logger = Logger.getLogger(DeleteCalcPaletteElement.class.getPackage().getName());

	public static FlexoActionType<DeleteCalcPaletteElement,ViewPointPaletteElement,ViewPointObject> actionType 
	= new FlexoActionType<DeleteCalcPaletteElement,ViewPointPaletteElement,ViewPointObject> (
			"delete_palette_element",
			FlexoActionType.editGroup,
			FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteCalcPaletteElement makeNewAction(ViewPointPaletteElement focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) 
		{
			return new DeleteCalcPaletteElement(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ViewPointPaletteElement object, Vector<ViewPointObject> globalSelection) 
		{
			return object != null;
		}

		@Override
		protected boolean isEnabledForSelection(ViewPointPaletteElement object, Vector<ViewPointObject> globalSelection) 
		{
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass (DeleteCalcPaletteElement.actionType, ViewPointPaletteElement.class);
	}


	DeleteCalcPaletteElement (ViewPointPaletteElement focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor)
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