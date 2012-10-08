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
import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.foundation.xml.FlexoNavigationMenuBuilder;
import org.openflexo.foundation.xml.FlexoProcessBuilder;

/**
 * @author bmangez
 * 
 *         <B>Class Description</B>
 */
public class TabComponentInstance extends PartialComponentInstance {

	private OperationNode operationNode;

	private ActionNode actionNode;

	private FlexoItemMenu itemMenu;

	public TabComponentInstance(TabComponentDefinition component, IETabWidget widget, IEWOComponent container) {
		super(component, container);
		setReusableWidget(widget);
	}

	/**
	 * @param component
	 */
	public TabComponentInstance(TabComponentDefinition component, IETabWidget widget) {
		super(component, widget.getWOComponent());
		setReusableWidget(widget);
	}

	/**
	 * @param component
	 */
	public TabComponentInstance(TabComponentDefinition component, FlexoItemMenu menu) {
		super(component, menu.getNavigationMenu());
		setItemMenu(menu);
	}

	/**
	 * @param component
	 */
	public TabComponentInstance(TabComponentDefinition component, OperationNode operation) {
		super(component, operation.getProcess());
		setOperationNode(operation);
	}

	/**
	 * @param component
	 */
	public TabComponentInstance(TabComponentDefinition component, ActionNode action) {
		super(component, action.getProcess());
		setActionNode(action);
	}

	public TabComponentInstance(FlexoComponentBuilder builder) {
		super(builder);
	}

	public TabComponentInstance(FlexoProcessBuilder builder) {
		super(builder);
	}

	@Deprecated
	public TabComponentInstance(FlexoNavigationMenuBuilder builder) {
		super(builder);
	}

	@Override
	public TabComponentDefinition getComponentDefinition() {
		return (TabComponentDefinition) super.getComponentDefinition();
	}

	public OperationNode getOperationNode() {
		return operationNode;
	}

	public void setOperationNode(OperationNode operationNode) {
		this.operationNode = operationNode;
		setOwner(operationNode);
	}

	public ActionNode getActionNode() {
		return actionNode;
	}

	public void setActionNode(ActionNode actionNode) {
		this.actionNode = actionNode;
		setOwner(actionNode);
	}

	public FlexoItemMenu getItemMenu() {
		return itemMenu;
	}

	public void setItemMenu(FlexoItemMenu itemMenu) {
		this.itemMenu = itemMenu;
		setOwner(itemMenu);
	}

	@Override
	public String getFullyQualifiedName() {
		return "TAB_COMPONENT_INSTANCE." + getComponentDefinition().getName();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "tab_component_instance";
	}

	public String getContextIdentifier() {
		return getOwner().getFullyQualifiedName() + "_" + getOwner().getFlexoID();
	}

	@Override
	public Vector<IObject> getWOComponentEmbeddedIEObjects() {
		return getComponentDefinition().getAllEmbeddedIEObjects();
	}
}
