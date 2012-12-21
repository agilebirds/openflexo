package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.owl.model.OWLClass;

public class OWLClassPatternRole extends ClassPatternRole<OWLClass> {

	public OWLClassPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Class<OWLClass> getAccessedClass() {
		return OWLClass.class;
	}

}
