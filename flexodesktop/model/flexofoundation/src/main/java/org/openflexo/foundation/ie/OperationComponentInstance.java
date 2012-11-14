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

import java.util.Enumeration;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.dm.BindingAdded;
import org.openflexo.foundation.ie.dm.BindingRemoved;
import org.openflexo.foundation.ie.menu.FlexoNavigationMenu;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.foundation.xml.FlexoNavigationMenuBuilder;
import org.openflexo.foundation.xml.FlexoProcessBuilder;

/**
 * Represents an instance of an OperationComponent in a given context
 * 
 * @author bmangez
 */
public class OperationComponentInstance extends ComponentInstance {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(OperationComponentInstance.class.getPackage().getName());
	private OperationNode _operationNode;

	public OperationComponentInstance(FlexoProcessBuilder builder) {
		super(builder);
	}

	public OperationComponentInstance(FlexoComponentBuilder builder) {
		super(builder);
	}

	public OperationComponentInstance(FlexoNavigationMenuBuilder builder) {
		super(builder);
	}

	/**
	 * 
	 * @param component
	 * @param menu
	 * @deprecated Menu items should never reference directly a component
	 */
	@Deprecated
	public OperationComponentInstance(OperationComponentDefinition component, FlexoNavigationMenu menu) {
		super(component, menu);
	}

	public OperationComponentInstance(OperationComponentDefinition component, OperationNode node) {
		super(component, node.getProcess());
		setOperationNode(node);
	}

	public OperationComponentInstance(OperationComponentDefinition component, IEWOComponent container) {
		super(component, container);
	}

	public OperationComponentDefinition getOperationComponentDefinition() {
		return (OperationComponentDefinition) getComponentDefinition();
	}

	public void setOperationNode(OperationNode node) {
		_operationNode = node;
		setOwner(node);
	}

	public OperationNode getOperationNode() {
		return _operationNode;
	}

	public FlexoProcess getRelatedProcess() {
		return getOperationNode().getRelatedProcess();
	}

	public boolean isUnderSubProcessNode() {
		return getOperationNode().isUnderSubProcessNode();
	}

	@Override
	public void update(FlexoObservable o, DataModification dataModification) {
		super.update(o, dataModification);
		if (o == getComponentDefinition() && (dataModification instanceof BindingAdded || dataModification instanceof BindingRemoved)) {
			if (_operationNode != null) {
				_operationNode.notifyBindingsChanged();
			}
		}
	}

	/*
	 * public void update(FlexoObservable o, DataModification arg) { if(arg
	 * instanceof ComponentNameChanged){ _componentName =
	 * (String)((ComponentNameChanged)arg).newValue(); } }
	 */

	@Override
	public String getFullyQualifiedName() {
		return "OPERATION_COMPONENT_INSTANCE." + getComponentDefinition().getName();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "operation_component_instance";
	}

	public TreeMap<IEHyperlinkWidget, ActionNode> getAllActionButtonPairs() {
		TreeMap<IEHyperlinkWidget, ActionNode> reply = new TreeMap<IEHyperlinkWidget, ActionNode>();
		Enumeration<IEHyperlinkWidget> en = getWOComponent().getAllAbstractButtonWidgetInterface().elements();
		while (en.hasMoreElements()) {
			IEHyperlinkWidget button = en.nextElement();
			ActionNode action = getOperationNode().getActionNodeForButton(button);
			if (action != null) {
				reply.put(button, action);
			}
		}
		return reply;
	}

	public String getTabKey() {
		if (getWOComponent().getFirstTabContainerTitle() == null) {
			return null;
		}
		return getOperationNode().getSelectedTabKey();
	}

	public String getContextIdentifier() {
		return getOperationNode().getName();
	}

	public ActionNode getActionNodeForButton(IEHyperlinkWidget button) {
		Enumeration<ActionNode> en = getOperationNode().getAllActionNodes().elements();
		ActionNode reply = null;
		while (en.hasMoreElements()) {
			reply = en.nextElement();
			if (button == reply.getAssociatedButtonWidget()) {
				return reply;
			}
		}
		return null;
	}

	@Override
	public Vector<IObject> getWOComponentEmbeddedIEObjects() {
		return getComponentDefinition().getAllEmbeddedIEObjects();
	}
}
