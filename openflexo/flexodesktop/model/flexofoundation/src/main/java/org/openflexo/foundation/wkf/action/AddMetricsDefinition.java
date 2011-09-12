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
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.MetricsDefinition;
import org.openflexo.foundation.wkf.WorkflowModelObject;
import org.openflexo.foundation.wkf.MetricsDefinition.MetricsType;


public abstract class AddMetricsDefinition extends FlexoAction<AddMetricsDefinition,FlexoWorkflow,WorkflowModelObject> 
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(AddMetricsDefinition.class.getPackage().getName());
    
    private String newMetricsName;
    private MetricsType type;
    private String unit;
    private String description;
    private boolean alwaysDefined=false;

    AddMetricsDefinition (FlexoActionType actionType, FlexoWorkflow focusedObject, Vector<WorkflowModelObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    private MetricsDefinition newMetricsDefinition;
    
    public MetricsDefinition getNewMetricsDefinition() {
		return newMetricsDefinition;
	}
    
    protected MetricsDefinition createMetricsDefinition() {
    	if (newMetricsDefinition==null) {
    		newMetricsDefinition = new MetricsDefinition(getFocusedObject().getProject(),getFocusedObject());
    		newMetricsDefinition.setName(newMetricsName);
    		newMetricsDefinition.setType(getType());
    		newMetricsDefinition.setDescription(getDescription());
    		newMetricsDefinition.setUnit(unit);
    		newMetricsDefinition.setAlwaysDefined(alwaysDefined);
    	}
    	return newMetricsDefinition;
    }
    
    public FlexoWorkflow getWorkflow() 
    {
        return getFocusedObject();
    }
    
    public String getNewMetricsName() 
    {
        return newMetricsName;
    }

    public void setNewMetricsName(String newMetricsName) 
    {
        this.newMetricsName = newMetricsName;
    }

	public MetricsType getType() {
		if (type==null)
			type = MetricsType.TEXT;
		return type;
	}

	public void setType(MetricsType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setAlwaysDefined(boolean alwaysDefined) {
		this.alwaysDefined = alwaysDefined;
	}
    
}
