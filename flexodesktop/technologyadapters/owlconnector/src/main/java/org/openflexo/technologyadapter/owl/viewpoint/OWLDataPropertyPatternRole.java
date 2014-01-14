package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;

@ModelEntity
@ImplementationClass(OWLDataPropertyPatternRole.OWLDataPropertyPatternRoleImpl.class)
@XMLElement
public interface OWLDataPropertyPatternRole extends DataPropertyPatternRole<OWLDataProperty>{


public static abstract  class OWLDataPropertyPatternRoleImpl extends DataPropertyPatternRole<OWLDataProperty>Impl implements OWLDataPropertyPatternRole
{

	public OWLDataPropertyPatternRoleImpl() {
		super();
	}

}
}
