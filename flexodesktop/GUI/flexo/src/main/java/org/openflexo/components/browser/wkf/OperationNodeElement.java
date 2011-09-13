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
package org.openflexo.components.browser.wkf;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ExpansionSynchronizedElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.action.OpenActionLevel;
import org.openflexo.foundation.wkf.action.OpenExecutionPetriGraph;
import org.openflexo.foundation.wkf.dm.ObjectVisibilityChanged;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenClosed;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenOpened;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.icon.WKFIconLibrary;


/**
 * Browser element representing an Operation Node
 *
 * @author sguerin
 *
 */
public class OperationNodeElement extends BrowserElement implements ExpansionSynchronizedElement
{
    static final Logger logger = Logger.getLogger(OperationNodeElement.class.getPackage().getName());

    public OperationNodeElement(OperationNode node, ProjectBrowser browser, BrowserElement parent)
    {
        super(node, BrowserElementType.OPERATION_NODE, browser, parent);
    }

	@Override
	public TreePath getTreePath() {
		return super.getTreePath();
	}

    private boolean isObserving = false;

    private void addObserver()
    {
        if (isObserving) {
			return;
		}
        if (getOperationNode().getActionPetriGraph() != null) {
            getOperationNode().getActionPetriGraph().addObserver(this);
            isObserving = true;
        } else if ((getOperationNode() instanceof SelfExecutableNode) && (((SelfExecutableNode)getOperationNode()).getExecutionPetriGraph()!=null)) {
        	((SelfExecutableNode)getOperationNode()).getExecutionPetriGraph().addObserver(this);
        	isObserving = true;
        }
    }
    @Override
    public void delete() {
    	if (getOperationNode().getActionPetriGraph() != null) {
    		getOperationNode().getActionPetriGraph().deleteObserver(this);
            isObserving = false;
        } else if ((getOperationNode() instanceof SelfExecutableNode) && (((SelfExecutableNode)getOperationNode()).getExecutionPetriGraph()!=null)) {
        	((SelfExecutableNode)getOperationNode()).getExecutionPetriGraph().deleteObserver(this);
        	isObserving = false;
        }
    	super.delete();
    }

    @Override
	protected void buildChildrenVector()
    {
        if (logger.isLoggable(Level.FINER)) {
			logger.finer("Building children for operation "+getName());
		}
        // We add component
        if (getOperationNode().hasWOComponent()) {
            if (getOperationNode().getComponentInstance().getComponentDefinition() != null) {
                addToChilds(getOperationNode().getComponentInstance().getComponentDefinition());
            }
        }

        // We add action nodes
        if (getOperationNode().getActionPetriGraph() != null) {
            addObserver();
            for (Enumeration<PetriGraphNode> e = getOperationNode().getActionPetriGraph().getSortedNodes(); e.hasMoreElements();) {
            	PetriGraphNode node = e.nextElement();
            	if(node instanceof ActionNode) {
					addToChilds(node);
				}
            }
        } else if ((getOperationNode() instanceof SelfExecutableNode) && (((SelfExecutableNode)getOperationNode()).getExecutionPetriGraph()!=null)) {
        	addObserver();
            Enumeration<PetriGraphNode> en = ((SelfExecutableNode)getOperationNode()).getExecutionPetriGraph().getSortedNodes();
            while (en.hasMoreElements()) {
                addToChilds(en.nextElement());
            }
        }


/*        // We add operator nodes
        for (Enumeration e = getOperationNode().getAllOperatorNodes().elements(); e.hasMoreElements();) {
            OperatorNode operatorNode = (OperatorNode) e.nextElement();
            addToChilds(operatorNode);
        }

        // We add event nodes
        for (Enumeration e = getOperationNode().getAllEventNodes().elements(); e.hasMoreElements();) {
            EventNode eventNode = (EventNode) e.nextElement();
            addToChilds(eventNode);
        }
*/
        // We add pre conditions
        for (Enumeration<FlexoPreCondition> e = getOperationNode().getPreConditions().elements(); e.hasMoreElements();) {
            addToChilds(e.nextElement());
        }

        // We add post conditions
        for (Enumeration<FlexoPostCondition<AbstractNode, AbstractNode>> e = getOperationNode().getOutgoingPostConditions().elements(); e.hasMoreElements();) {
            addToChilds(e.nextElement());
        }

    }

    @Override
	public String getName()
    {
        if (getOperationNode().getName() == null) {
            return getOperationNode().getDefaultName();
        }
        return getOperationNode().getName();
    }

    protected OperationNode getOperationNode()
    {
        return (OperationNode) getObject();
    }

    @Override
	public Icon getIcon()
    {
        if (getOperationNode() instanceof SelfExecutableNode) {
            return decorateIcon(WKFIconLibrary.SELF_EXECUTABLE_ICON);
        } else if (getOperationNode().isBeginNode()) {
            return WKFIconLibrary.BEGIN_OPERATION_ICON;
        } else if (getOperationNode().isEndNode()) {
            return WKFIconLibrary.END_OPERATION_ICON;
        } else {
            return super.getIcon();
        }
    }

    @Override
	public boolean isExpansionSynchronizedWithData()
    {
        if (_browser.getSelectionManager() != null) {
            return (getOperationNode().getProcess() == _browser.getSelectionManager().getRootFocusedObject());
        }
        return false;
    }

    @Override
	public boolean isExpanded()
    {
    	if (getOperationNode() instanceof SelfExecutableNode) {
    		return ((SelfExecutableNode)getOperationNode()).hasExecutionPetriGraph() && ((SelfExecutableNode)getOperationNode()).getExecutionPetriGraph().getIsVisible();
    	}
        if (getOperationNode().hasContainedPetriGraph()) {
            return getOperationNode().getActionPetriGraph().getIsVisible();
        }
        return false;
    }

    @Override
	public void expand()
    {
    	if (getOperationNode() instanceof SelfExecutableNode) {
    		if (((SelfExecutableNode)getOperationNode()).hasExecutionPetriGraph() && !isExpanded()) {
    			OpenExecutionPetriGraph.actionType.makeNewAction(getOperationNode(), null, getProjectBrowser().getEditor()).doAction();
    		}
    	} else if (getOperationNode().hasContainedPetriGraph() && !getOperationNode().isBeginNode() && !getOperationNode().isEndNode() && !isExpanded()) {
            OpenActionLevel.actionType.makeNewAction(getOperationNode(),null,getProjectBrowser().getEditor()).doAction();
        }
    }

    @Override
	public void collapse()
    {
    	if (getOperationNode() instanceof SelfExecutableNode) {
    		if (((SelfExecutableNode)getOperationNode()).hasExecutionPetriGraph() && isExpanded()) {
    			OpenExecutionPetriGraph.actionType.makeNewAction(getOperationNode(), null, getProjectBrowser().getEditor()).doAction();
    		}
    	} else if (getOperationNode().hasContainedPetriGraph() && isExpanded()) {
            OpenActionLevel.actionType.makeNewAction(getOperationNode(),null,getProjectBrowser().getEditor()).doAction();
        }
    }

    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
        if (_browser != null) {
        	if ((dataModification instanceof ObjectVisibilityChanged) || (dataModification instanceof PetriGraphHasBeenClosed) || (dataModification instanceof PetriGraphHasBeenOpened)) {
                _browser.notifyExpansionChanged(this);
            } else if((dataModification.propertyName()!=null) && dataModification.propertyName().equals("index")){
                return;
            } else {
                super.update(observable, dataModification);
            }
        }
    }

    @Override
	public boolean isNameEditable()
    {
        return true;
    }

    @Override
	public void setName(String aName)
    {
        getOperationNode().setName(aName);
    }

    @Override
	public boolean requiresExpansionFor(BrowserElement next)
    {
		if (next instanceof PreConditionElement) {
			return ((PreConditionElement)next).getPreCondition().isContainedIn(getOperationNode());
		} else if (next instanceof PostConditionElement) {
			FlexoPostCondition edge = ((PostConditionElement) next)
					.getPostCondition();
			if (((edge.getNextNode()!=null) && edge.getNextNode().isContainedIn(getOperationNode()))
					&& ((edge.getStartNode()!=null) && edge.getStartNode().isContainedIn(getOperationNode()))) {
				return true;
			}
			return false;
		} else if (next instanceof PortMapElement) {
             return false;
        } else if (next instanceof MessageElement) {
             return false;
        } else {
             return true;
        }
    }



}
