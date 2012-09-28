package org.openflexo.foundation.modelslot;

import org.openflexo.foundation.ontology.owl.OWLOntology;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.view.View;
import org.openflexo.xmlcode.XMLMapping;

/**
 * <p>
 * Implementation of the ModelSlot class for the OWL technoligical space.
 * 
 * @author Luka Le Roux
 * 
 */
public class OWLModelSlot extends AbstractModelSlot<OWLOntology> {

	@Override
	public TechnologicalSpace getTechnologicalSpace() {
		return TechnologicalSpace.OWL;
	}

	@Override
	public OWLOntology createEmptyModel(View view) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClassNameKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLMapping getXMLMapping() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		// TODO Auto-generated method stub
		return null;
	}

}
