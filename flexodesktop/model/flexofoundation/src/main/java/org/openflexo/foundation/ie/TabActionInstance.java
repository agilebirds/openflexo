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
package org.openflexo.foundation.ie;

import java.util.Vector;

import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.xml.FlexoProcessBuilder;


/**
 * @author gpolet
 * @deprecated
 */
@Deprecated
public class TabActionInstance extends TabComponentInstance
{
    /**
     * @param component
     * @deprecated
     */
    @Deprecated
	public TabActionInstance(TabComponentDefinition component, ActionNode node)
    {
        super(component, node);
        setActionNode(node);
    }

    public TabActionInstance(FlexoProcessBuilder builder)
    {
        super(builder);
    }

    public TabComponentDefinition getTabComponentDefinition()
    {
        return getComponentDefinition();
    }

    @Override
    public String getFullyQualifiedName()
    {
        return "TAB_OPERATION_COMPONENT_INSTANCE." + getComponentDefinition().getName();
    }

    /**
     * Overrides getClassNameKey
     *
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
    public String getClassNameKey()
    {
        return "tab_action_component_instance";
    }

	@Override
	public String getContextIdentifier() {
		return "action: "+getActionNode().getName();
	}

	@Override
	public Vector<IObject> getWOComponentEmbeddedIEObjects() {
		return getComponentDefinition().getAllEmbeddedIEObjects();
	}
}
