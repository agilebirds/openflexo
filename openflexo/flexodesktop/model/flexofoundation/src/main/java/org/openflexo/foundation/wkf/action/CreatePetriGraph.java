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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.FatherNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SelfExecutableActivityNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.foundation.wkf.node.SelfExecutableOperationNode;


public class CreatePetriGraph extends FlexoUndoableAction<CreatePetriGraph,FatherNode,WKFObject> 
{

    private static final Logger logger = Logger.getLogger(CreatePetriGraph.class.getPackage().getName());

    public static FlexoActionType<CreatePetriGraph,FatherNode,WKFObject> actionType = new FlexoActionType<CreatePetriGraph,FatherNode,WKFObject> ("create_petri_graph",FlexoActionType.defaultGroup) {

        /**
         * Factory method
         */
        @Override
		public CreatePetriGraph makeNewAction(FatherNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) 
        {
            return new CreatePetriGraph(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(FatherNode object, Vector<WKFObject> globalSelection) 
        {
            return false; // Action is never visible but always active.
        }

        @Override
		protected boolean isEnabledForSelection(FatherNode object, Vector<WKFObject> globalSelection) 
        {
            return object.getContainedPetriGraph() == null;
        }
                
    };
    
    CreatePetriGraph (FatherNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection,editor);
    }

    @Override
	protected void doAction(Object context) 
    {
		FatherNode node = getFocusedObject();
		if (node instanceof SelfExecutableNode) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("This call should be changed and CreateExcecutionPetriGraph action should be used instead.");
			if (((SelfExecutableNode) node).getExecutionPetriGraph() == null) {
				if (node instanceof SelfExecutableActivityNode) {
					newPetriGraph = ActivityPetriGraph.createNewActivityPetriGraph((SelfExecutableActivityNode) node);
				} else if (node instanceof SelfExecutableOperationNode) {
					newPetriGraph = OperationPetriGraph.createNewOperationPetriGraph((SelfExecutableOperationNode) node);
				}
			}
		} else {
			if (node.getContainedPetriGraph() == null) {
				logger.info("CreatePetriGraph");
				if (node instanceof AbstractActivityNode && ((AbstractActivityNode)node).mightHaveOperationPetriGraph()) {
					newPetriGraph = new OperationPetriGraph((AbstractActivityNode) node);
					((AbstractActivityNode) node).setOperationPetriGraph((OperationPetriGraph) newPetriGraph);
				} else if (node instanceof OperationNode) {
					newPetriGraph = new ActionPetriGraph((OperationNode) node);
					((OperationNode) node).setActionPetriGraph((ActionPetriGraph) newPetriGraph);
				}
			}
		}
		if (newPetriGraph!=null) {
			objectCreated("NEW_PETRI_GRAPH", newPetriGraph);
			objectCreated("NEW_BEGIN_NODE", newPetriGraph.getAllBeginNodes().firstElement());
			objectCreated("NEW_END_NODE", newPetriGraph.getAllEndNodes().firstElement());
		}
	}
    
    @Override
	protected void undoAction(Object context) 
    {
		logger.info("CreatePetriGraph: UNDO");
		newPetriGraph.delete();
    }

    @Override
	protected void redoAction(Object context)
    {
		logger.info("CreatePetriGraph: REDO");
        doAction(context);
   }

    private FlexoPetriGraph newPetriGraph = null;

	public FlexoPetriGraph getNewPetriGraph() 
	{
		return newPetriGraph;
	}

}
