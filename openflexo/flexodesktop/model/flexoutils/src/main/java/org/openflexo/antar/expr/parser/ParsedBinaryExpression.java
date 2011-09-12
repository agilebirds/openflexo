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
import org.openflexo.antar.expr.BinaryOperator;


/*
 * Created on 4 janv. 2006 by sguerin
 *
 * Flexo Application Suite
 * (c) Denali 2003-2005
 */

public class ParsedBinaryExpression extends ParsedExpression {

    private Token _leftOperand;
    private Token _rightOperand;
    
    public ParsedBinaryExpression(ParsedOperator operator, ListOfToken left, ListOfToken right) throws ParseException
    {
        this(operator,makeOperand(left),makeOperand(right));
    }

    public ParsedBinaryExpression(ParsedOperator operator, Token left, Token right) 
    {
        super(operator);
        _leftOperand = left;
        _rightOperand = right;
    }
    
	public BinaryOperator getBinaryOperator()
	{
		return (BinaryOperator)getOperator().getOperator();
	}

     public Token getLeftOperand() 
    {
        return _leftOperand;
    }

     public Token getRightOperand() 
    {
        return _rightOperand;
    }
    
    @Override
	public String toString()
    {
        return "("+_leftOperand+getOperator()+_rightOperand+")";
    }
    
}
