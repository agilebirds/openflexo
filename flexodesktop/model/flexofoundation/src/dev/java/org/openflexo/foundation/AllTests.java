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
package org.openflexo.foundation;

import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openflexo.diff.TestDiff;
import org.openflexo.diff.merge.TestMerge;
import org.openflexo.diff.merge.TestMerge2;
import org.openflexo.foundation.cg.TestCGFoundation;
import org.openflexo.foundation.dkv.TestPopulateDKV;
import org.openflexo.foundation.dm.TestBinding;
import org.openflexo.foundation.dm.TestDMType;
import org.openflexo.foundation.dm.TestLoadJar;
import org.openflexo.foundation.dm.eo.model.TestEOModelCreation;
import org.openflexo.foundation.dm.eo.model.TestEOModelLoad;
import org.openflexo.foundation.ie.TestCreateComponent;
import org.openflexo.foundation.ie.menu.action.TestMenu;
import org.openflexo.foundation.rm.TestRM;
import org.openflexo.foundation.wkf.TestDropWKFElement;
import org.openflexo.utils.UtilsTestSuite;
import org.openflexo.xmlcode.examples.XMLCoDeTestSuite;

/**
 * @author gpolet
 * 
 */
public class AllTests {

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
		TestSuite suite = new TestSuite("Test for FlexoFoundation model");

		// addStaticTestCase(TestXMLMappings.class, suite);
		addStaticTestCase(TestEOModelCreation.class, suite);
		addStaticTestCase(TestEOModelLoad.class, suite);
		addStaticTestCase(TestRM.class, suite);
		addStaticTestCase(TestCreateComponent.class, suite);
		addStaticTestCase(TestMenu.class, suite);
		addStaticTestCase(TestDiff.class, suite);
		addStaticTestCase(TestMerge.class, suite);
		addStaticTestCase(TestMerge2.class, suite);
		addStaticTestCase(TestLoadJar.class, suite);
		addStaticTestCase(TestDropWKFElement.class, suite);
		addStaticTestCase(TestBinding.class, suite);
		addStaticTestCase(TestDMType.class, suite);
		addStaticTestCase(TestPopulateDKV.class, suite);
		addStaticTestCase(TestCGFoundation.class, suite);
		// addStaticTestCase(ImportedObjectTestSuite.class, suite);
		addTestCaseDynamic("org.openflexo.fge.geom.TestGeom", "TestGraphicalEngineGeometry", suite);
		addTestCaseDynamic("org.openflexo.fge.geom.TestRectPolylin", "TestGraphicalEngineGeometryForRectPolylin", suite);
		addTestCaseDynamic("org.openflexo.fge.geom.TestUnion", "TestGraphicalEngineGeometry", suite);
		addTestCaseDynamic("org.openflexo.fge.geom.TestOperations", "TestGraphicalEngineGeometry", suite);
		addTestCaseDynamic("org.openflexo.fge.geom.TestArc", "TestGraphicalEngineGeometry", suite);
		addTestCaseDynamic("org.openflexo.xml.diff3.XMLDiff3Test", "FlexoXMLDiff", suite);
		addTestCaseDynamic("org.openflexo.generator.TestCG", "FlexoCodeGenerator", suite);
		addTestCaseDynamic("org.openflexo.generator.TestCG2", "FlexoCodeGenerator", suite);
		addTestCaseDynamic("org.openflexo.generator.TestRoundTrip", "FlexoCodeGenerator", suite);
		addTestCaseDynamic("org.openflexo.fps.TestFPS", "FlexoProjectSharing", suite);
		addTestCaseDynamic("org.openflexo.fps.TestFPS2", "FlexoProjectSharing", suite);
		addTestCaseDynamic("org.openflexo.rational.TestRRImport", "FlexoCrazyBean", suite);
		// addTestCaseDynamic("org.openflexo.ie.view.controller.TestIEController","FlexoInterfaceEditor",suite);
		// addTestCaseDynamic("org.openflexo.wkf.view.controller.TestWKFController","FlexoWorkflowEditor",suite);
		// addTestCaseDynamic("org.openflexo.dm.view.controller.TestDMController","FlexoDataModelEditor",suite);
		addTestCaseDynamic("org.openflexo.xml.diff2.DocumentsMappingTest", "FlexoXMLDiff", suite);
		addTestCaseDynamic("org.openflexo.generator.TestWar", "FlexoCodeGenerator", suite);
		// XMLCoDe breaks FPS Tests so it is done after all tests. The cause of this bug is only performed in the XMLCoDe tests so for now I
		// fix it by doing this.
		addStaticTestCase(XMLCoDeTestSuite.class, suite);
		addStaticTestCase(UtilsTestSuite.class, suite);
		return suite;
	}

}
