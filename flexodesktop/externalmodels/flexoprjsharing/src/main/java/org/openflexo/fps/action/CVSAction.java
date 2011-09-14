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
package org.openflexo.fps.action;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.ActionGroup;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.fps.CVSAbstractFile;
import org.openflexo.fps.CVSExplorable;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.CVSRepositoryList;
import org.openflexo.fps.FPSObject;
import org.openflexo.fps.SharedProject;


public abstract class CVSAction<A extends FlexoAction<?,T1,FPSObject>,T1 extends FPSObject> extends FlexoAction<A,T1,FPSObject>
{

    private static final Logger logger = Logger.getLogger(CVSAction.class.getPackage().getName());

    public static final ActionGroup SYNCHRONIZE_GROUP = new ActionGroup("synchronize",4);
    public static final ActionGroup CVS_OPERATIONS_GROUP = new ActionGroup("cvs_operations",5);
    public static final ActionGroup CVS_OVERRIDE_OPERATIONS_GROUP = new ActionGroup("cvs_override_operations",6);
    public static final ActionGroup EDITION_GROUP = new ActionGroup("edition",7);
    public static final ActionGroup CVS_HISTORY_GROUP = new ActionGroup("cvs_history",8);

    public SharedProject getSharedProject()
    {
    	return getSharedProject(getFocusedObject(),getGlobalSelection());
    }

    protected static SharedProject getSharedProject(FPSObject focusedObject, Vector<FPSObject> globalSelection)
    {
    	SharedProject returned = sharedProjectForObject(focusedObject);
    	if (globalSelection != null) {
    		Enumeration<FPSObject> en = globalSelection.elements();
    		while ((returned == null) && (en.hasMoreElements())) {
    			returned = sharedProjectForObject(en.nextElement());
    		}
    	}
    	return returned;
    }

    public static SharedProject sharedProjectForObject(FPSObject obj)
    {
   		if (obj instanceof CVSAbstractFile) {
			return ((CVSAbstractFile)obj).getSharedProject();
		}
   		return null;
    }
    
    /**
     * Purpose of this method is to compute a closed set embedding all selected objects, 
     * including current focused object
     * @return
     */
    protected static Vector<FPSObject> getSelectedTopLevelObjects(FPSObject focusedObject, Vector<FPSObject> globalSelection)
    {
    	//logger.info("getSelectedTopLevelObjects() with "+focusedObject+" and "+globalSelection);
    	Vector<FPSObject> returned = new Vector<FPSObject>();
    	if (focusedObject != null) {
    		returned.add(focusedObject);
    	}
    	if (globalSelection != null) {
    		for (FPSObject obj : globalSelection) {
    			boolean alreadyContained = false;
    			for (FPSObject temp : returned) {
    				if (obj.isContainedIn(temp)) alreadyContained = true;
    			}
    			if (!alreadyContained) { 
    				// Not already contained, add it
    				// Before to do it, look if some other are to be removed
    				Vector<FPSObject> removeThose = new Vector<FPSObject>();
    				for (FPSObject temp : returned) {
    					if (temp.isContainedIn(obj)) removeThose.add(temp);
    				}
    				returned.removeAll(removeThose);
    				returned.add(obj);
    			}
    		}
    	}
    	//logger.info("getSelectedTopLevelObjects() return "+returned);
    	return returned;
    }

     /**
     * Purpose of this method is to compute a closed set embedding all selected objects, 
     * including current focused object
     * @return
     */
    protected Vector<FPSObject> getSelectedTopLevelObjects()
    {
    	return getSelectedTopLevelObjects(getFocusedObject(),getGlobalSelection());
    }

    protected CVSAction (FlexoActionType<A,T1,FPSObject> actionType, T1 focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor)
    {
        super(actionType,focusedObject,globalSelection, editor);
    }

    protected Vector<CVSFile> getSelectedFiles()
    {
    	return getSelectedFiles(getSharedProject(),getSelectedTopLevelObjects());
    }

    /**
     * Return all AbstractCGFile concerned by current selection
     * @return
     */
    protected static Vector<CVSFile> getSelectedCVSFiles(FPSObject focusedObject, Vector<FPSObject> globalSelection)
    {
    	return getSelectedFiles(getSharedProject(focusedObject, globalSelection),getSelectedTopLevelObjects(focusedObject, globalSelection));
    }


    /**
     * Return all CVSFile concerned by current selection
     * @return
     */
    protected static Vector<CVSFile> getSelectedFiles(SharedProject sharedProject, Vector<FPSObject> selectedTopLevelObject)
    {
    	//logger.info("getSelectedFiles() with "+sharedProject+" and "+selectedTopLevelObject);
    	Vector<CVSFile> returned = new Vector<CVSFile>();
       	if (sharedProject == null) return returned;
           	for (CVSFile file : sharedProject.getAllCVSFiles()) {
    		for (FPSObject obj : selectedTopLevelObject) {
    			if (obj.contains(file)) {
    				returned.add(file);
    			}
    		}
    	}
    	//logger.info("getSelectedFiles() return "+returned);
    	return returned;
    }

    public static CVSRepositoryList getRepositoryList(FPSObject object){
        if (object instanceof CVSRepositoryList)
            return (CVSRepositoryList) object;
        if (object instanceof CVSExplorable)
            return ((CVSExplorable)object).getCVSRepository().getCVSRepositoryList();
        return null;
    }
    
    public CVSRepository getRepository(FPSObject object){
        if (object instanceof CVSRepository)
            return (CVSRepository) object;
        if (object instanceof CVSExplorable)
            return ((CVSExplorable)object).getCVSRepository();
        return null;
    }
}
