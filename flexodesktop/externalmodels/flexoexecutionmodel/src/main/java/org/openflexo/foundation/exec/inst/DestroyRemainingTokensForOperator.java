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
package org.openflexo.foundation.exec.inst;

import org.openflexo.foundation.wkf.node.OperatorNode;

public class DestroyRemainingTokensForOperator extends CustomInstruction {

	private OperatorNode operatorNode;
	
	public DestroyRemainingTokensForOperator (OperatorNode operatorNode)
	{
		super();
		this.operatorNode = operatorNode;
		setInlineComment("Destroy remaining tokens in current ProcessInstance for operator "+getOperatorNode().getName());
	}

	public OperatorNode getOperatorNode()
	{
		return operatorNode;
	}

	@Override
	public String toString()
	{
		return "[DestroyRemainingTokens:"+operatorNode+"]";
	}

	@Override
	public String getJavaStringRepresentation() 
	{
		return "destroyRemainingTokensForOperator("+operatorNode.getFlexoID()+");";
	}
	
	@Override
	public DestroyRemainingTokensForOperator clone()
	{
		DestroyRemainingTokensForOperator returned = new DestroyRemainingTokensForOperator(operatorNode);
		returned.setHeaderComment(getHeaderComment());
		returned.setInlineComment(getInlineComment());
		return returned;
	}

}
