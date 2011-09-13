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

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.OperatorNode;


/**
 * Browser element representing an Operation Node
 *
 * @author sguerin
 *
 */
public abstract class OperatorNodeElement extends BrowserElement
{

    public OperatorNodeElement(OperatorNode node, BrowserElementType elementType, ProjectBrowser browser, BrowserElement parent)
    {
        super(node, elementType, browser, parent);
    }

    @Override
	protected void buildChildrenVector()
    {
        // We add post conditions
        for (Enumeration e = getOperatorNode().getOutgoingPostConditions().elements(); e.hasMoreElements();) {
            addToChilds((FlexoPostCondition) e.nextElement());
        }
    }

    @Override
	public String getName()
    {
        if (getOperatorNode().getName() == null) {
            return getOperatorNode().getDefaultName();
        }
        return getOperatorNode().getName();
    }

    protected OperatorNode getOperatorNode()
    {
        return (OperatorNode) getObject();
    }

    @Override
	public boolean isNameEditable()
    {
        return true;
    }

    @Override
	public void setName(String aName)
    {
        getOperatorNode().setName(aName);
    }

}
