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
import org.openflexo.foundation.bindings.AbstractBinding;



public class ConditionPrimitiveExpression extends FlexoBuiltInExpression {

	private AbstractBinding conditionPrimitive;
	
	public ConditionPrimitiveExpression (AbstractBinding conditionPrimitive)
	{
		super();
		this.conditionPrimitive = conditionPrimitive;
	}

	@Override
	public String toString()
	{
		return "<ConditionPrimitiveExpression("+conditionPrimitive.getStringRepresentation()+")>";
	}

	@Override
	public String getJavaStringRepresentation() 
	{
		
		return "("+conditionPrimitive.getJavaCodeStringRepresentation()+")";
	}

	public AbstractBinding getConditionPrimitive()
	{
		return conditionPrimitive;
	}

	@Override
	public EvaluationType getEvaluationType() throws TypeMismatchException {
		return EvaluationType.BOOLEAN;
	}
}
