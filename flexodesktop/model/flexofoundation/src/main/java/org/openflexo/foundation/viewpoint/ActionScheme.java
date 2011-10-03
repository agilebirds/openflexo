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
package org.openflexo.foundation.viewpoint;

import java.util.Hashtable;

import org.openflexo.antar.expr.DefaultExpressionParser;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.UnresolvedExpressionException;
import org.openflexo.antar.expr.parser.ParseException;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.toolbox.StringUtils;


public class ActionScheme extends EditionScheme {

	private Expression condition;
	private String conditional;
	
	public ActionScheme() 
	{
		super();
	}

	@Override
	public EditionSchemeType getEditionSchemeType()
	{
		return EditionSchemeType.ActionScheme;
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.CED.ACTION_SCHEME_INSPECTOR;
	}

	public String getConditional() 
	{
		return conditional;
	}

	public void setConditional(String conditional) 
	{
		this.conditional = conditional;
		if (StringUtils.isNotEmpty(conditional)) {
			DefaultExpressionParser parser = new DefaultExpressionParser();
			try {
				condition = parser.parse(conditional);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean evaluateCondition(EditionPatternReference editionPatternReference)
	{
		return evaluateCondition(editionPatternReference.getEditionPatternInstance().getActors());
	}

	public boolean evaluateCondition(final Hashtable<String,?> parameterValues)
	{
		if (condition == null) {
			return true;
		}
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
