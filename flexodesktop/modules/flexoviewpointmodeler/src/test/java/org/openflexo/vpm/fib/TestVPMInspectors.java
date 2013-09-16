package org.openflexo.vpm.fib;

import java.io.File;

import org.openflexo.fib.FIBTestCase;

public class TestVPMInspectors extends FIBTestCase {

	public static void main(String[] args) {
		System.out.println(generateInspectorTestCaseClass(new File(System.getProperty("user.dir") + "/src/main/resources/Inspectors/VPM"),
				"Inspectors/VPM/"));
	}

	public void testAbstractActionSchemeInspector() {
		validateFIB("Inspectors/VPM/AbstractActionScheme.inspector");
	}

	public void testAbstractCreationSchemeInspector() {
		validateFIB("Inspectors/VPM/AbstractCreationScheme.inspector");
	}

	public void testActionSchemeInspector() {
		validateFIB("Inspectors/VPM/ActionScheme.inspector");
	}

	public void testCheckboxParameterInspector() {
		validateFIB("Inspectors/VPM/CheckboxParameter.inspector");
	}

	public void testClassParameterInspector() {
		validateFIB("Inspectors/VPM/ClassParameter.inspector");
	}

	public void testClassPatternRoleInspector() {
		validateFIB("Inspectors/VPM/ClassPatternRole.inspector");
	}

	public void testCloningSchemeInspector() {
		validateFIB("Inspectors/VPM/CloningScheme.inspector");
	}

	public void testConnectorOverridingGraphicalRepresentationInspector() {
		validateFIB("Inspectors/VPM/ConnectorOverridingGraphicalRepresentation.inspector");
	}

	public void testConnectorPatternRoleInspector() {
		validateFIB("Inspectors/VPM/ConnectorPatternRole.inspector");
	}

	public void testCreationSchemeInspector() {
		validateFIB("Inspectors/VPM/CreationScheme.inspector");
	}

	public void testDataPropertyParameterInspector() {
		validateFIB("Inspectors/VPM/DataPropertyParameter.inspector");
	}

	public void testDataPropertyPatternRoleInspector() {
		validateFIB("Inspectors/VPM/DataPropertyPatternRole.inspector");
	}

	public void testDeletionSchemeInspector() {
		validateFIB("Inspectors/VPM/DeletionScheme.inspector");
	}

	public void testDiagramModelSlotInspector() {
		validateFIB("Inspectors/VPM/DiagramModelSlot.inspector");
	}

	public void testDiagramPaletteInspector() {
		validateFIB("Inspectors/VPM/DiagramPalette.inspector");
	}

	public void testDiagramPaletteElementInspector() {
		validateFIB("Inspectors/VPM/DiagramPaletteElement.inspector");
	}

	public void testDiagramPatternRoleInspector() {
		validateFIB("Inspectors/VPM/DiagramPatternRole.inspector");
	}

	public void testDiagramSpecificationInspector() {
		validateFIB("Inspectors/VPM/DiagramSpecification.inspector");
	}

	public void testDiagramSpecificationResourceInspector() {
		validateFIB("Inspectors/VPM/DiagramSpecificationResource.inspector");
	}

	public void testDropDownParameterInspector() {
		validateFIB("Inspectors/VPM/DropDownParameter.inspector");
	}

	public void testDropSchemeInspector() {
		validateFIB("Inspectors/VPM/DropScheme.inspector");
	}

	public void testEditionPatternInspector() {
		validateFIB("Inspectors/VPM/EditionPattern.inspector");
	}

	public void testEditionPatternInstanceParameterInspector() {
		validateFIB("Inspectors/VPM/EditionPatternInstanceParameter.inspector");
	}

	public void testEditionPatternInstancePatternRoleInspector() {
		validateFIB("Inspectors/VPM/EditionPatternInstancePatternRole.inspector");
	}

	public void testEditionPatternObjectInspector() {
		validateFIB("Inspectors/VPM/EditionPatternObject.inspector");
	}

	public void testEditionSchemeInspector() {
		validateFIB("Inspectors/VPM/EditionScheme.inspector");
	}

	public void testEditionSchemeParameterInspector() {
		validateFIB("Inspectors/VPM/EditionSchemeParameter.inspector");
	}

	public void testExampleDiagramInspector() {
		validateFIB("Inspectors/VPM/ExampleDiagram.inspector");
	}

	public void testExampleDiagramConnectorInspector() {
		validateFIB("Inspectors/VPM/ExampleDiagramConnector.inspector");
	}

	public void testExampleDiagramObjectInspector() {
		validateFIB("Inspectors/VPM/ExampleDiagramObject.inspector");
	}

	public void testExampleDiagramShapeInspector() {
		validateFIB("Inspectors/VPM/ExampleDiagramShape.inspector");
	}

	public void testFlexoModelObjectPatternRoleInspector() {
		validateFIB("Inspectors/VPM/FlexoModelObjectPatternRole.inspector");
	}

	public void testFlexoObjectParameterInspector() {
		validateFIB("Inspectors/VPM/FlexoObjectParameter.inspector");
	}

	public void testFloatParameterInspector() {
		validateFIB("Inspectors/VPM/FloatParameter.inspector");
	}

	public void testGraphicalElementPatternRoleInspector() {
		validateFIB("Inspectors/VPM/GraphicalElementPatternRole.inspector");
	}

	public void testIndividualParameterInspector() {
		validateFIB("Inspectors/VPM/IndividualParameter.inspector");
	}

	public void testIndividualPatternRoleInspector() {
		validateFIB("Inspectors/VPM/IndividualPatternRole.inspector");
	}

	public void testInnerModelSlotParameterInspector() {
		validateFIB("Inspectors/VPM/InnerModelSlotParameter.inspector");
	}

	public void testIntegerParameterInspector() {
		validateFIB("Inspectors/VPM/IntegerParameter.inspector");
	}

	public void testLinkSchemeInspector() {
		validateFIB("Inspectors/VPM/LinkScheme.inspector");
	}

	public void testListParameterInspector() {
		validateFIB("Inspectors/VPM/ListParameter.inspector");
	}

	public void testModelSlotInspector() {
		validateFIB("Inspectors/VPM/ModelSlot.inspector");
	}

	public void testNamedViewPointObjectInspector() {
		validateFIB("Inspectors/VPM/NamedViewPointObject.inspector");
	}

	public void testNavigationSchemeInspector() {
		validateFIB("Inspectors/VPM/NavigationScheme.inspector");
	}

	public void testObjectPropertyParameterInspector() {
		validateFIB("Inspectors/VPM/ObjectPropertyParameter.inspector");
	}

	public void testObjectPropertyPatternRoleInspector() {
		validateFIB("Inspectors/VPM/ObjectPropertyPatternRole.inspector");
	}

	public void testOntologicObjectPatternRoleInspector() {
		validateFIB("Inspectors/VPM/OntologicObjectPatternRole.inspector");
	}

	public void testPatternRoleInspector() {
		validateFIB("Inspectors/VPM/PatternRole.inspector");
	}

	public void testPrimitivePatternRoleInspector() {
		validateFIB("Inspectors/VPM/PrimitivePatternRole.inspector");
	}

	public void testPropertyParameterInspector() {
		validateFIB("Inspectors/VPM/PropertyParameter.inspector");
	}

	public void testPropertyPatternRoleInspector() {
		validateFIB("Inspectors/VPM/PropertyPatternRole.inspector");
	}

	public void testShapeOverridingGraphicalRepresentationInspector() {
		validateFIB("Inspectors/VPM/ShapeOverridingGraphicalRepresentation.inspector");
	}

	public void testShapePatternRoleInspector() {
		validateFIB("Inspectors/VPM/ShapePatternRole.inspector");
	}

	public void testSynchronizationSchemeInspector() {
		validateFIB("Inspectors/VPM/SynchronizationScheme.inspector");
	}

	public void testTextAreaParameterInspector() {
		validateFIB("Inspectors/VPM/TextAreaParameter.inspector");
	}

	public void testTextFieldParameterInspector() {
		validateFIB("Inspectors/VPM/TextFieldParameter.inspector");
	}

	public void testTypeAwareModelSlotInspector() {
		validateFIB("Inspectors/VPM/TypeAwareModelSlot.inspector");
	}

	public void testURIParameterInspector() {
		validateFIB("Inspectors/VPM/URIParameter.inspector");
	}

	public void testViewPointInspector() {
		validateFIB("Inspectors/VPM/ViewPoint.inspector");
	}

	public void testViewPointLibraryInspector() {
		validateFIB("Inspectors/VPM/ViewPointLibrary.inspector");
	}

	public void testViewPointObjectInspector() {
		validateFIB("Inspectors/VPM/ViewPointObject.inspector");
	}

	public void testViewPointResourceInspector() {
		validateFIB("Inspectors/VPM/ViewPointResource.inspector");
	}

	public void testVirtualModelInspector() {
		validateFIB("Inspectors/VPM/VirtualModel.inspector");
	}

	public void testVirtualModelModelSlotInspector() {
		validateFIB("Inspectors/VPM/VirtualModelModelSlot.inspector");
	}

	public void testVirtualModelResourceInspector() {
		validateFIB("Inspectors/VPM/VirtualModelResource.inspector");
	}

}
