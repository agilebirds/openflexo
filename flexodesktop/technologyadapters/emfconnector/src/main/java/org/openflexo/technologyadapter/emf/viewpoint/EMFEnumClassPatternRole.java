package org.openflexo.technologyadapter.emf.viewpoint;

import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.technologyadapter.emf.metamodel.EMFEnumClass;

@ModelEntity
@ImplementationClass(EMFEnumClassPatternRole.EMFEnumClassPatternRoleImpl.class)
@XMLElement
public interface EMFEnumClassPatternRole extends ClassPatternRole<EMFEnumClass>{


public static abstract  class EMFEnumClassPatternRoleImpl extends ClassPatternRole<EMFEnumClass>Impl implements EMFEnumClassPatternRole
{

	public EMFEnumClassPatternRoleImpl() {
		super();
	}

}
}
