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
 * located in src/dev/java. Generated code is located in org.openflexo.antar.expr.parser.analysis, org.openflexo.antar.expr.parser.lexer,
 * org.openflexo.antar.expr.parser.node, org.openflexo.antar.expr.parser.parser
 * 
 * @author sylvain
 */
public class ExpressionParser {

	private static final Logger logger = Logger.getLogger(ExpressionParser.class.getPackage().getName());

	public static Expression parse(String anExpression) throws ParseException {
		try {
			System.out.println("Parsing: " + anExpression);

			// Create a Parser instance.
			// Parser p = new Parser(new Lexer(new PushbackReader(new InputStreamReader(System.in), 1024)));
			// Parser p = new Parser(new Lexer(new PushbackReader(new StringReader("(45 + 36/2)*3 + 5*2"))));
			Parser p = new Parser(new Lexer(new PushbackReader(new StringReader(anExpression))));

			// Parse the input.
			Start tree = p.parse();

			// Apply the translation.
			ExpressionSemanticsAnalyzer t = new ExpressionSemanticsAnalyzer();
			tree.apply(t);

			System.out.println("tree.getPExpr() = " + tree.getPExpr() + " of " + tree.getPExpr().getClass().getName());
			/*APlusExpr a = (APlusExpr) tree.getPExpr();
			System.out.println("left=" + a.getExpr());
			System.out.println("right=" + a.getFactor());*/
			return postSemanticAnalysisReduction(t.getExpression());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ParseException(e.getMessage());
		}
	}

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
