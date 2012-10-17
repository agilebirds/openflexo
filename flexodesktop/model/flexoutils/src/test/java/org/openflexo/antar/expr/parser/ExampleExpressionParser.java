package org.openflexo.antar.expr.parser;

import org.openflexo.antar.expr.Expression;

public class ExampleExpressionParser {
	public static void main(String[] arguments) {
		try {
			// Expression e = ExpressionParser.parse("a+b*c+2.3 = d + 2 + 'fuck'");
			// Expression e = ExpressionParser.parse("coucou.les.gars(1,2+3,7.8,'fuck').en.short(1)");
			// Expression e = ExpressionParser.parse("a.b(2).c(1)");
			// Expression e = ExpressionParser.parse("testFunction(-pi/2,7.8,1-9*7/9,aVariable,foo1+foo2,e)");
			Expression e = ExpressionParser.parse("a ? b : c");
			System.out.println("Parsed: " + e);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}