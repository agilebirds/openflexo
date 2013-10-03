package org.openflexo.ve.fib;

import java.io.File;

import org.openflexo.fib.FIBTestCase;

public class TestVEFibs extends FIBTestCase {

	public static void main(String[] args) {
		System.out.println(generateFIBTestCaseClass(new File(System.getProperty("user.dir") + "/src/main/resources/Fib"), "Fib/"));
	}

	public void testOntologyView() {
		validateFIB("Fib/OntologyView.fib");
	}

	public void testVirtualModelInstanceView() {
		validateFIB("Fib/VirtualModelInstanceView.fib");
	}

}
