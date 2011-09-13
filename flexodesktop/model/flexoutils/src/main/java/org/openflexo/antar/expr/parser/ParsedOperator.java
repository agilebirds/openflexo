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
package org.openflexo.antar.expr.parser;
import org.openflexo.antar.expr.Operator;
import org.openflexo.antar.expr.OperatorNotSupportedException;

/*
 * Created on 4 janv. 2006 by sguerin
 *
 * Flexo Application Suite
 * (c) Denali 2003-2005
 */

public class ParsedOperator implements AbstractToken {

	private Operator _operator;
	private Operator _alternativeOperator; // When ambigous
	private String _serializationValue;
	private ExpressionParser _parser;
	
	public ParsedOperator(Operator operator, ExpressionParser parser)
	{
		super();
		_parser = parser;
		_operator = operator;
		try {
			_serializationValue = parser.getSymbol(operator);
		} catch (OperatorNotSupportedException e) {
			_serializationValue = "<unsupported>";
		}
	}
	
	public ParsedOperator(Operator operator, Operator alternativeOperator, ExpressionParser parser)
	{
		this(operator,parser);
		_alternativeOperator = alternativeOperator;
	}
	
	public int getPriority()
	{
		return _operator.getPriority();
	}

	@Override
	public String toString()
	{
		return /*(_operator instanceof UnaryOperator ? "Unary:" : "Binary:")+*/_serializationValue;
	}

	public Operator getOperator() {
		return _operator;
	}

	public Operator getAlternativeOperator() 
	{
		return _alternativeOperator;
	}
	
	public boolean isAmbigous()
	{
		return _alternativeOperator != null;
	}

	public ExpressionParser getExpressionParser() 
	{
		return _parser;
	}

}
