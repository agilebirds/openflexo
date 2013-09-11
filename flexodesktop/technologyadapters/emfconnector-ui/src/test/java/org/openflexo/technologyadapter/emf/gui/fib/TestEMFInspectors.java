package org.openflexo.technologyadapter.emf.gui.fib;

import org.openflexo.fib.FIBTestCase;

public class TestEMFInspectors extends FIBTestCase {

	public void testEMFMetaModelInspector() {
		validateFIB("Inspectors/EMF/EMFMetaModel.inspector");
	}

	public void testEMFMetaModelResourceInspector() {
		validateFIB("Inspectors/EMF/EMFMetaModelResource.inspector");
	}

	public void testEMFModelInspector() {
		validateFIB("Inspectors/EMF/EMFModel.inspector");
	}

	public void testEMFModelResourceInspector() {
		validateFIB("Inspectors/EMF/EMFModelResource.inspector");
	}

}
