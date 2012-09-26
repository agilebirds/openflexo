package org.openflexo.foundation.modelslot;

import org.openflexo.foundation.ontology.xsd.ProjectXSOntology;
import org.openflexo.foundation.ontology.xsd.XSOntology;

public class XSModelSlot extends AbstractModelSlot <XSOntology> {

	@Override
	public TechnologicalSpace getTechnologicalSpace() {
		return TechnologicalSpace.XSD;
	}
	
	@Override
	public ProjectXSOntology createEmptyModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
