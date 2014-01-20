package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;

@ModelEntity
@ImplementationClass(OWLDataPropertyPatternRole.OWLDataPropertyPatternRoleImpl.class)
@XMLElement
public interface OWLDataPropertyPatternRole extends DataPropertyPatternRole<OWLDataProperty> {

	public static abstract class OWLDataPropertyPatternRoleImpl extends DataPropertyPatternRoleImpl<OWLDataProperty> implements
			OWLDataPropertyPatternRole {

		public OWLDataPropertyPatternRoleImpl() {
			super();
		}

	}
}
