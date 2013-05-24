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

import java.util.Vector;

/*
 * Created on 4 janv. 2006 by sguerin
 *
 * Flexo Application Suite
 * (c) Denali 2003-2005
 */

public class Function extends Token {

	private Word _call;
	private Vector<Token> _parameters;

	public Function(Word call, Vector<Token> parameters) throws ParseException {
		super();
		_call = call;
		_parameters = parameters;
	}

	@Override
	public String toString() {
		String returned = _call.toString() + "(";
		boolean isFirst = true;
		for (AbstractToken token : _parameters) {
			returned += (isFirst ? "" : ",") + token;
			isFirst = false;
		}
		return returned + ")";
	}

	private static Token makeParameter(ListOfToken listOfToken) throws ParseException {
		if (listOfToken.size() == 0) {
			throw new ParseException("Syntax error: invalid null operand");
		} else if (listOfToken.size() == 1) {
			if (listOfToken.firstElement() instanceof Token) {
				return (Token) listOfToken.firstElement();
			}
			if (listOfToken.firstElement() instanceof ListOfToken) {
				return makeParameter((ListOfToken) listOfToken.firstElement());
			} else {
				throw new ParseException("Syntax error: invalid operand");
			}
		}

		else {
			try {
				// System.out.println("TRYING TO DECODE AS FUNCTION: "+listOfToken);
				return makeFunction(listOfToken);
			} catch (ParseException e) {
				// System.out.println("FAILED TO EXPRESS AS A FUNCTION: "+listOfToken);
				// e.printStackTrace();
				// System.out.println("TRY AS AN EXPRESSION: "+listOfToken);
				return Expression.makeExpression(listOfToken);
			}
		}
	}

	protected static Function makeFunction(ListOfToken unparsedList) throws ParseException {
		if (unparsedList.size() == 1 && unparsedList.firstElement() instanceof ListOfToken) {
			return makeFunction((ListOfToken) unparsedList.firstElement());
		} else {
			// On y va, ca rigole plus
			// System.out.println("makeFunction with "+unparsedList);
			if (unparsedList.size() == 2 && unparsedList.get(0) instanceof Word && unparsedList.get(1) instanceof ListOfToken) {
				ListOfToken unparsedParamList = (ListOfToken) unparsedList.get(1);

				// Reduce functions first
				ListOfToken functionsReducedParamList = new ListOfToken();
				for (int i = 0; i < unparsedParamList.size(); i++) {
					AbstractToken tok = unparsedParamList.elementAt(i);
					if (tok instanceof Word && i + 1 < unparsedParamList.size()
							&& unparsedParamList.elementAt(i + 1) instanceof ListOfToken) {
						ListOfToken tryToBuildFunction = new ListOfToken();
						tryToBuildFunction.add(tok);
						tryToBuildFunction.add(unparsedParamList.elementAt(i + 1));
						functionsReducedParamList.add(tryToBuildFunction);
						i++;
					} else {
						functionsReducedParamList.add(tok);
					}
				}

				// System.out.println("AFTER FUNCTION REDUCTION : "+functionsReducedParamList);

				// Then reduce comma(s)
				ListOfToken commaReducedParamList = new ListOfToken();
				ListOfToken currentTokens = new ListOfToken();
				for (AbstractToken tok : functionsReducedParamList) {
					if (tok instanceof Comma) {
						if (currentTokens.size() == 0) {
							throw new ParseException("Syntax error near " + tok);
						} else if (currentTokens.size() > 1) {
							ListOfToken newListOfTokens = new ListOfToken();
							newListOfTokens.addAll(currentTokens);
							commaReducedParamList.add(newListOfTokens);
						} else if (currentTokens.size() == 1) {
							commaReducedParamList.add(currentTokens.firstElement());
						}
						currentTokens.clear();
					} else {
						currentTokens.add(tok);
					}
				}
				if (currentTokens.size() > 1) {
					ListOfToken newListOfTokens = new ListOfToken();
					newListOfTokens.addAll(currentTokens);
					commaReducedParamList.add(newListOfTokens);
				} else if (currentTokens.size() == 1) {
					commaReducedParamList.add(currentTokens.firstElement());
				}
				currentTokens.clear();

				// System.out.println("AFTER COMMA REDUCTION : "+commaReducedParamList);

				Vector<Token> paramList = new Vector<Token>();

				for (int i = 0; i < commaReducedParamList.size(); i++) {
					AbstractToken tok = commaReducedParamList.elementAt(i);
					// Must be a token
					if (tok instanceof Token) {
						paramList.add((Token) tok);
					} else if (tok instanceof ListOfToken) {
						paramList.add(makeParameter((ListOfToken) tok));
					} else {
						throw new ParseException("Syntax error near " + tok);
					}
				}
				// System.out.println("paramList="+paramList);

				return new Function((Word) unparsedList.get(0), paramList);
			}
			throw new ParseException("Syntax error near " + unparsedList.elementAt(0));

		}

	}

	public Word getCall() {
		return _call;
	}

	public Vector<Token> getParameters() {
		return _parameters;
	}

	@Override
	public String getSerializationValue() {
		String returned = _call.getSerializationValue() + "(";
		boolean isFirst = true;
		for (Token token : _parameters) {
			returned += (isFirst ? "" : ",") + token.getSerializationValue();
			isFirst = false;
		}
		return returned + ")";
	}

}
