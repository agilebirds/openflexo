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
import org.openflexo.foundation.rm.FlexoProject;


/**
 * @author gpolet
 *
 */
public class ProjectExcelExportAction extends FlexoGUIAction<ProjectExcelExportAction, FlexoProject, FlexoModelObject>
{

    public static final FlexoActionType<ProjectExcelExportAction, FlexoProject, FlexoModelObject> actionType = new FlexoActionType<ProjectExcelExportAction, FlexoProject, FlexoModelObject>("project_excel_export",FlexoActionType.docGroup) {

        @Override
        protected boolean isEnabledForSelection(FlexoProject object, Vector<FlexoModelObject> globalSelection)
        {
            return true;
        }

        @Override
        protected boolean isVisibleForSelection(FlexoProject object, Vector<FlexoModelObject> globalSelection)
        {
            return true;
        }

        @Override
        public ProjectExcelExportAction makeNewAction(FlexoProject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor)
        {
            return new ProjectExcelExportAction(focusedObject,globalSelection,editor);
        }
        
    };
    
    static {
        FlexoModelObject.addActionForClass (ProjectExcelExportAction.actionType, FlexoProject.class);
    }
    
    /**
     * @param actionType
     * @param focusedObject
     * @param globalSelection
     * @param editor
     */
    protected ProjectExcelExportAction(FlexoProject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

}
