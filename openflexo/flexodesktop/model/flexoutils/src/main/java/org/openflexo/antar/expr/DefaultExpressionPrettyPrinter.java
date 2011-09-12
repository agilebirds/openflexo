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

import org.openflexo.antar.expr.Constant.BooleanConstant;
import org.openflexo.antar.expr.Constant.DateConstant;
import org.openflexo.antar.expr.Constant.DurationConstant;
import org.openflexo.antar.expr.Constant.EnumConstant;
import org.openflexo.antar.expr.Constant.FloatConstant;
import org.openflexo.antar.expr.Constant.IntegerConstant;
import org.openflexo.antar.expr.Constant.StringConstant;
import org.openflexo.antar.pp.ExpressionPrettyPrinter;
import org.openflexo.toolbox.Duration;
import org.openflexo.xmlcode.StringEncoder;


public class DefaultExpressionPrettyPrinter extends ExpressionPrettyPrinter {

	private StringEncoder.DateConverter dateConverter = new StringEncoder.DateConverter();
	private Duration.DurationStringConverter durationConverter = new Duration.DurationStringConverter();
	
	public DefaultExpressionPrettyPrinter()
	{
		super(new DefaultGrammar());
	}

	protected DefaultExpressionPrettyPrinter(ExpressionGrammar grammar)
	{
		super(grammar);
	}

 	@Override
	protected String makeStringRepresentation(BooleanConstant constant) 
	{
		if (constant == BooleanConstant.FALSE) return "false";
		else if (constant == BooleanConstant.TRUE) return "true";
		return "???";
	}

	@Override
	protected String makeStringRepresentation(FloatConstant constant)
	{
		return Double.toString(constant.getValue());
	}

	@Override
	protected String makeStringRepresentation(IntegerConstant constant) 
	{
		return Long.toString(constant.getValue());
	}

	@Override
	protected String makeStringRepresentation(StringConstant constant) 
	{
		return '"'+constant.getValue()+'"';
	}

	@Override
	protected String makeStringRepresentation(SymbolicConstant constant) 
	{
		return constant.getSymbol();
	}

	@Override
	protected String makeStringRepresentation(Function function) 
	{
		StringBuffer args = new StringBuffer();
		boolean isFirst = true;
		for (Expression e : function.getArgs()) {
			args.append((isFirst?"":",")+getStringRepresentation(e));
			isFirst = false;
		}
		return function.getName()
		+"("
		+args
		+")";
	}

	@Override
	protected String makeStringRepresentation(UnaryOperatorExpression expression) 
	{
	   	try {
			return "("
			+getSymbol(expression.getOperator())+"("
			+getStringRepresentation(expression.getArgument())+")"
			+")";
		} catch (OperatorNotSupportedException e) {
			return "<unsupported>";
		}
	}

    @Override
	protected String makeStringRepresentation (BinaryOperatorExpression expression)
    {
    	try {
			return "("
			+getStringRepresentation(expression.getLeftArgument())
			+" "
			+getSymbol(expression.getOperator())
			+" "
			+getStringRepresentation(expression.getRightArgument())
			+")";
		} catch (OperatorNotSupportedException e) {
			return "<unsupported>";
		}
    }

	@Override
	protected String makeStringRepresentation(DateConstant constant) 
	{
		if (constant == null || constant.getDate() == null) return "[null]";
		return "["+dateConverter.convertToString(constant.getDate())+"]";
	}

	@Override
	protected String makeStringRepresentation(DurationConstant constant)
	{
		if (constant == null || constant.getDuration() == null) return "[null]";
		return "["+durationConverter.convertToString(constant.getDuration())+"]";
	}

    @Override
	protected final String makeStringRepresentation (EnumConstant constant)
    {
    	return constant.getName();
    }

}
