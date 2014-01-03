package org.openflexo.fib;

import org.openflexo.fib.utils.FIBTestCase;
import org.openflexo.toolbox.FileResource;

public class TestCommonFlexoInspectors extends FIBTestCase {

	public static void main(String[] args) {
		System.out.println(generateInspectorTestCaseClass(new FileResource("Inspectors/COMMON"), "Inspectors/COMMON/"));
	}

	public void testConditionalSectionInspector() {
		validateFIB("Inspectors/COMMON/ConditionalSection.inspector");
	}

	public void testDocForModelObjectInspector() {
		validateFIB("Inspectors/COMMON/DocForModelObject.inspector");
	}

	public void testEditionPatternInstanceInspector() {
		validateFIB("Inspectors/COMMON/EditionPatternInstance.inspector");
	}

	public void testEntitySectionInspector() {
		validateFIB("Inspectors/COMMON/EntitySection.inspector");
	}

	public void testERDiagramSectionInspector() {
		validateFIB("Inspectors/COMMON/ERDiagramSection.inspector");
	}

	public void testFlexoObjectInspector() {
		validateFIB("Inspectors/COMMON/FlexoObject.inspector");
	}

	public void testFlexoProjectObjectInspector() {
		validateFIB("Inspectors/COMMON/FlexoProjectObject.inspector");
	}

	public void testIterationSectionInspector() {
		validateFIB("Inspectors/COMMON/IterationSection.inspector");
	}

	public void testModelObjectSectionInspector() {
		validateFIB("Inspectors/COMMON/ModelObjectSection.inspector");
	}

	public void testNormalSectionInspector() {
		validateFIB("Inspectors/COMMON/NormalSection.inspector");
	}

	public void testOperationScreenSectionInspector() {
		validateFIB("Inspectors/COMMON/OperationScreenSection.inspector");
	}

	public void testPredefinedSectionInspector() {
		validateFIB("Inspectors/COMMON/PredefinedSection.inspector");
	}

	public void testProcessSectionInspector() {
		validateFIB("Inspectors/COMMON/ProcessSection.inspector");
	}

	public void testProjectInspector() {
		validateFIB("Inspectors/COMMON/Project.inspector");
	}

	public void testRepositoryFolderInspector() {
		validateFIB("Inspectors/COMMON/RepositoryFolder.inspector");
	}

	public void testRoleSectionInspector() {
		validateFIB("Inspectors/COMMON/RoleSection.inspector");
	}

	public void testTOCEntryInspector() {
		validateFIB("Inspectors/COMMON/TOCEntry.inspector");
	}

	public void testTOCRepositoryInspector() {
		validateFIB("Inspectors/COMMON/TOCRepository.inspector");
	}

	public void testViewSectionInspector() {
		validateFIB("Inspectors/COMMON/ViewSection.inspector");
	}

}
