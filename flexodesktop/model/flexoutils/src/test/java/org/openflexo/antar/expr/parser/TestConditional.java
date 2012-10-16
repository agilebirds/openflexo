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

import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.DefaultExpressionParser;
import org.openflexo.antar.expr.EvaluationContext;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.Variable;
import org.openflexo.antar.expr.oldparser.ExpressionParser;
import org.openflexo.antar.expr.oldparser.ParseException;
import org.openflexo.antar.expr.oldparser.Word;
import org.openflexo.antar.expr.oldparser.ExpressionParser.VariableFactory;

public class TestConditional {

	public static void main(String[] args) {
		DefaultExpressionParser parser = new DefaultExpressionParser();

		System.out.println("ok");

		try {
			Expression parsed = parser.parse("coucou=true");
			Expression evaluation = parsed.evaluate(new EvaluationContext(new ExpressionParser.DefaultConstantFactory(),
					new VariableFactory() {
						@Override
						public Expression makeVariable(Word value) {
							System.out.println("Hop");
							if (value.getValue().equals("coucou")) {
								return Constant.BooleanConstant.TRUE;
							}
							return new Variable(value.getValue());
						}
					}, new ExpressionParser.DefaultFunctionFactory()));
			System.out.println("parsed=" + parsed);
			System.out.println("evaluation=" + evaluation + " is a " + evaluation.getClass().getName());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TypeMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
