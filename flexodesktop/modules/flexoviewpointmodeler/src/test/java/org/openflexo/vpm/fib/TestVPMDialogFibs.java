package org.openflexo.vpm.fib;

import java.io.File;

import org.openflexo.fib.utils.FIBTestCase;

public class TestVPMDialogFibs extends FIBTestCase {

	public static void main(String[] args) {
		System.out.println(generateFIBTestCaseClass(new File(System.getProperty("user.dir") + "/src/main/resources/Fib/Dialog"),
				"Fib/Dialog/"));
	}

	public void testCreateDiagramSpecificationDialog() {
		validateFIB("Fib/Dialog/CreateDiagramSpecificationDialog.fib");
	}

	public void testCreateEditionActionDialog() {
		validateFIB("Fib/Dialog/CreateEditionActionDialog.fib");
	}

	public void testCreateExampleDrawingDialog() {
		validateFIB("Fib/Dialog/CreateExampleDrawingDialog.fib");
	}

	public void testCreateModelSlotDialog() {
		validateFIB("Fib/Dialog/CreateModelSlotDialog.fib");
	}

	public void testCreatePaletteDialog() {
		validateFIB("Fib/Dialog/CreatePaletteDialog.fib");
	}

	public void testCreatePatternRoleDialog() {
		validateFIB("Fib/Dialog/CreatePatternRoleDialog.fib");
	}

	public void testCreateViewPointDialog() {
		validateFIB("Fib/Dialog/CreateViewPointDialog.fib");
	}

	public void testCreateVirtualModelDialog() {
		validateFIB("Fib/Dialog/CreateVirtualModelDialog.fib");
	}

	public void testPushToPaletteDialog() {
		validateFIB("Fib/Dialog/PushToPaletteDialog.fib");
	}

	public void testShowFMLRepresentationDialog() {
		validateFIB("Fib/Dialog/ShowFMLRepresentationDialog.fib");
	}

}
