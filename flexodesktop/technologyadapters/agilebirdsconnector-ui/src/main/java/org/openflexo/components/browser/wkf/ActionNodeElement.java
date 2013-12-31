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

import javax.swing.Icon;
import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ExpansionSynchronizedElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.action.OpenExecutionPetriGraph;
import org.openflexo.foundation.wkf.dm.ObjectVisibilityChanged;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenClosed;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenOpened;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.icon.WKFIconLibrary;

/**
 * Browser element representing an Action Node
 * 
 * @author sguerin
 * 
 */
public class ActionNodeElement extends BrowserElement implements ExpansionSynchronizedElement {

	public ActionNodeElement(ActionNode node, ProjectBrowser browser, BrowserElement parent) {
		super(node, BrowserElementType.ACTION_NODE, browser, parent);
	}

	@Override
	public TreePath getTreePath() {
		return super.getTreePath();
	}

	@Override
	public String getName() {
		if (getActionNode().getName() == null) {
			return getActionNode().getDefaultName();
		}
		return getActionNode().getName();
	}

	private boolean isObserving = false;

	private void addObserver() {
		if (isObserving) {
			return;
		}
		if (getActionNode() instanceof SelfExecutableNode && ((SelfExecutableNode) getActionNode()).getExecutionPetriGraph() != null) {
			((SelfExecutableNode) getActionNode()).getExecutionPetriGraph().addObserver(this);
			isObserving = true;
		}
	}

	@Override
	public void delete() {
		if (getActionNode() instanceof SelfExecutableNode && ((SelfExecutableNode) getActionNode()).getExecutionPetriGraph() != null) {
			((SelfExecutableNode) getActionNode()).getExecutionPetriGraph().deleteObserver(this);
			isObserving = false;
		}
		super.delete();
	}

	@Override
	protected void buildChildrenVector() {
		if (getActionNode() instanceof SelfExecutableNode && ((SelfExecutableNode) getActionNode()).getExecutionPetriGraph() != null) {
			addObserver();
			Enumeration en = ((SelfExecutableNode) getActionNode()).getExecutionPetriGraph().getSortedNodes();
			while (en.hasMoreElements()) {
				addToChilds((FlexoModelObject) en.nextElement());
			}
		}

		// We add pre conditions
		for (Enumeration e = getActionNode().getPreConditions().elements(); e.hasMoreElements();) {
			addToChilds((FlexoPreCondition) e.nextElement());
		}

		// We add post conditions
		for (Enumeration e = getActionNode().getOutgoingPostConditions().elements(); e.hasMoreElements();) {
			addToChilds((FlexoPostCondition) e.nextElement());
		}

	}

	protected ActionNode getActionNode() {
		return (ActionNode) getObject();
	}

	@Override
	public Icon getIcon() {
		if (getActionNode() instanceof SelfExecutableNode) {
			return decorateIcon(WKFIconLibrary.SELF_EXECUTABLE_ICON);
		} else if (getActionNode().isBeginNode()) {
			return WKFIconLibrary.BEGIN_ACTION_ICON;
		} else if (getActionNode().isEndNode()) {
			return WKFIconLibrary.END_ACTION_ICON;
		} else {
			return super.getIcon();
		}
	}

	@Override
	public boolean isNameEditable() {
		return true;
	}

	@Override
	public void setName(String aName) {
		getActionNode().setName(aName);
	}

	/**
	 * Overrides update
	 * 
	 * @see org.openflexo.components.browser.BrowserElement#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification.propertyName() != null && dataModification.propertyName().equals("index")) {
			return;
		}
		if (_browser != null) {
			if (dataModification instanceof ObjectVisibilityChanged || dataModification instanceof PetriGraphHasBeenClosed
					|| dataModification instanceof PetriGraphHasBeenOpened) {
				_browser.notifyExpansionChanged(this);
			}
		}
		super.update(observable, dataModification);
	}

	@Override
	public void collapse() {
		if (getActionNode() instanceof SelfExecutableNode) {
			if (((SelfExecutableNode) getActionNode()).hasExecutionPetriGraph() && isExpanded()) {
				OpenExecutionPetriGraph.actionType.makeNewAction(getActionNode(), null, getProjectBrowser().getEditor()).doAction();
			}
		}
	}

	@Override
	public void expand() {
		if (getActionNode() instanceof SelfExecutableNode) {
			if (((SelfExecutableNode) getActionNode()).hasExecutionPetriGraph() && !isExpanded()) {
				OpenExecutionPetriGraph.actionType.makeNewAction(getActionNode(), null, getProjectBrowser().getEditor()).doAction();
			}
		}
	}

	@Override
	public boolean isExpanded() {
		if (getActionNode() instanceof SelfExecutableNode) {
			return ((SelfExecutableNode) getActionNode()).hasExecutionPetriGraph()
					&& ((SelfExecutableNode) getActionNode()).getExecutionPetriGraph().getIsVisible();
		}
		return false;
	}

	@Override
	public boolean isExpansionSynchronizedWithData() {
		if (_browser.getSelectionManager() != null) {
			return getActionNode().getProcess() == _browser.getSelectionManager().getRootFocusedObject();
		}
		return false;
	}

	@Override
	public boolean requiresExpansionFor(BrowserElement next) {
		if (next instanceof PreConditionElement) {
			return ((PreConditionElement) next).getPreCondition().isContainedIn(getActionNode());
		} else if (next instanceof PostConditionElement) {
			FlexoPostCondition<?, ?> edge = ((PostConditionElement) next).getPostCondition();
			if (edge.getNextNode() != null && edge.getStartNode() != null && edge.getNextNode().isContainedIn(getActionNode())
					&& edge.getStartNode().isContainedIn(getActionNode())) {
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
