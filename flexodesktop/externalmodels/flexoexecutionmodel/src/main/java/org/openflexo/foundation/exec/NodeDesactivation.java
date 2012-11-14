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
package org.openflexo.foundation.exec;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.ControlGraph;
import org.openflexo.antar.Procedure;
import org.openflexo.antar.ProcedureCall;
import org.openflexo.foundation.bindings.BindingAssignment;
import org.openflexo.foundation.wkf.edge.ExternalMessageInEdge;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.NodeType;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SelfExecutableActionNode;
import org.openflexo.foundation.wkf.node.SelfExecutableActivityNode;
import org.openflexo.foundation.wkf.node.SelfExecutableOperationNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.ToolBox;

public abstract class NodeDesactivation<N extends FlexoNode> extends ControlGraphBuilder {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(NodeDesactivation.class.getPackage().getName());

	private N node;

	/**
	 * Returns control graph associated to desactivation of supplied node
	 * 
	 * @param node
	 * @return the computed control graph
	 * @throws NotSupportedException
	 *             when an element contained in the model is not currently supported by execution model
	 * @throws InvalidModelException
	 *             when the model is not conform (validation should have failed) and thus workflow cannot be computed
	 */
	public static ControlGraph desactivateNode(FlexoNode node, boolean interprocedural) throws NotSupportedException, InvalidModelException {
		ControlGraphBuilder cgBuilder = getDesactivationNodeBuilder(node);

		if (cgBuilder != null) {

			if (interprocedural) {
				Procedure procedure = cgBuilder.makeProcedure();
				ProcedureCall returned = new ProcedureCall(procedure);
				returned.appendHeaderComment("Node " + node.getName() + " is desactivating", true);
				return returned;
			} else {
				ControlGraph returned = cgBuilder.makeControlGraph(interprocedural);
				returned.appendHeaderComment("Node " + node.getName() + " is desactivating", true);
				return returned;
			}
		} else {
			throw new NotSupportedException("Dont know what to do with a " + node);
		}
	}

	public static ControlGraphBuilder getDesactivationNodeBuilder(FlexoNode node) throws NotSupportedException, InvalidModelException {
		if (node instanceof AbstractActivityNode) {
			if (node instanceof SubProcessNode) {
				return new SubProcessActivityNodeDesactivation((SubProcessNode) node);
			} else if (node instanceof ActivityNode) {
				if (node instanceof SelfExecutableActivityNode) {
					return new SelfExecutableActivityNodeDesactivation((SelfExecutableActivityNode) node);
				} else {
					if (node.getNodeType() == NodeType.BEGIN) {
						return new BeginActivityNodeDesactivation((ActivityNode) node);
					} else if (node.getNodeType() == NodeType.END) {
						return new EndActivityNodeDesactivation((ActivityNode) node);
					} else { // Normal node
						return new NormalActivityNodeDesactivation((ActivityNode) node);
					}
				}
			}
		} else if (node instanceof OperationNode) {
			if (node instanceof SelfExecutableOperationNode) {
				return new SelfExecutableOperationNodeDesactivation((SelfExecutableOperationNode) node);
			} else {
				if (node.getNodeType() == NodeType.BEGIN) {
					return new BeginOperationNodeDesactivation((OperationNode) node);
				} else if (node.getNodeType() == NodeType.END) {
					return new EndOperationNodeDesactivation((OperationNode) node);
				} else { // Normal node
					return new NormalOperationNodeDesactivation((OperationNode) node);
				}
			}
		} else if (node instanceof ActionNode) {
			if (node instanceof SelfExecutableActionNode) {
				return new SelfExecutableActionNodeDesactivation((SelfExecutableActionNode) node);
			} else {
				if (node.getNodeType() == NodeType.BEGIN) {
					return new BeginActionNodeDesactivation((ActionNode) node);
				} else if (node.getNodeType() == NodeType.END) {
					return new EndActionNodeDesactivation((ActionNode) node);
				} else { // Normal node
					return new NormalActionNodeDesactivation((ActionNode) node);
				}
			}
		}

		throw new NotSupportedException("Dont know what to do with a " + node);
	}

	public NodeDesactivation(N node) {
		super();
		this.node = node;
	}

	@Override
	protected String getProcedureComment() {
		return FlexoLocalization.localizedForKeyWithParams("this_method_represents_code_to_be_executed_when_node_($0)_is_desactivated",
				getNode().getName());
	}

	@Override
	public final ControlGraph makeControlGraph(boolean interprocedural) throws InvalidModelException, NotSupportedException {
		ControlGraph returned = makeSequentialControlGraph(makeControlGraphCommonPrelude(interprocedural),
				makeSpecificControlGraph(interprocedural), makeControlGraphCommonPostlude(interprocedural));

		return returned;
	}

	public abstract ControlGraph makeSpecificControlGraph(boolean interprocedural) throws InvalidModelException, NotSupportedException;

	protected final ControlGraph makeControlGraphCommonPrelude(boolean interprocedural) {
		ControlGraph EXECUTE_DESACTIVATION_PRIMITIVE = null;
		ControlGraph EXECUTE_DESACTIVATION_ASSIGNMENTS = null;

		if (getNode().getDesactivationPrimitive() != null) {
			EXECUTE_DESACTIVATION_PRIMITIVE = makeControlGraphForExecutionPrimitive(getNode().getDesactivationPrimitive());
			EXECUTE_DESACTIVATION_PRIMITIVE.setHeaderComment("Call desactivation primitive for node " + getNode().getName());
		}

		if (getNode().getDesactivationAssignments().size() > 0) {
			Vector<ControlGraph> allAssignments = new Vector<ControlGraph>();
			for (BindingAssignment assignment : getNode().getDesactivationAssignments()) {
				allAssignments.add(makeControlGraphForAssignment(assignment));
			}
			EXECUTE_DESACTIVATION_ASSIGNMENTS = makeSequentialControlGraph(allAssignments);
			EXECUTE_DESACTIVATION_ASSIGNMENTS.setHeaderComment("Perform assignments declared for desactivation of node "
					+ getNode().getName());
		}

		return makeSequentialControlGraph(EXECUTE_DESACTIVATION_PRIMITIVE, EXECUTE_DESACTIVATION_ASSIGNMENTS);
	}

	protected ControlGraph makeControlGraphCommonPostlude(boolean interprocedural) throws InvalidModelException, NotSupportedException {
		Vector<ControlGraph> sendTokensStatements = new Vector<ControlGraph>();

		for (FlexoPostCondition edge : getNode().getOutgoingPostConditions()) {
			sendTokensStatements.add(SendToken.sendToken(edge, interprocedural));
		}

		ControlGraph SEND_TOKENS = makeFlowControlGraph(sendTokensStatements);

		Vector<ControlGraph> sendMessagesStatements = new Vector<ControlGraph>();

		for (FlexoPostCondition edge : getNode().getOutgoingPostConditions()) {
			if (edge instanceof ExternalMessageInEdge) {
				sendMessagesStatements.add(SendMessage.sendMessage((ExternalMessageInEdge) edge, interprocedural));
			}
		}

		ControlGraph SEND_MESSAGES = makeFlowControlGraph(sendMessagesStatements);

		ControlGraph returned = makeSequentialControlGraph(SEND_MESSAGES, SEND_TOKENS);

		return returned;
	}

	@Override
	protected String getProcedureName() {
		return "deactivate_" + ToolBox.capitalize(ToolBox.getJavaName(getNode().getName())) + "_" + getNode().getFlexoID();
	}

	public N getNode() {
		return node;
	}

	public void setNode(N node) {
		this.node = node;
	}

}
