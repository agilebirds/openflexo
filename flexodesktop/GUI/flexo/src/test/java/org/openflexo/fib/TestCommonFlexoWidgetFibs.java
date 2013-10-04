package org.openflexo.fib;

import org.openflexo.fib.utils.FIBTestCase;
import org.openflexo.toolbox.FileResource;

public class TestCommonFlexoWidgetFibs extends FIBTestCase {

	public static void main(String[] args) {
		System.out.println(generateFIBTestCaseClass(new FileResource("Fib/Widget"), "Fib/Widget/"));
	}

	public void testDeclareConnectorInEditionPatternDialog() {
		validateFIB("Fib/Widget/DeclareConnectorInEditionPatternDialog.fib");
	}

	public void testDeclareShapeInEditionPatternDialog() {
		validateFIB("Fib/Widget/DeclareShapeInEditionPatternDialog.fib");
	}

	public void testFIBDiagramPaletteBrowser() {
		validateFIB("Fib/Widget/FIBDiagramPaletteBrowser.fib");
	}

	public void testFIBExampleDiagramBrowser() {
		validateFIB("Fib/Widget/FIBExampleDiagramBrowser.fib");
	}

	public void testFIBViewPointBrowser() {
		validateFIB("Fib/Widget/FIBViewPointBrowser.fib");
	}

	public void testFIBViewPointLibraryBrowser() {
		validateFIB("Fib/Widget/FIBViewPointLibraryBrowser.fib");
	}

	public void testFIBVirtualModelBrowser() {
		validateFIB("Fib/Widget/FIBVirtualModelBrowser.fib");
	}

}
