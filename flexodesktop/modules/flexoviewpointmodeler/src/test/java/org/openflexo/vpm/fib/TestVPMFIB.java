package org.openflexo.vpm.fib;

import org.openflexo.FlexoCst;
import org.openflexo.components.widget.FIBDiagramPaletteBrowser;
import org.openflexo.components.widget.FIBExampleDiagramBrowser;
import org.openflexo.components.widget.FIBViewPointBrowser;
import org.openflexo.components.widget.FIBViewPointLibraryBrowser;
import org.openflexo.components.widget.FIBVirtualModelBrowser;
import org.openflexo.fib.FIBTestCase;
import org.openflexo.vpm.VPMCst;

public class TestVPMFIB extends FIBTestCase {

	public void testStandardEditionPatternView() {
		validateFIB(VPMCst.STANDARD_EDITION_PATTERN_VIEW_FIB);
	}

	public void testDiagramEditionPatternView() {
		validateFIB(VPMCst.DIAGRAM_EDITION_PATTERN_VIEW_FIB);
	}

	public void testVirtualModelView() {
		validateFIB(VPMCst.VIRTUAL_MODEL_VIEW_FIB);
	}

	public void testDiagramSpecificationView() {
		validateFIB(VPMCst.DIAGRAM_SPECIFICATION_VIEW_FIB);
	}

	public void testViewPointView() {
		validateFIB(VPMCst.VIEWPOINT_VIEW_FIB);
	}

	public void testCreateDiagramSpecificationDialog() {
		validateFIB(VPMCst.CREATE_DIAGRAM_SPECIFICATION_DIALOG_FIB);
	}

	public void testCreateEditionActionDialog() {
		validateFIB(VPMCst.CREATE_EDITION_ACTION_DIALOG_FIB);
	}

	public void testCreateExampleDiagramDialog() {
		validateFIB(VPMCst.CREATE_EXAMPLE_DIAGRAM_DIALOG_FIB);
	}

	public void testCreateModelSlotDialog() {
		validateFIB(VPMCst.CREATE_MODEL_SLOT_DIALOG_FIB);
	}

	public void testCreatePaletteDialog() {
		validateFIB(VPMCst.CREATE_PALETTE_DIALOG_FIB);
	}

	public void testCreatePatternRoleDialog() {
		validateFIB(VPMCst.CREATE_PATTERN_ROLE_DIALOG_FIB);
	}

	public void testCreateViewPointDialog() {
		validateFIB(VPMCst.CREATE_VIEW_POINT_DIALOG_FIB);
	}

	public void testCreateVirtualModelDialog() {
		validateFIB(VPMCst.CREATE_VIRTUAL_MODEL_DIALOG_FIB);
	}

	public void testDeclareConnectorInEditionPatternDialog() {
		validateFIB(FlexoCst.DECLARE_CONNECTOR_IN_EDITION_PATTERN_DIALOG_FIB);
	}

	public void testDeclareShapeInEditionPatternDialog() {
		validateFIB(FlexoCst.DECLARE_SHAPE_IN_EDITION_PATTERN_DIALOG_FIB);
	}

	public void testPushToPaletteDialog() {
		validateFIB(VPMCst.PUSH_TO_PALETTE_DIALOG_FIB);
	}

	public void testReviewUnsavedDialog() {
		validateFIB(FlexoCst.REVIEW_UNSAVED_DIALOG_FIB);
	}

	public void testShowLanguageRepresentationDialog() {
		validateFIB(VPMCst.SHOW_FML_REPRESENTATION_DIALOG_FIB);
	}

	public void testDiagramPaletteBrowser() {
		validateFIB(FIBDiagramPaletteBrowser.FIB_FILE);
	}

	public void testExampleDiagramBrowser() {
		validateFIB(FIBExampleDiagramBrowser.FIB_FILE);
	}

	public void testViewPointBrowser() {
		validateFIB(FIBViewPointBrowser.FIB_FILE);
	}

	public void testViewPointLibraryBrowser() {
		validateFIB(FIBViewPointLibraryBrowser.FIB_FILE);
	}

	public void testVirtualModelBrowser() {
		validateFIB(FIBVirtualModelBrowser.FIB_FILE);
	}

}
