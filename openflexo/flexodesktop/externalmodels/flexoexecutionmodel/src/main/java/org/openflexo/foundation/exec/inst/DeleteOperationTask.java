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

import org.openflexo.foundation.wkf.node.OperationNode;

public class DeleteOperationTask extends CustomInstruction {

	private OperationNode operationNode;
	
	public DeleteOperationTask (OperationNode operationNode)
	{
		super();
		this.operationNode = operationNode;
		setInlineComment("Delete current OperationTask for node "+getOperationNode().getName());
	}

	public OperationNode getOperationNode()
	{
		return operationNode;
	}

	@Override
	public String toString()
	{
		return "[DeleteOperationTask:"+operationNode+"]";
	}

	@Override
	public String getJavaStringRepresentation() 
	{
		return "deleteOperationTask("+getOperationNode().getFlexoID()+");";
	}
	
	@Override
	public DeleteOperationTask clone()
	{
		DeleteOperationTask returned = new DeleteOperationTask(operationNode);
		returned.setHeaderComment(getHeaderComment());
		returned.setInlineComment(getInlineComment());
		return returned;
	}

}
