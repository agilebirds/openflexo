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
import org.openflexo.antar.Nop;
import org.openflexo.antar.Procedure;
import org.openflexo.antar.Type;
import org.openflexo.antar.expr.BinaryOperatorExpression;
import org.openflexo.antar.expr.BooleanBinaryOperator;
import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.Variable;


import org.openflexo.foundation.exec.expr.HasWaitingTokensOnOperator;
import org.openflexo.foundation.exec.inst.DestroyRemainingTokensForOperator;
import org.openflexo.foundation.exec.inst.StoreTokenOnOperator;
import org.openflexo.foundation.wkf.edge.ExternalMessageInEdge;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.ANDOperator;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.StringUtils;

public class OperatorANDExecution extends OperatorNodeExecution {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(OperatorANDExecution.class.getPackage().getName());

	private FlexoPostCondition<?, ?> edge;
	
	protected OperatorANDExecution(ANDOperator operatorNode, FlexoPostCondition<?, ?> edge)
	{
		super(operatorNode);
		this.edge = edge;
		if (edge == null && operatorNode.getIncomingPostConditions().size() == 1) {
			// No need to make it ambigous, consider the only one incoming edge
			this.edge =operatorNode.getIncomingPostConditions().firstElement();
		}
	}
	

	@Override
	protected final ControlGraph makeControlGraph(boolean interprocedural) throws InvalidModelException,NotSupportedException
	{
		if (!interprocedural) {
			if (getEdge() == null)
				throw new InvalidModelException("Operator AND named "+getOperatorNode().getName()+" execution called from a null edge");
			return makeControlGraph(getEdge(), interprocedural);
		}

		Conditional iterateOnEntries = null;
		
		Conditional conditional = null;
		for (FlexoPostCondition<?, ?> edge : getOperatorNode().getIncomingPostConditions()) {
			Expression condition = new BinaryOperatorExpression(
					BooleanBinaryOperator.EQUALS,
					getEdgeVariable(),
					new Constant.IntegerConstant(edge.getFlexoID()));
			Conditional currentConditional = new Conditional(
					condition,
					makeControlGraph(edge,interprocedural),
					"Operator was called from edge "+edge.getDerivedNameFromStartingObject());
			if (conditional != null) {
				conditional.setElseStatement(currentConditional);
			}
			else {
				iterateOnEntries = currentConditional;
			}
			conditional = currentConditional;
		}
		
		return iterateOnEntries;
	}

	protected final ControlGraph makeControlGraph(FlexoPostCondition<?, ?> entry, boolean interprocedural) throws InvalidModelException,NotSupportedException
	{
		Expression condition = null;
		
		if (entry == null) {
			throw new InvalidModelException("Operator AND named "+getOperatorNode().getName()+" execution called from a null edge");
		}

		for (FlexoPostCondition<?, ?> tempEntry : getOperatorNode().getIncomingPostConditions()) {
			if (tempEntry != entry) {
				Expression currentCondition = new HasWaitingTokensOnOperator(getOperatorNode(), tempEntry);
				if (condition == null) {
					condition = currentCondition;
				}
				else {
					condition = new BinaryOperatorExpression(BooleanBinaryOperator.AND,condition,currentCondition);
				}
			}
		}
		
		ControlGraph DELETE_TOKENS = new DestroyRemainingTokensForOperator(getOperatorNode());
		ControlGraph SEND_TOKENS_TO_OUTPUTS = null;
		
		Vector<ControlGraph> sendTokensStatements = new Vector<ControlGraph>();

		for (FlexoPostCondition edge : getOperatorNode().getOutgoingPostConditions()) {
			sendTokensStatements.add(SendToken.sendToken(edge,interprocedural));
		}

		ControlGraph SEND_TOKENS = makeFlowControlGraph(sendTokensStatements);

		Vector<ControlGraph> sendMessagesStatements = new Vector<ControlGraph>();

		for (FlexoPostCondition edge : getOperatorNode().getOutgoingPostConditions()) {
			if (edge instanceof ExternalMessageInEdge) {
				sendMessagesStatements.add(SendMessage.sendMessage((ExternalMessageInEdge)edge,interprocedural));
			}
		}

		ControlGraph SEND_MESSAGES = makeFlowControlGraph(sendMessagesStatements);

		SEND_TOKENS_TO_OUTPUTS = makeSequentialControlGraph(
				SEND_MESSAGES,
				SEND_TOKENS);
		
		if (SEND_TOKENS_TO_OUTPUTS instanceof Nop && !SEND_TOKENS_TO_OUTPUTS.hasComment()) SEND_TOKENS_TO_OUTPUTS.setInlineComment("nothing to do");

		ControlGraph OPERATOR_AND_TRIGGER = makeSequentialControlGraph(new Nop("Operator AND triggers"),DELETE_TOKENS,SEND_TOKENS_TO_OUTPUTS);
		
		if (condition == null) {
			return OPERATOR_AND_TRIGGER;
		}
		
		else return new Conditional(
				condition,
				OPERATOR_AND_TRIGGER,
				new StoreTokenOnOperator(getOperatorNode(),entry));
		
	}
	
	/**
	 * Overrides parent's method by providing precondition identifier as argument
	 */
	@Override
	protected Procedure makeProcedure() throws InvalidModelException,NotSupportedException
	{
		return new Procedure(
				getProcedureName(),
				makeControlGraph(true),
				getProcedureComment(),
				new Procedure.ProcedureParameter(getEdgeVariable(),new Type("int")));
	}


	@Override
	protected String getProcedureComment()
	{
		StringBuffer returned = new StringBuffer();
		returned.append(FlexoLocalization.localizedForKeyWithParams("this_method_represents_code_to_be_executed_when_operator_($0)_is_executed",getOperatorNode().getName()));
		returned.append(StringUtils.LINE_SEPARATOR);
		returned.append(StringUtils.LINE_SEPARATOR);
		returned.append("@param "+getEdgeVariable().getName()+" ");
		returned.append(FlexoLocalization.localizedForKey("identifier_of_edge_which_activates_this_node"));	
		return returned.toString();
	}

	private Variable edgeVariable = null;
	
	private Variable getEdgeVariable()
	{
		if (edgeVariable == null) edgeVariable = new Variable("edgeId");
		return edgeVariable;
	}
	
	public FlexoPostCondition<?, ?> getEdge() 
	{
		return edge;
	}

	public AbstractNode getEdgeOrigin() 
	{
		if (getEdge() != null) {
			return getEdge().getStartNode();
		}
		return null;
	}

	@Override
	public ANDOperator getOperatorNode() 
	{
		return (ANDOperator)super.getOperatorNode();
	}


}
