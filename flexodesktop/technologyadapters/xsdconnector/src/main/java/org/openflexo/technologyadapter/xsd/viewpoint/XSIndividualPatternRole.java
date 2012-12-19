package org.openflexo.technologyadapter.xsd.viewpoint;

import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;

public class XSIndividualPatternRole extends IndividualPatternRole<XSOntIndividual> {

	public XSIndividualPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Class<XSOntIndividual> getAccessedClass() {
		return XSOntIndividual.class;
	}

}
