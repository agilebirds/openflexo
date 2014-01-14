package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.ObjectPropertyPatternRole;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;

@ModelEntity
@ImplementationClass(OWLObjectPropertyPatternRole.OWLObjectPropertyPatternRoleImpl.class)
@XMLElement
public interface OWLObjectPropertyPatternRole extends ObjectPropertyPatternRole<OWLObjectProperty>{


public static abstract  class OWLObjectPropertyPatternRoleImpl extends ObjectPropertyPatternRole<OWLObjectProperty>Impl implements OWLObjectPropertyPatternRole
{

	public OWLObjectPropertyPatternRoleImpl() {
		super();
	}

}
}
