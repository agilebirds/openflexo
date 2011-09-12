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
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.PortRegisteryHasBeenClosed;
import org.openflexo.foundation.wkf.dm.PortRegisteryHasBeenOpened;
import org.openflexo.localization.FlexoLocalization;

public class OpenPortRegistery extends FlexoUndoableAction<OpenPortRegistery,FlexoProcess,WKFObject> 
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(OpenPortRegistery.class.getPackage().getName());

    public static FlexoActionType<OpenPortRegistery,FlexoProcess,WKFObject> actionType 
    = new FlexoActionType<OpenPortRegistery,FlexoProcess,WKFObject> 
    ("open_port_registery",FlexoActionType.defaultGroup) {

        /**
         * Factory method
         */
        @Override
		public OpenPortRegistery makeNewAction(FlexoProcess focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) 
        {
            return new OpenPortRegistery(focusedObject, globalSelection,editor);
        }

        @Override
		protected boolean isVisibleForSelection(FlexoProcess object, Vector<WKFObject> globalSelection) 
        {
            return object!=null && !object.isImported();
        }

        @Override
		protected boolean isEnabledForSelection(FlexoProcess object, Vector<WKFObject> globalSelection) 
        {
            return isVisibleForSelection(object, globalSelection);
        }
                
    };
    
    OpenPortRegistery (FlexoProcess focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection,editor);
    }

    @Override
	protected void doAction(Object context) 
    {
   		//getFocusedObject().getPortRegistery().setIsVisible(!getFocusedObject().getPortRegistery().getIsVisible());
    	if (getFocusedObject().getPortRegistery().getIsVisible()) {
    		getFocusedObject().getPortRegistery().setIsVisible(false);
    		getFocusedObject().setChanged();
    		getFocusedObject().notifyObservers(new PortRegisteryHasBeenClosed(getFocusedObject().getPortRegistery()));
    	}
    	else {
    		getFocusedObject().getPortRegistery().setIsVisible(true);
    		getFocusedObject().setChanged();
    		getFocusedObject().notifyObservers(new PortRegisteryHasBeenOpened(getFocusedObject().getPortRegistery()));
    	}	
    }

    @Override
	public String getLocalizedName ()
    {
        if ((getFocusedObject()).getPortRegistery() != null) {
            if ((getFocusedObject()).getPortRegistery().getIsVisible()) {
                return FlexoLocalization.localizedForKey("close_port_registery");
            }
            else {
                return FlexoLocalization.localizedForKey("open_port_registery");
           }
        }
        return super.getLocalizedName();
        
    }

    @Override
	protected void undoAction(Object context) 
    {
        doAction(context);
    }

    @Override
	protected void redoAction(Object context)
    {
        doAction(context);
   }

}
