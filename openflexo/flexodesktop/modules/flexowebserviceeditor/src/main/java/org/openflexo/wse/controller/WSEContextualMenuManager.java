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
package org.openflexo.wse.controller;

import java.awt.Component;
import java.awt.event.MouseEvent;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.action.AddServiceInterface;
import org.openflexo.foundation.wkf.action.AddServiceOperation;
import org.openflexo.foundation.wkf.action.WKFDelete;
import org.openflexo.selection.ContextualMenuManager;


/**
 * 
 * Contextual menu manager for this module
 * 
 * @author yourname
 */
public class WSEContextualMenuManager extends ContextualMenuManager {

    private WSEController _controller;
    
    public WSEContextualMenuManager(WSESelectionManager selectionManager, FlexoEditor editor, WSEController controller)
    {
        super(selectionManager,editor);
        _controller = controller;
    }
    
     @Override
	public FlexoModelObject getFocusedObject(Component focusedComponent, MouseEvent e)
    {
         // put some code here to detect focused object
         // finally calls super's implementation
          return super.getFocusedObject(focusedComponent,e);
    }
     
     @Override
	public boolean acceptAction(FlexoActionType actionType){
    	 	if(actionType.equals(WKFDelete.actionType)) return false;
    	 	else if (actionType.equals(AddServiceInterface.actionType)) return false;
    	 	else if (actionType.equals(AddServiceOperation.actionType)) return false;
    	 	return true;
     }

}
