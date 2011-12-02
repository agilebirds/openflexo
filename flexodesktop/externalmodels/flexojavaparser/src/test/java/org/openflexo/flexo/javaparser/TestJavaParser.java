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
package org.openflexo.flexo.javaparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.openflexo.foundation.dm.DMType;
import org.openflexo.javaparser.FJPJavaClass;
import org.openflexo.javaparser.FJPJavaField;
import org.openflexo.javaparser.FJPJavaMethod;
import org.openflexo.javaparser.FJPJavaSource;
import org.openflexo.javaparser.JavaParser;
import org.openflexo.toolbox.FileResource;

import com.thoughtworks.qdox.model.ClassLibrary;

public class TestJavaParser extends TestCase {

	private static final Logger logger = Logger.getLogger(TestJavaParser.class.getPackage().getName());

	private static JavaParser parser = new JavaParser(new ClassLibrary(null));

	public void test1() {
		FJPJavaClass parsedClass = test(new FileResource("TestJavaParser/TestJava1.java"));
		assertEquals(parsedClass.getMethods().length, 0);
		assertEquals(parsedClass.getFields().length, 0);
		assertEquals(parsedClass.getNestedClasses().length, 0);
		assertNotNull(parsedClass.getJavadoc());
	}

	public void test2() throws ClassNotFoundException {
		FJPJavaClass parsedClass = test(new FileResource("TestJavaParser/TestJava2.java"));
		logger.info("parsedClass=" + parsedClass + " " + parsedClass.getName() + "\n");
		logger.info("parsedClass=" + parsedClass + " " + parsedClass.getJavadoc() + "\n");
		assertNotNull(parsedClass.getJavadoc());
		assertEquals(parsedClass.getMethods().length, 2);
		FJPJavaMethod method1 = parsedClass.getMethodBySignature("method1");
		assertNotNull(method1);
		assertTrue(method1.isStatic());
		assertNotNull(method1.getJavadoc());
		logger.info("Method1 javadoc " + method1.getJavadoc());
		logger.info("Method1 source code " + method1.getSourceCode());
		FJPJavaMethod method2 = parsedClass.getMethodBySignature("method2", DMType.makeUnresolvedDMType("java.lang.String"),
				DMType.makeUnresolvedDMType("boolean"));
		assertNotNull(method2);
		assertTrue(method2.isPrivate());
		assertTrue(method2.isSynchronized());
		assertFalse(method2.isPublic());
		assertFalse(method2.isProtected());
		assertTrue(method2.isPrivate());
		logger.info("Method2 javadoc " + method2.getJavadoc());
		logger.info("Method2 source code " + method2.getSourceCode());
		assertNull(method2.getJavadoc());
		assertEquals(parsedClass.getFields().length, 4);
		FJPJavaField field1 = parsedClass.getFieldByName("anInteger");
		assertNotNull(field1);
		assertNotNull(field1.getJavadoc());
		FJPJavaField field2 = parsedClass.getFieldByName("aString");
		logger.info("field2 javadoc " + field2.getJavadoc());
		assertNotNull(field2);
		assertTrue(field2.isPublic());
		assertTrue(field2.isStatic());
		assertTrue(field2.isFinal());
		assertNotNull(field2.getJavadoc());
		FJPJavaField field3 = parsedClass.getFieldByName("aFloat");
		assertNotNull(field3);
		assertNull(field3.getJavadoc());
		assertEquals(parsedClass.getNestedClasses().length, 0);
	}

	public void test3() throws ClassNotFoundException {
		FJPJavaClass parsedClass = test(new FileResource("TestJavaParser/TestJava3.java"));
		assertEquals(parsedClass.getMethods().length, 3);
		assertEquals(parsedClass.getFields().length, 5);
		assertEquals(parsedClass.getNestedClasses().length, 2);
		FJPJavaClass innerClass = parsedClass.getNestedClassByName("AnInnerClass");
		assertNotNull(innerClass);
		assertTrue(innerClass.isPublic());
		assertTrue(innerClass.isStatic());
		logger.info("innerClass javadoc " + innerClass.getJavadoc());
		assertNotNull(innerClass.getJavadoc());
		FJPJavaClass TESTJAVA1 = parsedClass.getClassByName("test.TestJava1");
		FJPJavaMethod method1 = innerClass.getMethodBySignature("aMethod", DMType.makeUnresolvedDMType(TESTJAVA1.asType().getValue()));
		assertNotNull(method1);
		assertNotNull(method1.getJavadoc());
		FJPJavaClass innerInterface = parsedClass.getNestedClassByName("AnInterface");
		assertNotNull(innerInterface);
		assertTrue(innerInterface.isInterface());
		logger.info("innerInterface javadoc " + innerInterface.getJavadoc());
		assertNotNull(innerInterface.getJavadoc());
	}

	public void test4() throws ClassNotFoundException {
		FJPJavaClass parsedClass = test(new FileResource("TestJavaParser/TestJava4.java"));
		assertEquals(parsedClass.getMethods().length, 5);
		assertEquals(parsedClass.getFields().length, 5);
		assertEquals(parsedClass.getNestedClasses().length, 3);
		FJPJavaClass innerClass = parsedClass.getNestedClassByName("AnInnerClass");
		assertNotNull(innerClass);
		assertTrue(innerClass.isPublic());
		assertTrue(innerClass.isStatic());
		logger.info("innerClass javadoc " + innerClass.getJavadoc());
		assertNotNull(innerClass.getJavadoc());
		FJPJavaClass TESTJAVA1 = parsedClass.getClassByName("test.TestJava1");
		FJPJavaMethod method1 = innerClass.getMethodBySignature("aMethod", DMType.makeUnresolvedDMType(TESTJAVA1.asType().getValue()));
		assertNotNull(method1);
		assertNotNull(method1.getJavadoc());
		FJPJavaClass innerInterface = parsedClass.getNestedClassByName("AnInterface");
		assertNotNull(innerInterface);
		assertTrue(innerInterface.isInterface());
		logger.info("innerInterface javadoc " + innerInterface.getJavadoc());
		assertNotNull(innerInterface.getJavadoc());
	}

	public void test5() throws ClassNotFoundException {
		test(new FileResource("TestJavaParser/TestJava5.java"));
	}

	private FJPJavaClass test(File aFile) {
		assertTrue(aFile.exists());

		assertTrue(aFile.exists());

		FJPJavaSource source = null;
		try {
			source = new FJPJavaSource(aFile, parser);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}

		assertEquals(source.getClasses().length, 1);

		return source.getRootClass();

	}

}
