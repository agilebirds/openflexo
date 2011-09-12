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

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.WorkflowModelObject;


public class AddOperationMetricsDefinition extends AddMetricsDefinition {

    public static FlexoActionType<AddMetricsDefinition,FlexoWorkflow,WorkflowModelObject> actionType 
    = new FlexoActionType<AddMetricsDefinition,FlexoWorkflow,WorkflowModelObject> (
    		"add_operation_metrics_definition",
    		FlexoActionType.newMenu,
    		FlexoActionType.newMenuGroup1,
    		FlexoActionType.ADD_ACTION_TYPE) {
    	
    	/**
    	 * Factory method
    	 */
    	@Override
		public AddMetricsDefinition makeNewAction(FlexoWorkflow focusedObject, Vector<WorkflowModelObject> globalSelection, FlexoEditor editor) 
    	{
    		return new AddOperationMetricsDefinition(focusedObject, globalSelection, editor);
    	}
    	
    	@Override
		protected boolean isVisibleForSelection(FlexoWorkflow object, Vector<WorkflowModelObject> globalSelection) 
    	{
    		return false;
    	}
    	
    	@Override
		protected boolean isEnabledForSelection(FlexoWorkflow object, Vector<WorkflowModelObject> globalSelection) 
    	{
    		return object!=null;
    	}
    	
    };

    static {
    	FlexoModelObject.addActionForClass(actionType, FlexoWorkflow.class);
    }
    
    protected AddOperationMetricsDefinition(FlexoWorkflow focusedObject, Vector<WorkflowModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		getFocusedObject().addToOperationMetricsDefinitions(createMetricsDefinition());
	}

}
