package org.openflexo.ve.fib;

import org.openflexo.fib.FIBTestCase;
import org.openflexo.ve.VECst;
import org.openflexo.ve.widget.FIBViewLibraryBrowser;

public class TestVEFIB extends FIBTestCase {

	public void testOntologyView() {
		validateFIB(VECst.ONTOLOGY_VIEW_FIB);
	}

	public void testCreateViewDialogView() {
		validateFIB(VECst.CREATE_VIEW_DIALOG_FIB);
	}

	public void testCreateVirtualModelInstanceDialogView() {
		validateFIB(VECst.CREATE_VIRTUAL_MODEL_INSTANCE_DIALOG_FIB);
	}

	public void testConfigureModelSlotInstanceDialog() {
		validateFIB(VECst.CONFIGURE_TYPESAFE_MODEL_SLOT_INSTANCE_DIALOG_FIB);
	}

	public void testConfigureFreeModelSlotInstanceDialog() {
		validateFIB(VECst.CONFIGURE_FREE_MODEL_SLOT_INSTANCE_DIALOG_FIB);
	}

	public void testCreateDiagramDialogDialog() {
		validateFIB(VECst.CREATE_DIAGRAM_DIALOG_FIB);
	}

	public void testDeleteDiagramElementsDialog() {
		validateFIB(VECst.DELETE_DIAGRAM_ELEMENTS_DIALOG_FIB);
	}

	public void testReindexDiagramElementsDialog() {
		validateFIB(VECst.REINDEX_DIAGRAM_ELEMENTS_DIALOG_FIB);
	}

	public void testViewLibraryBrowser() {
		validateFIB(FIBViewLibraryBrowser.FIB_FILE);
	}

}
