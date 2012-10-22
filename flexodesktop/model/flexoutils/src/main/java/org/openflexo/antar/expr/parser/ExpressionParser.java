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

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.logging.Logger;

import org.openflexo.antar.expr.ArithmeticUnaryOperator;
import org.openflexo.antar.expr.Constant.ArithmeticConstant;
import org.openflexo.antar.expr.Constant.FloatConstant;
import org.openflexo.antar.expr.Constant.IntegerConstant;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.UnaryOperatorExpression;
import org.openflexo.antar.expr.parser.lexer.Lexer;
import org.openflexo.antar.expr.parser.node.Start;
import org.openflexo.antar.expr.parser.parser.Parser;

/**
 * This class provides the parsing service for AnTAR expressions and bindings. This includes syntaxic and semantics analyzer.<br>
 * 
 * SableCC is extensively used to perform this. To compile and generate the grammar, please invoke {@link CompileAntarExpressionParser}
 * located in src/dev/java. The grammar is located in src/main/resources/antar_expr.grammar.<br>
 * Generated code is located in org.openflexo.antar.expr.parser.analysis, org.openflexo.antar.expr.parser.lexer,
 * org.openflexo.antar.expr.parser.node, org.openflexo.antar.expr.parser.parser
 * 
 * @author sylvain
 */
public class ExpressionParser {

	private static final Logger logger = Logger.getLogger(ExpressionParser.class.getPackage().getName());

	/**
	 * This is the method to invoke to perform a parsing. Syntaxic and (some) semantics analyzer are performed and returned value is an
	 * Expression conform to AnTAR expression abstract syntaxic tree
	 * 
	 * @param anExpression
	 * @return
	 * @throws ParseException
	 *             if expression was not parsable
	 */
	public static Expression parse(String anExpression) throws ParseException {
		try {
			// System.out.println("Parsing: " + anExpression);

			// Create a Parser instance.
			Parser p = new Parser(new Lexer(new PushbackReader(new StringReader(anExpression))));

			// Parse the input.
			Start tree = p.parse();

			// Apply the semantics analyzer.
			ExpressionSemanticsAnalyzer t = new ExpressionSemanticsAnalyzer();
			tree.apply(t);

			return postSemanticAnalysisReduction(t.getExpression());
		} catch (Exception e) {
			throw new ParseException(e.getMessage() + " while parsing " + anExpression);
		}
	}

	/**
	 * This method is invoked at the end of the parsing to perform some trivial reductions (eg, a combination of a minus and an arithmetic
	 * value results in a negative arithmetic value)
	 * 
	 * @param e
	 * @return
	 */
	private static Expression postSemanticAnalysisReduction(Expression e) {
		if (e instanceof UnaryOperatorExpression && ((UnaryOperatorExpression) e).getOperator() == ArithmeticUnaryOperator.UNARY_MINUS
				&& ((UnaryOperatorExpression) e).getArgument() instanceof ArithmeticConstant) {
			// In this case, we will reduce this into a negative single arithmetic constant
			ArithmeticConstant<?> c = (ArithmeticConstant<?>) ((UnaryOperatorExpression) e).getArgument();
			if (c instanceof IntegerConstant) {
				return new IntegerConstant(-((IntegerConstant) c).getValue());
			} else if (c instanceof FloatConstant) {
				return new FloatConstant(-((FloatConstant) c).getValue());
			} else {
				logger.warning("Unexpected " + c);
			}
		}
		return e;
	}
}
