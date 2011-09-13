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
package org.openflexo.foundation.utils;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet Created on 3 oct. 2005
 */
public class FlexoObjectIDManager
{

    private FlexoProject currentProject;

    private static final Logger logger = FlexoLogger.getLogger(FlexoObjectIDManager.class.getPackage().toString());

    private static final FlexoObjectIDManager instance = new FlexoObjectIDManager();

    private Vector<FlexoModelObject> badObjects;
    private Hashtable<Long,FlexoModelObject> used;
    
    /**
     * 
     */
    private FlexoObjectIDManager()
    {
    }

    public static FlexoObjectIDManager getInstance()
    {
        return instance;
    }

    public Vector<FlexoModelObject> checkProject(FlexoProject project, boolean fixProblems)
    {
        if (logger.isLoggable(Level.INFO))
            logger.info("Start checking flexoID for project " + project.getProjectName());

        currentProject = project;

        // First load all unloaded resources
        for (FlexoResource resource : project.getResources().values()) {
            if (resource instanceof FlexoStorageResource) {
                ((FlexoStorageResource)resource).getResourceData(); // no need to mark as modified .setIsModified();
            }
        }
        
        // Iterate on all objects to validate
        used = new Hashtable<Long,FlexoModelObject>();
        badObjects = new Vector<FlexoModelObject>();
        Vector<FlexoModelObject> objectsToUnregister = new Vector<FlexoModelObject>();
        for (FlexoModelObject object : new Vector<FlexoModelObject>(project.getAllRegisteredObjects())) {
            if (object.getProject() == project) {
            	if (object.getXMLResourceData()==null) {
            		continue;
            	}
            	if (object.getXMLResourceData().getFlexoResource()==null) {
            		continue;
            	}
            	if (object.getXMLResourceData().getFlexoResource().getResourceData()==object.getXMLResourceData())
            		testAndSetID(object,fixProblems);
            	else {
            		if (logger.isLoggable(Level.WARNING))
						logger.warning("This object "+object+" is registered but should not!");
            		objectsToUnregister.add(object);
            	}
            }
        }

        for (FlexoModelObject obj : objectsToUnregister) {
			project.unregister(obj);
		}
        
        /*for (FlexoModelObject obj : badObjects) {
        	if (obj instanceof FlexoXMLSerializableObject) {
        		((FlexoXMLSerializableObject)obj).getXMLResourceData().setIsModified();
        	}
        }*/
        
        if (logger.isLoggable(Level.INFO))
            logger.info("Found " + badObjects.size() + " objects that have an incorrect flexoID");
        return badObjects;
    }
    
     private void testAndSetID(FlexoModelObject o, boolean fixProblems)
    {
        if (o.isCreatedByCloning())
            if (logger.isLoggable(Level.WARNING))
                logger.warning("An object was found with status beingCloned " + o.getXMLRepresentation());
        if (o.getFlexoID() < 0 || o.getFlexoID() > currentProject.getLastUniqueID()) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Found an object with an invalid ID: " + o.getXMLRepresentation());
            if (fixProblems)
                o.setFlexoID(o.getProject().getNewFlexoID());
            badObjects.add(o);
        }
        FlexoModelObject old = used.put(new Long(o.getFlexoID()), o);
        if (old != null && old != o) {
            long newID = o.getProject().getNewFlexoID();
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Found two different objects with the same flexoID:" + o.getFlexoID()+ " Object1: "+old.getClass().getName()+"["+old.getSerializationIdentifier()+"]" + " and Object2:" + o.getClass().getName()+"["+o.getSerializationIdentifier()+"] Replace id with "+newID);
            if (fixProblems)
                o.setFlexoID(newID);
            badObjects.add(o);
        }
    }
}
