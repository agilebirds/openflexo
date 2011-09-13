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
package org.openflexo.antar.expr;

import java.util.Vector;

import org.openflexo.antar.expr.parser.Word;


public class Variable extends Expression {

	private String name;

	public Variable(String name) 
	{
		super();
		this.name = name;
	}

	@Override
	public int getDepth()
	{
		return 0;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}
	
	@Override
	public Expression evaluate(EvaluationContext context) 
	{
		if (context != null) return context.getVariableFactory().makeVariable(new Word(getName()));
		return this;
	}

	@Override
	public EvaluationType getEvaluationType()
	{
		return EvaluationType.LITERAL;
	}

	@Override
	protected Vector<Expression> getChilds()
	{
		return null;
	}

	public boolean isValid()
	{
		if (name.length() == 0) return false;
		
		boolean startingPathItem = true;
		for (int i=0; i<name.length(); i++) {
			char c = name.charAt(i);
			if (c=='.') {
				startingPathItem = true;
			}
			else {
				boolean isNormalChar = ((c>='A' && c<='Z')
						|| (c>='a' && c<='z')
						|| (c=='(' || c==')' || c=='_') // See Java authorized characters
						|| (c>='0' && c<='9' && !startingPathItem));
				if (!isNormalChar) return false;
				startingPathItem = false;
			}
		}
		return true;

	}


}
