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
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.ontology.shema.OESLObject;
import org.openflexo.foundation.ontology.shema.OEShemaFolder;
import org.openflexo.foundation.ontology.shema.OEShemaLibrary;


public class AddShemaFolder extends FlexoAction<AddShemaFolder,OESLObject,OESLObject>
{

    private static final Logger logger = Logger.getLogger(AddShemaFolder.class.getPackage().getName());

    public static FlexoActionType<AddShemaFolder,OESLObject,OESLObject> actionType 
    = new FlexoActionType<AddShemaFolder,OESLObject,OESLObject> (
    		"add_new_shema_folder",
    		FlexoActionType.newMenu,FlexoActionType.defaultGroup,FlexoActionType.ADD_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public AddShemaFolder makeNewAction(OESLObject focusedObject, Vector<OESLObject> globalSelection, FlexoEditor editor) 
        {
            return new AddShemaFolder(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(OESLObject object, Vector<OESLObject> globalSelection) 
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(OESLObject object, Vector<OESLObject> globalSelection) 
        {
            return ((object != null) 
                    && ((object instanceof OEShemaFolder)
                            || (object instanceof OEShemaLibrary)));
        }
                
    };
    
    static {
        FlexoModelObject.addActionForClass (AddShemaFolder.actionType, OEShemaLibrary.class);
        FlexoModelObject.addActionForClass (AddShemaFolder.actionType, OEShemaFolder.class);
    }
    

    private OEShemaFolder _newFolder;
    private OEShemaFolder _parentFolder;
    private String _newFolderName;
    
    AddShemaFolder (OESLObject focusedObject, Vector<OESLObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    @Override
	protected void doAction(Object context) throws InvalidParametersException
    {
        logger.info ("Add shema folder");
        if (getFocusedObject() != null) {
            if (getParentFolder() != null) {
                _newFolder = OEShemaFolder.createNewFolder(getParentFolder().getShemaLibrary(), getParentFolder(), getNewFolderName());
            }
            else {
            	if (!getFocusedObject().getProject().getFlexoComponentLibrary().hasRootFolder()) {
            		_parentFolder = OEShemaFolder.createNewRootFolder(getFocusedObject().getProject().getShemaLibrary());
            	}
            	if (getFocusedObject() instanceof OEShemaFolder) {
            		_parentFolder = (OEShemaFolder)getFocusedObject();
            	}
            	else {
            		_parentFolder = getFocusedObject().getProject().getShemaLibrary().getRootFolder();
            	}
                 _newFolder = OEShemaFolder.createNewFolder(getParentFolder().getShemaLibrary(), getParentFolder(), getNewFolderName());
            }
        }
        else {
        	throw new InvalidParametersException("unable to create shema folder: no focused object supplied");
        }
    }

    public String getNewFolderName() 
    {
        return _newFolderName;
    }

    public void setNewFolderName(String newFolderName) 
    {
        _newFolderName = newFolderName;
    }

    public OEShemaFolder getParentFolder()
    {
        return _parentFolder;
    }

    public void setParentFolder(OEShemaFolder parentFolder) 
    {
        _parentFolder = parentFolder;
    }

    public OEShemaFolder getNewFolder() 
    {
        return _newFolder;
    }

    
}
