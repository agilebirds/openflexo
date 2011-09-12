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
import org.openflexo.foundation.ontology.calc.CalcDrawingShema;
import org.openflexo.foundation.ontology.calc.CalcObject;


public class DeleteCalcDrawingShema extends FlexoAction<DeleteCalcDrawingShema,CalcDrawingShema,CalcObject> 
{

	private static final Logger logger = Logger.getLogger(DeleteCalcDrawingShema.class.getPackage().getName());

	public static FlexoActionType<DeleteCalcDrawingShema,CalcDrawingShema,CalcObject> actionType 
	= new FlexoActionType<DeleteCalcDrawingShema,CalcDrawingShema,CalcObject> (
			"delete_calc_drawing_shema",
			FlexoActionType.editGroup,
			FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteCalcDrawingShema makeNewAction(CalcDrawingShema focusedObject, Vector<CalcObject> globalSelection, FlexoEditor editor) 
		{
			return new DeleteCalcDrawingShema(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CalcDrawingShema object, Vector<CalcObject> globalSelection) 
		{
			return object != null;
		}

		@Override
		protected boolean isEnabledForSelection(CalcDrawingShema object, Vector<CalcObject> globalSelection) 
		{
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass (DeleteCalcDrawingShema.actionType, CalcDrawingShema.class);
	}


	DeleteCalcDrawingShema (CalcDrawingShema focusedObject, Vector<CalcObject> globalSelection, FlexoEditor editor)
	{
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context)
	{
		logger.info ("Delete calc drawing shema");  	

		getFocusedObject().delete();
	}


}