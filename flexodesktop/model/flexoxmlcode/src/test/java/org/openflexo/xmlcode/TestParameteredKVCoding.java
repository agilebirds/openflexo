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
package org.openflexo.xmlcode;

import java.util.Hashtable;

import org.openflexo.xmlcode.InvalidKeyValuePropertyException;
import org.openflexo.xmlcode.ParameteredKeyValueProperty;
import org.openflexo.xmlcode.SingleKeyValueProperty;

import junit.framework.TestCase;

public class TestParameteredKVCoding extends TestCase {

	public static class A
	{
		public B b = new B();
	}

	public static class B
	{
		public C c = new C();
	}

	public static class C
	{
		private int v = 8;
		public int getV() { return v; };
		public void setV(int i) { v = i;};
	}
	    
	public void testCompoundBindings()
	{
		A a = new A();
		SingleKeyValueProperty kvp = new SingleKeyValueProperty(A.class,"b.c.v",true);
		assertEquals(8,kvp.getObjectValue(a));
		kvp.setObjectValue(17,a);
		assertEquals(17,kvp.getObjectValue(a));
	}
	
	public void test1()
	{
		performTest("test1(1,2)", new TestClass(), 3, Integer.TYPE, true);
	}
	
	public void test2()
	{
		performTest("test1(3,a)", new TestClass(), 93, Integer.TYPE, true);
	}
	
	public void test3()
	{
		performTest("test2(3,'foo')", new TestClass(), 6, Integer.TYPE, true);
	}
	
	public void test4()
	{
		performTest("test2(2,f)", new TestClass(), 97, Integer.TYPE, true);
	}
	
	public void test5()
	{
		performTest("test3(false,1,1.8,'foo')", new TestClass(), "foo1.81false", String.class, true);
	}
	
	public void test6()
	{
		performTest("test4(7,true).test5.result", new TestClass(), "test5", String.class, false);
	}
	
	public void test7()
	{
		performTest("t.test4(7,true).test7(1,false).result", new TestClass(), "test7-1-false", String.class, false);
	}
	
	public void test8()
	{
		performTest("t.test4(3.141592654,false).test6(false,1,test4(2,false),test4(2,false).test5)", new TestClass(), 4, Integer.TYPE, true);
	}
	
	public void test9()
	{
		performTest("t.test4(f,true).test6(false,test9(a),test4(7,true),test4(2,false).test7(test9(2),true))", new TestClass(), 8109, Integer.TYPE, true);
        //        Test2Class(9.8,true).test6(false,90*90,Test2Class(7,true),Test2Class(7,false).test7(180,true))
        //        Test2Class(9.8,true).test6(false,8100,Test2Class(7,true),Test3Class("test7-180-true"))
        //        8100+9
	}
	
	public void test10()
	{
		try {
			ParameteredKeyValueProperty property = new ParameteredKeyValueProperty(TestClass.class,"test8(x)",false);
			fail("This property should not be instanciable");
		}
		catch (InvalidKeyValuePropertyException e) {
			assertTrue(e.getMessage().contains("No public field x found"));
		}
 	}
	
	public void test11()
	{
		try {
			ParameteredKeyValueProperty property = new ParameteredKeyValueProperty(TestClass.class,"test8(1)",false);
			fail("This property should not be instanciable");
		}
		catch (InvalidKeyValuePropertyException e) {
			assertTrue(e.getMessage().contains("Ambigous methods found"));
		}
 	}
	
    private static void performTest (String propertyName, Object anObject, Object expectedResult, Class expectedType, boolean isParametered)
    {
    	if (ParameteredKeyValueProperty.isParameteredKeyValuePropertyPattern(propertyName)) {
    		assertTrue("Parametered key-value property",isParametered);
        	System.out.println(">>>>>>>>>>>>>>>>> "+propertyName+" >>>>>>>>> PARAMETERED");
    		ParameteredKeyValueProperty property = new ParameteredKeyValueProperty(anObject.getClass(),propertyName,false);
    		System.out.println("ParameteredKeyValueProperty, result="+property.getObjectValue(anObject));
       		assertEquals(expectedType,property.getType());
       		assertEquals(expectedResult,property.getObjectValue(anObject));
    	}
    	else {
    		assertFalse("Non-parametered key-value property",isParametered);
    		System.out.println(">>>>>>>>>>>>>>>>> "+propertyName+" >>>>>>>>> NORMAL");
    		SingleKeyValueProperty property = new SingleKeyValueProperty(anObject.getClass(),propertyName,false);
    		System.out.println("SingleKeyValueProperty, result="+property.getObjectValue(anObject));
    		assertEquals(expectedType,property.getType());
    		assertEquals(expectedResult,property.getObjectValue(anObject));
    	}
     }
    
 
    public static class TestClass
    {
    	public int a = 90;
    	public String b;
       	public Float f = 9.8754f;
       	public int test1(int i,int j) { return i+j; }
       	public int test2(int i,String a) { return i+a.length(); }
      	public int test2(int i,Double a) { return (int)Math.pow(a, i); }
      	public int getTest2(int i,Float a) { return 2; }
       	public String test3(boolean a, Integer b, Double d, String s) { return s+d+b+a; }
       	public TestClass2 test4(float i, Boolean b) { return new TestClass2(i,b); }
       	public TestClass t = this;
       	public int test8(int i) { return a*i; }
      	public String getTest8(long a) { return ""; }
      	public int test9(long i) { return (int)(a*i); }
    }

    public static class TestClass2
    {
    	public double d;
       	public boolean b;
       	public TestClass2(double d, boolean b) { this.d = d; this.b = b; }
    	public TestClass3 test5 = new TestClass3("test5");
      	public int test6(boolean a, Integer i, TestClass2 t2, TestClass3 t3) { return i+(int)d; }
       	public TestClass3 test7(int i, Boolean b) { return new TestClass3("test7-"+i+"-"+b); }
    }

    public static class TestClass3
    {
    	public TestClass3 (String s) { result = s; }
    	public String result;
    }
    
    public static class TestClass4
    {
    	public static final String BPE = "bpe";
    	public static final String SWL = "swl";
    	private Hashtable<String,Integer> posX;
    	public TestClass4 (int bpeValue, int swlValue) { 
    		posX = new Hashtable<String,Integer>(); 
    		posX.put(BPE,bpeValue);
       		posX.put(SWL,swlValue);
    	}
    	public int getPosX(String context) { 
    		//System.out.println("posX="+posX);
    		//System.out.println("context="+context);
    		return posX.get(context); 
    	}
    	public void setPosX(int value, String context) { posX.put(context,value); }
    	public void setPosX(String value, String context) { /* Just to fool people */ }
    }
    
    public static class TestClass5
    {
    	public static final String MY_CONTEXT = TestClass4.BPE;
    	public TestClass4 c4 = new TestClass4(8,90);
   }
    
 	public void test12()
	{
		TestClass4 testValue = new TestClass4(7,89);
		
		ParameteredKeyValueProperty posx_bpe = new ParameteredKeyValueProperty(TestClass4.class,"posX('bpe')",true);
		ParameteredKeyValueProperty posx_swl = new ParameteredKeyValueProperty(TestClass4.class,"posX('swl')",true);
		assertEquals(7, posx_bpe.getObjectValue(testValue));
		assertEquals(89, posx_swl.getObjectValue(testValue));

		ParameteredKeyValueProperty static_posx_bpe = new ParameteredKeyValueProperty(TestClass4.class,"posX(BPE)",true);
		ParameteredKeyValueProperty static_posx_swl = new ParameteredKeyValueProperty(TestClass4.class,"posX(SWL)",true);
		assertEquals(7, static_posx_bpe.getObjectValue(testValue));
		assertEquals(89, static_posx_swl.getObjectValue(testValue));
		
		posx_bpe.setObjectValue(18,testValue);
		assertEquals(18, posx_bpe.getObjectValue(testValue));
		
		posx_swl.setObjectValue(890,testValue);
		assertEquals(890, posx_swl.getObjectValue(testValue));
		
		static_posx_bpe.setObjectValue(45,testValue);
		assertEquals(45, static_posx_bpe.getObjectValue(testValue));
		
		static_posx_swl.setObjectValue(17,testValue);
		assertEquals(17, static_posx_swl.getObjectValue(testValue));
		
	}
	
 	public void test13()
	{
 		TestClass5 testValue = new TestClass5();
		ParameteredKeyValueProperty posx_bpe = new ParameteredKeyValueProperty(TestClass5.class,"c4.posX(MY_CONTEXT)",true);
		assertEquals(8, posx_bpe.getObjectValue(testValue));
		posx_bpe.setObjectValue(78,testValue);
		assertEquals(78, posx_bpe.getObjectValue(testValue));
	}


}
