package org.openflexo.technologyadapter.emf.viewpoint;

import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;

public class EMFObjectIndividualPatternRole extends IndividualPatternRole<EMFObjectIndividual> {

	public EMFObjectIndividualPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Class<EMFObjectIndividual> getAccessedClass() {
		return EMFObjectIndividual.class;
	}

}
