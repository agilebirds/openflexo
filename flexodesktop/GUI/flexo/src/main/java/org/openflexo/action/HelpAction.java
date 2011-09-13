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
package org.openflexo.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;

public class HelpAction extends FlexoGUIAction<HelpAction,FlexoModelObject,FlexoModelObject> 
{

    public static FlexoActionType<HelpAction,FlexoModelObject,FlexoModelObject> actionType = new FlexoActionType<HelpAction,FlexoModelObject,FlexoModelObject> ("help",FlexoActionType.helpGroup) {

        /**
         * Factory method
         */
        @Override
		public HelpAction makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) 
        {
            return new HelpAction(focusedObject, globalSelection,editor);
        }

        @Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) 
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) 
        {
            return (object != null) && (object instanceof InspectableObject);
        }
                
    };
    
    static {
        FlexoModelObject.addActionForClass (HelpAction.actionType, FlexoModelObject.class);
    }
    
    HelpAction (FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection,editor);
    }

    @Override
	public String getLocalizedName ()
    {
        if (getFocusedObject() != null) {
           /* String shortClassName = null;
            String extClassName = getFocusedObject().getClass().getName();
            StringTokenizer st = new StringTokenizer(extClassName,".");
            while (st.hasMoreTokens()) shortClassName = st.nextToken();*/
            return FlexoLocalization.localizedForKey("help_on")+" "+FlexoLocalization.localizedForKey(getFocusedObject().getClassNameKey());
        }
        return null;
    }
}
