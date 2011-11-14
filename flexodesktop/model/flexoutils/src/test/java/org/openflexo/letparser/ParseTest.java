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
package org.openflexo.letparser;

import junit.framework.TestCase;

public class ParseTest extends TestCase {

	public ParseTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test0() {
		try {
			System.out.println("START Test0");
			String test = "machin";
			System.out.println(test);
			Token parsed = Parser.parse(test);
			System.out.println("Test0: Parsed: " + parsed.getClass().getName() + " : " + parsed);
			System.out.println("Normalized=" + parsed.getSerializationValue());
			test = "78";
			System.out.println(test);
			parsed = Parser.parse(test);
			System.out.println("Test0: Parsed: " + parsed.getClass().getName() + " : " + parsed);
			System.out.println("Normalized=" + parsed.getSerializationValue());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test1() {
		try {
			System.out.println("START Test1");
			String test = "(machin = 8 &!& ((truc=bidule) || (bidule=7)))";
			System.out.println(test);
			Token parsed = Parser.parse(test);
			System.out.println("Test1: Parsed: " + parsed.getClass().getName() + " : " + parsed);
			System.out.println("Normalized=" + parsed.getSerializationValue());
			fail();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void test2() {
		try {
			System.out.println("START Test2");
			String test = "(machin = 8 hop && ((truc=bidule) || (bidule=7)))";
			System.out.println(test);
			Token parsed = Parser.parse(test);
			System.out.println("Test2: Parsed: " + parsed.getClass().getName() + " : " + parsed);
			System.out.println("Normalized=" + parsed.getSerializationValue());
			fail();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void test3() {
		try {
			System.out.println("START Test3");
			String test = "(machin_chose = 8 AND ((truc=bidule) || (bidule=7.87)))";
			System.out.println(test);
			Token parsed = Parser.parse(test);
			System.out.println("Test3: Parsed: " + parsed.getClass().getName() + " : " + parsed);
			System.out.println("Normalized=" + parsed.getSerializationValue());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test4() {
		try {
			System.out.println("START Test4");
			String test = "(machin = true && machin = 8 && ((truc=bidule) || (bidule=7) || (bidule=false)))";
			System.out.println(test);
			Token parsed = Parser.parse(test);
			System.out.println("Test4: Parsed: " + parsed.getClass().getName() + " : " + parsed);
			System.out.println("Normalized=" + parsed.getSerializationValue());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test5() {
		try {
			System.out.println("START Test5");
			String test = "truc(bordel,$3," + '"' + "hop" + '"' + ",'a',false))";
			System.out.println(test);
			Token parsed = Parser.parse(test);
			System.out.println("Test5: Parsed: " + parsed.getClass().getName() + " : " + parsed);
			System.out.println("Normalized=" + parsed.getSerializationValue());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test6() {
		try {
			System.out.println("START Test6");
			String test = "truc(bordel,,machin,chose)";
			System.out.println(test);
			Token parsed = Parser.parse(test);
			System.out.println("Test6: Parsed: " + parsed.getClass().getName() + " : " + parsed);
			System.out.println("Normalized=" + parsed.getSerializationValue());
			fail();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void test7() {
		try {
			System.out.println("START Test7");
			String test = "truc(0,chose(chose2($3," + '"' + "hop" + '"' + ",'a',false)),machin,chose,2,test(test1,test2))";
			System.out.println(test);
			Token parsed = Parser.parse(test);
			System.out.println("Test7: Parsed: " + parsed.getClass().getName() + " : " + parsed);
			System.out.println("Normalized=" + parsed.getSerializationValue());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test8() {
		try {
			System.out.println("START Test8");
			String test = "truc(bordel.truc.hop,chose.truc=machin.x.a)";
			System.out.println(test);
			Token parsed = Parser.parse(test);
			System.out.println("Test8: Parsed: " + parsed.getClass().getName() + " : " + parsed);
			System.out.println("Normalized=" + parsed.getSerializationValue());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test9() {
		try {
			System.out.println("START Test9");
			String test = "truc(bordel,$3,'a',false,(machin_chose = 8 AND ((truc=bidule) || (bidule(chose,machin)=7.87)))))";
			System.out.println(test);
			Token parsed = Parser.parse(test);
			System.out.println("Test9: Parsed: " + parsed.getClass().getName() + " : " + parsed);
			System.out.println("Normalized=" + parsed.getSerializationValue());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test10() {
		try {
			System.out.println("START Test10");
			String test = "(machin_chose = 8 AND ((truc=bidule(chose,truc,$3)) || (bidule=7.87)))";
			System.out.println(test);
			Token parsed = Parser.parse(test);
			System.out.println("Test10: Parsed: " + parsed.getClass().getName() + " : " + parsed);
			System.out.println("Normalized=" + parsed.getSerializationValue());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

}
