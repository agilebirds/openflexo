package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;

import com.google.common.reflect.TypeToken;

public class TestBinding extends TestCase {

	private static final DefaultBindingFactory BINDING_FACTORY = new DefaultBindingFactory();
	private static final TestBindingContext BINDING_CONTEXT = new TestBindingContext();
	private static final TestBindingModel BINDING_MODEL = new TestBindingModel();

	public static class TestBindingContext implements Bindable, BindingEvaluationContext {

		public static String aString = "this is a test";
		public static boolean aBoolean = false;
		public static int anInt = 7;
		public static List<String> aList = new ArrayList<String>();

		static {
			aList.add("this");
			aList.add("is");
			aList.add("a");
			aList.add("test");
		}

		@Override
		public BindingFactory getBindingFactory() {
			return BINDING_FACTORY;
		}

		@Override
		public BindingModel getBindingModel() {
			return BINDING_MODEL;
		}

		@Override
		public Object getValue(BindingVariable variable) {

			System.out.println("Value for " + variable + " ?");

			if (variable.getVariableName().equals("aString")) {
				return aString;
			} else if (variable.getVariableName().equals("aBoolean")) {
				return aBoolean;
			} else if (variable.getVariableName().equals("anInt")) {
				return anInt;
			} else if (variable.getVariableName().equals("aList")) {
				return aList;
			}
			return null;
		}
	}

	// String aString;
	// Boolean aBoolean;
	// List<String> aList;
	public static class TestBindingModel extends BindingModel {
		public TestBindingModel() {
			super();
			addToBindingVariables(new BindingVariableImpl(BINDING_CONTEXT, "aString", String.class));
			addToBindingVariables(new BindingVariableImpl(BINDING_CONTEXT, "aBoolean", Boolean.TYPE));
			addToBindingVariables(new BindingVariableImpl(BINDING_CONTEXT, "anInt", Integer.TYPE));
			addToBindingVariables(new BindingVariableImpl(BINDING_CONTEXT, "aList", new TypeToken<List<String>>() {
			}.getType()));
		}
	}

	/*public static class TestObject implements Bindable, BindingEvaluationContext {

		private Object object;
		private BindingDefinition bindingDefinition;
		private BindingModel bindingModel;

		private BindingEvaluator(Object object) {
			this.object = object;
			bindingDefinition = new BindingDefinition("object", object.getClass(), BindingDefinitionType.GET, true);
			bindingModel = new BindingModel();
			bindingModel.addToBindingVariables(new BindingVariableImpl(this, "object", object.getClass()));
			BINDING_FACTORY.setBindable(this);
		}

		private static String normalizeBindingPath(String bindingPath) {
			DefaultExpressionParser parser = new DefaultExpressionParser();
			Expression expression = null;
			try {
				expression = ExpressionParser.parse(bindingPath);
				expression = expression.transform(new ExpressionTransformer() {
					@Override
					public Expression performTransformation(Expression e) throws TransformException {
						if (e instanceof BindingValueAsExpression) {
							BindingValueAsExpression bv = (BindingValueAsExpression) e;
							if (bv.getBindingPath().size() > 0) {
								AbstractBindingPathElement firstPathElement = bv.getBindingPath().get(0);
								if (!(firstPathElement instanceof NormalBindingPathElement)
										|| !((NormalBindingPathElement) firstPathElement).property.equals("object")) {
									bv.getBindingPath().add(0, new NormalBindingPathElement("object"));
								}
							}
							return bv;
						}
						return e;
					}
				});

				return expression.toString();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return expression.toString();
		}

		@Override
		public BindingModel getBindingModel() {
			return bindingModel;
		}

		@Override
		public BindingFactory getBindingFactory() {
			return BINDING_FACTORY;
		}

		@Override
		public Object getValue(BindingVariable variable) {
			return object;
		}

	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void test1() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("toString", thisIsATest, thisIsATest);
	}

	public void test2() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("toString()", thisIsATest, thisIsATest);
	}

	public void test3() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("substring(2,8)", thisIsATest, "llo wo");
	}

	public void test4() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("substring(2,5*2-2)", thisIsATest, "llo wo");
	}

	public void test5() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("toString()+toString()", thisIsATest, "Hello world, this is a testHello world, this is a test");
	}

	public void test6() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("toString()+' hash='+object.hashCode()", thisIsATest, thisIsATest + " hash=" + thisIsATest.hashCode());
	}

	public void test7() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("substring(0,5)+' '+substring(23,27).toUpperCase()", thisIsATest, "Hello TEST");
	}

	public void test8() {
		genericTest("object*2-7", 10, 13);
	}

	public void test9() {
		String thisIsATest = "Hello world, this is a test";
		genericTest("substring(3,length()-2)+' hash='+hashCode()", thisIsATest, "lo world, this is a te hash=" + thisIsATest.hashCode());
	}*/

	public static void genericTest(String bindingPath, Type expectedType, Object expectedResult) {

		System.out.println("Evaluate " + bindingPath);

		BINDING_FACTORY.setBindable(BINDING_CONTEXT);
		AbstractBinding binding = BINDING_FACTORY.convertFromString(bindingPath);
		binding.setBindingDefinition(new BindingDefinition("test", expectedType, BindingDefinitionType.GET, true));

		System.out.println("Parsed " + binding + " as " + binding.getClass());

		if (!binding.isBindingValid()) {
			fail(binding.invalidBindingReason());
		}

		Object evaluation = null;
		try {
			evaluation = binding.getBindingValue(BINDING_CONTEXT);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
			fail();
		} catch (NullReferenceException e) {
			e.printStackTrace();
			fail();
		}

		System.out.println("Evaluated as " + evaluation);

		System.out.println("expectedResult = " + expectedResult + " of " + expectedResult.getClass());
		System.out.println("evaluation = " + evaluation + " of " + evaluation.getClass());

		assertEquals(expectedResult, TypeUtils.castTo(evaluation, expectedType));

		/*Object evaluatedResult = null;
		try {
			evaluatedResult = BindingEvaluator.evaluateBinding(bindingPath, object);
		} catch (InvalidKeyValuePropertyException e) {
			fail();
		} catch (TypeMismatchException e) {
			fail();
		} catch (NullReferenceException e) {
			fail();
		}
		System.out.println("Evaluated as " + evaluatedResult);

		if (expectedResult instanceof Number) {
			if (evaluatedResult instanceof Number) {
				assertEquals(((Number) expectedResult).doubleValue(), ((Number) evaluatedResult).doubleValue());
			} else {
				fail("Evaluated value is not a number (expected: " + expectedResult + ") but " + evaluatedResult);
			}
		} else {
			assertEquals(expectedResult, evaluatedResult);
		}*/
	}

	public void test1() {
		genericTest("aString", String.class, "this is a test");
	}

	public void test2() {
		genericTest("aString.substring(5,7)", String.class, "is");
	}

	public void test3() {
		genericTest("aString.substring(anInt+3,anInt+7)", String.class, "test");
	}

	public void test4() {
		genericTest("aString.length", Integer.class, 14);
	}

	public void test5() {
		genericTest("aString.length+aList.size()", Integer.class, 18);
	}

	public void test6() {
		genericTest("aString.length > aList.size()", Boolean.class, true);
	}

	public void test7() {
		genericTest("aString.length > aList.size()", Boolean.TYPE, true);
	}

	public void test8() {
		genericTest("aString == null", Boolean.TYPE, false);
	}

	public void test9() {
		genericTest("aString == ''", Boolean.TYPE, false);
	}

	public void test10() {
		TestBindingContext.aString = "";
		genericTest("aString == ''", Boolean.TYPE, true);
	}

	public void test11() {
		TestBindingContext.aString = "foo";
		genericTest("aString+((aString != 'foo' ? ('=' + aString) : ''))", String.class, "foo");
		TestBindingContext.aString = "foo2";
		genericTest("aString+((aString != 'foo' ? ('=' + aString) : ''))", String.class, "foo2=foo2");
	}

	public void test12() {
		TestBindingContext.aString = "";
		genericTest("anInt > 2 ? 'anInt > 2' : 'anInt<=2' ", String.class, "anInt > 2");
	}

	public void test13() {
		genericTest("aString != null", Boolean.TYPE, true);
	}
}
