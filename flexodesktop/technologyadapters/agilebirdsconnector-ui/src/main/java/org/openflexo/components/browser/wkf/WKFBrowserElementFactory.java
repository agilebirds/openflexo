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

import java.util.logging.Logger;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.Status;
import org.openflexo.foundation.wkf.StatusList;
import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.ANDOperator;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.ComplexOperator;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.ExclusiveEventBasedOperator;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.foundation.wkf.node.InclusiveOperator;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.OROperator;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SWITCHOperator;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.ws.AbstractMessageDefinition;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.MessageBindings;
import org.openflexo.foundation.wkf.ws.MessageDefinition;
import org.openflexo.foundation.wkf.ws.PortMapRegistery;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.wkf.ws.ServiceMessageDefinition;
import org.openflexo.foundation.wkf.ws.ServiceOperation;

public class WKFBrowserElementFactory implements BrowserElementFactory {
	protected static final Logger logger = Logger.getLogger(WKFBrowserElementFactory.class.getPackage().getName());

	@Override
	public BrowserElement makeNewElement(FlexoObject object, ProjectBrowser browser, BrowserElement parent) {
		if (object instanceof FlexoWorkflow) {
			return new WorkflowElement((FlexoWorkflow) object, browser, parent);
		} else if (object instanceof FlexoProcess) {
			return new ProcessElement((FlexoProcess) object, browser, parent);
		} else if (object instanceof SubProcessNode) {
			return new SubProcessNodeElement((SubProcessNode) object, browser, parent);
		} else if (object instanceof ActivityNode) {
			return new ActivityNodeElement((ActivityNode) object, browser, parent);
		} else if (object instanceof OperationNode) {
			return new OperationNodeElement((OperationNode) object, browser, parent);
		} else if (object instanceof ActionNode) {
			return new ActionNodeElement((ActionNode) object, browser, parent);
		} else if (object instanceof EventNode) {
			return new EventNodeElement((EventNode) object, browser, parent);
		} else if (object instanceof ANDOperator) {
			return new OperatorAndElement((ANDOperator) object, browser, parent);
		} else if (object instanceof InclusiveOperator) {
			return new OperatorInclusiveElement((InclusiveOperator) object, browser, parent);
		} else if (object instanceof ExclusiveEventBasedOperator) {
			return new OperatorExclusiveEventBasedElement((ExclusiveEventBasedOperator) object, browser, parent);
		} else if (object instanceof ComplexOperator) {
			return new OperatorComplexElement((ComplexOperator) object, browser, parent);
		} else if (object instanceof OROperator) {
			return new OperatorOrElement((OROperator) object, browser, parent);
		} else if (object instanceof IFOperator) {
			return new OperatorIfElement((IFOperator) object, browser, parent);
		} else if (object instanceof LOOPOperator) {
			return new OperatorLoopElement((LOOPOperator) object, browser, parent);
		} else if (object instanceof SWITCHOperator) {
			return new OperatorSwitchElement((SWITCHOperator) object, browser, parent);
		} else if (object instanceof FlexoPreCondition) {
			return new PreConditionElement((FlexoPreCondition) object, browser, parent);
		} else if (object instanceof FlexoPostCondition) {
			return new PostConditionElement((FlexoPostCondition) object, browser, parent);
		} else if (object instanceof Role) {
			return new RoleElement((Role) object, browser, parent);
		} else if (object instanceof Status) {
			return new StatusElement((Status) object, browser, parent);
		} else if (object instanceof RoleList) {
			return new RoleListElement((RoleList) object, browser, parent);
		} else if (object instanceof FlexoPort) {
			return new PortElement((FlexoPort) object, browser, parent);
		} else if (object instanceof FlexoPortMap) {
			return new PortMapElement((FlexoPortMap) object, browser, parent);
		} else if (object instanceof PortRegistery) {
			return new PortRegisteryElement((PortRegistery) object, browser, parent);
		} else if (object instanceof PortMapRegistery) {
			return new PortMapRegisteryElement((PortMapRegistery) object, browser, parent);
		} else if (object instanceof StatusList) {
			return new StatusListElement((StatusList) object, browser, parent);
		} else if (object instanceof MessageBindings) {
			return new MessageElement((MessageBindings) object, browser, parent);
		} else if (object instanceof MessageDefinition || object instanceof ServiceMessageDefinition) {
			// just to show that there are TWO types of message definitions
			return new MessageDefinitionElement((AbstractMessageDefinition) object, browser, parent);
		} else if (object instanceof ServiceInterface) {
			return new ServiceInterfaceElement((ServiceInterface) object, browser, parent);
		} else if (object instanceof ServiceOperation) {
			return new ServiceOperationElement((ServiceOperation) object, browser, parent);
		} else if (object instanceof WKFGroup) {
			return new GroupElement((WKFGroup) object, browser, parent);
		} else if (object instanceof ProcessFolder) {
			return new ProcessFolderElement((ProcessFolder) object, browser, parent);
		}
		return null;
	}

}
