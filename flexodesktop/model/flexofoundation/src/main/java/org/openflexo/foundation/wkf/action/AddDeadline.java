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
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.DeadLine;
import org.openflexo.foundation.wkf.DeadLineList;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;

@Deprecated
public class AddDeadline extends FlexoAction<AddDeadline,WKFObject,WKFObject> 
{

    private static final Logger logger = Logger.getLogger(AddDeadline.class.getPackage().getName());
    
    public static FlexoActionType<AddDeadline,WKFObject,WKFObject> actionType 
    = new FlexoActionType<AddDeadline,WKFObject,WKFObject> (
    		"add_new_deadline",
    		FlexoActionType.newMenu,
    		FlexoActionType.newMenuGroup1,
    		FlexoActionType.ADD_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public AddDeadline makeNewAction(WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) 
        {
            return new AddDeadline(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(WKFObject object, Vector<WKFObject> globalSelection) 
        {
            return object instanceof FlexoProcess && !((FlexoProcess)object).isImported();
        }

        @Override
		protected boolean isEnabledForSelection(WKFObject object, Vector<WKFObject> globalSelection) 
        {
            return ((object != null) && ((object instanceof FlexoProcess) || (object instanceof DeadLineList) || (object instanceof DeadLine)));
        }
                
    };
    
    private String _newDeadlineName;
    private DeadLine _newDeadline;

    AddDeadline (WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

     public FlexoProcess getProcess() 
    {
         if (getFocusedObject() != null)  {
             return getFocusedObject().getProcess();
         }
        return null;
    }
    
    public String getNewDeadlineName() 
    {
        return _newDeadlineName;
    }

    public void setNewDeadlineName(String newDeadlineName) 
    {
        _newDeadlineName = newDeadlineName;
    }
    
    @Override
	protected void doAction(Object context) 
    {
        logger.info ("Add deadline");
        if (getProcess() != null)  {
            DeadLineList deadlineList = getProcess().getDeadLineList();
            deadlineList.addToDeadLines(_newDeadline = new DeadLine(getProcess(), getNewDeadlineName()));
        }
        else {
            logger.warning("Focused process is null !");
        }
    }

    public DeadLine getNewDeadline() 
    {
        return _newDeadline;
    }

 
}
