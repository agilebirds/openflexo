package org.openflexo.technologyadapter.xsd.viewpoint;

import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;

@ModelEntity
@ImplementationClass(XSClassPatternRole.XSClassPatternRoleImpl.class)
@XMLElement
public interface XSClassPatternRole extends ClassPatternRole<XSOntClass>{


public static abstract  class XSClassPatternRoleImpl extends ClassPatternRole<XSOntClass>Impl implements XSClassPatternRole
{

	public XSClassPatternRoleImpl() {
		super();
	}

}
}
