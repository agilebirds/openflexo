package org.openflexo.fge.fib;

import java.io.File;

import org.openflexo.fib.utils.FIBTestCase;

public class TestDianaFibs extends FIBTestCase {

	public static void main(String[] args) {
		System.out.println(generateFIBTestCaseClass(new File(System.getProperty("user.dir") + "/src/main/resources/Fib"), "Fib/"));
	}

	public void testBackgroundStylePanel() {
		validateFIB("Fib/BackgroundStylePanel.fib");
	}

	public void testForegroundStylePanel() {
		validateFIB("Fib/ForegroundStylePanel.fib");
	}

	public void testShadowStylePanel() {
		validateFIB("Fib/ShadowStylePanel.fib");
	}

	public void testShapeSelectorPanel() {
		validateFIB("Fib/ShapeSelectorPanel.fib");
	}

	public void testTextStylePanel() {
		validateFIB("Fib/TextStylePanel.fib");
	}
}
