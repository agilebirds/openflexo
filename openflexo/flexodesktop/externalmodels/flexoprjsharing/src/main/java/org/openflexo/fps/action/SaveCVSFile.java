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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.FPSObject;


public class SaveCVSFile extends CVSAction<SaveCVSFile,CVSFile>
{

    private static final Logger logger = Logger.getLogger(SaveCVSFile.class.getPackage().getName());

    public static FlexoActionType<SaveCVSFile,CVSFile,FPSObject> actionType 
    = new FlexoActionType<SaveCVSFile,CVSFile,FPSObject> (
    		"save_file",EDITION_GROUP,FlexoActionType.NORMAL_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public SaveCVSFile makeNewAction(CVSFile focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) 
        {
            return new SaveCVSFile(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(CVSFile object, Vector<FPSObject> globalSelection) 
        {
            return (object != null && object.getSharedProject() != null);
       }

        @Override
		protected boolean isEnabledForSelection(CVSFile object, Vector<FPSObject> globalSelection) 
        {
            return ((object != null) && (object.isEdited()));
       }
                
    };
    
    static {
        FlexoModelObject.addActionForClass (SaveCVSFile.actionType, CVSFile.class);
    }
    

    SaveCVSFile (CVSFile focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    @Override
	protected void doAction(Object context) throws SaveResourceException
    {
    	logger.info ("Save edited file "+getFocusedObject().getFileName());
    	if (getFocusedObject() != null) {
    		getFocusedObject().save();
     	}
     }
	
}
