package org.openflexo.technologyadapter.emf.viewpoint;

import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;

@ModelEntity
@ImplementationClass(EMFClassClassPatternRole.EMFClassClassPatternRoleImpl.class)
@XMLElement
public interface EMFClassClassPatternRole extends ClassPatternRole<EMFClassClass> {

	public static abstract class EMFClassClassPatternRoleImpl extends ClassPatternRoleImpl<EMFClassClass> implements
			EMFClassClassPatternRole {

		public EMFClassClassPatternRoleImpl() {
			super();
		}

	}
}
