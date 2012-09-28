package org.openflexo.foundation.modelslot;

import org.openflexo.foundation.ontology.xsd.ProjectXSOntology;
import org.openflexo.foundation.ontology.xsd.XSOntology;
import org.openflexo.foundation.view.View;

/**
 * <p>
 * Implementation of the ModelSlot class for the XSD technoligical space.
 * 
 * @author Luka Le Roux
 * 
 */
public class XSModelSlot extends AbstractModelSlot<XSOntology> {

	@Override
	public TechnologicalSpace getTechnologicalSpace() {
		return TechnologicalSpace.XSD;
	}

	@Override
	public ProjectXSOntology createEmptyModel(View view) {
		// TODO Auto-generated method stub
		return null;
	}

}
