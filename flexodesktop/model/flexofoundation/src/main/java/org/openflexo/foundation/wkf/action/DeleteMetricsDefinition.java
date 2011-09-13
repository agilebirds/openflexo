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
import org.openflexo.foundation.wkf.MetricsDefinition;


public class DeleteMetricsDefinition extends FlexoAction<DeleteMetricsDefinition,MetricsDefinition,MetricsDefinition> 
{

    private static final Logger logger = Logger.getLogger(DeleteMetricsDefinition.class.getPackage().getName());
    
    public static FlexoActionType<DeleteMetricsDefinition,MetricsDefinition,MetricsDefinition> actionType 
    = new FlexoActionType<DeleteMetricsDefinition,MetricsDefinition,MetricsDefinition> (
    		"delete_metrics_definition",
    		FlexoActionType.editGroup,
    		FlexoActionType.DELETE_ACTION_TYPE) {

    	/**
         * Factory method
         */
        @Override
		public DeleteMetricsDefinition makeNewAction(MetricsDefinition focusedObject, Vector<MetricsDefinition> globalSelection, FlexoEditor editor) 
        {
            return new DeleteMetricsDefinition(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(MetricsDefinition focusedObject, Vector<MetricsDefinition> globalSelection) 
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(MetricsDefinition focusedObject, Vector<MetricsDefinition> globalSelection) 
        {
            return (focusedObject != null || (globalSelection != null && globalSelection.size() > 0));
        }
                
    };
    
    static {
    	FlexoModelObject.addActionForClass(actionType, MetricsDefinition.class);
    }
    
    protected DeleteMetricsDefinition(MetricsDefinition focusedObject, Vector<MetricsDefinition> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Delete MetricsDefinition(s)");
		for (MetricsDefinition metricsDefinition : getMetricsDefinitionToDelete())
			metricsDefinition.delete();
	}

	public Vector<MetricsDefinition> getMetricsDefinitionToDelete() {
		Vector<MetricsDefinition> metricsToDelete = new Vector<MetricsDefinition>();
		if (getGlobalSelection() != null) {
			for (FlexoModelObject o : getGlobalSelection()) {
				if (o instanceof MetricsDefinition)
					metricsToDelete.add((MetricsDefinition) o);
			}
		}
		if (!metricsToDelete.contains(getFocusedObject()))
			metricsToDelete.add(getFocusedObject());
		return metricsToDelete;
	}

	public MetricsDefinition getMetricsDefinition() {
		return getFocusedObject();
	}

}
