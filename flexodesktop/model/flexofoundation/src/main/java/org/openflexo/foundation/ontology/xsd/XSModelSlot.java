package org.openflexo.foundation.ontology.xsd;

import org.openflexo.foundation.view.View;
import org.openflexo.foundation.viewpoint.AbstractModelSlot;
import org.openflexo.foundation.viewpoint.TechnologicalSpace;

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
		// TODO this is a stub
		return ProjectXSOntology.createNewProjectOntology(view.getProject());
	}

	@Override
	public String getFullyQualifiedName() {
		return "XSModelSlot";
	}

	@Override
	public String getClassNameKey() {
		return "xsd_model_slot";
	}

}
