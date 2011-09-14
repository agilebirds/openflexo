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

import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.OperatorNode;

public class StoreTokenOnOperator extends CustomInstruction {

	private OperatorNode operatorNode;
	private FlexoPostCondition<?, ?> entry;
	
	public StoreTokenOnOperator (OperatorNode operatorNode,FlexoPostCondition<?, ?> entry)
	{
		super();
		this.operatorNode = operatorNode;
		this.entry = entry;
		setInlineComment("Store a new token on operator"+operatorNode.getName()+" and entry "+((FlexoPostCondition)entry).getDerivedNameFromStartingObject());
	}

	@Override
	public String toString()
	{
		return "[StoreTokenOnOperator:"+operatorNode.getName()+","+((FlexoPostCondition)entry).getDerivedNameFromStartingObject()+"]";
	}

	@Override
	public String getJavaStringRepresentation() 
	{
		return "storeTokenOnOperator("+getOperatorNode().getFlexoID()+","+((FlexoPostCondition)entry).getFlexoID()+");";
	}
	
	@Override
	public StoreTokenOnOperator clone()
	{
		StoreTokenOnOperator returned = new StoreTokenOnOperator(operatorNode,entry);
		returned.setHeaderComment(getHeaderComment());
		returned.setInlineComment(getInlineComment());
		return returned;
	}

	public FlexoPostCondition<?, ?> getEntry() {
		return entry;
	}

	public OperatorNode getOperatorNode() {
		return operatorNode;
	}

}
