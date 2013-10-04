package org.openflexo.fib;

import org.openflexo.fib.utils.FIBTestCase;
import org.openflexo.toolbox.FileResource;

public class TestCommonFlexoFibs extends FIBTestCase {

	public static void main(String[] args) {
		System.out.println(generateFIBTestCaseClass(new FileResource("Fib"), "Fib/"));
	}

	public void testAskResourceCenterDirectory() {
		validateFIB("Fib/AskResourceCenterDirectory.fib");
	}

	public void testComponentSelector() {
		validateFIB("Fib/ComponentSelector.fib");
	}

	public void testDescriptionWidget() {
		validateFIB("Fib/DescriptionWidget.fib");
	}

	public void testDocGenerationChooser() {
		validateFIB("Fib/DocGenerationChooser.fib");
	}

	public void testEditionPatternInstanceSelector() {
		validateFIB("Fib/EditionPatternInstanceSelector.fib");
	}

	public void testEditionPatternSelector() {
		validateFIB("Fib/EditionPatternSelector.fib");
	}

	public void testERDiagramSelector() {
		validateFIB("Fib/ERDiagramSelector.fib");
	}

	public void testFIBClassSelector() {
		validateFIB("Fib/FIBClassSelector.fib");
	}

	public void testFIBIndividualSelector() {
		validateFIB("Fib/FIBIndividualSelector.fib");
	}

	public void testFIBInformationSpaceBrowser() {
		validateFIB("Fib/FIBInformationSpaceBrowser.fib");
	}

	public void testFIBOntologyBrowser() {
		validateFIB("Fib/FIBOntologyBrowser.fib");
	}

	public void testFIBOntologyClassEditor() {
		validateFIB("Fib/FIBOntologyClassEditor.fib");
	}

	public void testFIBOntologyDataPropertyEditor() {
		validateFIB("Fib/FIBOntologyDataPropertyEditor.fib");
	}

	public void testFIBOntologyEditor() {
		validateFIB("Fib/FIBOntologyEditor.fib");
	}

	public void testFIBOntologyIndividualEditor() {
		validateFIB("Fib/FIBOntologyIndividualEditor.fib");
	}

	public void testFIBOntologyObjectPropertyEditor() {
		validateFIB("Fib/FIBOntologyObjectPropertyEditor.fib");
	}

	public void testFIBOntologySelector() {
		validateFIB("Fib/FIBOntologySelector.fib");
	}

	public void testFIBPropertySelector() {
		validateFIB("Fib/FIBPropertySelector.fib");
	}

	public void testFIBViewPointLibraryBrowser() {
		validateFIB("Fib/FIBViewPointLibraryBrowser.fib");
	}

	public void testInstallDefaultPackagedResourceCenterDirectory() {
		validateFIB("Fib/InstallDefaultPackagedResourceCenterDirectory.fib");
	}

	public void testJIRAIssueReportDialog() {
		validateFIB("Fib/JIRAIssueReportDialog.fib");
	}

	public void testJIRASubmitIssueReportDialog() {
		validateFIB("Fib/JIRASubmitIssueReportDialog.fib");
	}

	public void testJIRAURLCredentialsDialog() {
		validateFIB("Fib/JIRAURLCredentialsDialog.fib");
	}

	public void testMainPaneTobBar() {
		validateFIB("Fib/MainPaneTobBar.fib");
	}

	public void testMetaModelSelector() {
		validateFIB("Fib/MetaModelSelector.fib");
	}

	public void testModelSelector() {
		validateFIB("Fib/ModelSelector.fib");
	}

	public void testNewServerProject() {
		validateFIB("Fib/NewServerProject.fib");
	}

	public void testProcessFolderSelector() {
		validateFIB("Fib/ProcessFolderSelector.fib");
	}

	public void testProcessNodeSelector() {
		validateFIB("Fib/ProcessNodeSelector.fib");
	}

	public void testProcessSelector() {
		validateFIB("Fib/ProcessSelector.fib");
	}

	public void testProjectSelector() {
		validateFIB("Fib/ProjectSelector.fib");
	}

	public void testResourceCenterEditor() {
		validateFIB("Fib/ResourceCenterEditor.fib");
	}

	public void testResourceCenterSelector() {
		validateFIB("Fib/ResourceCenterSelector.fib");
	}

	public void testResourceSelector() {
		validateFIB("Fib/ResourceSelector.fib");
	}

	public void testRoleSelector() {
		validateFIB("Fib/RoleSelector.fib");
	}

	public void testSaveProjects() {
		validateFIB("Fib/SaveProjects.fib");
	}

	public void testServerClientModelView() {
		validateFIB("Fib/ServerClientModelView.fib");
	}

	public void testViewFolderSelector() {
		validateFIB("Fib/ViewFolderSelector.fib");
	}

	public void testViewPointSelector() {
		validateFIB("Fib/ViewPointSelector.fib");
	}

	public void testViewSelector() {
		validateFIB("Fib/ViewSelector.fib");
	}

	public void testVirtualModelInstanceSelector() {
		validateFIB("Fib/VirtualModelInstanceSelector.fib");
	}

	public void testVirtualModelSelector() {
		validateFIB("Fib/VirtualModelSelector.fib");
	}

	public void testWebServiceURLDialog() {
		validateFIB("Fib/WebServiceURLDialog.fib");
	}

	public void testWelcomePanel() {
		validateFIB("Fib/WelcomePanel.fib");
	}

}
