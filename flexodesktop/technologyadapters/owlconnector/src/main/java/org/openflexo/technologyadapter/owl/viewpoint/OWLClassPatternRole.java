package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.technologyadapter.owl.model.OWLClass;

@ModelEntity
@ImplementationClass(OWLClassPatternRole.OWLClassPatternRoleImpl.class)
@XMLElement
public interface OWLClassPatternRole extends ClassPatternRole<OWLClass>{


public static abstract  class OWLClassPatternRoleImpl extends ClassPatternRole<OWLClass>Impl implements OWLClassPatternRole
{

	public OWLClassPatternRoleImpl() {
		super();
	}

}
}
