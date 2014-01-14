package org.openflexo.technologyadapter.xsd.viewpoint;

import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;

@ModelEntity
@ImplementationClass(XSIndividualPatternRole.XSIndividualPatternRoleImpl.class)
@XMLElement
public interface XSIndividualPatternRole extends IndividualPatternRole<XSOntIndividual>{


public static abstract  class XSIndividualPatternRoleImpl extends IndividualPatternRole<XSOntIndividual>Impl implements XSIndividualPatternRole
{

	public XSIndividualPatternRoleImpl() {
		super();
	}

}
}
