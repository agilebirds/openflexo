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
package org.openflexo.foundation.exec.expr;

import org.openflexo.antar.expr.EvaluationType;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.OperatorNode;



public class HasWaitingTokensOnOperator extends FlexoBuiltInExpression {

	private OperatorNode operator;
	private FlexoPostCondition<?, ?> entry;
	
	public HasWaitingTokensOnOperator(OperatorNode operator, FlexoPostCondition<?, ?> entry) {
		super();
		this.operator = operator;
		this.entry = entry;
	}
	
	public FlexoPostCondition<?, ?> getEntry() {
		return entry;
	}

	public void setEntry(FlexoPostCondition<?, ?> entry) {
		this.entry = entry;
	}

	public OperatorNode getOperatorNode() {
		return operator;
	}

	public void setOperatorNode(OperatorNode operator) {
		this.operator = operator;
	}

	@Override
	public String toString()
	{
		return "<HasWaitingTokensOnOperator("+getOperatorNode().getName()+","+getEntry().getDerivedNameFromStartingObject()+")>";
	}

	@Override
	public String getJavaStringRepresentation() 
	{
		return "(hasWaitingTokensOnOperator("+getOperatorNode().getFlexoID()+","+getEntry().getFlexoID()+"))";
	}

	@Override
	public EvaluationType getEvaluationType() throws TypeMismatchException 
	{
		return EvaluationType.BOOLEAN;
	}
}
