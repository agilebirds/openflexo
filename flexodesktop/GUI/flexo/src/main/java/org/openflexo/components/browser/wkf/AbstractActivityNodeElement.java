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

import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ExpansionSynchronizedElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.foundation.wkf.action.OpenExecutionPetriGraph;
import org.openflexo.foundation.wkf.action.OpenOperationLevel;
import org.openflexo.foundation.wkf.dm.ObjectVisibilityChanged;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenClosed;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenOpened;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class AbstractActivityNodeElement extends BrowserElement implements ExpansionSynchronizedElement {

	private static final Logger logger = Logger.getLogger(AbstractActivityNodeElement.class.getPackage().getName());

	public AbstractActivityNodeElement(AbstractActivityNode node, BrowserElementType elementType, ProjectBrowser browser,
			BrowserElement parent) {
		super(node, elementType, browser, parent);
	}

	@Override
	public TreePath getTreePath() {
		return super.getTreePath();
	}

	private boolean isObserving = false;

	private void addObserver() {
		if (isObserving) {
			return;
		}
		if (getAbstractActivityNode().getOperationPetriGraph() != null) {
			getAbstractActivityNode().getOperationPetriGraph().addObserver(this);
			isObserving = true;
		} else if (getAbstractActivityNode() instanceof SelfExecutableNode
				&& ((SelfExecutableNode) getAbstractActivityNode()).getExecutionPetriGraph() != null) {
			((SelfExecutableNode) getAbstractActivityNode()).getExecutionPetriGraph().addObserver(this);
			isObserving = true;
		}
	}

	@Override
	public void delete() {
		if (getAbstractActivityNode().getOperationPetriGraph() != null) {
			getAbstractActivityNode().getOperationPetriGraph().deleteObserver(this);
			isObserving = false;
		} else if (getAbstractActivityNode() instanceof SelfExecutableNode
				&& ((SelfExecutableNode) getAbstractActivityNode()).getExecutionPetriGraph() != null) {
			((SelfExecutableNode) getAbstractActivityNode()).getExecutionPetriGraph().deleteObserver(this);
			isObserving = false;
		}
		super.delete();
	}

	@Override
	protected void buildChildrenVector() {
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("Building children for activity " + getName());
		}
		// We add operation nodes
		if (getAbstractActivityNode().getOperationPetriGraph() != null) {
			addObserver();
			Enumeration en = getAbstractActivityNode().getOperationPetriGraph().getSortedNodes();
			while (en.hasMoreElements()) {
				addToChilds((FlexoModelObject) en.nextElement());
			}
		} else if (getAbstractActivityNode() instanceof SelfExecutableNode
				&& ((SelfExecutableNode) getAbstractActivityNode()).getExecutionPetriGraph() != null) {
			addObserver();
			Enumeration en = ((SelfExecutableNode) getAbstractActivityNode()).getExecutionPetriGraph().getSortedNodes();
			while (en.hasMoreElements()) {
				addToChilds((FlexoModelObject) en.nextElement());
			}
			for (WKFGroup group : ((SelfExecutableNode) getAbstractActivityNode()).getExecutionPetriGraph().getGroups()) {
				addToChilds(group);
			}
		}
		/*
				// We add operator nodes
				for (Enumeration e = getAbstractActivityNode().getAllOperatorNodes()
						.elements(); e.hasMoreElements();) {
					OperatorNode operatorNode = (OperatorNode) e.nextElement();
					addToChilds(operatorNode);
				}

				// We add event nodes
				for (Enumeration e = getAbstractActivityNode().getAllEventNodes()
						.elements(); e.hasMoreElements();) {
					EventNode eventNode = (EventNode) e.nextElement();
					addToChilds(eventNode);
				}
		*/
		// We add pre conditions
		for (Enumeration e = getAbstractActivityNode().getPreConditions().elements(); e.hasMoreElements();) {
			addToChilds((FlexoPreCondition) e.nextElement());
		}
		// We add post conditions
		for (Enumeration e = getAbstractActivityNode().getOutgoingPostConditions().elements(); e.hasMoreElements();) {
			addToChilds((FlexoPostCondition) e.nextElement());
		}
	}

	@Override
	public String getName() {
		if (getAbstractActivityNode().getName() == null) {
			return getAbstractActivityNode().getDefaultName();
		}
		return getAbstractActivityNode().getName();
	}

	protected AbstractActivityNode getAbstractActivityNode() {
		return (AbstractActivityNode) getObject();
	}

	@Override
	public boolean isExpansionSynchronizedWithData() {
		if (_browser.getSelectionManager() != null) {
			return (getAbstractActivityNode().getProcess() == _browser.getSelectionManager().getRootFocusedObject());
		}
		return false;
	}

	@Override
	public boolean isExpanded() {
		if (getAbstractActivityNode() instanceof SelfExecutableNode) {
			return ((SelfExecutableNode) getAbstractActivityNode()).hasExecutionPetriGraph()
					&& ((SelfExecutableNode) getAbstractActivityNode()).getExecutionPetriGraph().getIsVisible();
		}
		if (getAbstractActivityNode().hasContainedPetriGraph()) {
			return getAbstractActivityNode().getOperationPetriGraph().getIsVisible();
		}
		return false;
	}

	@Override
	public void expand() {
		if (getAbstractActivityNode() instanceof SelfExecutableNode) {
			if (((SelfExecutableNode) getAbstractActivityNode()).hasExecutionPetriGraph() && !isExpanded()) {
				OpenExecutionPetriGraph.actionType.makeNewAction(getAbstractActivityNode(), null, getProjectBrowser().getEditor())
						.doAction();
			}
		} else if (getAbstractActivityNode().hasContainedPetriGraph() && !isExpanded()) {
			OpenOperationLevel.actionType.makeNewAction(getAbstractActivityNode(), null, getProjectBrowser().getEditor()).doAction();
			/*getAbstractActivityNode().getOperationPetriGraph().setIsVisible(
					true);*/
		}
	}

	@Override
	public void collapse() {
		if (getAbstractActivityNode() instanceof SelfExecutableNode) {
			if (((SelfExecutableNode) getAbstractActivityNode()).hasExecutionPetriGraph() && isExpanded()) {
				OpenExecutionPetriGraph.actionType.makeNewAction(getAbstractActivityNode(), null, getProjectBrowser().getEditor())
						.doAction();
			}
		} else if (getAbstractActivityNode().hasContainedPetriGraph() && isExpanded()) {
			OpenOperationLevel.actionType.makeNewAction(getAbstractActivityNode(), null, getProjectBrowser().getEditor()).doAction();
			/*getAbstractActivityNode().getOperationPetriGraph().setIsVisible(
					false);*/
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (_browser != null) {
			if (dataModification instanceof ObjectVisibilityChanged || dataModification instanceof PetriGraphHasBeenClosed
					|| dataModification instanceof PetriGraphHasBeenOpened) {
				_browser.notifyExpansionChanged(this);
			} else if (dataModification.propertyName() != null && dataModification.propertyName().equals("index")) {
				return;
			} else {
				super.update(observable, dataModification);
			}
		}
	}

	@Override
	public boolean isNameEditable() {
		return true;
	}

	@Override
	public void setName(String aName) {
		getAbstractActivityNode().setName(aName);
	}

	@Override
	public boolean requiresExpansionFor(BrowserElement next) {
		if (next instanceof PreConditionElement) {
			return ((PreConditionElement) next).getPreCondition().isContainedIn(getAbstractActivityNode());
		} else if (next instanceof PostConditionElement) {
			FlexoPostCondition edge = ((PostConditionElement) next).getPostCondition();
			if ((edge.getNextNode() != null && edge.getNextNode().isContainedIn(getAbstractActivityNode()))
					&& (edge.getStartNode() != null && edge.getStartNode().isContainedIn(getAbstractActivityNode()))) {
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
