package org.openflexo.fib.widget;

import java.io.File;

import org.openflexo.fib.utils.FIBTestCase;

/**
 * Test that FIBs defined in tooling context are valid
 * 
 * @author sylvain
 * 
 */
public class TestToolingFibs extends FIBTestCase {

	public static void main(String[] args) {
		System.out.println(generateFIBTestCaseClass(new File(System.getProperty("user.dir") + "/src/main/resources/Fib"), "Fib/"));
	}

	public void testClassSelector() {
		validateFIB("Fib/ClassSelector.fib");
	}

	public void testLocalizedEditor() {
		validateFIB("Fib/LocalizedEditor.fib");
	}

	public void testLoggingViewer() {
		validateFIB("Fib/LoggingViewer.fib");
	}

}
