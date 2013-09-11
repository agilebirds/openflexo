package org.openflexo.ve.fib;

import java.io.File;

import org.openflexo.fib.FIBTestCase;

public class TestVEInspectors extends FIBTestCase {

	public static void main(String[] args) {
		System.out.println(generateInspectorTestCaseClass(new File(System.getProperty("user.dir") + "/src/main/resources/Inspectors/VE"),
				"Inspectors/VE/"));
	}

	public void testDiagramInspector() {
		validateFIB("Inspectors/VE/Diagram.inspector");
	}

	public void testViewInspector() {
		validateFIB("Inspectors/VE/View.inspector");
	}

	public void testViewConnectorInspector() {
		validateFIB("Inspectors/VE/ViewConnector.inspector");
	}

	public void testViewLibraryInspector() {
		validateFIB("Inspectors/VE/ViewLibrary.inspector");
	}

	public void testViewObjectInspector() {
		validateFIB("Inspectors/VE/ViewObject.inspector");
	}

	public void testViewShapeInspector() {
		validateFIB("Inspectors/VE/ViewShape.inspector");
	}

	public void testVirtualModelInstanceInspector() {
		validateFIB("Inspectors/VE/VirtualModelInstance.inspector");
	}
}
