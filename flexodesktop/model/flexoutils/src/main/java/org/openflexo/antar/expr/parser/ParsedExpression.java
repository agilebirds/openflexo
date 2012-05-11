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

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.openflexo.antar.expr.BinaryOperator;
import org.openflexo.antar.expr.UnaryOperator;

/*
 * Created on 4 janv. 2006 by sguerin
 *
 * Flexo Application Suite
 * (c) Denali 2003-2005
 */

public abstract class ParsedExpression extends Token {

	private ParsedOperator _operator;

	public ParsedExpression(ParsedOperator operator) {
		super();
		_operator = operator;
	}

	protected static Token makeOperand(ListOfToken listOfToken) throws ParseException {
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
				// System.out.println("FAILED TO EXPRESS AS AN EXPRESSION: "+listOfToken+" because "+e.getMessage());
				// e.printStackTrace();
				// System.out.println("TRY AS FUNCTION: "+listOfToken);
				return ParsedFunction.makeFunction(listOfToken);
			}
		}
	}

	public ParsedOperator getOperator() {
		return _operator;
	}

	@Override
	public abstract String toString();

	protected static ParsedExpression makeExpression(ListOfToken aListOfTokens) throws ParseException {
		if ((aListOfTokens.size() == 1) && (aListOfTokens.firstElement() instanceof ParsedExpression)) {
			return (ParsedExpression) aListOfTokens.firstElement();
		}

		if ((aListOfTokens.size() == 1) && (aListOfTokens.firstElement() instanceof ListOfToken)) {
			return makeExpression((ListOfToken) aListOfTokens.firstElement());
		} else {
			boolean hasAmbigousOperators = false;
			for (AbstractToken token : aListOfTokens) {
				if (token instanceof ParsedOperator && ((ParsedOperator) token).isAmbigous()) {
					hasAmbigousOperators = true;
				}
			}
			if (hasAmbigousOperators) {
				// System.out.println("Found ambigous operator !!!");
				Vector<ListOfToken> evaluateThis = new Vector<ListOfToken>();
				evaluateThis.add(aListOfTokens);
				for (AbstractToken token : aListOfTokens) {
					if (token instanceof ParsedOperator && ((ParsedOperator) token).isAmbigous()) {
						ParsedOperator ambigousOperator = (ParsedOperator) token;
						Vector<ListOfToken> newListsToEvaluate = new Vector<ListOfToken>();
						for (ListOfToken aList : evaluateThis) {
							ListOfToken cloneList = (ListOfToken) aList.clone();
							for (int i = 0; i < cloneList.size(); i++) {
								AbstractToken t = cloneList.elementAt(i);
								if (t == ambigousOperator) { // This is the ambigous operator
									cloneList.set(
											i,
											new ParsedOperator(ambigousOperator.getAlternativeOperator(), ambigousOperator
													.getExpressionParser()));
								}
							}
							newListsToEvaluate.add(cloneList);
						}
						evaluateThis.addAll(newListsToEvaluate);
					}
				}
				/* for (ListOfToken aList : evaluateThis) {
					 System.out.println("Evaluating : "+aList);
				   }*/

				for (ListOfToken aList : evaluateThis) {
					// System.out.println("Evaluating : "+aList);
					try {
						return _internallyMakeExpression(aList);
					} catch (ParseException e) {
						// Lets evaluate an other one
					}
				}

				// No way, abort
				throw new ParseException("Parse error");

			} else {
				return _internallyMakeExpression(aListOfTokens);
			}
		}
	}

	private static ParsedExpression _internallyMakeExpression(ListOfToken aListOfTokens) throws ParseException {

		// Reduce functions first
		ListOfToken functionsReducedParamList = new ListOfToken();
		for (int i = 0; i < aListOfTokens.size(); i++) {
			AbstractToken tok = aListOfTokens.elementAt(i);
			if ((tok instanceof Word) && (i + 1 < aListOfTokens.size()) && (aListOfTokens.elementAt(i + 1) instanceof ListOfToken)) {
				ListOfToken tryToBuildFunction = new ListOfToken();
				tryToBuildFunction.add(tok);
				tryToBuildFunction.add(aListOfTokens.elementAt(i + 1));
				functionsReducedParamList.add(tryToBuildFunction);
				i++;
				// System.out.println("J'identifie une fonction "+tok+" with args "+tryToBuildFunction);
			} else {
				functionsReducedParamList.add(tok);
			}
		}

		ListOfToken unparsedList = functionsReducedParamList;

		// ListOfToken unparsedList = aListOfTokens;

		Vector<IndexedOperator> allOperators = new Vector<IndexedOperator>();
		for (int i = 0; i < unparsedList.size(); i++) {
			if (unparsedList.elementAt(i) instanceof ParsedOperator) {
				IndexedOperator toAdd = new IndexedOperator();
				toAdd.operator = (ParsedOperator) unparsedList.elementAt(i);
				toAdd.index = i;
				allOperators.insertElementAt(toAdd, 0);
			}
		}

		Collections.sort(allOperators, new Comparator<IndexedOperator>() {
			@Override
			public int compare(IndexedOperator o1, IndexedOperator o2) {
				return o2.operator.getPriority() - o1.operator.getPriority();
			}
		});

		if (allOperators.size() == 0) {
			// System.out.println("unparsedList of size: "+unparsedList.size());
			/*for (AbstractToken token : unparsedList) {
			    	System.out.println("token: "+token+" of "+token.getClass().getSimpleName());
			    }*/
			StringBuffer sb = new StringBuffer();
			for (AbstractToken token : unparsedList) {
				sb.append(token.toString());
			}
			throw new ParseException("Syntax error: no operator found in " + sb.toString());
		}

		IndexedOperator pivot = allOperators.firstElement();

		if (pivot.operator.getOperator() instanceof BinaryOperator) {
			ListOfToken left = new ListOfToken();
			for (int i = 0; i < pivot.index; i++) {
				left.add(unparsedList.elementAt(i));
			}
			ListOfToken right = new ListOfToken();
			for (int i = pivot.index + 1; i < unparsedList.size(); i++) {
				right.add(unparsedList.elementAt(i));
			}
			return new ParsedBinaryExpression(pivot.operator, left, right);
		}

		else if (pivot.operator.getOperator() instanceof UnaryOperator) {

			/*if (pivot.index != 0)             
				 	throw new ParseException("Parse error near "+pivot.operator.toString());
			   	ListOfToken right = new ListOfToken();
				for (int i=1; i<unparsedList.size(); i++) right.add(unparsedList.elementAt(i));    
				return new ParsedUnaryExpression(pivot.operator,right);*/

			ListOfToken right = new ListOfToken();
			for (int i = pivot.index + 1; i < unparsedList.size(); i++) {
				right.add(unparsedList.elementAt(i));
			}
			ParsedUnaryExpression newParsedUnaryExpression = new ParsedUnaryExpression(pivot.operator, right);
			ListOfToken newUnparsedList = new ListOfToken();
			for (int i = 0; i < pivot.index; i++) {
				newUnparsedList.add(unparsedList.elementAt(i));
			}
			newUnparsedList.add(newParsedUnaryExpression);
			return makeExpression(newUnparsedList);
		}

		else {
			throw new ParseException("Internal error");
		}

	}

	protected static class IndexedOperator {
		protected int index;
		protected ParsedOperator operator;

		@Override
		public String toString() {
			return "(" + index + "," + operator + ")";
		}
	}

}
