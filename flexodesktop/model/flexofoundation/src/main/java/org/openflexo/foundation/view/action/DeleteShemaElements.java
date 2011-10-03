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
package org.openflexo.foundation.view.action;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.ontology.AbstractOntologyObject;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.ontology.OntologyStatement;
import org.openflexo.foundation.view.OEConnector;
import org.openflexo.foundation.view.OEShape;
import org.openflexo.foundation.view.OEShemaElement;
import org.openflexo.foundation.view.OEShemaObject;


public class DeleteShemaElements extends FlexoUndoableAction<DeleteShemaElements,OEShemaElement,OEShemaElement>
{

    private static final Logger logger = Logger.getLogger(DeleteShemaElements.class.getPackage().getName());

    public static FlexoActionType<DeleteShemaElements,OEShemaElement,OEShemaElement> actionType 
    = new FlexoActionType<DeleteShemaElements,OEShemaElement,OEShemaElement>(
    		"delete", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public DeleteShemaElements makeNewAction(OEShemaElement focusedObject, Vector<OEShemaElement> globalSelection, FlexoEditor editor)
        {
            return new DeleteShemaElements(focusedObject, globalSelection,editor);
        }

        @Override
		protected boolean isVisibleForSelection(OEShemaElement object, Vector<OEShemaElement> globalSelection)
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(OEShemaElement focusedObject, Vector<OEShemaElement> globalSelection)
        {
        	Vector<OEShemaElement> objectsToDelete = objectsToDelete(focusedObject,globalSelection);
        	return (objectsToDelete.size() > 0);
         }

    };

	static {
		FlexoModelObject.addActionForClass (DeleteShemaElements.actionType, OEShape.class);
		FlexoModelObject.addActionForClass (DeleteShemaElements.actionType, OEConnector.class);
	}

	protected static Vector<OEShemaElement> objectsToDelete(OEShemaElement focusedObject, Vector<OEShemaElement> globalSelection)
	{
		Vector<OEShemaElement> allSelection = new Vector<OEShemaElement>();
        if (globalSelection == null || !globalSelection.contains(focusedObject)) {
        	allSelection.add(focusedObject);
        }
        if (globalSelection != null) {
        	for (OEShemaElement o : globalSelection) {
        		allSelection.add(o);
        	}
        }
        
		Vector<OEShemaElement> returned = new Vector<OEShemaElement>();
    	for (OEShemaElement o : allSelection) {
    		boolean isContainedByAnOtherObject = false;
    	   	for (OEShemaElement potentielContainer : allSelection) {
        		if (potentielContainer != o && o.isContainedIn(potentielContainer)) {
        			isContainedByAnOtherObject = true;
        			break;
        		}
        	}
    	   	if (!isContainedByAnOtherObject) returned.add(o);
    	}
      
        return returned;
	}
	
    private Vector<OntologicObjectEntry> _ontologicObjectsToBeDeleted;
    private Vector<OEShemaElement> _objectsToDelete;
    private Vector<OEShemaElement> _objectsThatWillBeDeleted;
    public boolean deleteOntologicObjects = true;
    
	protected DeleteShemaElements(OEShemaElement focusedObject, Vector<OEShemaElement> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection,editor);
        logger.info("Created DeleteShemaElements action focusedObject="+focusedObject+"globalSelection="+globalSelection);
    }

    @Override
	protected void doAction(Object context)
    {
        if (logger.isLoggable(Level.INFO)) logger.info("DeleteShemaElements");
        if (logger.isLoggable(Level.INFO)) logger.info("selection is: " + getGlobalSelection());
        if (logger.isLoggable(Level.INFO)) logger.info("selection to delete is: " + getObjectsToDelete());
        if (logger.isLoggable(Level.INFO) && deleteOntologicObjects) logger.info("ontologic objects to delete are: " + getObjectsToDelete());

        if (deleteOntologicObjects) {
          	for (OntologicObjectEntry e : getOntologicObjectsToBeDeleted()) {
        		if (e.deleteIt && e.ontologicObject instanceof OntologyStatement) e.ontologicObject.delete();
        	}
           	for (OntologicObjectEntry e : getOntologicObjectsToBeDeleted()) {
        		if (e.deleteIt && !(e.ontologicObject instanceof OntologyStatement)) e.ontologicObject.delete();
        	}
        }
        
        for (OEShemaElement o : getObjectsToDelete()) {
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

    public Vector<OEShemaElement> getObjectsToDelete()
    {
    	if (_objectsToDelete == null) {
    		_objectsToDelete = objectsToDelete(getFocusedObject(), getGlobalSelection());
    		_objectsThatWillBeDeleted = new Vector<OEShemaElement>();
           	for (OEShemaElement o : _objectsToDelete) {
           		addAllEmbeddedObjects(o,_objectsThatWillBeDeleted);
           	}  		
    	}
    	return _objectsToDelete;
    } 

    public Vector<OEShemaElement> getObjectsThatWillBeDeleted()
    {
    	if (_objectsThatWillBeDeleted == null) {
    		_objectsThatWillBeDeleted = new Vector<OEShemaElement>();
           	for (OEShemaElement o : getObjectsToDelete()) {
           		addAllEmbeddedObjects(o,_objectsThatWillBeDeleted);
           	}  		
    	}
    	return _objectsThatWillBeDeleted;
    } 
    
    private void addAllEmbeddedObjects(OEShemaElement o, Vector<OEShemaElement> v)
    {
    	if (!v.contains(o)) v.add(o);
    	if (o instanceof OEShape) {
    		OEShape s = (OEShape)o;
    		for (OEConnector connector : s.getIncomingConnectors())
    			if (!v.contains(connector)) v.add(connector);
       		for (OEConnector connector : s.getOutgoingConnectors())
    			if (!v.contains(connector)) v.add(connector);
    	}
    	for (OEShemaObject child : o.getChilds()) {
    		if (child instanceof OEShemaElement) addAllEmbeddedObjects((OEShemaElement)child,v);
    	}
    }
    
    public class OntologicObjectEntry
    {
    	
    	public OntologicObjectEntry(boolean deleteIt,
				AbstractOntologyObject ontologicObject)
		{
			super();
			this.deleteIt = deleteIt;
			this.ontologicObject = ontologicObject;
		}
		public boolean deleteIt;
    	public AbstractOntologyObject ontologicObject;
    }

    public Vector<OntologicObjectEntry> getOntologicObjectsToBeDeleted()
    {
    	if (_ontologicObjectsToBeDeleted == null) {
    		_ontologicObjectsToBeDeleted = new Vector<OntologicObjectEntry>();
    		for (OEShemaElement e : getObjectsThatWillBeDeleted()) {
    			if (e.getEditionPatternReferences() != null) {
    				for (EditionPatternReference ref : e.getEditionPatternReferences()) {
    					EditionPatternInstance epInstance = ref.getEditionPatternInstance();
    					for (String s : epInstance.getActors().keySet()) {
    						FlexoModelObject actor = epInstance.getActors().get(s);
    						if (actor instanceof AbstractOntologyObject) {
    							if (!_ontologicObjectsToBeDeleted.contains(actor))
    								_ontologicObjectsToBeDeleted.add(
    										new OntologicObjectEntry(true,(AbstractOntologyObject)actor));
    						}
    					}
     				}
    			}
    		}
    	}
    	return _ontologicObjectsToBeDeleted;
    }
    
    public void selectAll()
    {
    	for (OntologicObjectEntry e : getOntologicObjectsToBeDeleted()) {
    		e.deleteIt = true;
    	}
    }

    public void selectNone()
    {
    	for (OntologicObjectEntry e : getOntologicObjectsToBeDeleted()) {
    		e.deleteIt = false;
    	}
    }

	public boolean getDeleteOntologicObjects()
	{
		return deleteOntologicObjects;
	}

	public void setDeleteOntologicObjects(boolean deleteOntologicObjects)
	{
		if (deleteOntologicObjects) selectAll();
		else selectNone();
		this.deleteOntologicObjects = deleteOntologicObjects;
	}
}
