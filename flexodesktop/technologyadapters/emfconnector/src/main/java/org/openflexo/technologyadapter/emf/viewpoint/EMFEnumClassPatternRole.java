package org.openflexo.technologyadapter.emf.viewpoint;

import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.emf.metamodel.EMFEnumClass;

@ModelEntity
@ImplementationClass(EMFEnumClassPatternRole.EMFEnumClassPatternRoleImpl.class)
@XMLElement
public interface EMFEnumClassPatternRole extends ClassPatternRole<EMFEnumClass> {

	public static abstract class EMFEnumClassPatternRoleImpl extends ClassPatternRoleImpl<EMFEnumClass> implements EMFEnumClassPatternRole {

		public EMFEnumClassPatternRoleImpl() {
			super();
		}

	}
}
