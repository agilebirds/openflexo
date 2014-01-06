package org.openflexo.fib;

import org.openflexo.fib.utils.FIBTestCase;
import org.openflexo.toolbox.FileResource;

public class TestCommonFlexoDialogFibs extends FIBTestCase {

	public static void main(String[] args) {
		System.out.println(generateFIBTestCaseClass(new FileResource("Fib/Dialog"), "Fib/Dialog/"));
	}

	public void testPushToPaletteDialog() {
		validateFIB("Fib/Dialog/PushToPaletteDialog.fib");
	}

	public void testReviewUnsavedDialog() {
		validateFIB("Fib/Dialog/ReviewUnsavedDialog.fib");
	}

}
