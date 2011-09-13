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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.ontology.AbstractOntologyObject;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;


public class DeleteOntologyObjects extends FlexoUndoableAction<DeleteOntologyObjects,OntologyObject,OntologyObject>
{

    private static final Logger logger = Logger.getLogger(DeleteOntologyObjects.class.getPackage().getName());

    public static FlexoActionType<DeleteOntologyObjects,OntologyObject,OntologyObject> actionType 
    = new FlexoActionType<DeleteOntologyObjects,OntologyObject,OntologyObject>(
    		"delete", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public DeleteOntologyObjects makeNewAction(OntologyObject focusedObject, Vector<OntologyObject> globalSelection, FlexoEditor editor)
        {
            return new DeleteOntologyObjects(focusedObject, globalSelection,editor);
        }

        @Override
		protected boolean isVisibleForSelection(OntologyObject object, Vector<OntologyObject> globalSelection)
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(OntologyObject focusedObject, Vector<OntologyObject> globalSelection)
        {
        	Vector<OntologyObject> objectsToDelete = objectsToDelete(focusedObject,globalSelection);
        	if (objectsToDelete.size() == 0) return false;
            for (OntologyObject o : globalSelection) {
            	if (o.getIsReadOnly()) return false;
            }
            return true;
         }

    };

	static {
		FlexoModelObject.addActionForClass (DeleteOntologyObjects.actionType, OntologyObject.class);
	}

	protected static Vector<OntologyObject> objectsToDelete(OntologyObject focusedObject, Vector<OntologyObject> globalSelection)
	{
		Vector<OntologyObject> returned = new Vector<OntologyObject>();
        if (globalSelection == null || !globalSelection.contains(focusedObject)) {
        	returned.add(focusedObject);
        }
        if (globalSelection != null) {
        	for (AbstractOntologyObject o : globalSelection) {
        		if (o instanceof OntologyClass
        				|| o instanceof OntologyIndividual
        				|| o instanceof OntologyObjectProperty
        				|| o instanceof OntologyDataProperty) {
        			returned.add((OntologyObject)o);
        		}
        	}
        }
        return returned;
	}
	
	
	protected DeleteOntologyObjects(OntologyObject focusedObject, Vector<OntologyObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection,editor);
        logger.info("Created DeleteOntologyObjects action focusedObject="+focusedObject+"globalSelection="+globalSelection);
    }

    @Override
	protected void doAction(Object context)
    {
        if (logger.isLoggable(Level.INFO)) logger.info("DeleteOntologyObjects");
        if (logger.isLoggable(Level.INFO)) logger.info("selection is: " + getGlobalSelection());
        if (logger.isLoggable(Level.INFO)) logger.info("selection to delete is: " + getObjectsToDelete());
        for (OntologyObject o : getObjectsToDelete()) {
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

    private Vector<OntologyObject> _objectsToDelete;

    /**
     * This method returns all the objects on which the delete method needs to
     * be called. This should not be done by some other code than the one
     * located in the doAction method. This method can be used in either
     * initialiazer or finalizer of the action.
     * 
     * @return All the objects to be deleted.
     */
    public Vector<OntologyObject> getObjectsToDelete()
    {
    	if (_objectsToDelete == null) {
    		_objectsToDelete = objectsToDelete(getFocusedObject(), getGlobalSelection());
    	}
    	return _objectsToDelete;
    } 

}
