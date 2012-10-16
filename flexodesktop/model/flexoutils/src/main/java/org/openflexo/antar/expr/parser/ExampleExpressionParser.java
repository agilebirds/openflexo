package org.openflexo.antar.expr.parser;

import org.openflexo.antar.expr.Expression;

public class ExampleExpressionParser {
	public static void main(String[] arguments) {
		try {
			Expression e = ExpressionParser.parse("a+b*c");
			System.out.println("Parsed: " + e);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}