package org.openflexo.antar.expr.parser;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.openflexo.antar.expr.DefaultExpressionPrettyPrinter;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.TypeMismatchException;

public class TestParsing extends TestCase {

	private DefaultExpressionPrettyPrinter prettyPrinter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		prettyPrinter = new DefaultExpressionPrettyPrinter();
	}

	private void tryToParse(String anExpression, String expectedEvaluatedExpression, boolean shouldFail) {

		try {
			System.out.println("Parsing... " + anExpression);
			Expression parsed = ExpressionParser.parse(anExpression);
			System.out.println("Parsed " + anExpression);
			System.out.println("Successfully parsed as : " + parsed.getClass().getSimpleName());
			System.out.println("Normalized: " + prettyPrinter.getStringRepresentation(parsed));
			System.out.println("Evaluated: " + prettyPrinter.getStringRepresentation(parsed.evaluate()));
			if (shouldFail) {
				fail();
			}
			System.out.println("expectedEvaluatedExpression=" + "'" + expectedEvaluatedExpression + "'");
			System.out.println("prettyPrinter.getStringRepresentation(parsed.evaluate())=" + "'"
					+ prettyPrinter.getStringRepresentation(parsed.evaluate()) + "'");
			assertEquals(expectedEvaluatedExpression, prettyPrinter.getStringRepresentation(parsed.evaluate()));
		} catch (ParseException e) {
			if (!shouldFail) {
				e.printStackTrace();
				fail();
			} else {
				System.out.println("Parsing " + anExpression + " has failed as expected: " + e.getMessage());
			}
		} catch (TypeMismatchException e) {
			if (!shouldFail) {
				e.printStackTrace();
				fail();
			} else {
				System.out.println("Parsing " + anExpression + " has failed as expected: " + e.getMessage());
			}
		}

		/*try {
			System.out.println("\nParsing " + aString);
			Expression parsed = parser.parse(aString);
			System.out.println("Successfully parsed as : " + parsed.getClass().getSimpleName());
			System.out.println("Normalized: " + prettyPrinter.getStringRepresentation(parsed));
			System.out.println("Evaluated: " + prettyPrinter.getStringRepresentation(parsed.evaluate()));
			if (shouldFail) {
				fail();
			}
			assertEquals(expectedEvaluatedExpression, prettyPrinter.getStringRepresentation(parsed.evaluate()));
		} catch (ParseException e) {
			if (!shouldFail) {
				e.printStackTrace();
				fail();
			} else {
				System.out.println("Parsing " + aString + " has failed as expected: " + e.getMessage());
			}
		} catch (TypeMismatchException e) {
			if (!shouldFail) {
				e.printStackTrace();
				fail();
			} else {
				System.out.println("Parsing " + aString + " has failed as expected: " + e.getMessage());
			}
		}*/
	}

	public void test0() {
		tryToParse("machin", "machin", false);
	}

	public void test1() {
		tryToParse("34", "34", false);
	}

	public void test1b() {
		tryToParse("7.8", "7.8", false);
	}

	public void test1c() {
		tryToParse("0.876e-9", "7.8", false);
	}

	public void test1d() {
		tryToParse("1.876E12", "7.8", false);
	}

	public void test2() {
		tryToParse("machin+1", "(machin + 1)", false);
	}

	public void test3() {
		tryToParse("machin+1*6-8/7+bidule", "(((machin + 6) - 1.1428571428571428) + bidule)", false);
	}

	public void test4() {
		tryToParse("-89", "-89", false);
	}

	public void test51() {
		tryToParse("1+sin(1)", "-1.0", false);
	}

	public void test52() {
		tryToParse("sin(-pi/2)", "-1.0", false);
	}

	public void test53() {
		tryToParse("a=b", "-1.0", false);
	}

	public void test61() {
		tryToParse("a | b", "", false);
	}

	public void test62() {
		tryToParse("a & b", "", false);
	}

	public void test63() {
		tryToParse("false && true", "", false);
	}

	public void test6() {
		tryToParse("!a&&b", "((!(a)) & b)", false);
	}

	public void test7() {
		tryToParse("cos(-pi/2,7)", "", true);
	}

	public void test71() {
		tryToParse("coucou.les.gars", "", true);
	}

	public void test72() {
		tryToParse("coucou.les.gars(1,pi)", "", true);
	}

	public void test73() {
		tryToParse("testFunction(-pi/2,7.8,1-9*7/9,aVariable,foo1+foo2,e)",
				"testFunction(-1.5707963267948966,7.8,-6.0,aVariable,\"foo1&foo2\",false,\"e\")", false);
	}

	public void test74() {
		tryToParse("testFunction(-pi/2,7.8,1-9*7/9,aVariable,foo1+foo2,false && true,e)",
				"testFunction(-1.5707963267948966,7.8,-6.0,aVariable,\"foo1&foo2\",false,\"e\")", false);
	}

	public void test75() {
		tryToParse("\"foo1\"", "", false);
	}

	public void test76() {
		tryToParse("a(b+\"foo1\"+\"foo2\")", "", false);
	}

	public void test77() {
		tryToParse("testFunction(-pi/2,7.8,1-9*7/9+\"foo2\",aVariable,false && true,e)",
				"testFunction(-1.5707963267948966,7.8,-6.0,aVariable,\"foo1&foo2\",false,\"e\")", false);
	}

	public void test78() {
		tryToParse("'foo1'", "", false);
	}

	public void test8() {
		tryToParse("testFunction(-pi/2,7.8,1-9*7/9,aVariable,\"foo1\"+'&'+\"foo2\",false && true,'e')",
				"testFunction(-1.5707963267948966,7.8,-6.0,aVariable,\"foo1&foo2\",false,\"e\")", false);
	}

	public void test81() {
		tryToParse("-atan(2)", "1.651284257012876", false);
	}

	public void test9() {
		tryToParse("-(atan(-pi/2)*(3-5*pi/7+8/9))", "1.651284257012876", false);
	}

	public void test10() {
		tryToParse("-cos(atan(-pi/2)*(3-5*pi/7+8/9))", "0.08040105411083133", false);
	}

	public void test11() {
		tryToParse("coucou.coucou.coucou", "coucou.coucou.coucou", false);
	}

	public void test12() {
		tryToParse("a && (c || d && (!f)) ||b", "((a & (c | (d & (!(f))))) | b)", false);
	}

	public void test13() {
		tryToParse("1 < 2", "true", false);
	}

	public void test14() {
		tryToParse("7-x-(-x-6-8*2)", "((7 - x) - (((-(x)) - 6) - 16))", false);
	}

	public void test15() {
		tryToParse("1+function(test,4<7-x)", "(1 + function(test,(4 < (7 - x))))", false);
	}

	public void test16() {
		tryToParse("function1(function2(8,9,10))", "function1(function2(8,9,10))", false);
	}

	public void test17() {
		tryToParse("binding1.a.b == binding2.a.b*7", "(binding1.a.b = (binding2.a.b * 7))", false);
	}

	public void test18() {
		tryToParse("\"a + ( 2 + b )\"+2", "\"a + ( 2 + b )2\"", false);
	}

	public void test19() {
		tryToParse("a\"b", "", true);
	}

	public void test20() {
		tryToParse("a'b", "", true);
	}

	public void test21() {
		tryToParse("\"", "", true);
	}

	public void test22() {
		tryToParse("\"a + (( 2 + b )\"+2", "\"a + (( 2 + b )2\"", false);
	}

	public void test23() {
		tryToParse("test23 ( fdfd + 1", "", true);
	}

	public void test24() {
		tryToParse("test24 [ fdfd + 1", "", true);
	}

	public void test25() throws java.text.ParseException {
		Date date = new SimpleDateFormat("dd/MM/yy HH:mm").parse("17/12/07 15:55");
		SimpleDateFormat localeDateFormat = new SimpleDateFormat();
		tryToParse("(([dd/MM/yy HH:mm,17/12/07 12:54] + [3h] ) + [1min])",
				"[" + localeDateFormat.toPattern() + "," + localeDateFormat.format(date) + "]", false);
	}

	public void test26() throws java.text.ParseException {
		Date date = new SimpleDateFormat("dd/MM/yy HH:mm").parse("17/12/07 15:55");
		SimpleDateFormat localeDateFormat = new SimpleDateFormat();
		tryToParse("([dd/MM/yy HH:mm,17/12/07 12:54] + ( [3h] + [1min]))",
				"[" + localeDateFormat.toPattern() + "," + localeDateFormat.format(date) + "]", false);
	}

}
