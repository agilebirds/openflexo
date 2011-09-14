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
package org.openflexo.foundation.cg.action;

import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.ActionGroup;
import org.openflexo.foundation.action.ActionMenu;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGFolder;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.version.AbstractCGFileVersion;


public abstract class AbstractGCAction<A extends FlexoAction<?,T1,CGObject>,T1 extends CGObject> extends FlexoAction<A,T1,CGObject>
{

    public static final ActionGroup ROUND_TRIP_MENU_GROUP = new ActionGroup("round_trip_menu",4);
    public static final ActionGroup ROUND_TRIP_GROUP = new ActionGroup("round_trip",5);
    public static final ActionGroup SHOW_GROUP = new ActionGroup("show",6);
    public static final ActionGroup REFRESH_GROUP = new ActionGroup("refresh",7);

    public static final ActionMenu GENERATE_MENU = new ActionMenu("code_generation",ROUND_TRIP_MENU_GROUP);
    public static final ActionMenu EDITION_MENU = new ActionMenu("edition",ROUND_TRIP_MENU_GROUP);
    public static final ActionMenu MERGE_MENU = new ActionMenu("merge",ROUND_TRIP_MENU_GROUP);
    public static final ActionMenu MODEL_MENU = new ActionMenu("model",ROUND_TRIP_MENU_GROUP);
        
    public static final ActionMenu SHOW_MENU = new ActionMenu("show",SHOW_GROUP);
    
    // Groups in GENERATE_MENU
    public static final ActionGroup VALIDATION_GROUP = new ActionGroup("validation",0);
    public static final ActionGroup SYNCHRO_GROUP = new ActionGroup("synchonization",1);
    public static final ActionGroup GENERATION_GROUP = new ActionGroup("generation",2);
    public static final ActionGroup WRITE_GROUP = new ActionGroup("write",3);
    public static final ActionGroup WAR_GROUP = new ActionGroup("war",4);

    // Groups in MERGE_MENU
    public static final ActionGroup MARK_GROUP1 = new ActionGroup("mark_group1",0);
    public static final ActionGroup MARK_GROUP2 = new ActionGroup("mark_group2",1);
    public static final ActionGroup OVERRIDE_GROUP = new ActionGroup("override",2);
 
    // Groups in MODEL_MENU
    public static final ActionGroup MODEL_GROUP1 = new ActionGroup("model_group1",0);
    public static final ActionGroup MODEL_GROUP2 = new ActionGroup("model_group2",0);
       public static final ActionGroup MODEL_GROUP3 = new ActionGroup("model_group3",1);

    // Versionning menu
    public static final ActionGroup versioningGroup = new ActionGroup("versioning",9);
    public static final ActionMenu versionningMenu = new ActionMenu("versioning",versioningGroup);
    public static final ActionGroup versionningActionsGroup = new ActionGroup("versionning_actions",0);
    public static final ActionGroup versionningShowGroup = new ActionGroup("versionning_show",1);
    public static final ActionGroup versionningCleanGroup = new ActionGroup("versionning_clean",2);

    public GenerationRepository getRepository()
    {
    	return getRepository(getFocusedObject(),getGlobalSelection());
    }

    public static GenerationRepository getRepository(CGObject focusedObject, Vector<CGObject> globalSelection)
    {
    	GenerationRepository returned = repositoryForObject(focusedObject);
    	if (globalSelection != null && returned!=null) {
    		Enumeration<CGObject> en = globalSelection.elements();
    		while ((returned == null) && (en.hasMoreElements())) {
    			returned = repositoryForObject(en.nextElement());
    		}
    	}
    	return returned;
    }

    public static GenerationRepository repositoryForObject(CGObject obj)
    {
   		if (obj instanceof GenerationRepository) {
			return (GenerationRepository)obj;
		}
		else if (obj instanceof CGSymbolicDirectory) {
			return ((CGSymbolicDirectory)obj).getGeneratedCodeRepository();
		}
		else if (obj instanceof CGFolder) {
			return ((CGFolder)obj).getSymbolicDirectory().getGeneratedCodeRepository();
		}
		else if (obj instanceof CGFile) {
			return ((CGFile)obj).getRepository();
		}
		else if (obj instanceof AbstractCGFileVersion) {
			return ((AbstractCGFileVersion)obj).getCGFile().getRepository();
		}
   		return null;
    }
    
    /**
     * Purpose of this method is to compute a closed set embedding all selected objects, 
     * including current focused object
     * @return
     */
    public static Vector<CGObject> getSelectedTopLevelObjects(CGObject focusedObject, Vector<CGObject> globalSelection)
    {
    	Vector<CGObject> returned = new Vector<CGObject>();
    	if (focusedObject != null) {
    		returned.add(focusedObject);
    	}
    	if (globalSelection != null) {
    		for (CGObject obj : globalSelection) {
    			boolean alreadyContained = false;
    			for (CGObject temp : returned) {
    				if (obj.isContainedIn(temp)) alreadyContained = true;
    			}
    			if (!alreadyContained) { 
    				// Not already contained, add it
    				// Before to do it, look if some other are to be removed
    				Vector<CGObject> removeThose = new Vector<CGObject>();
    				for (CGObject temp : returned) {
    					if (temp.isContainedIn(obj)) removeThose.add(temp);
    				}
    				returned.removeAll(removeThose);
    				returned.add(obj);
    			}
    		}
    	}
    	return returned;
    }

    /**
     * Purpose of this method is to compute a closed set embedding all selected objects, 
     * including current focused object
     * @return
     */
    protected Vector<CGObject> getSelectedTopLevelObjects()
    {
    	return getSelectedTopLevelObjects(getFocusedObject(),getGlobalSelection());
    }

    protected AbstractGCAction (FlexoActionType<A,T1,CGObject> actionType, T1 focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor)
    {
        super(actionType,focusedObject,globalSelection, editor);
    }

    /**
     * Return all AbstractCGFile concerned by current selection
     * @return
     */
    protected Vector<CGFile> getSelectedFiles()
    {
    	return getSelectedFiles(getRepository(),getSelectedTopLevelObjects());
    }

    /**
     * Return all AbstractCGFile concerned by current selection
     * @return
     */
    protected Vector<CGFile> getSelectedFiles(GenerationRepository repository, Vector<CGObject> selectedTopLevelObject)
    {
    	Vector<CGFile> returned = new Vector<CGFile>();
    	for (CGFile file : repository.getFiles()) {
    		for (CGObject obj : selectedTopLevelObject) {
    			if (obj.contains(file)) {
    	   			returned.add(file);
    			}
    			if (obj instanceof AbstractCGFileVersion) {
       	   			returned.add(((AbstractCGFileVersion)obj).getCGFile());
    			}
    		}
      	}
    	return returned;
    }

}
