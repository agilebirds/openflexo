package org.openflexo.technologyadapter.owl.gui.fib;

import org.openflexo.fib.utils.FIBTestCase;
import org.openflexo.technologyadapter.owl.controller.OWLFIBLibrary;

public class TestOWLFibs extends FIBTestCase {

	public void testCreateOntologyClassDialog() {
		validateFIB(OWLFIBLibrary.CREATE_ONTOLOGY_CLASS_DIALOG_FIB);
	}

	public void testCreateOntologyIndividualDialog() {
		validateFIB(OWLFIBLibrary.CREATE_ONTOLOGY_INDIVIDUAL_FIB);
	}

	public void testDeleteOntologyObjectsDialog() {
		validateFIB(OWLFIBLibrary.DELETE_ONTOLOGY_OBJECTS_DIALOG_FIB);
	}

	public void testCreateDataPropertyDialog() {
		validateFIB(OWLFIBLibrary.CREATE_DATA_PROPERTY_DIALOG_FIB);
	}

	public void testCreateObjectPropertyDialog() {
		validateFIB(OWLFIBLibrary.CREATE_OBJECT_PROPERTY_DIALOG_FIB);
	}

	public void testAddDataPropertyStatementPanel() {
		validateFIB("Fib/AddDataPropertyStatementPanel.fib");
	}

	public void testAddIsAPropertyPanel() {
		validateFIB("Fib/AddIsAPropertyPanel.fib");
	}

	public void testAddObjectPropertyStatementPanel() {
		validateFIB("Fib/AddObjectPropertyStatementPanel.fib");
	}

	public void testAddRestrictionPanel() {
		validateFIB("Fib/AddRestrictionPanel.fib");
	}

	public void testFIBOntologyLibraryBrowser() {
		validateFIB("Fib/FIBOntologyLibraryBrowser.fib");
	}

	public void testFIBOWLClassEditor() {
		validateFIB("Fib/FIBOWLClassEditor.fib");
	}

	public void testFIBOWLDataPropertyEditor() {
		validateFIB("Fib/FIBOWLDataPropertyEditor.fib");
	}

	public void testFIBOWLIndividualEditor() {
		validateFIB("Fib/FIBOWLIndividualEditor.fib");
	}

	public void testFIBOWLObjectPropertyEditor() {
		validateFIB("Fib/FIBOWLObjectPropertyEditor.fib");
	}

}
