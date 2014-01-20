package org.openflexo.technologyadapter.owl.viewpoint;

import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.model.OWLClass;

@ModelEntity
@ImplementationClass(OWLClassPatternRole.OWLClassPatternRoleImpl.class)
@XMLElement
public interface OWLClassPatternRole extends ClassPatternRole<OWLClass> {

	public static abstract class OWLClassPatternRoleImpl extends ClassPatternRoleImpl<OWLClass> implements OWLClassPatternRole {

		public OWLClassPatternRoleImpl() {
			super();
		}

	}
}
