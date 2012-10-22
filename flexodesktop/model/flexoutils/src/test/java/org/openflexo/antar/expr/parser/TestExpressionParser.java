package org.openflexo.antar.expr.parser;

import junit.framework.TestCase;

import org.openflexo.antar.expr.BinaryOperatorExpression;
import org.openflexo.antar.expr.BindingValueAsExpression;
import org.openflexo.antar.expr.BooleanBinaryOperator;
import org.openflexo.antar.expr.ConditionalExpression;
import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.Constant.BooleanConstant;
import org.openflexo.antar.expr.Constant.FloatConstant;
import org.openflexo.antar.expr.Constant.FloatSymbolicConstant;
import org.openflexo.antar.expr.Constant.IntegerConstant;
import org.openflexo.antar.expr.Constant.StringConstant;
import org.openflexo.antar.expr.DefaultExpressionPrettyPrinter;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.UnaryOperatorExpression;

public class TestExpressionParser extends TestCase {

	private DefaultExpressionPrettyPrinter prettyPrinter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		prettyPrinter = new DefaultExpressionPrettyPrinter();
	}

	private Expression tryToParse(String anExpression, String expectedEvaluatedExpression,
			Class<? extends Expression> expectedExpressionClass, Object expectedEvaluation, boolean shouldFail) {

		try {
			System.out.println("Parsing... " + anExpression);
			Expression parsed = ExpressionParser.parse(anExpression);
			Expression evaluated = parsed.evaluate();
			System.out.println("Parsed " + anExpression);
			System.out.println("Successfully parsed as : " + parsed.getClass().getSimpleName());
			System.out.println("Normalized: " + prettyPrinter.getStringRepresentation(parsed));
			System.out.println("Evaluated: " + prettyPrinter.getStringRepresentation(evaluated));
			if (shouldFail) {
				fail();
			}
			assertEquals(expectedExpressionClass, parsed.getClass());
			assertEquals(expectedEvaluatedExpression, prettyPrinter.getStringRepresentation(evaluated));
			if (expectedEvaluation != null) {
				if (!(evaluated instanceof Constant)) {
					fail("Evaluated value is not a constant (expected: " + expectedEvaluation + ") but " + expectedEvaluation);
				}
				if (expectedEvaluation instanceof Number) {
					Object value = ((Constant<?>) evaluated).getValue();
					if (value instanceof Number) {
						assertEquals(((Number) expectedEvaluation).doubleValue(), ((Number) value).doubleValue());
					} else {
						fail("Evaluated value is not a number (expected: " + expectedEvaluation + ") but " + expectedEvaluation);
					}
				} else {
					assertEquals(expectedEvaluation, ((Constant<?>) evaluated).getValue());
				}
			}
			return parsed;
		} catch (ParseException e) {
			if (!shouldFail) {
				e.printStackTrace();
				fail();
			} else {
				System.out.println("Parsing " + anExpression + " has failed as expected: " + e.getMessage());
			}
			return null;
		} catch (TypeMismatchException e) {
			if (!shouldFail) {
				e.printStackTrace();
				fail();
			} else {
				System.out.println("Parsing " + anExpression + " has failed as expected: " + e.getMessage());
			}
			return null;
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

	public void testBindingValue() {
		tryToParse("foo", "foo", BindingValueAsExpression.class, null, false);
	}

	public void testBindingValue2() {
		tryToParse("foo_foo2", "foo_foo2", BindingValueAsExpression.class, null, false);
	}

	public void testBindingValue3() {
		tryToParse("foo.foo2.foo3", "foo.foo2.foo3", BindingValueAsExpression.class, null, false);
	}

	public void testBindingValue4() {
		tryToParse("method(1)", "method(1)", BindingValueAsExpression.class, null, false);
	}

	public void testBindingValue5() {
		tryToParse("a.b.c.method(1)", "a.b.c.method(1)", BindingValueAsExpression.class, null, false);
	}

	public void testBindingValue6() {
		tryToParse("this.is.a(1,2+3,7.8,'foo').little.test(1)", "this.is.a(1,5,7.8,\"foo\").little.test(1)",
				BindingValueAsExpression.class, null, false);
	}

	public void testNumericValue1() {
		tryToParse("34", "34", IntegerConstant.class, 34, false);
	}

	public void testNumericValue2() {
		tryToParse("7.8", "7.8", FloatConstant.class, 7.8, false);
	}

	public void testNumericValue3() {
		tryToParse("1.876E12", "1.876E12", FloatConstant.class, 1.876E12, false);
	}

	public void testNumericValue4() {
		tryToParse("0.876e-9", "8.76E-10", FloatConstant.class, 8.76E-10, false);
	}

	public void testNumericValue5() {
		tryToParse("-89", "-89", IntegerConstant.class, -89, false);
	}

	public void testNumericValue6() {
		tryToParse("-89.7856", "-89.7856", FloatConstant.class, -89.7856, false);
	}

	public void testNumericValue7() {
		tryToParse("1+1", "2", BinaryOperatorExpression.class, 2, false);
	}

	public void testNumericValue8() {
		tryToParse("1+(2*7-9)", "6", BinaryOperatorExpression.class, 6, false);
	}

	public void testNumericValue9() {
		tryToParse("1+((298*7.1e-3)-9)", "-5.8842", BinaryOperatorExpression.class, -5.8842, false);
	}

	public void testStringValue1() {
		tryToParse("\"foo1\"", "\"foo1\"", StringConstant.class, "foo1", false);
	}

	public void testStringValue2() {
		tryToParse("'foo1'", "\"foo1\"", StringConstant.class, "foo1", false);
	}

	public void testStringValue3() {
		tryToParse("\"foo1\"+\"foo2\"", "\"foo1foo2\"", BinaryOperatorExpression.class, "foo1foo2", false);
	}

	public void testStringValue4() {
		tryToParse("\"foo1\"+'and'+\"foo2\"", "\"foo1andfoo2\"", BinaryOperatorExpression.class, "foo1andfoo2", false);
	}

	public void testExpression1() {
		tryToParse("machin+1", "(machin + 1)", BinaryOperatorExpression.class, null, false);
	}

	public void testExpression2() {
		tryToParse("machin+1*6-8/7+bidule", "(((machin + 6) - 1.1428571428571428) + bidule)", BinaryOperatorExpression.class, null, false);
	}

	public void testExpression3() {
		tryToParse("7-x-(-x-6-8*2)", "((7 - x) - (((-(x)) - 6) - 16))", BinaryOperatorExpression.class, null, false);
	}

	public void testExpression4() {
		tryToParse("1+function(test,4<7-x)", "(1 + function(test,(4 < (7 - x))))", BinaryOperatorExpression.class, null, false);
	}

	public void testTrigonometricComputing1() {
		tryToParse("sin(-pi/2)", "-1.0", UnaryOperatorExpression.class, -1, false);
	}

	public void testTrigonometricComputing2() {
		tryToParse("-atan(2)", "-1.1071487177940904", UnaryOperatorExpression.class, -1.1071487177940904, false);
	}

	public void testTrigonometricComputing3() {
		tryToParse("-(atan(-pi/2)*(3-5*pi/7+8/9))", "1.651284257012876", UnaryOperatorExpression.class, 1.651284257012876, false);
	}

	public void testTrigonometricComputing4() {
		tryToParse("-cos(atan(-pi/2)*(3-5*pi/7+8/9))", "0.08040105411083133", UnaryOperatorExpression.class, 0.08040105411083133, false);
	}

	public void testEquality() {
		Expression e = tryToParse("a==b", "(a = b)", BinaryOperatorExpression.class, null, false);
		assertEquals(BooleanBinaryOperator.EQUALS, ((BinaryOperatorExpression) e).getOperator());
	}

	public void testEquality2() {
		tryToParse("binding1.a.b == binding2.a.b*7", "(binding1.a.b = (binding2.a.b * 7))", BinaryOperatorExpression.class, null, false);
	}

	public void testOr1() {
		Expression e = tryToParse("a|b", "(a | b)", BinaryOperatorExpression.class, null, false);
		assertEquals(BooleanBinaryOperator.OR, ((BinaryOperatorExpression) e).getOperator());
	}

	public void testOr2() {
		Expression e = tryToParse("a||b", "(a | b)", BinaryOperatorExpression.class, null, false);
		assertEquals(BooleanBinaryOperator.OR, ((BinaryOperatorExpression) e).getOperator());
	}

	public void testAnd1() {
		Expression e = tryToParse("a&b", "(a & b)", BinaryOperatorExpression.class, null, false);
		assertEquals(BooleanBinaryOperator.AND, ((BinaryOperatorExpression) e).getOperator());
	}

	public void testAnd2() {
		Expression e = tryToParse("a&&b", "(a & b)", BinaryOperatorExpression.class, null, false);
		assertEquals(BooleanBinaryOperator.AND, ((BinaryOperatorExpression) e).getOperator());
	}

	public void testBoolean1() {
		tryToParse("false", "false", BooleanConstant.FALSE.getClass(), false, false);
	}

	public void testBoolean2() {
		tryToParse("true", "true", BooleanConstant.TRUE.getClass(), true, false);
	}

	public void testBoolean3() {
		tryToParse("false && true", "false", BinaryOperatorExpression.class, false, false);
	}

	public void testBooleanExpression1() {
		tryToParse("!a&&b", "((!(a)) & b)", BinaryOperatorExpression.class, null, false);
	}

	public void testPi() {
		tryToParse("pi", "3.141592653589793", FloatSymbolicConstant.class, null, false);
	}

	public void testPi2() {
		tryToParse("-pi/2", "-1.5707963267948966", BinaryOperatorExpression.class, null, false);
	}

	public void testComplexCall() {
		tryToParse("testFunction(-pi/2,7.8,1-9*7/9,aVariable,foo1+foo2,e)",
				"testFunction(-1.5707963267948966,7.8,-6.0,aVariable,(foo1 + foo2),e)", BindingValueAsExpression.class, null, false);
	}

	public void testImbricatedCall() {
		tryToParse("function1(function2(8+1,9,10-1))", "function1(function2(9,9,9))", BindingValueAsExpression.class, null, false);
	}

	public void testEmptyCall() {
		tryToParse("function1()", "function1()", BindingValueAsExpression.class, null, false);
	}

	public void testComplexBooleanExpression() {
		tryToParse("a && (c || d && (!f)) ||b", "((a & (c | (d & (!(f))))) | b)", BinaryOperatorExpression.class, null, false);
	}

	public void testArithmeticNumberComparison1() {
		tryToParse("1 < 2", "true", BinaryOperatorExpression.class, true, false);
	}

	public void testArithmeticNumberComparison2() {
		tryToParse("0.1109 < 1.1108E-03", "false", BinaryOperatorExpression.class, false, false);
	}

	public void testStringConcatenation() {
		tryToParse("\"a + ( 2 + b )\"+2", "\"a + ( 2 + b )2\"", BinaryOperatorExpression.class, null, false);
	}

	public void testParsingError1() {
		tryToParse("a\"b", "", null, null, true);
	}

	public void testParsingError2() {
		tryToParse("a'b", "", null, null, true);
	}

	public void testParsingError3() {
		tryToParse("\"", "", null, null, true);
	}

	public void testParsingError4() {
		tryToParse("test23 ( fdfd + 1", "", null, null, true);
	}

	public void testParsingError5() {
		tryToParse("test24 [ fdfd + 1", "", null, null, true);
	}

	public void testIgnoredChars() {
		tryToParse(" test  \n\n", "test", BindingValueAsExpression.class, null, false);
	}

	public void testConditional1() {
		tryToParse("a?b:c", "(a ? b : c)", ConditionalExpression.class, null, false);
	}

	public void testConditional2() {
		tryToParse("a > 9 ?true:false", "((a > 9) ? true : false)", ConditionalExpression.class, null, false);
	}

	public void testConditional3() {
		tryToParse("a+1 > 10-7 ?8+4:5", "(((a + 1) > 3) ? 12 : 5)", ConditionalExpression.class, null, false);
	}

	public void testConditional4() {
		tryToParse("a+1 > (a?1:2) ?8+4:5", "(((a + 1) > (a ? 1 : 2)) ? 12 : 5)", ConditionalExpression.class, null, false);
	}

	public void testConditional5() {
		tryToParse("2 < 3 ? 4:2", "4", ConditionalExpression.class, 4, false);
	}

	public void testConditional6() {
		tryToParse("2 > 3 ? 4:2", "2", ConditionalExpression.class, 2, false);
	}

	public void testInvalidConditional() {
		tryToParse("2 > 3 ? 3", "", ConditionalExpression.class, null, true);
	}

	/*public void test25() throws java.text.ParseException {
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
	*/
}
