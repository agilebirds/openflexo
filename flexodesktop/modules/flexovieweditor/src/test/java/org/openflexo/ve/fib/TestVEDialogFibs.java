package org.openflexo.ve.fib;

import java.io.File;

import org.openflexo.fib.FIBTestCase;

public class TestVEDialogFibs extends FIBTestCase {

	public static void main(String[] args) {
		System.out.println(generateFIBTestCaseClass(new File(System.getProperty("user.dir") + "/src/main/resources/Fib/Dialog"),
				"Fib/Dialog/"));
	}

	public void testChooseAndConfigureCreationSchemeDialog() {
		validateFIB("Fib/Dialog/ChooseAndConfigureCreationSchemeDialog.fib");
	}

	public void testConfigureFreeModelSlotInstanceDialog() {
		validateFIB("Fib/Dialog/ConfigureFreeModelSlotInstanceDialog.fib");
	}

	public void testConfigureModelSlotInstanceDialog() {
		validateFIB("Fib/Dialog/ConfigureModelSlotInstanceDialog.fib");
	}

	public void testConfigureTypeSafeModelSlotInstanceDialog() {
		validateFIB("Fib/Dialog/ConfigureTypeSafeModelSlotInstanceDialog.fib");
	}

	public void testConfigureVirtualModelSlotInstanceDialog() {
		validateFIB("Fib/Dialog/ConfigureVirtualModelSlotInstanceDialog.fib");
	}

	public void testCreateDiagramDialog() {
		validateFIB("Fib/Dialog/CreateDiagramDialog.fib");
	}

	public void testCreateViewDialog() {
		validateFIB("Fib/Dialog/CreateViewDialog.fib");
	}

	public void testCreateVirtualModelInstanceDialog() {
		validateFIB("Fib/Dialog/CreateVirtualModelInstanceDialog.fib");
	}

	public void testDeleteDiagramElementsDialog() {
		validateFIB("Fib/Dialog/DeleteDiagramElementsDialog.fib");
	}

	public void testReindexDiagramElementsDialog() {
		validateFIB("Fib/Dialog/ReindexDiagramElementsDialog.fib");
	}

}
