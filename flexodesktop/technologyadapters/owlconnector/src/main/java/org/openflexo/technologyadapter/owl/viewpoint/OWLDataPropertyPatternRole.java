package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;

public class OWLDataPropertyPatternRole extends DataPropertyPatternRole<OWLDataProperty> {

	public OWLDataPropertyPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Class<OWLDataProperty> getAccessedClass() {
		return OWLDataProperty.class;
	}

}
