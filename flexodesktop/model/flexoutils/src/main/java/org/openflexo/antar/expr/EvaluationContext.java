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

import org.openflexo.antar.expr.parser.ExpressionParser;
import org.openflexo.antar.expr.parser.ExpressionParser.ConstantFactory;
import org.openflexo.antar.expr.parser.ExpressionParser.FunctionFactory;
import org.openflexo.antar.expr.parser.ExpressionParser.VariableFactory;

public class EvaluationContext 
{
	private ExpressionParser.ConstantFactory _constantFactory;
	private ExpressionParser.VariableFactory _variableFactory;
	private ExpressionParser.FunctionFactory _functionFactory;
	
	public EvaluationContext()
	{
		this(new ExpressionParser.DefaultConstantFactory(),
				new ExpressionParser.DefaultVariableFactory(),
				new ExpressionParser.DefaultFunctionFactory());
	}
	
	public EvaluationContext(ConstantFactory constantFactory, VariableFactory variableFactory, FunctionFactory functionFactory)
	{
		super();
		_constantFactory = constantFactory;
		_variableFactory = variableFactory;
		_functionFactory = functionFactory;
	}
	
	public ExpressionParser.ConstantFactory getConstantFactory() 
	{
		return _constantFactory;
	}
	public ExpressionParser.FunctionFactory getFunctionFactory()
	{
		return _functionFactory;
	}
	public ExpressionParser.VariableFactory getVariableFactory() 
	{
		return _variableFactory;
	}
}
