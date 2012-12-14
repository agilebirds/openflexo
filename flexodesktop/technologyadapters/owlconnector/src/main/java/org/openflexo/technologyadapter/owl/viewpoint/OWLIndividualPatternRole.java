package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;

public class OWLIndividualPatternRole extends IndividualPatternRole<OWLIndividual> {

	public OWLIndividualPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Class<OWLIndividual> getAccessedClass() {
		return OWLIndividual.class;
	}

}
