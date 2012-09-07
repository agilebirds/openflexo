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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.expr.ArithmeticBinaryOperator;
import org.openflexo.antar.expr.ArithmeticUnaryOperator;
import org.openflexo.antar.expr.BinaryOperator;
import org.openflexo.antar.expr.BinaryOperatorExpression;
import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.ExpressionGrammar;
import org.openflexo.antar.expr.Function;
import org.openflexo.antar.expr.Operator;
import org.openflexo.antar.expr.OperatorNotSupportedException;
import org.openflexo.antar.expr.SymbolicConstant;
import org.openflexo.antar.expr.UnaryOperator;
import org.openflexo.antar.expr.UnaryOperatorExpression;
import org.openflexo.antar.expr.Variable;
import org.openflexo.toolbox.Duration;
import org.openflexo.toolbox.Duration.DurationStringConverter;
import org.openflexo.xmlcode.StringEncoder.DateConverter;

/*
 * Created on 4 janv. 2006 by sguerin
 *
 * Flexo Application Suite
 * (c) Denali 2003-2005
 */

public class ExpressionParser {

	private ExpressionGrammar grammar;
	private DateConverter dateConverter = new DateConverter();
	private DurationStringConverter durationConverter = new DurationStringConverter();

	public ExpressionParser(ExpressionGrammar grammar) {
		super();
		this.grammar = grammar;
	}

	public Expression parse(String aString, Bindable bindable) throws ParseException {
		String preprocessedString = preprocessString(aString);
		BufferedReader rdr = new BufferedReader(new StringReader(preprocessedString));
		Token parsed = parse(rdr);
		return makeExpression(parsed, bindable);
	}

	// Perform some lexical checks...

	private String preprocessString(String aString) throws ParseException {
		StringBuffer returned = new StringBuffer();
		if (aString.length() == 0) {
			throw new ParseException("Empty string");
		}
		int parentLevel = 0;
		boolean escaping = false;
		char waitedEscapingEndChar = '?';
		for (int i = 0; i < aString.length(); i++) {
			char c = aString.charAt(i);
			if (!escaping) {
				if (c == '\'') {
					escaping = true;
					waitedEscapingEndChar = '\'';
				} else if (c == '"') {
					escaping = true;
					waitedEscapingEndChar = '"';
				} else if (c == '[') {
					escaping = true;
					waitedEscapingEndChar = ']';
				} else if (c == '(') {
					parentLevel++;
				} else if (c == ')') {
					parentLevel--;
				}
				returned.append(c);
			} else {
				if (c == waitedEscapingEndChar) {
					escaping = false;
				}
				if (c == ']') {
					returned.append('['); // We use [] to embed date or duration representation
				} else {
					returned.append(c);
				}
			}
		}
		if (parentLevel != 0) {
			throw new ParseException("Unbalanced parenthesis: " + aString);
		}
		if (escaping) {
			throw new ParseException("Unbalanced escaping char : expecting " + waitedEscapingEndChar);
		}
		return returned.toString();
	}

	/*public ParsedExpression parseExpression (String aString) throws ParseException
	{
		Token result = parse(aString);
		if (result instanceof ParsedExpression) return (ParsedExpression)result;
		throw new ParseException("Could not parse as an expression "+aString);
	}

	public Function parseFunction (String aString) throws ParseException
	{
		Token result = parse(aString);
		if (result instanceof Function) return (Function)result;
		throw new ParseException("Could not parse as an function "+aString);
	}*/

	private StreamTokenizer initStreamTokenizer(Reader rdr) {
		// Always need to setup StreamTokenizer
		StreamTokenizer input = new StreamTokenizer(rdr);
		input.wordChars('_', '_');
		input.quoteChar('[');

		for (UnaryOperator op : getAllSupportedUnaryOperators()) {
			try {
				considerAsOperator(getSymbol(op), input);
				considerAsOperator(getAlternativeSymbol(op), input);
			} catch (OperatorNotSupportedException e) {
				// Shoud not happen
				e.printStackTrace();
			}
		}

		for (BinaryOperator op : getAllSupportedBinaryOperators()) {
			try {
				considerAsOperator(getSymbol(op), input);
				considerAsOperator(getAlternativeSymbol(op), input);
			} catch (OperatorNotSupportedException e) {
				// Shoud not happen
				e.printStackTrace();
			}
		}

		/* input.ordinaryChar('/');
		input.ordinaryChar('&');
		input.ordinaryChar('!');
		input.ordinaryChar('-');*/
		return input;
	}

	private void considerAsOperator(String symbol, StreamTokenizer input) {
		if (symbol == null) {
			return;
		}
		// System.out.println("considerAsOperator: "+symbol);
		char firstChar = symbol.charAt(0);
		if (firstChar >= 'a' && firstChar <= 'z' || firstChar >= 'A' && firstChar <= 'Z') {
			// Ignore this
		} else {
			// System.out.println("Ordinary char: "+firstChar);
			input.ordinaryChar(firstChar);
		}
	}

	private Token parse(Reader rdr) throws ParseException {
		ListOfToken unparsedList = parseLevel(initStreamTokenizer(rdr));
		if (unparsedList.size() == 1 && unparsedList.firstElement() instanceof Token) {
			return (Token) unparsedList.firstElement();
		}
		try {
			return ParsedFunction.makeFunction(unparsedList);
		} catch (ParseException e) {
			return ParsedExpression.makeExpression(unparsedList);
		}

	}

	/**
	 * Return a vector of AbstractToken (Operator,Word,Value) and Vector elements
	 * 
	 * @return
	 */
	private ListOfToken parseLevel(StreamTokenizer input) throws ParseException {
		ListOfToken returned = new ListOfToken();

		try {
			// Read input file and build array

			String currentInput = "";
			boolean levelSeemsToBeFinished = false;
			boolean prefixedBy$ = false;

			while (!levelSeemsToBeFinished && input.nextToken() != StreamTokenizer.TT_EOF) {
				// System.out.println("currentInput="+currentInput+" input="+input);

				if (input.ttype == StreamTokenizer.TT_WORD) {
					// System.out.println("Found string: "+ input.sval);
					handlesCurrentInput(returned, currentInput);
					currentInput = "";
					handlesWordAddition(returned, input.sval);
				} else if (input.ttype == StreamTokenizer.TT_NUMBER) {
					// System.out.println("Found double: "+ input.nval);
					handlesCurrentInput(returned, currentInput);
					currentInput = "";
					double valueAsDouble = input.nval;
					if (valueAsDouble < 0) {
						if (returned.size() > 0 && returned.lastElement() != null
								&& (returned.lastElement() instanceof Value || returned.lastElement() instanceof Word)) {
							returned.add(new ParsedOperator(ArithmeticBinaryOperator.SUBSTRACTION, this));
						} else {
							returned.add(new ParsedOperator(ArithmeticUnaryOperator.UNARY_MINUS, this));
						}
						valueAsDouble = -valueAsDouble;
					}
					Value value = Value.createValue(valueAsDouble);
					value.setPrefixedBy$(prefixedBy$);
					returned.add(value);
					prefixedBy$ = false;
				}
				// looks for quotes indicating delimited strings
				else if (input.ttype == '[') {
					// Then the string will be in the sval field
					// System.out.println("Found delimited string: "+ input.sval);
					handlesCurrentInput(returned, currentInput);
					currentInput = "";
					// Is it a date or a duration ?
					String parseThis = input.sval;
					try {
						Date parsedDate = dateConverter.tryToConvertFromString(parseThis);
						DateValue value = DateValue.createDateValue(parsedDate);
						value.setPrefixedBy$(prefixedBy$);
						returned.add(value);
						prefixedBy$ = false;
					} catch (java.text.ParseException cannotParseAsADate) {
						// Lets continue...
						try {
							Duration parsedDuration = durationConverter.tryToConvertFromString(parseThis);
							DurationValue value = DurationValue.createDurationValue(parsedDuration);
							value.setPrefixedBy$(prefixedBy$);
							returned.add(value);
							prefixedBy$ = false;
						} catch (java.text.ParseException cannotParseAsADurationEither) {
							Value value = StringValue.createStringValue("<unparsable>");
							value.setPrefixedBy$(prefixedBy$);
							returned.add(value);
							prefixedBy$ = false;
						}
					}
				} else if (input.ttype == '"') {
					// Then the string will be in the sval field
					// System.out.println("Found delimited string: "+ input.sval);
					handlesCurrentInput(returned, currentInput);
					currentInput = "";
					// handlesWordAddition(returned,input.sval);
					Value value = StringValue.createStringValue(input.sval);
					value.setPrefixedBy$(prefixedBy$);
					returned.add(value);
					prefixedBy$ = false;
				} else if (input.ttype == '\'') {
					// Then the string will be in the sval field
					// System.out.println("Found delimited string: "+ input.sval);
					handlesCurrentInput(returned, currentInput);
					currentInput = "";
					// handlesWordAddition(returned,input.sval);
					Value value;
					if (input.sval.length() == 0) {
						value = CharValue.createCharValue(input.sval.charAt(0));
					} else {
						value = StringValue.createStringValue(input.sval);
					}
					value.setPrefixedBy$(prefixedBy$);
					returned.add(value);
					prefixedBy$ = false;
				} else if (input.ttype == '$') {
					// Then the string will be in the sval field
					// System.out.println("Found delimited string: "+ input.sval);
					handlesCurrentInput(returned, currentInput);
					currentInput = "";
					prefixedBy$ = true;
				} else {
					char foundChar = (char) input.ttype;
					// System.out.println("Found Ordinry Character: "+ foundChar);
					if (foundChar == '(') {
						handlesCurrentInput(returned, currentInput);
						currentInput = "";
						returned.add(parseLevel(input));
					} else if (foundChar == ')') {
						levelSeemsToBeFinished = true;
					} else {
						currentInput = currentInput + foundChar;
					}
				}
			}

			handlesCurrentInput(returned, currentInput);
			currentInput = "";

			// System.out.println("Done");

			return returned;
		} catch (IOException exception) {
			throw new ParseException("IOException occured: " + exception.getMessage());
		}

	}

	private void handlesCurrentInput(Vector<AbstractToken> returned, String currentInput) throws ParseException {
		if (currentInput.equals("")) {
			return;
		}
		if (currentInput.equals(",")) {
			returned.add(new Comma());
			return;
		}
		ParsedOperator operator = matchOperator(currentInput);
		if (operator != null) {
			returned.add(operator);
		} else {
			throw new ParseException("Invalid characters : " + currentInput);
		}
	}

	private void handlesWordAddition(Vector<AbstractToken> returned, String word) throws ParseException {
		if (word.equals("")) {
			return;
		}
		if (word.equalsIgnoreCase("true") || word.equalsIgnoreCase("yes")) {
			returned.add(new BooleanValue(true));
		} else if (word.equalsIgnoreCase("false") || word.equalsIgnoreCase("no")) {
			returned.add(new BooleanValue(false));
		} else if (matchOperator(word) != null) {
			returned.add(matchOperator(word));
		} else {
			returned.add(new Word(word));
		}
	}

	private ParsedOperator matchOperator(String anInput) {
		UnaryOperator unaryOperator = matchUnaryOperator(anInput);
		BinaryOperator binaryOperator = matchBinaryOperator(anInput);

		if (unaryOperator != null) {
			if (binaryOperator != null) {
				// Ambigous operator
				return new ParsedOperator(unaryOperator, binaryOperator, this);
			} else {
				return new ParsedOperator(unaryOperator, this);
			}
		} else {
			if (binaryOperator != null) {
				return new ParsedOperator(binaryOperator, this);
			}
		}
		return null;
	}

	private UnaryOperator matchUnaryOperator(String anInput) {
		for (UnaryOperator operator : getAllSupportedUnaryOperators()) {
			try {
				if (anInput.toUpperCase().equalsIgnoreCase(getSymbol(operator)) || getAlternativeSymbol(operator) != null
						&& anInput.toUpperCase().equalsIgnoreCase(getAlternativeSymbol(operator))) {
					return operator;
				}
			} catch (OperatorNotSupportedException e) {
				// This operator is not supported, skip tracking for this one
			}
		}
		return null;
	}

	private BinaryOperator matchBinaryOperator(String anInput) {
		for (BinaryOperator operator : getAllSupportedBinaryOperators()) {
			try {
				if (anInput.toUpperCase().equalsIgnoreCase(getSymbol(operator)) || getAlternativeSymbol(operator) != null
						&& anInput.toUpperCase().equalsIgnoreCase(getAlternativeSymbol(operator))) {
					return operator;
				}
			} catch (OperatorNotSupportedException e) {
				// This operator is not supported, skip tracking for this one
			}
		}
		return null;
	}

	private Expression makeExpression(Token parsed, Bindable bindable) throws ParseException {
		if (parsed instanceof Word) {
			for (SymbolicConstant c : SymbolicConstant.allKnownSymbolicConstants) {
				if (((Word) parsed).getValue().equalsIgnoreCase(c.getSymbol())) {
					return (Constant) c;
				}
			}
			return _variableFactory.makeVariable((Word) parsed, bindable);
		} else if (parsed instanceof Value) {
			return _constantFactory.makeConstant((Value) parsed, bindable);
		} else if (parsed instanceof ParsedFunction) {
			ParsedFunction f = (ParsedFunction) parsed;
			Vector<Expression> args = new Vector<Expression>();
			for (Token t : f.getParameters()) {
				args.add(makeExpression(t, bindable));
			}
			return _functionFactory.makeFunction(f.getCall().getValue(), args, bindable);
		} else if (parsed instanceof ParsedBinaryExpression) {
			ParsedBinaryExpression e = (ParsedBinaryExpression) parsed;
			return new BinaryOperatorExpression(e.getBinaryOperator(), makeExpression(e.getLeftOperand(), bindable), makeExpression(
					e.getRightOperand(), bindable));
		} else if (parsed instanceof ParsedUnaryExpression) {
			ParsedUnaryExpression e = (ParsedUnaryExpression) parsed;
			return new UnaryOperatorExpression(e.getUnaryOperator(), makeExpression(e.getOperand(), bindable));
		}

		throw new ParseException("Parse error: unexpected token found");
	}

	protected BinaryOperator[] getAllSupportedBinaryOperators() {
		return grammar.getAllSupportedBinaryOperators();
	}

	protected UnaryOperator[] getAllSupportedUnaryOperators() {
		return grammar.getAllSupportedUnaryOperators();
	}

	protected String getAlternativeSymbol(Operator operator) throws OperatorNotSupportedException {
		return grammar.getAlternativeSymbol(operator);
	}

	protected String getSymbol(Operator operator) throws OperatorNotSupportedException {
		return grammar.getSymbol(operator);
	}

	public static interface VariableFactory {
		public Expression makeVariable(Word value, Bindable bindable);
	}

	public static class DefaultVariableFactory implements VariableFactory {
		private Hashtable<String, Variable> hash = new Hashtable<String, Variable>();

		@Override
		public Variable makeVariable(Word value, Bindable bindable) {
			Variable returned = hash.get(value.getValue());
			if (returned == null) {
				returned = new Variable(value.getValue());
				hash.put(value.getValue(), returned);
			}
			return returned;
		}
	}

	public static interface ConstantFactory {
		public Expression makeConstant(Value value, Bindable bindable);
	}

	public static class DefaultConstantFactory implements ConstantFactory {
		@Override
		public Constant makeConstant(Value value, Bindable bindable) {
			if (value == null) {
				return /*Constant.ObjectSymbolicConstant.NULL;*/new Constant.StringConstant("null");
			}
			if (value instanceof BooleanValue) {
				if (((BooleanValue) value).getBooleanValue()) {
					return Constant.BooleanConstant.TRUE;
				} else {
					return Constant.BooleanConstant.FALSE;
				}
			} else if (value instanceof CharValue) {
				return new Constant.StringConstant(((CharValue) value).getStringValue());
			} else if (value instanceof StringValue) {
				return new Constant.StringConstant(((StringValue) value).getStringValue());
			} else if (value instanceof EnumValue) {
				return new Constant.EnumConstant(((EnumValue) value).getStringValue());
			} else if (value instanceof FloatValue) {
				return new Constant.FloatConstant(((FloatValue) value).getDoubleValue());
			} else if (value instanceof IntValue) {
				return new Constant.IntegerConstant(((IntValue) value).getIntValue());
			} else if (value instanceof DateValue) {
				return new Constant.DateConstant(((DateValue) value).getDateValue());
			} else if (value instanceof DurationValue) {
				return new Constant.DurationConstant(((DurationValue) value).getDurationValue());
			}
			return new Constant.StringConstant("?");
		}
	}

	public static interface FunctionFactory {
		public Expression makeFunction(String functionName, List<Expression> args, Bindable bindable);
	}

	public static class DefaultFunctionFactory implements FunctionFactory {
		@Override
		public Function makeFunction(String functionName, List<Expression> args, Bindable bindable) {
			return new Function(functionName, args);
		}
	}

	private ConstantFactory _constantFactory = new DefaultConstantFactory();

	private VariableFactory _variableFactory = new DefaultVariableFactory();

	private FunctionFactory _functionFactory = new DefaultFunctionFactory();

	public ConstantFactory getConstantFactory() {
		return _constantFactory;
	}

	public void setConstantFactory(ConstantFactory constantFactory) {
		_constantFactory = constantFactory;
	}

	public FunctionFactory getFunctionFactory() {
		return _functionFactory;
	}

	public void setFunctionFactory(FunctionFactory functionFactory) {
		_functionFactory = functionFactory;
	}

	public VariableFactory getVariableFactory() {
		return _variableFactory;
	}

	public void setVariableFactory(VariableFactory variableFactory) {
		_variableFactory = variableFactory;
	}

}
