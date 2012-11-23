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
package org.openflexo.letparser;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/*
 * Created on 4 janv. 2006 by sguerin
 *
 * Flexo Application Suite
 * (c) Denali 2003-2005
 */

public class Expression extends Token {

	private Operator _operator;
	private Token _leftOperand;
	private Token _rightOperand;

	public Expression(Operator operator, ListOfToken left, ListOfToken right) throws ParseException {
		this(operator, makeOperand(left), makeOperand(right));
	}

	public Expression(Operator operator, Token left, Token right) {
		super();
		_operator = operator;
		_leftOperand = left;
		_rightOperand = right;
	}

	private static Token makeOperand(ListOfToken listOfToken) throws ParseException {
		if (listOfToken.size() == 0) {
			throw new ParseException("Syntax error: invalid null operand");
		} else if (listOfToken.size() == 1) {
			if (listOfToken.firstElement() instanceof Token) {
				return (Token) listOfToken.firstElement();
			}
			if (listOfToken.firstElement() instanceof ListOfToken) {
				return makeOperand((ListOfToken) listOfToken.firstElement());
			} else {
				throw new ParseException("Syntax error: invalid operand");
			}
		}

		else {
			try {
				// System.out.println("TRYING TO DECODE AS EXPRESSION: "+listOfToken);
				return makeExpression(listOfToken);
			} catch (ParseException e) {
				// System.out.println("FAILED TO EXPRESS AS AN EXPRESSION: "+listOfToken);
				// e.printStackTrace();
				// System.out.println("TRY AS FUNCTION: "+listOfToken);
				return Function.makeFunction(listOfToken);
			}
		}
	}

	public Token getLeftOperand() {
		return _leftOperand;
	}

	public Operator getOperator() {
		return _operator;
	}

	public Token getRightOperand() {
		return _rightOperand;
	}

	@Override
	public String toString() {
		return "(" + _leftOperand + _operator + _rightOperand + ")";
	}

	@Override
	public String getSerializationValue() {
		return "(" + _leftOperand.getSerializationValue() + " " + _operator.getSymbol() + " " + _rightOperand.getSerializationValue() + ")";
	}

	protected static Expression makeExpression(ListOfToken aListOfTokens) throws ParseException {
		if (aListOfTokens.size() == 1 && aListOfTokens.firstElement() instanceof ListOfToken) {
			return makeExpression((ListOfToken) aListOfTokens.firstElement());
		} else {
			// On y va, ca rigole plus

			// Reduce functions first
			ListOfToken functionsReducedParamList = new ListOfToken();
			for (int i = 0; i < aListOfTokens.size(); i++) {
				AbstractToken tok = aListOfTokens.elementAt(i);
				if (tok instanceof Word && i + 1 < aListOfTokens.size() && aListOfTokens.elementAt(i + 1) instanceof ListOfToken) {
					ListOfToken tryToBuildFunction = new ListOfToken();
					tryToBuildFunction.add(tok);
					tryToBuildFunction.add(aListOfTokens.elementAt(i + 1));
					functionsReducedParamList.add(tryToBuildFunction);
					i++;
				} else {
					functionsReducedParamList.add(tok);
				}
			}

			ListOfToken unparsedList = functionsReducedParamList;

			Vector<IndexedOperator> allOperators = new Vector<IndexedOperator>();
			for (int i = 0; i < unparsedList.size(); i++) {
				if (i % 2 != 0) {
					// Impair: must be an operator
					if (!(unparsedList.elementAt(i) instanceof Operator)) {
						throw new ParseException("Syntax error near " + unparsedList.elementAt(i));
					} else {
						IndexedOperator toAdd = new IndexedOperator();
						toAdd.operator = (Operator) unparsedList.elementAt(i);
						toAdd.index = i;
						allOperators.insertElementAt(toAdd, 0);
					}
				}
			}
			Collections.sort(allOperators, new Comparator<IndexedOperator>() {
				@Override
				public int compare(IndexedOperator o1, IndexedOperator o2) {
					return o2.operator.getPriority() - o1.operator.getPriority();
				}

			});
			// System.out.println ("sorted operators = "+allOperators);

			if (allOperators.size() == 0) {
				throw new ParseException("Syntax error: no operator found");
			}

			IndexedOperator pivot = allOperators.firstElement();
			ListOfToken left = new ListOfToken();
			for (int i = 0; i < pivot.index; i++) {
				left.add(unparsedList.elementAt(i));
			}
			ListOfToken right = new ListOfToken();
			for (int i = pivot.index + 1; i < unparsedList.size(); i++) {
				right.add(unparsedList.elementAt(i));
			}
			return new Expression(pivot.operator, left, right);
		}

	}

	protected static class IndexedOperator {
		protected int index;
		protected Operator operator;

		@Override
		public String toString() {
			return "(" + index + "," + operator + ")";
		}
	}

}
