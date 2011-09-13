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

import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ExpansionSynchronizedElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.foundation.wkf.action.OpenLoopedPetriGraph;
import org.openflexo.foundation.wkf.dm.ObjectVisibilityChanged;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenClosed;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenOpened;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.PetriGraphNode;


/**
 * @author gpolet
 *
 */
public class OperatorLoopElement extends OperatorNodeElement implements ExpansionSynchronizedElement
{

    /**
     * @param object
     * @param browser
     */
    public OperatorLoopElement(LOOPOperator object, ProjectBrowser browser, BrowserElement parent)
    {
        super(object, BrowserElementType.OPERATOR_LOOP_NODE, browser,parent);
    }

	@Override
	public TreePath getTreePath() {
		return super.getTreePath();
	}

    private boolean isObserving = false;

    private void addObserver()
    {
        if (isObserving)
            return;
        if (getLOOPOperator().getExecutionPetriGraph()!=null) {
        	getLOOPOperator().getExecutionPetriGraph().addObserver(this);
        	isObserving = true;
        }
    }
    @Override
    public void delete() {
    	if (getLOOPOperator().getExecutionPetriGraph()!=null) {
        	getLOOPOperator().getExecutionPetriGraph().deleteObserver(this);
        	isObserving = false;
        }
    	super.delete();
    }

    @Override
    protected void buildChildrenVector() {
    	if (getLOOPOperator().hasExecutionPetriGraph()) {
    		addObserver();
    		for(Enumeration<PetriGraphNode> en = getLOOPOperator().getExecutionPetriGraph().getSortedNodes();en.hasMoreElements();) {
    			PetriGraphNode element = en.nextElement();
                if (!element.isGrouped())
                	addToChilds(element);
    		}
    		for(WKFGroup group:getLOOPOperator().getExecutionPetriGraph().getGroups())
    			addToChilds(group);
    	}
    	super.buildChildrenVector();
    }

    @Override
    public boolean isNameEditable() {
    	return true;
    }

    public LOOPOperator getLOOPOperator() {
    	return (LOOPOperator) getObject();
    }

    @Override
    public void setName(String name) {
    	getLOOPOperator().setName(name);
    }

	@Override
	public void collapse() {
		if (getLOOPOperator().hasExecutionPetriGraph() && isExpanded())
			OpenLoopedPetriGraph.actionType.makeNewAction(getLOOPOperator(), null, getProjectBrowser().getEditor()).doAction();
	}

	@Override
	public void expand() {
		if (getLOOPOperator().hasExecutionPetriGraph() && !isExpanded())
			OpenLoopedPetriGraph.actionType.makeNewAction(getLOOPOperator(), null, getProjectBrowser().getEditor()).doAction();
	}

	@Override
	public boolean isExpanded() {
		return getLOOPOperator().hasExecutionPetriGraph() && getLOOPOperator().getExecutionPetriGraph().getIsVisible();
	}

	@Override
	public boolean isExpansionSynchronizedWithData() {
		if (_browser.getSelectionManager() != null) {
			return (getLOOPOperator().getProcess() == _browser
					.getSelectionManager().getRootFocusedObject());
		}
		return false;
	}

	@Override
	public boolean requiresExpansionFor(BrowserElement next) {
		if (next instanceof PreConditionElement) {
			return ((PreConditionElement)next).getPreCondition().isContainedIn(getLOOPOperator());
		} else if (next instanceof PostConditionElement) {
			FlexoPostCondition edge = ((PostConditionElement) next)
					.getPostCondition();
			if ((edge.getNextNode()!=null && edge.getNextNode().isContainedIn(getLOOPOperator()))
					&& (edge.getStartNode()!=null && edge.getStartNode().isContainedIn(getLOOPOperator()))) {
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

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (_browser != null) {
			if (dataModification instanceof ObjectVisibilityChanged || dataModification instanceof PetriGraphHasBeenClosed || dataModification instanceof PetriGraphHasBeenOpened) {
				_browser.notifyExpansionChanged(this);
			}
		}
		super.update(observable, dataModification);
	}
}
