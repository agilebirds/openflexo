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

import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ExpansionSynchronizedElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.foundation.wkf.action.OpenGroup;
import org.openflexo.foundation.wkf.dm.ObjectVisibilityChanged;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.AbstractNode;


/**
 * Browser element representing an Action Node
 *
 * @author sguerin
 *
 */
public class GroupElement extends BrowserElement implements ExpansionSynchronizedElement
{

    public GroupElement(WKFGroup group, ProjectBrowser browser, BrowserElement parent)
    {
        super(group, BrowserElementType.GROUP, browser, parent);
    }

	@Override
	public TreePath getTreePath() {
		return super.getTreePath();
	}

    @Override
	public String getName()
    {
        return getGroup().getGroupName();
    }

    @Override
	protected void buildChildrenVector()
    {
    	for (AbstractNode node: getGroup().getNodes())
    		addToChilds(node);
    }

    protected WKFGroup getGroup()
    {
        return (WKFGroup) getObject();
    }

    @Override
	public boolean isNameEditable()
    {
        return true;
    }

    @Override
	public void setName(String aName)
    {
        getGroup().setGroupName(aName);
    }

	@Override
	public void collapse() {
		if (isExpanded())
			OpenGroup.actionType.makeNewAction(getGroup(), null, getProjectBrowser().getEditor()).doAction();
	}

	@Override
	public void expand() {
		if (!isExpanded())
			OpenGroup.actionType.makeNewAction(getGroup(), null, getProjectBrowser().getEditor()).doAction();
	}

	@Override
	public boolean isExpanded() {
		return getGroup().isExpanded();
	}

	@Override
	public boolean isExpansionSynchronizedWithData() {
		if (_browser.getSelectionManager() != null) {
            return (getGroup().getProcess() == _browser.getSelectionManager().getRootFocusedObject());
        }
        return false;
	}

	@Override
	public boolean requiresExpansionFor(BrowserElement next) {
		if (next instanceof PreConditionElement) {
			return ((PreConditionElement)next).getPreCondition().isContainedIn(getGroup());
		} else if (next instanceof PostConditionElement) {
			FlexoPostCondition edge = ((PostConditionElement) next)
					.getPostCondition();
			if ((edge.getNextNode().isContainedIn(getGroup()))
					&& (edge.getStartNode().isContainedIn(getGroup()))) {
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
			if (dataModification instanceof ObjectVisibilityChanged) {
				_browser.notifyExpansionChanged(this);
			}
		}
		super.update(observable, dataModification);
	}

}
