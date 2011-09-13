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
import org.openflexo.antar.Nop;
import org.openflexo.foundation.exec.inst.DestroyRemainingTokensForOperator;
import org.openflexo.foundation.wkf.edge.ExternalMessageInEdge;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.OROperator;
import org.openflexo.logging.FlexoLogger;

public class OperatorORExecution extends OperatorNodeExecution {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(OperatorORExecution.class.getPackage().getName());

	protected OperatorORExecution(OROperator operatorNode)
	{
		super(operatorNode);
	}
	

	@Override
	protected final ControlGraph makeControlGraph(boolean interprocedural) throws InvalidModelException,NotSupportedException
	{
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

		return makeSequentialControlGraph(DELETE_TOKENS,SEND_TOKENS_TO_OUTPUTS);
		
	}

	@Override
	public OROperator getOperatorNode() 
	{
		return (OROperator)super.getOperatorNode();
	}


}
