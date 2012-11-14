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

import org.openflexo.antar.Conditional;
import org.openflexo.antar.ControlGraph;
import org.openflexo.antar.Procedure;
import org.openflexo.antar.ProcedureCall;
import org.openflexo.antar.Type;
import org.openflexo.antar.expr.BinaryOperatorExpression;
import org.openflexo.antar.expr.BooleanBinaryOperator;
import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.Variable;
import org.openflexo.foundation.bindings.BindingAssignment;
import org.openflexo.foundation.exec.inst.ChangeProcessStatus;
import org.openflexo.foundation.exec.inst.ResetTimeOut;
import org.openflexo.foundation.wkf.Status;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.EventNode.EVENT_TYPE;
import org.openflexo.foundation.wkf.node.EventNode.TriggerType;
import org.openflexo.foundation.wkf.node.FatherNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.NodeType;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SelfExecutableActionNode;
import org.openflexo.foundation.wkf.node.SelfExecutableActivityNode;
import org.openflexo.foundation.wkf.node.SelfExecutableOperationNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

public abstract class NodeActivation<N extends FlexoNode> extends ControlGraphBuilder {

	private static final Logger logger = FlexoLogger.getLogger(NodeActivation.class.getPackage().getName());

	private N node;
	private FlexoPreCondition pre;

	/**
	 * Returns control graph associated to activation of supplied node, without precising the precondition which is the cause of this node
	 * activation
	 * 
	 * @param node
	 * @return the computed control graph
	 * @throws NotSupportedException
	 *             when an element contained in the model is not currently supported by execution model
	 * @throws InvalidModelException
	 *             when the model is not conform (validation should have failed) and thus workflow cannot be computed
	 */
	public static ControlGraph activateNode(FlexoNode node, boolean interprocedural) throws NotSupportedException, InvalidModelException {
		return activateNode(node, null, interprocedural);
	}

	/**
	 * Returns control graph associated to activation of supplied node, asserting that the supplied precondition is the cause of node
	 * activation
	 * 
	 * @param node
	 * @param pre
	 *            precondition which activate the node
	 * @return
	 * @throws NotSupportedException
	 *             when an element contained in the model is not currently supported by execution model
	 * @throws InvalidModelException
	 *             when the model is not conform (validation should have failed) and thus workflow cannot be computed
	 */
	public static ControlGraph activateNode(FlexoNode node, FlexoPreCondition pre, boolean interprocedural) throws NotSupportedException,
			InvalidModelException {
		if (pre != null) {
			if (pre.getAttachedNode() == null) {
				logger.severe("Activation of a node with a precondition not attached to it !");
			}
		}
		ControlGraphBuilder cgBuilder = getActivationNodeBuilder(node, pre);

		if (cgBuilder != null) {

			if (interprocedural) {
				Procedure procedure = cgBuilder.makeProcedure();
				ProcedureCall returned = new ProcedureCall(procedure);
				if (node.getPreConditions().size() > 1) {
					if (pre == null) {
						throw new InvalidModelException("Node activation called for " + node + " with a null precondition");
					}
					returned.addArgument(new Constant.IntegerConstant(pre.getFlexoID()));
					returned.appendHeaderComment("Node " + node.getName() + " is activating from precondition " + pre.getName(), true);
				} else {
					returned.appendHeaderComment("Node " + node.getName() + " is activating", true);
				}
				return returned;
			} else {
				ControlGraph returned = cgBuilder.makeControlGraph(interprocedural);
				returned.appendHeaderComment("Node " + node.getName() + " is activating", true);
				return returned;
			}
		} else {
			throw new NotSupportedException("Dont know what to do with a " + node);
		}
	}

	public static ControlGraphBuilder getActivationNodeBuilder(FlexoNode node, FlexoPreCondition pre) throws NotSupportedException,
			InvalidModelException {
		if (node instanceof AbstractActivityNode) {
			if (node instanceof SubProcessNode) {
				return new SubProcessActivityNodeActivation((SubProcessNode) node, pre);
			} else if (node instanceof ActivityNode) {
				if (node instanceof SelfExecutableActivityNode) {
					return new SelfExecutableActivityNodeActivation((SelfExecutableActivityNode) node, pre);
				} else {
					if (node.getNodeType() == NodeType.BEGIN) {
						return new BeginActivityNodeActivation((ActivityNode) node, pre);
					} else if (node.getNodeType() == NodeType.END) {
						return new EndActivityNodeActivation((ActivityNode) node, pre);
					} else { // Normal node
						return new NormalActivityNodeActivation((ActivityNode) node, pre);
					}
				}
			}
		} else if (node instanceof OperationNode) {
			if (node instanceof SelfExecutableOperationNode) {
				return new SelfExecutableOperationNodeActivation((SelfExecutableOperationNode) node, pre);
			} else {
				if (node.getNodeType() == NodeType.BEGIN) {
					return new BeginOperationNodeActivation((OperationNode) node, pre);
				} else if (node.getNodeType() == NodeType.END) {
					return new EndOperationNodeActivation((OperationNode) node, pre);
				} else { // Normal node
					return new NormalOperationNodeActivation((OperationNode) node, pre);
				}
			}
		} else if (node instanceof ActionNode) {
			if (node instanceof SelfExecutableActionNode) {
				return new SelfExecutableActionNodeActivation((SelfExecutableActionNode) node, pre);
			} else {
				if (node.getNodeType() == NodeType.BEGIN) {
					return new BeginActionNodeActivation((ActionNode) node, pre);
				} else if (node.getNodeType() == NodeType.END) {
					return new EndActionNodeActivation((ActionNode) node, pre);
				} else { // Normal node
					return new NormalActionNodeActivation((ActionNode) node, pre);
				}
			}
		}

		throw new NotSupportedException("Dont know what to do with a " + node);
	}

	protected NodeActivation(N node, FlexoPreCondition pre) {
		super();
		this.node = node;
		this.pre = pre;
		// No need to make it ambigous, consider the only one precondition
		if (pre == null && getNode().getPreConditions().size() == 1) {
			this.pre = getNode().getPreConditions().firstElement();
		}
	}

	protected NodeActivation(N node) {
		this(node, null);
	}

	@Override
	protected String getProcedureComment() {
		StringBuffer returned = new StringBuffer();
		returned.append(FlexoLocalization.localizedForKeyWithParams(
				"this_method_represents_code_to_be_executed_when_node_($0)_is_activated", getNode().getName()));
		if (getNode().getPreConditions().size() > 1) {
			returned.append(StringUtils.LINE_SEPARATOR);
			returned.append(StringUtils.LINE_SEPARATOR);
			returned.append("@param " + getPreconditionVariable().getName() + " ");
			returned.append(FlexoLocalization.localizedForKey("identifier_of_precondition_which_activates_this_node"));
		}
		return returned.toString();
	}

	/**
	 * Overrides parent's method by providing precondition identifier as argument
	 */
	@Override
	protected Procedure makeProcedure() throws InvalidModelException, NotSupportedException {
		if (getNode().getPreConditions().size() > 1) {
			return new Procedure(getProcedureName(), makeControlGraph(true), getProcedureComment(), new Procedure.ProcedureParameter(
					getPreconditionVariable(), new Type("int")));
		} else {
			return super.makeProcedure();
		}
	}

	private Variable preconditionVariable = null;

	private Variable getPreconditionVariable() {
		if (preconditionVariable == null) {
			preconditionVariable = new Variable("preconditionId");
		}
		return preconditionVariable;
	}

	@Override
	protected final ControlGraph makeControlGraph(boolean interprocedural) throws InvalidModelException, NotSupportedException {
		return makeSequentialControlGraph(makeControlGraphCommonPrelude(interprocedural), makeSpecificControlGraph(interprocedural),
				makeControlGraphCommonPostlude(interprocedural));
	}

	protected abstract ControlGraph makeSpecificControlGraph(boolean interprocedural) throws InvalidModelException, NotSupportedException;

	protected final ControlGraph makeControlGraphCommonPrelude(boolean interprocedural) {
		ControlGraph SET_PROCESS_STATUS = null;
		ControlGraph EXECUTE_ACTIVATION_PRIMITIVE = null;
		ControlGraph EXECUTE_ACTIVATION_ASSIGNMENTS = null;

		if (getNode().getNewStatus() != null) {
			SET_PROCESS_STATUS = makeChangeProcessStatus(getNode().getNewStatus());
		}

		if (getNode().getActivationPrimitive() != null) {
			EXECUTE_ACTIVATION_PRIMITIVE = makeControlGraphForExecutionPrimitive(getNode().getActivationPrimitive());
			EXECUTE_ACTIVATION_PRIMITIVE.setHeaderComment("Call activation primitive for node " + getNode().getName());
		}

		if (getNode().getActivationAssignments().size() > 0) {
			Vector<ControlGraph> allAssignments = new Vector<ControlGraph>();
			for (BindingAssignment assignment : getNode().getActivationAssignments()) {
				allAssignments.add(makeControlGraphForAssignment(assignment));
			}
			EXECUTE_ACTIVATION_ASSIGNMENTS = makeSequentialControlGraph(allAssignments);
			EXECUTE_ACTIVATION_ASSIGNMENTS.setHeaderComment("Perform assignments declared for activation of node " + getNode().getName());
		}

		return makeSequentialControlGraph(SET_PROCESS_STATUS, EXECUTE_ACTIVATION_PRIMITIVE, EXECUTE_ACTIVATION_ASSIGNMENTS);
	}

	protected ControlGraph makeControlGraphCommonPostlude(boolean interprocedural) throws NotSupportedException, InvalidModelException {
		ControlGraph ACTIVATE_ABOVE_BEGIN_NODE = null;

		if (getNode().getPreConditions().size() > 1 && interprocedural) {
			// Many preconditions, embedded all preconditions in a conditional structure
			Conditional evaluatePrecondition = null;
			for (FlexoPreCondition currentPre : getNode().getPreConditions()) {
				Expression condition = new BinaryOperatorExpression(BooleanBinaryOperator.EQUALS, getPreconditionVariable(),
						new Constant.IntegerConstant(currentPre.getFlexoID()));
				FlexoNode beginNode = currentPre.getAttachedBeginNode();
				if (beginNode == null) {
					throw new InvalidModelException("Node " + getNode() + " defines a pre-condition without attached BEGIN node");
				}
				if (beginNode == getNode()) {
					throw new InvalidModelException("Inconsistent data !!!");
				}
				Conditional conditional = new Conditional(condition,
						NodeActivation.activateNode(beginNode,/*currentPre,*/interprocedural), "Originally activated precondition is "
								+ beginNode.getName());
				if (evaluatePrecondition != null) {
					evaluatePrecondition.setElseStatement(conditional);
				} else {
					ACTIVATE_ABOVE_BEGIN_NODE = conditional;
				}
				evaluatePrecondition = conditional;
			}
		}

		else {
			if (getNode() instanceof FatherNode && pre != null) {
				FlexoNode beginNode = pre.getAttachedBeginNode();
				if (beginNode == null) {
					throw new InvalidModelException("Node " + getNode() + " defines a pre-condition without attached BEGIN node");
				}
				if (beginNode == getNode()) {
					throw new InvalidModelException("Inconsistent data !!!");
				}
				ACTIVATE_ABOVE_BEGIN_NODE = activateNode(beginNode, interprocedural);
			}
		}

		return makeSequentialControlGraph(ACTIVATE_ABOVE_BEGIN_NODE);
	}

	@Override
	protected String getProcedureName() {
		return "activate_" + ToolBox.capitalize(ToolBox.getJavaName(getNode().getName())) + "_" + getNode().getFlexoID();
	}

	public N getNode() {
		return node;
	}

	public FlexoPreCondition getPre() {
		return pre;
	}

	protected static final ChangeProcessStatus makeChangeProcessStatus(Status newStatus) {
		return new ChangeProcessStatus(newStatus);
	}

	protected final ControlGraph makeResetTimeOut() {
		Vector<ControlGraph> resetTimeOutStatements = new Vector<ControlGraph>();

		for (EventNode event : getNode().getParentPetriGraph().getAllEventNodes()) {
			if (TriggerType.TIMER.equals(event.getTrigger()) && EVENT_TYPE.Intermediate == event.getEventType()) {
				resetTimeOutStatements.add(new ResetTimeOut(event));
			}
		}

		return makeSequentialControlGraph(resetTimeOutStatements);
	}

}
