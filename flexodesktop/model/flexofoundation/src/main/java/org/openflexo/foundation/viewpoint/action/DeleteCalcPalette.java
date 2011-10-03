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
import org.openflexo.foundation.viewpoint.CalcPalette;


public class DeleteCalcPalette extends FlexoAction<DeleteCalcPalette,CalcPalette,CalcObject> 
{

	private static final Logger logger = Logger.getLogger(DeleteCalcPalette.class.getPackage().getName());

	public static FlexoActionType<DeleteCalcPalette,CalcPalette,CalcObject> actionType 
	= new FlexoActionType<DeleteCalcPalette,CalcPalette,CalcObject> (
			"delete_calc_palette",
			FlexoActionType.editGroup,
			FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteCalcPalette makeNewAction(CalcPalette focusedObject, Vector<CalcObject> globalSelection, FlexoEditor editor) 
		{
			return new DeleteCalcPalette(focusedObject, globalSelection, editor);
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
		FlexoModelObject.addActionForClass (DeleteCalcPalette.actionType, CalcPalette.class);
	}


	DeleteCalcPalette (CalcPalette focusedObject, Vector<CalcObject> globalSelection, FlexoEditor editor)
	{
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context)
	{
		logger.info ("Delete calc palette");  	

		getFocusedObject().delete();
	}


}