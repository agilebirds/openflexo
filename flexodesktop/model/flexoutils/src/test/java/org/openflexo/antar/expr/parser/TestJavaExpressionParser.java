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

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.openflexo.antar.expr.DefaultExpressionParser;
import org.openflexo.antar.expr.DefaultExpressionPrettyPrinter;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.oldparser.ParseException;

public class TestJavaExpressionParser extends TestCase {

	private DefaultExpressionParser parser;
	private DefaultExpressionPrettyPrinter prettyPrinter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		parser = new DefaultExpressionParser();
		prettyPrinter = new DefaultExpressionPrettyPrinter();
	}

	private void tryToParse(String aString, String expectedEvaluatedExpression, boolean shouldFail) {
		try {
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
		}
	}

	public void test0() {
		tryToParse("machin", "machin", false);
	}

	public void test1() {
		tryToParse("34", "34", false);
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

	public void test5() {
		tryToParse("sin(-pi/2)", "-1.0", false);
	}

	public void test6() {
		tryToParse("!a&&b", "((!(a)) & b)", false);
	}

	public void test7() {
		tryToParse("cos(-pi/2,7)", "", true);
	}

	public void test8() {
		tryToParse("testFunction(-pi/2,7.8,1-9*7/9,aVariable,\"foo1\"+'&'+\"foo2\",false && true,'e')",
				"testFunction(-1.5707963267948966,7.8,-6.0,aVariable,\"foo1&foo2\",false,\"e\")", false);
	}

	public void test9() {
		tryToParse("-atan(-pi/2)*(3-5*pi/7+8/9)", "1.651284257012876", false);
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

	/*public void test0()
	{
		try {
			System.out.println ("START Test0");
			String test = "machin";
			System.out.println (test);
			Token parsed = ExpressionParser.parse(test);
			System.out.println ("Test0: Parsed: "+parsed.getClass().getName()+" : "+parsed);
			System.out.println ("Normalized="+parsed.getSerializationValue());
			test = "78";
			System.out.println (test);
			parsed = ExpressionParser.parse(test);
			System.out.println ("Test0: Parsed: "+parsed.getClass().getName()+" : "+parsed);
			System.out.println ("Normalized="+parsed.getSerializationValue());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test1()
	{
		try {
			System.out.println ("START Test1");
			String test = "(machin = 8 &!& ((truc=bidule) || (bidule=7)))";
			System.out.println (test);
			Token parsed = ExpressionParser.parse(test);
			System.out.println ("Test1: Parsed: "+parsed.getClass().getName()+" : "+parsed);
			System.out.println ("Normalized="+parsed.getSerializationValue());
			fail();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void test2()
	{
		try {
			System.out.println ("START Test2");
			String test = "(machin = 8 hop && ((truc=bidule) || (bidule=7)))";
			System.out.println (test);
			Token parsed = ExpressionParser.parse(test);
			System.out.println ("Test2: Parsed: "+parsed.getClass().getName()+" : "+parsed);
			System.out.println ("Normalized="+parsed.getSerializationValue());
			fail();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void test3()
	{
		try {
			System.out.println ("START Test3");
			String test = "(machin_chose = 8 AND ((truc=bidule) || (bidule=7.87)))";
			System.out.println (test);
			Token parsed = ExpressionParser.parse(test);
			System.out.println ("Test3: Parsed: "+parsed.getClass().getName()+" : "+parsed);
			System.out.println ("Normalized="+parsed.getSerializationValue());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test4()
	{
		try {
			System.out.println ("START Test4");
			String test = "(machin = true && machin = 8 && ((truc=bidule) || (bidule=7) || (bidule=false)))";
			System.out.println (test);
			Token parsed = ExpressionParser.parse(test);
			System.out.println ("Test4: Parsed: "+parsed.getClass().getName()+" : "+parsed);
			System.out.println ("Normalized="+parsed.getSerializationValue());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test5()
	{
		try {
			System.out.println ("START Test5");
			String test = "truc(bordel,$3,"+'"'+"hop"+'"'+",'a',false))";
			System.out.println (test);
			Token parsed = ExpressionParser.parse(test);
			System.out.println ("Test5: Parsed: "+parsed.getClass().getName()+" : "+parsed);
			System.out.println ("Normalized="+parsed.getSerializationValue());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test6()
	{
		try {
			System.out.println ("START Test6");
			String test = "truc(bordel,,machin,chose)";
			System.out.println (test);
			Token parsed = ExpressionParser.parse(test);
			System.out.println ("Test6: Parsed: "+parsed.getClass().getName()+" : "+parsed);
			System.out.println ("Normalized="+parsed.getSerializationValue());
			fail();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void test7()
	{
		try {
			System.out.println ("START Test7");
			String test = "truc(0,chose(chose2($3,"+'"'+"hop"+'"'+",'a',false)),machin,chose,2,test(test1,test2))";
			System.out.println (test);
			Token parsed = ExpressionParser.parse(test);
			System.out.println ("Test7: Parsed: "+parsed.getClass().getName()+" : "+parsed);
			System.out.println ("Normalized="+parsed.getSerializationValue());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test8()
	{
		try {
			System.out.println ("START Test8");
			String test = "truc(bordel.truc.hop,chose.truc=machin.x.a)";
			System.out.println (test);
			Token parsed = ExpressionParser.parse(test);
			System.out.println ("Test8: Parsed: "+parsed.getClass().getName()+" : "+parsed);
			System.out.println ("Normalized="+parsed.getSerializationValue());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test9()
	{
		try {
			System.out.println ("START Test9");
			String test = "truc(bordel,$3,'a',false,(machin_chose = 8 AND ((truc=bidule) || (bidule(chose,machin)=7.87)))))";
			System.out.println (test);
			Token parsed = ExpressionParser.parse(test);
			System.out.println ("Test9: Parsed: "+parsed.getClass().getName()+" : "+parsed);
			System.out.println ("Normalized="+parsed.getSerializationValue());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test10()
	{
		try {
			System.out.println ("START Test10");
			String test = "(machin_chose = 8 AND ((truc=bidule(chose,truc,$3)) || (bidule=7.87)))";
			System.out.println (test);
			Token parsed = ExpressionParser.parse(test);
			System.out.println ("Test10: Parsed: "+parsed.getClass().getName()+" : "+parsed);
			System.out.println ("Normalized="+parsed.getSerializationValue());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}*/

}
