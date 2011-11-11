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
package org.openflexo.fge.geom;

import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author gpolet
 * 
 */
public class AllGeometryTests {

	private static void addTestCaseDynamic(String testClassName, String flexoProjectName, TestSuite suite) {
		try {
			Class testClass = Class.forName(testClassName);
			addStaticTestCase(testClass, suite);
		} catch (ClassNotFoundException e) {
			System.out.println("WARNING : class '" + testClassName + "' not found in classpath and will be skipped.");
			System.out.println("Add project " + flexoProjectName + " to your runtime unit test classpath to run this test class :"
					+ testClassName);
		}
	}

	private static void addStaticTestCase(Class testClass, TestSuite suite) {
		try {
			suite.addTest((Test) testClass.getMethod("suite").invoke(testClass));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			suite.addTestSuite(testClass);
		}
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for FGE geometry layer");

		addStaticTestCase(TestGeom.class, suite);
		addStaticTestCase(TestOperations.class, suite);
		addStaticTestCase(TestRectPolylin.class, suite);
		addStaticTestCase(TestUnion.class, suite);
		addStaticTestCase(TestArc.class, suite);
		return suite;
	}

}
