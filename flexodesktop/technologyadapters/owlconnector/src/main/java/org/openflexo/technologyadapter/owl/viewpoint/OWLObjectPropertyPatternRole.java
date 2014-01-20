package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.ObjectPropertyPatternRole;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;

@ModelEntity
@ImplementationClass(OWLObjectPropertyPatternRole.OWLObjectPropertyPatternRoleImpl.class)
@XMLElement
public interface OWLObjectPropertyPatternRole extends ObjectPropertyPatternRole<OWLObjectProperty> {

	public static abstract class OWLObjectPropertyPatternRoleImpl extends ObjectPropertyPatternRoleImpl<OWLObjectProperty> implements
			OWLObjectPropertyPatternRole {

		public OWLObjectPropertyPatternRoleImpl() {
			super();
		}

	}
}
