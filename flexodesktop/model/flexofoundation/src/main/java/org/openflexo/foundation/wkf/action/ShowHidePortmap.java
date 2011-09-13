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
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.localization.FlexoLocalization;

public class ShowHidePortmap extends FlexoUndoableAction<ShowHidePortmap,FlexoPortMap,WKFObject>
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ShowHidePortmap.class.getPackage().getName());

    public static FlexoActionType<ShowHidePortmap,FlexoPortMap,WKFObject> actionType
    = new FlexoActionType<ShowHidePortmap,FlexoPortMap,WKFObject>
    ("show_portmap",FlexoActionType.defaultGroup) {

        /**
         * Factory method
         */
        @Override
		public ShowHidePortmap makeNewAction(FlexoPortMap focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor)
        {
            return new ShowHidePortmap(focusedObject, globalSelection,editor);
        }

        @Override
		protected boolean isVisibleForSelection(FlexoPortMap object, Vector<WKFObject> globalSelection)
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(FlexoPortMap object, Vector<WKFObject> globalSelection)
        {
            return true;
        }

    };

    ShowHidePortmap (FlexoPortMap focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection,editor);
    }

    @Override
	protected void doAction(Object context)
    {
    	if (getFocusedObject() != null)
    		getFocusedObject().setIsVisible(!getFocusedObject().getIsVisible());
     }

    @Override
	public String getLocalizedName ()
    {
        if (getFocusedObject() != null) {
            if (getFocusedObject().getIsVisible()) {
				return FlexoLocalization.localizedForKey("hide_portmap");
			} else {
				return FlexoLocalization.localizedForKey("show_portmap");
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
