package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;

@ModelEntity
@ImplementationClass(OWLIndividualPatternRole.OWLIndividualPatternRoleImpl.class)
@XMLElement
public interface OWLIndividualPatternRole extends IndividualPatternRole<OWLIndividual> {

	public static abstract class OWLIndividualPatternRoleImpl extends IndividualPatternRoleImpl<OWLIndividual> implements
			OWLIndividualPatternRole {

		public OWLIndividualPatternRoleImpl() {
			super();
		}

	}
}
