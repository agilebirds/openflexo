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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.ontology.calc.CalcDrawingConnector;
import org.openflexo.foundation.ontology.calc.CalcDrawingObject;
import org.openflexo.foundation.ontology.calc.CalcDrawingShape;


public class DeleteCalcShemaElements extends FlexoUndoableAction<DeleteCalcShemaElements,CalcDrawingObject,CalcDrawingObject>
{

    private static final Logger logger = Logger.getLogger(DeleteCalcShemaElements.class.getPackage().getName());

    public static FlexoActionType<DeleteCalcShemaElements,CalcDrawingObject,CalcDrawingObject> actionType 
    = new FlexoActionType<DeleteCalcShemaElements,CalcDrawingObject,CalcDrawingObject>(
    		"delete", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public DeleteCalcShemaElements makeNewAction(CalcDrawingObject focusedObject, Vector<CalcDrawingObject> globalSelection, FlexoEditor editor)
        {
            return new DeleteCalcShemaElements(focusedObject, globalSelection,editor);
        }

        @Override
		protected boolean isVisibleForSelection(CalcDrawingObject object, Vector<CalcDrawingObject> globalSelection)
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(CalcDrawingObject focusedObject, Vector<CalcDrawingObject> globalSelection)
        {
        	Vector<CalcDrawingObject> objectsToDelete = objectsToDelete(focusedObject,globalSelection);
        	return (objectsToDelete.size() > 0);
         }

    };

	static {
		FlexoModelObject.addActionForClass (DeleteCalcShemaElements.actionType, CalcDrawingShape.class);
		FlexoModelObject.addActionForClass (DeleteCalcShemaElements.actionType, CalcDrawingConnector.class);
	}

	protected static Vector<CalcDrawingObject> objectsToDelete(CalcDrawingObject focusedObject, Vector<CalcDrawingObject> globalSelection)
	{
		Vector<CalcDrawingObject> allSelection = new Vector<CalcDrawingObject>();
        if (globalSelection == null || !globalSelection.contains(focusedObject)) {
        	allSelection.add(focusedObject);
        }
        if (globalSelection != null) {
        	for (CalcDrawingObject o : globalSelection) {
        		allSelection.add(o);
        	}
        }
        
		Vector<CalcDrawingObject> returned = new Vector<CalcDrawingObject>();
    	for (CalcDrawingObject o : allSelection) {
    		boolean isContainedByAnOtherObject = false;
    	   	for (CalcDrawingObject potentielContainer : allSelection) {
        		if (potentielContainer != o && o.isContainedIn(potentielContainer)) {
        			isContainedByAnOtherObject = true;
        			break;
        		}
        	}
    	   	if (!isContainedByAnOtherObject) returned.add(o);
    	}
      
        return returned;
	}
	
	
	protected DeleteCalcShemaElements(CalcDrawingObject focusedObject, Vector<CalcDrawingObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection,editor);
        logger.info("Created DeleteCalcShemaElements action focusedObject="+focusedObject+"globalSelection="+globalSelection);
    }

    @Override
	protected void doAction(Object context)
    {
        if (logger.isLoggable(Level.INFO)) logger.info("DeleteCalcShemaElements");
        if (logger.isLoggable(Level.INFO)) logger.info("selection is: " + getGlobalSelection());
        if (logger.isLoggable(Level.INFO)) logger.info("selection to delete is: " + getObjectsToDelete());
        for (CalcDrawingObject o : getObjectsToDelete()) {
        	o.delete();
        }
    }

    @Override
	protected void undoAction(Object context)
    {
        logger.warning("UNDO DELETE not implemented yet !");
    }

    @Override
	protected void redoAction(Object context)
    {
        logger.warning("REDO DELETE not implemented yet !");
    }

    private Vector<CalcDrawingObject> _objectsToDelete;

    public Vector<CalcDrawingObject> getObjectsToDelete()
    {
    	if (_objectsToDelete == null) {
    		_objectsToDelete = objectsToDelete(getFocusedObject(), getGlobalSelection());
    	}
    	return _objectsToDelete;
    } 

}
