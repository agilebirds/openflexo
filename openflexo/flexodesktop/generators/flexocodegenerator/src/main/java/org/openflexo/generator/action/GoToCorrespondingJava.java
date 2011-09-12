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
package org.openflexo.generator.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.generator.cg.CGWOFile;


/**
 * @author gpolet
 *
 */
public class GoToCorrespondingJava extends FlexoGUIAction<GoToCorrespondingJava, CGWOFile, CGWOFile>
{

    public static FlexoActionType<GoToCorrespondingJava, CGWOFile, CGWOFile> actionType = new FlexoActionType<GoToCorrespondingJava, CGWOFile, CGWOFile>("go_to_corresponding_java",GCAction.SHOW_GROUP,FlexoActionType.NORMAL_ACTION_TYPE) {

        @Override
        protected boolean isEnabledForSelection(CGWOFile object, Vector<CGWOFile> globalSelection)
        {
            return true;
        }

        @Override
        protected boolean isVisibleForSelection(CGWOFile object, Vector<CGWOFile> globalSelection)
        {
            return true;
        }

        @Override
        public GoToCorrespondingJava makeNewAction(CGWOFile focusedObject, Vector<CGWOFile> globalSelection, FlexoEditor editor)
        {
            return new GoToCorrespondingJava(focusedObject,globalSelection,editor);
        }
        
    };
    
    static {
        FlexoModelObject.addActionForClass(GoToCorrespondingJava.actionType, CGWOFile.class);
    }
    
    /**
     * @param actionType
     * @param focusedObject
     * @param globalSelection
     * @param editor
     */
    protected GoToCorrespondingJava(CGWOFile focusedObject, Vector<CGWOFile> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

}
