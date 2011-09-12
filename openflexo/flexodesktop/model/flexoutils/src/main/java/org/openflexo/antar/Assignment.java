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
package org.openflexo.antar;

import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.Variable;

public class Assignment extends Instruction {

	private Variable receiver;
	private Expression assignmentValue;
	
	public Assignment(Variable receiver, Expression assignmentValue) {
		super();
		this.receiver = receiver;
		this.assignmentValue = assignmentValue;
	}
	
	public Expression getAssignmentValue() 
	{
		return assignmentValue;
	}
	
	public void setAssignmentValue(Expression assignmentValue) 
	{
		this.assignmentValue = assignmentValue;
	}
	
	public Variable getReceiver() 
	{
		return receiver;
	}
	
	public void setReceiver(Variable receiver)
	{
		this.receiver = receiver;
	}
	
	@Override
	public Assignment clone()
	{
		Assignment returned = new Assignment(receiver,assignmentValue);
		returned.setHeaderComment(getHeaderComment());
		returned.setInlineComment(getInlineComment());
		return returned;
	}

}
