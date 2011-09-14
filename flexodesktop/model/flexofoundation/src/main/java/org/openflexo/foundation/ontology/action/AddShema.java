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

import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.ontology.calc.OntologyCalc;
import org.openflexo.foundation.ontology.shema.OESLObject;
import org.openflexo.foundation.ontology.shema.OEShemaDefinition;
import org.openflexo.foundation.ontology.shema.OEShemaFolder;
import org.openflexo.foundation.ontology.shema.OEShemaLibrary;
import org.openflexo.foundation.ontology.shema.OEShemaDefinition.DuplicateShemaNameException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

public class AddShema extends FlexoAction<AddShema,OESLObject,OESLObject> 
{

    private static final Logger logger = Logger.getLogger(AddShema.class.getPackage().getName());

    public static FlexoActionType<AddShema,OESLObject,OESLObject> actionType 
    = new FlexoActionType<AddShema,OESLObject,OESLObject> (
    		"add_new_shema",
    		FlexoActionType.newMenu,
    		FlexoActionType.defaultGroup,
    		FlexoActionType.ADD_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public AddShema makeNewAction(OESLObject focusedObject, Vector<OESLObject> globalSelection, FlexoEditor editor) 
        {
            return new AddShema(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(OESLObject object, Vector<OESLObject> globalSelection) 
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(OESLObject object, Vector<OESLObject> globalSelection) 
        {
            return (object instanceof OEShemaFolder 
            		|| object instanceof OEShemaDefinition
            		|| object instanceof OEShemaLibrary);
        }
                
    };
    
    static {
        FlexoModelObject.addActionForClass (AddShema.actionType, OEShemaLibrary.class);
        FlexoModelObject.addActionForClass (AddShema.actionType, OEShemaFolder.class);
        FlexoModelObject.addActionForClass (AddShema.actionType, OEShemaDefinition.class);
    }
    

    
     private OEShemaDefinition _newShema;

     private OEShemaFolder _folder;

     public boolean useCalc = true;
     public String newShemaName;
     public OntologyCalc calc;       
    
    
	AddShema (OESLObject focusedObject, Vector<OESLObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    @Override
	protected void doAction(Object context) throws DuplicateResourceException,NotImplementedException,InvalidParameterException, DuplicateShemaNameException
    {
       	logger.info ("Add shema");  	
    	
    	if (getFolder() == null) {
    		throw new InvalidParameterException("folder is undefined");
    	}
    	if (StringUtils.isEmpty(newShemaName)) {
    		throw new InvalidParameterException("shema name is undefined");
    	}
    	if (getProject().getShemaLibrary().getShemaNamed(newShemaName)!=null)
    		throw new DuplicateShemaNameException(newShemaName);

    	_newShema = new OEShemaDefinition(newShemaName, getFolder().getShemaLibrary(), getFolder(), getProject(),true);
       	if (useCalc) _newShema.setCalc(calc);
       	logger.info ("Added shema "+_newShema+" for project "+_newShema.getProject());
    	// Creates the resource here
    	_newShema.getShemaResource();
    }

     public FlexoProject getProject()
    {
    	if (getFocusedObject() != null) return getFocusedObject().getProject();
    	return null;
    }
    
    public OEShemaDefinition getNewShema() 
    {
        return _newShema;
    }

    public OEShemaLibrary getShemaLibrary()
    {
    	if (getFocusedObject() != null) return getFocusedObject().getShemaLibrary();
    	return null;
    }
    
	public OEShemaFolder getFolder()
	{
		if (_folder == null) {
		   if ((getFocusedObject() != null) && (getFocusedObject() instanceof OEShemaDefinition)) {
            	_folder = ((OEShemaDefinition)getFocusedObject()).getFolder();
            } else if ((getFocusedObject() != null) && (getFocusedObject() instanceof OEShemaFolder)) {
            	_folder = (OEShemaFolder)getFocusedObject();
            } else if ((getFocusedObject() != null) && (getFocusedObject() instanceof OEShemaLibrary)) {
            	_folder = ((OEShemaLibrary)getFocusedObject()).getRootFolder();
            }

		}
		return _folder;
	}

	public void setFolder(OEShemaFolder folder) 
	{
		_folder = folder;
	}

	public String errorMessage;
	
	public boolean isValid()
	{
		if (getFolder() == null) {
			errorMessage = FlexoLocalization.localizedForKey("no_folder_defined");
			return false;
		}
		else if (calc == null && useCalc) {
			errorMessage = FlexoLocalization.localizedForKey("no_ontologic_calc_defined");
			return false;
		}
    	if (StringUtils.isEmpty(newShemaName)) {
			errorMessage = FlexoLocalization.localizedForKey("no_shema_name_defined");
			return false;
		}
		if (getProject().getShemaLibrary().getShemaNamed(newShemaName) != null) {
			errorMessage = FlexoLocalization.localizedForKey("a_shema_with_that_name_already_exists");
			return false;
		}
		return true;
	}
}