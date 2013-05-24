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

import org.openflexo.antar.Assignment;
import org.openflexo.antar.Conditional;
import org.openflexo.antar.ControlGraph;
import org.openflexo.antar.Declaration;
import org.openflexo.antar.Procedure;
import org.openflexo.antar.ProcedureCall;
import org.openflexo.antar.Type;
import org.openflexo.antar.expr.ArithmeticBinaryOperator;
import org.openflexo.antar.expr.BinaryOperatorExpression;
import org.openflexo.antar.expr.BooleanBinaryOperator;
import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.Variable;
import org.openflexo.foundation.exec.expr.StoredTokensOnPrecondition;
import org.openflexo.foundation.exec.inst.StoreTokenOnPrecondition;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.edge.TokenEdge;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.SelfExecutableActionNode;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

public class SendTokenToPrecondition extends ControlGraphBuilder {

	private FlexoPreCondition precondition;
	private FlexoPostCondition<?, ?> edge;

	private SendTokenToPrecondition(FlexoPreCondition precondition, FlexoPostCondition<?, ?> edge) {
		super();
		this.edge = edge;
		this.precondition = precondition;
		if (edge == null && precondition.getIncomingPostConditions().size() == 1) {
			// No need to make it ambigous, consider the only one incoming edge
			this.edge = precondition.getIncomingPostConditions().firstElement();
		}
	}

	public static ControlGraphBuilder getSendTokenToPreconditionBuilder(FlexoPreCondition precondition, FlexoPostCondition<?, ?> edge)
			throws NotSupportedException, InvalidModelException {
		return new SendTokenToPrecondition(precondition, edge);
	}

	/**
	 * Returns control graph associated to token sending to precondition
	 * 
	 * @param node
	 * @return the computed control graph
	 * @throws NotSupportedException
	 *             when an element contained in the model is not currently supported by execution model
	 * @throws InvalidModelException
	 *             when the model is not conform (validation should have failed) and thus workflow cannot be computed
	 */
	public static ControlGraph sendTokenToPrecondition(FlexoPreCondition precondition, FlexoPostCondition<?, ?> edge,
			boolean interprocedural) throws NotSupportedException, InvalidModelException {
		ControlGraphBuilder cgBuilder = getSendTokenToPreconditionBuilder(precondition, edge);

		AbstractNode edgeOrigin = null;

		if (edge != null) {
			edgeOrigin = edge.getStartNode();
		}

		if (interprocedural) {
			Procedure procedure = cgBuilder.makeProcedure();
			ProcedureCall returned = new ProcedureCall(procedure);
			if (precondition.getIncomingPostConditions().size() > 1) {
				if (edge == null) {
					throw new InvalidModelException("Precondition receives a token from a null origin edge");
				}
				returned.addArgument(new Constant.IntegerConstant(edge.getFlexoID()));
			}
			returned.appendHeaderComment("Precondition " + precondition.getName() + " receive a new token from node "
					+ (edgeOrigin != null ? edgeOrigin.getName() : "null"), true);
			return returned;
		} else {
			ControlGraph returned = cgBuilder.makeControlGraph(interprocedural);
			returned.appendHeaderComment("Precondition " + precondition.getName() + " receive a new token from node "
					+ (edgeOrigin != null ? edgeOrigin.getName() : "null"), true);
			return returned;
		}
	}

	@Override
	protected String getProcedureComment() {
		StringBuffer returned = new StringBuffer();
		if (getPrecondition().getAttachedBeginNode() != null) {
			returned.append(FlexoLocalization.localizedForKeyWithParams(
					"this_method_represents_code_to_be_executed_when_precondition_($0)_receive_a_new_token", getPrecondition().getName()));
		} else {
			returned.append(FlexoLocalization.localizedForKeyWithParams(
					"this_method_represents_code_to_be_executed_when_precondition_of_node_($0)_receive_a_new_token", getPrecondition()
							.getAttachedNode().getName()));
		}
		if (getPrecondition().getIncomingPostConditions().size() > 1) {
			returned.append(StringUtils.LINE_SEPARATOR);
			returned.append(StringUtils.LINE_SEPARATOR);
			returned.append("@param " + getEdgeVariable().getName() + " ");
			returned.append(FlexoLocalization.localizedForKey("identifier_of_edge_which_activates_this_node"));
		}
		return returned.toString();
	}

	private Variable edgeVariable = null;

	private Variable getEdgeVariable() {
		if (edgeVariable == null) {
			edgeVariable = new Variable("edgeId");
		}
		return edgeVariable;
	}

	private Variable tokenIncrementVariable = null;

	private Variable getTokenIncrementVariable() {
		if (tokenIncrementVariable == null) {
			tokenIncrementVariable = new Variable("tokenIncrement");
		}
		return tokenIncrementVariable;
	}

	/**
	 * Overrides parent's method by providing precondition identifier as argument
	 */
	@Override
	protected Procedure makeProcedure() throws InvalidModelException, NotSupportedException {
		if (getPrecondition().getIncomingPostConditions().size() > 1) {
			return new Procedure(getProcedureName(), makeControlGraph(true), getProcedureComment(), new Procedure.ProcedureParameter(
					getEdgeVariable(), new Type("int")));
		} else {
			return super.makeProcedure();
		}
	}

	private ControlGraph makeControlGraphForTokenIncrementSetting() {
		Declaration decl = new Declaration(new Type("int"), getTokenIncrementVariable(), null);

		boolean hasDifferentValues = false;
		int tokenIncrementValue = 1;
		for (FlexoPostCondition edge : getPrecondition().getIncomingPostConditions()) {
			if (edge instanceof TokenEdge) {
				if (!hasDifferentValues && ((TokenEdge) edge).getTokenIncrem() != tokenIncrementValue) {
					hasDifferentValues = true;
				}
				tokenIncrementValue = ((TokenEdge) edge).getTokenIncrem();
			}
		}

		if (!hasDifferentValues) {
			decl.setInitializationValue(new Constant.IntegerConstant(tokenIncrementValue));
			return decl;
		}

		Conditional testAndSet = null;

		Conditional conditional = null;
		for (FlexoPostCondition edge : getPrecondition().getIncomingPostConditions()) {
			Expression condition = new BinaryOperatorExpression(BooleanBinaryOperator.EQUALS, getEdgeVariable(),
					new Constant.IntegerConstant(edge.getFlexoID()));
			Conditional currentConditional = new Conditional(condition, new Assignment(getTokenIncrementVariable(),
					new Constant.IntegerConstant(edge instanceof TokenEdge ? ((TokenEdge) edge).getTokenIncrem() : 1)),
					"Precondition receive token from edge " + edge.getDerivedNameFromStartingObject());
			if (conditional != null) {
				conditional.setElseStatement(currentConditional);
			} else {
				testAndSet = currentConditional;
			}
			conditional = currentConditional;
		}

		return makeSequentialControlGraph(decl, testAndSet);
	}

	@Override
	protected ControlGraph makeControlGraph(boolean interprocedural) throws InvalidModelException, NotSupportedException {
		Expression tokenIncrement = null;
		ControlGraph setTokenIncrement = null;

		if (interprocedural && getPrecondition().getIncomingPostConditions().size() > 1) {
			setTokenIncrement = makeControlGraphForTokenIncrementSetting();
			tokenIncrement = getTokenIncrementVariable();
		} else {
			int tokenIncrementValue = edge != null ? edge.getTokenIncrem() : 1;
			tokenIncrement = new Constant.IntegerConstant(tokenIncrementValue);
		}

		StoredTokensOnPrecondition storedTokensOnPrecondition = new StoredTokensOnPrecondition(precondition);
		Expression storedTokensOnPreconditionIncrementedWithCurrentTokenIncrement = new BinaryOperatorExpression(
				ArithmeticBinaryOperator.ADDITION, storedTokensOnPrecondition, tokenIncrement);
		if (precondition.getInitTokenNbr() != 0) {
			storedTokensOnPreconditionIncrementedWithCurrentTokenIncrement = new BinaryOperatorExpression(
					ArithmeticBinaryOperator.ADDITION, storedTokensOnPreconditionIncrementedWithCurrentTokenIncrement,
					new Constant.IntegerConstant(precondition.getInitTokenNbr()));
		}

		Expression condition = new BinaryOperatorExpression(BooleanBinaryOperator.GREATER_THAN_OR_EQUALS,
				storedTokensOnPreconditionIncrementedWithCurrentTokenIncrement,
				new Constant.IntegerConstant(precondition.getTriggerLevel()));

		StoreTokenOnPrecondition STORE_TOKEN = new StoreTokenOnPrecondition(precondition);

		if (precondition.getAttachedNode() instanceof ActionNode && !(precondition.getAttachedNode() instanceof SelfExecutableActionNode)
				&& ((ActionNode) precondition.getAttachedNode()).isNormalNode()) {
			// If this is a FlexoAction, just store token
			return STORE_TOKEN;
		}

		else {

			if (precondition.getIncomingPostConditions().size() == 1 && precondition.getIncomingPostConditions().firstElement() == edge
					&& tokenIncrement instanceof Constant.IntegerConstant && ((Constant.IntegerConstant) tokenIncrement).getValue() == 1
					&& precondition.getInitTokenNbr() == 0 && precondition.getTriggerLevel() == 1) {
				// In this special case where i am the only one edge arriving to this precondition
				// and if init token number was set to one, i skip the test, asserting this is
				// trivial workflow logic
				return NodeActivation.activateNode(precondition.getAttachedNode(), precondition, interprocedural);
			} else {
				return makeSequentialControlGraph(
						setTokenIncrement,
						new Conditional(condition, NodeActivation.activateNode(precondition.getAttachedNode(), precondition,
								interprocedural), STORE_TOKEN, "Test if precondition"
								+ (precondition.getAttachedBeginNode() != null ? " " + precondition.getName() : "") + " of node "
								+ precondition.getAttachedNode().getName() + " is triggering"));
			}

		}

	}

	@Override
	protected String getProcedureName() {
		if (getPrecondition() != null && getPrecondition().getAttachedNode() != null) {
			if (getPrecondition().getAttachedBeginNode() != null) {
				return "precondition_" + ToolBox.capitalize(ToolBox.getJavaName(getPrecondition().getName())) + "_of_"
						+ ToolBox.capitalize(ToolBox.getJavaName(getPrecondition().getAttachedNode().getName())) + "_"
						+ getPrecondition().getFlexoID() + "_receiveNewToken";
			} else {
				return "precondition_" + ToolBox.capitalize(ToolBox.getJavaName(getPrecondition().getAttachedNode().getName())) + "_"
						+ getPrecondition().getFlexoID() + "_receiveNewToken";
			}
		} else if (getPrecondition() != null) {
			return "precondition_" + ToolBox.capitalize(ToolBox.getJavaName(getPrecondition().getName())) + "_of_???_"
					+ getPrecondition().getFlexoID() + "_receiveNewToken";
		} else {
			return "precondition_???_receiveNewToken";
		}
	}

	public FlexoPostCondition<?, ?> getEdge() {
		return edge;
	}

	public AbstractNode getEdgeOrigin() {
		if (getEdge() != null) {
			return getEdge().getStartNode();
		}
		return null;
	}

	public FlexoPreCondition getPrecondition() {
		return precondition;
	}

}
