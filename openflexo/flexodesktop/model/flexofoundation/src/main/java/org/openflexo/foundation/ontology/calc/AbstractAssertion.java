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
package org.openflexo.foundation.ontology.calc;

import java.util.Hashtable;

import org.openflexo.antar.expr.DefaultExpressionParser;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.UnresolvedExpressionException;
import org.openflexo.antar.expr.parser.ParseException;


public abstract class AbstractAssertion extends CalcObject {

	private AddIndividual _action;
	private String conditional;
	private Expression condition;

	public void setAction(AddIndividual action) 
	{
		_action = action;
	}

	public AddIndividual getAction() 
	{
		return _action;
	}
	
	public EditionScheme getScheme()
	{
		return getAction().getScheme();
	}
	
	@Override
	public OntologyCalc getCalc() 
	{
		return getAction().getCalc();
	}
	
	public String getConditional() 
	{
		return conditional;
	}

	public void setConditional(String conditional) 
	{
		this.conditional = conditional;
		if (conditional != null) {
			DefaultExpressionParser parser = new DefaultExpressionParser();
			try {
				condition = parser.parse(conditional);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean evaluateCondition(final Hashtable<String,Object> parameterValues)
	{
		if (condition == null) return true;
		try {
			return condition.evaluateCondition(parameterValues);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (UnresolvedExpressionException e) {
			e.printStackTrace();
		}
		return false;
	}

}
