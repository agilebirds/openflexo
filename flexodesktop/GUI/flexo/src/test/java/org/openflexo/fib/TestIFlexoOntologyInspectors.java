package org.openflexo.fib;

import org.openflexo.fib.utils.FIBTestCase;
import org.openflexo.toolbox.FileResource;

public class TestIFlexoOntologyInspectors extends FIBTestCase {

	public static void main(String[] args) {
		System.out.println(generateInspectorTestCaseClass(new FileResource("Inspectors/IFlexoOntology"), "Inspectors/IFlexoOntology/"));
	}

	public void testIFlexoOntologyInspector() {
		validateFIB("Inspectors/IFlexoOntology/IFlexoOntology.inspector");
	}

	public void testIFlexoOntologyClassInspector() {
		validateFIB("Inspectors/IFlexoOntology/IFlexoOntologyClass.inspector");
	}

	public void testIFlexoOntologyConceptInspector() {
		validateFIB("Inspectors/IFlexoOntology/IFlexoOntologyConcept.inspector");
	}

	public void testIFlexoOntologyDataPropertyInspector() {
		validateFIB("Inspectors/IFlexoOntology/IFlexoOntologyDataProperty.inspector");
	}

	public void testIFlexoOntologyIndividualInspector() {
		validateFIB("Inspectors/IFlexoOntology/IFlexoOntologyIndividual.inspector");
	}

	public void testIFlexoOntologyObjectPropertyInspector() {
		validateFIB("Inspectors/IFlexoOntology/IFlexoOntologyObjectProperty.inspector");
	}
}
