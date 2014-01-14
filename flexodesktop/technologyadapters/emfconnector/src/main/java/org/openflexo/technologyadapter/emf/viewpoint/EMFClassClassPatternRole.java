package org.openflexo.technologyadapter.emf.viewpoint;

import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;

@ModelEntity
@ImplementationClass(EMFClassClassPatternRole.EMFClassClassPatternRoleImpl.class)
@XMLElement
public interface EMFClassClassPatternRole extends ClassPatternRole<EMFClassClass>{


public static abstract  class EMFClassClassPatternRoleImpl extends ClassPatternRole<EMFClassClass>Impl implements EMFClassClassPatternRole
{

	public EMFClassClassPatternRoleImpl() {
		super();
	}

}
}
