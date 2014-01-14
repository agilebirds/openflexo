package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;

@ModelEntity
@ImplementationClass(OWLIndividualPatternRole.OWLIndividualPatternRoleImpl.class)
@XMLElement
public interface OWLIndividualPatternRole extends IndividualPatternRole<OWLIndividual>{


public static abstract  class OWLIndividualPatternRoleImpl extends IndividualPatternRole<OWLIndividual>Impl implements OWLIndividualPatternRole
{

	public OWLIndividualPatternRoleImpl() {
		super();
	}

}
}
