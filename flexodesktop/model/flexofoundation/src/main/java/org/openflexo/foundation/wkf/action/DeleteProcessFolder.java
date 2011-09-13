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
package org.openflexo.foundation.wkf.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.UndoException;
import org.openflexo.foundation.wkf.InvalidParentProcessException;
import org.openflexo.foundation.wkf.ProcessFolder;


public class DeleteProcessFolder extends FlexoAction<DeleteProcessFolder,ProcessFolder,ProcessFolder>
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DeleteProcessFolder.class.getPackage().getName());

    public static FlexoActionType<DeleteProcessFolder,ProcessFolder,ProcessFolder> actionType
    = new FlexoActionType<DeleteProcessFolder,ProcessFolder,ProcessFolder> ("delete_process_folder",FlexoActionType.DELETE_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public DeleteProcessFolder makeNewAction(ProcessFolder focusedObject, Vector<ProcessFolder> globalSelection, FlexoEditor editor)
        {
            return new DeleteProcessFolder(focusedObject, globalSelection,editor);
        }

        @Override
		protected boolean isVisibleForSelection(ProcessFolder object, Vector<ProcessFolder> globalSelection)
        {
            return object != null;
        }

        @Override
		protected boolean isEnabledForSelection(ProcessFolder object, Vector<ProcessFolder> globalSelection)
        {
            return object != null;
        }

    };

    DeleteProcessFolder (ProcessFolder focusedObject, Vector<ProcessFolder> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection,editor);
    }

    static {
    	FlexoModelObject.addActionForClass(actionType, ProcessFolder.class);
    }

    @Override
	protected void doAction(Object context) throws InvalidParentProcessException, UndoException {
    	for(FlexoModelObject folder:getGlobalSelectionAndFocusedObject())
    		if (folder instanceof ProcessFolder)
    			folder.delete();
    }

}
