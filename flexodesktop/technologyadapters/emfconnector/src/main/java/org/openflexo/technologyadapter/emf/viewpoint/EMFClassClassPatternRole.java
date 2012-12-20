package org.openflexo.technologyadapter.emf.viewpoint;

import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;

public class EMFClassClassPatternRole extends ClassPatternRole<EMFClassClass> {

	public EMFClassClassPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Class<EMFClassClass> getAccessedClass() {
		return EMFClassClass.class;
	}

}
