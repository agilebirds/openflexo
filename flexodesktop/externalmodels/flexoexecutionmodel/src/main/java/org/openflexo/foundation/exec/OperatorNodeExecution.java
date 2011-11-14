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

import java.util.logging.Logger;

import org.openflexo.antar.ControlGraph;
import org.openflexo.antar.Procedure;
import org.openflexo.antar.ProcedureCall;
import org.openflexo.antar.expr.Constant;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.ANDOperator;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.OROperator;
import org.openflexo.foundation.wkf.node.OperatorNode;
import org.openflexo.foundation.wkf.node.SWITCHOperator;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.ToolBox;

public abstract class OperatorNodeExecution extends ControlGraphBuilder {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(OperatorNodeExecution.class.getPackage().getName());

	private OperatorNode operatorNode;

	public static ControlGraphBuilder getExecutionNodeBuilder(OperatorNode operatorNode, FlexoPostCondition<?, ?> edge)
			throws NotSupportedException, InvalidModelException {
		if (operatorNode instanceof ANDOperator) {
			return (new OperatorANDExecution((ANDOperator) operatorNode, edge));
		}

		else if (operatorNode instanceof OROperator) {
			return (new OperatorORExecution((OROperator) operatorNode));
		}

		else if (operatorNode instanceof IFOperator) {
			return (new OperatorIFExecution((IFOperator) operatorNode));
		}

		else if (operatorNode instanceof LOOPOperator) {
			return (new OperatorLOOPExecution((LOOPOperator) operatorNode));
		}

		else if (operatorNode instanceof SWITCHOperator) {
			return (new OperatorSWITCHExecution((SWITCHOperator) operatorNode));
		}

		throw new NotSupportedException("Dont know what to do with a " + operatorNode);

	}

	public static ControlGraph executeNode(OperatorNode operatorNode, FlexoPostCondition<?, ?> edge, boolean interprocedural)
			throws NotSupportedException, InvalidModelException {
		ControlGraphBuilder cgBuilder = getExecutionNodeBuilder(operatorNode, edge);

		if (cgBuilder != null) {

			if (interprocedural) {
				Procedure procedure = cgBuilder.makeProcedure();
				ProcedureCall returned = new ProcedureCall(procedure);
				if (operatorNode instanceof ANDOperator) {
					returned.addArgument(new Constant.IntegerConstant(edge.getFlexoID()));
				}
				returned.appendHeaderComment("Operator " + operatorNode.getName() + " is executing", true);
				return returned;
			} else {
				ControlGraph returned = cgBuilder.makeControlGraph(interprocedural);
				returned.appendHeaderComment("Operator " + operatorNode.getName() + " is executing", true);
				return returned;
			}
		} else {
			throw new NotSupportedException("Dont know what to do with a " + operatorNode);
		}

	}

	protected OperatorNodeExecution(OperatorNode operatorNode) {
		super();
		this.operatorNode = operatorNode;
	}

	@Override
	protected abstract ControlGraph makeControlGraph(boolean interprocedural) throws InvalidModelException, NotSupportedException;

	@Override
	protected String getProcedureName() {
		return "executeOperator_" + ToolBox.capitalize(ToolBox.getJavaName(getOperatorNode().getName())) + "_"
				+ getOperatorNode().getFlexoID();
	}

	public OperatorNode getOperatorNode() {
		return operatorNode;
	}

	@Override
	protected String getProcedureComment() {
		StringBuffer returned = new StringBuffer();
		returned.append(FlexoLocalization.localizedForKeyWithParams(
				"this_method_represents_code_to_be_executed_when_operator_($0)_is_executed", getOperatorNode().getName()));
		return returned.toString();
	}

}
