package org.openflexo.antar.binding;

import java.util.Hashtable;
import java.util.List;

import junit.framework.TestCase;

import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.Variable;
import org.openflexo.antar.expr.parser.ExpressionParser;
import org.openflexo.antar.expr.parser.ParseException;

public class TestExpression extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testVariable1() {
		try {
			List<Variable> vars = Expression.extractVariables("this+is+a+test");
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(new Variable("this")));
			assertTrue(vars.contains(new Variable("is")));
			assertTrue(vars.contains(new Variable("a")));
			assertTrue(vars.contains(new Variable("test")));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		} catch (TypeMismatchException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testVariable2() {
		try {
			List<Variable> vars = Expression.extractVariables("i+(am-a/test)+2");
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(new Variable("i")));
			assertTrue(vars.contains(new Variable("am")));
			assertTrue(vars.contains(new Variable("a")));
			assertTrue(vars.contains(new Variable("test")));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		} catch (TypeMismatchException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testVariable3() {
		try {
			List<Variable> vars = Expression.extractVariables("this.is.a.little.test+and+this+is.not()");
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(new Variable("this.is.a.little.test")));
			assertTrue(vars.contains(new Variable("and")));
			assertTrue(vars.contains(new Variable("this")));
			assertTrue(vars.contains(new Variable("is.not()")));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		} catch (TypeMismatchException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testPrimitive1() {
		try {
			List<Expression> vars = Expression.extractPrimitives("i+am+a+test");
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(new Variable("i")));
			assertTrue(vars.contains(new Variable("am")));
			assertTrue(vars.contains(new Variable("a")));
			assertTrue(vars.contains(new Variable("test")));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		} catch (TypeMismatchException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testPrimitive2() {
		try {
			List<Expression> vars = Expression.extractPrimitives("i+(am-a/test)+2");
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(new Variable("i")));
			assertTrue(vars.contains(new Variable("am")));
			assertTrue(vars.contains(new Variable("a")));
			assertTrue(vars.contains(new Variable("test")));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		} catch (TypeMismatchException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testPrimitive3() {
		try {
			List<Expression> vars = Expression.extractPrimitives("i.am.a.little.test+and+following+is.not()");
			System.out.println("Variables:" + vars);
			assertEquals(4, vars.size());
			assertTrue(vars.contains(new Variable("i.am.a.little.test")));
			assertTrue(vars.contains(new Variable("and")));
			assertTrue(vars.contains(new Variable("following")));
			assertTrue(vars.contains(new Variable("is.not()")));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		} catch (TypeMismatchException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testEvaluate1() {
		try {
			Expression e = ExpressionParser.parse("a+(b-c)/2");
			Hashtable<String, Object> variables = new Hashtable<String, Object>();
			variables.put("a", 1);
			variables.put("b", 10);
			variables.put("c", 3);
			Expression evaluated = e.evaluate(variables);
			System.out.println("evaluated=" + evaluated);
			assertEquals(ExpressionParser.parse("4.5"), evaluated);
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		} catch (TypeMismatchException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testEvaluate2() {
		try {
			Expression e = ExpressionParser.parse("a+(b-2-c)/2");
			Hashtable<String, Object> variables = new Hashtable<String, Object>();
			variables.put("a", 1);
			variables.put("b", 10);
			Expression evaluated = e.evaluate(variables);
			System.out.println("evaluated=" + evaluated);
			assertEquals(ExpressionParser.parse("1+(8-c)/2"), evaluated);
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		} catch (TypeMismatchException e) {
			e.printStackTrace();
			fail();
		}
	}
}
