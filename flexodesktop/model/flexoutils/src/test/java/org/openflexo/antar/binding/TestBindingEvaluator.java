package org.openflexo.antar.binding;

import junit.framework.TestCase;

public class TestBindingEvaluator extends TestCase {

	public static void genericTest(String bindingPath, Object object, Object expectedResult) {

		System.out.println("Evaluate " + bindingPath);
		Object evaluatedResult = BindingEvaluator.evaluateBinding(bindingPath, object);
		System.out.println("Evaluated as " + evaluatedResult);

		if (expectedResult instanceof Number) {
			if (evaluatedResult instanceof Number) {
				assertEquals(((Number) expectedResult).doubleValue(), ((Number) evaluatedResult).doubleValue());
			} else {
				fail("Evaluated value is not a number (expected: " + expectedResult + ") but " + evaluatedResult);
			}
		} else {
			assertEquals(expectedResult, evaluatedResult);
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
	}

}
