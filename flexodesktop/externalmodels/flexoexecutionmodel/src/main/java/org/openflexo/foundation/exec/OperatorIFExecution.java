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
import org.openflexo.antar.expr.Expression;
import org.openflexo.foundation.wkf.edge.ExternalMessageInEdge;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.logging.FlexoLogger;

public class OperatorIFExecution extends OperatorNodeExecution {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(OperatorIFExecution.class.getPackage().getName());

	protected OperatorIFExecution(IFOperator operatorNode) {
		super(operatorNode);
	}

	@Override
	protected final ControlGraph makeControlGraph(boolean interprocedural) throws InvalidModelException, NotSupportedException {
		if (getOperatorNode().getConditionPrimitive() == null) {
			throw new InvalidModelException("Operator IF " + getOperatorNode().getName() + " doesn't define any condition primitive");
		}

		Expression condition = makeCondition(getOperatorNode().getConditionPrimitive());

		Vector<ControlGraph> trueEdgeStatements = new Vector<ControlGraph>();
		Vector<ControlGraph> falseEdgeStatements = new Vector<ControlGraph>();

		for (FlexoPostCondition<?, ?> edge : getOperatorNode().getOutgoingPostConditions()) {
			if (edge instanceof ExternalMessageInEdge) {
				if (edge.isPositiveEvaluation()) {
					trueEdgeStatements.add(SendMessage.sendMessage((ExternalMessageInEdge) edge, interprocedural));
				} else {
					falseEdgeStatements.add(SendMessage.sendMessage((ExternalMessageInEdge) edge, interprocedural));
				}
			}
		}

		for (FlexoPostCondition<?, ?> edge : getOperatorNode().getOutgoingPostConditions()) {
			if (edge.isPositiveEvaluation()) {
				trueEdgeStatements.add(SendToken.sendToken(edge, interprocedural));
			} else {
				falseEdgeStatements.add(SendToken.sendToken(edge, interprocedural));
			}
		}

		ControlGraph trueStatement = makeSequentialControlGraph(trueEdgeStatements);
		if (trueStatement instanceof Nop && !trueStatement.hasComment()) {
			trueStatement.setInlineComment("nothing to do when condition is true");
		}

		ControlGraph falseStatement = makeSequentialControlGraph(falseEdgeStatements);
		if (falseStatement instanceof Nop && !trueStatement.hasComment()) {
			falseStatement.setInlineComment("nothing to do when condition is false");
		}

		return new Conditional(condition, trueStatement, falseStatement, "Evaluate operator IF named " + getOperatorNode().getName());

	}

	@Override
	public IFOperator getOperatorNode() {
		return (IFOperator) super.getOperatorNode();
	}

}
