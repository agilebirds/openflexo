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
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoProcessNode;
import org.openflexo.foundation.wkf.InvalidParentProcessException;
import org.openflexo.foundation.wkf.ProcessFolder;


public class AddToProcessFolder extends FlexoAction<AddToProcessFolder,FlexoModelObject,FlexoModelObject>
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(AddToProcessFolder.class.getPackage().getName());

    public static FlexoActionType<AddToProcessFolder,FlexoModelObject,FlexoModelObject> actionType
    = new FlexoActionType<AddToProcessFolder,FlexoModelObject,FlexoModelObject> ("add_to_process_folder",FlexoActionType.defaultGroup) {

        /**
         * Factory method
         */
        @Override
		public AddToProcessFolder makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor)
        {
            return new AddToProcessFolder(focusedObject, globalSelection,editor);
        }

        @Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection)
        {
            return false;
        }

        @Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection)
        {
            return getProcessNode(object) != null && !getProcessNode(object).isImported();
        }

    };

    private FlexoProcessNode getProcessNode() {
    	return getProcessNode(getFocusedObject());
	}

    static FlexoProcessNode getProcessNode(FlexoModelObject object) {
    	if (object instanceof FlexoProcessNode)
    		return (FlexoProcessNode) object;
    	else if (object instanceof FlexoProcess) {
    		return ((FlexoProcess)object).getProcessNode();
    	}
		return null;
    }

    AddToProcessFolder (FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection,editor);
    }

    static {
    	FlexoModelObject.addActionForClass(actionType, FlexoProcessNode.class);
    	FlexoModelObject.addActionForClass(actionType, FlexoProcess.class);
    }

    private ProcessFolder destination;

    @Override
	protected void doAction(Object context) throws InvalidParentProcessException, UndoException {
    	if (getDestination()!=null) {
    		getDestination().addToProcesses(getProcessNode());
    	}
    }

	public void setDestination(ProcessFolder destination) {
		this.destination = destination;
	}

	public ProcessFolder getDestination() {
		return destination;
	}

}
