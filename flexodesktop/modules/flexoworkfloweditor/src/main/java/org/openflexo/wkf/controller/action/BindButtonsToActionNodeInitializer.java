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
package org.openflexo.wkf.controller.action;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.wkf.action.BindButtonsToActionNode;
import org.openflexo.foundation.wkf.action.OpenActionLevel;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.wkf.view.popups.AssociateActionsWithButtons;


public class BindButtonsToActionNodeInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	BindButtonsToActionNodeInitializer(WKFControllerActionInitializer actionInitializer)
	{
		super(BindButtonsToActionNode.actionType,actionInitializer);
	}
	
	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() 
	{
		return (WKFControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionFinalizer<BindButtonsToActionNode> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<BindButtonsToActionNode>() {
			@Override
			public boolean run(ActionEvent e, BindButtonsToActionNode action)
			{
				OperationNode node = action.getFocusedObject();
				if (node.getActionPetriGraph() != null && !node.getActionPetriGraph().getIsVisible()){
					node.getActionPetriGraph().setIsVisible(true);
		        }
				return true;
			}
		};
	}
	
	@Override
	protected FlexoActionInitializer<BindButtonsToActionNode> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<BindButtonsToActionNode>() {
            @Override
			public boolean run(ActionEvent e, BindButtonsToActionNode action)
            {
                OperationNode node = action.getFocusedObject();
                action.setOperationNode(node);
                Iterator i = node.getAllActionNodes().iterator();
                Vector<ActionNode> actions = new Vector<ActionNode>();
                while (i.hasNext()) {
                    ActionNode element = (ActionNode) i.next();
                    if (!element.isBeginNode() && !element.isEndNode())
                        actions.add(element);
                }
                Vector b = node.getComponentInstance().getComponentDefinition().getWOComponent().getAllButtonInterface();
                i = b.iterator();
                Vector<IEHyperlinkWidget> buttons = new Vector<IEHyperlinkWidget>();
                while (i.hasNext()) {
                	IEHyperlinkWidget w = (IEHyperlinkWidget) i.next();
                    if (w.hasActionType())
                        buttons.add(w);
                }
                if (buttons.size() > 0) {
                	if (node.getContainedPetriGraph()==null || !node.getContainedPetriGraph().getIsVisible())
                		OpenActionLevel.actionType.makeNewAction(node, null, action.getEditor()).doAction();
                    if (logger.isLoggable(Level.INFO))
                        logger.info("Some buttons require an associated flexo action");
                    AssociateActionsWithButtons associator = new AssociateActionsWithButtons(buttons, actions, node,
                    		action.getException());
                    associator.setVisible(true);
                    int retval = associator.getButtonPressed();
                    action.setActions(associator.getActions());
                    action.setAssociations(associator.getAssociations());
                    action.setInsertActionNode(associator.getInsertActionNode());
                    action.setCleanActions(associator.isCleanActions());
                    action.setButtons(buttons);
                    if (retval == AssociateActionsWithButtons.OK)
                    	action.setRetval(BindButtonsToActionNode.OK);
                    else if (retval == AssociateActionsWithButtons.IGNORE)
                    	action.setRetval(BindButtonsToActionNode.IGNORE);
                    else
                    	action.setRetval(BindButtonsToActionNode.CANCEL);
                    return true;
                } else if (logger.isLoggable(Level.INFO))
                    logger.info("There are no buttons that require an action");
                return false;
            }
        };
	}

}
