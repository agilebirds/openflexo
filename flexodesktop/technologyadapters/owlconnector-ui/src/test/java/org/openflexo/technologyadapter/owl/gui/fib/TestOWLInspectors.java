package org.openflexo.technologyadapter.owl.gui.fib;

import org.openflexo.fib.FIBTestCase;

public class TestOWLInspectors extends FIBTestCase {

	public void testDataPropertyStatementPatternRoleInspector() {
		validateFIB("Inspectors/OWL/DataPropertyStatementPatternRole.inspector");
	}

	public void testObjectPropertyStatementPatternRoleInspector() {
		validateFIB("Inspectors/OWL/ObjectPropertyStatementPatternRole.inspector");
	}

	public void testOWLConceptInspector() {
		validateFIB("Inspectors/OWL/OWLConcept.inspector");
	}

	public void testOWLOntologyInspector() {
		validateFIB("Inspectors/OWL/OWLOntology.inspector");
	}

	public void testOWLOntologyResourceInspector() {
		validateFIB("Inspectors/OWL/OWLOntologyResource.inspector");
	}

	public void testOWLStatementInspector() {
		validateFIB("Inspectors/OWL/OWLStatement.inspector");
	}

}
