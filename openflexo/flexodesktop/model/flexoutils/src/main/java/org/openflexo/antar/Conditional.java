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

public class Conditional extends ControlGraph {

	private Expression condition;
	private ControlGraph thenStatement;
	private ControlGraph elseStatement; // Might be null when no "else" statement
	
	public Conditional(Expression condition, ControlGraph thenStatement) 
	{
		this(condition,thenStatement,(ControlGraph)null);
	}
	
	public Conditional(Expression condition, ControlGraph thenStatement, String headerComment) 
	{
		this(condition,thenStatement);
		setHeaderComment(headerComment);
	}
	
	public Conditional(Expression condition, ControlGraph thenStatement, ControlGraph elseStatement)
	{
		super();
		this.condition = condition;
		this.thenStatement = thenStatement;
		this.elseStatement = elseStatement;
	}
	
	public Conditional(Expression condition, ControlGraph thenStatement, ControlGraph elseStatement, String headerComment)
	{
		this(condition,thenStatement,elseStatement);
		setHeaderComment(headerComment);
	}
	
	public Conditional(Expression condition, ControlGraph thenStatement, ControlGraph elseStatement, String headerComment, String inlineComment)
	{
		this(condition,thenStatement,elseStatement,headerComment);
		setInlineComment(inlineComment);
	}
	
	public Expression getCondition() {
		return condition;
	}
	public void setCondition(Expression condition) {
		this.condition = condition;
	}
	public ControlGraph getElseStatement() {
		return elseStatement;
	}
	public void setElseStatement(ControlGraph elseStatement) {
		this.elseStatement = elseStatement;
	}
	public ControlGraph getThenStatement() {
		return thenStatement;
	}
	public void setThenStatement(ControlGraph thenStatement) {
		this.thenStatement = thenStatement;
	}
	
	@Override
	public String toString ()
	{
		return "IF ("+condition+") THEN { \n"+thenStatement+" } "+(elseStatement!=null?"ELSE { \n"+elseStatement+" } ":"");
	}

	@Override
	public ControlGraph normalize()
	{
		return new Conditional(condition,thenStatement.normalize(),(elseStatement!=null?elseStatement.normalize():null),getHeaderComment(),getInlineComment());
	}


}
