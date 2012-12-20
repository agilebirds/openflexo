package org.openflexo.technologyadapter.xsd.viewpoint;

import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.xsd.model.XSOntClass;

public class XSClassPatternRole extends ClassPatternRole<XSOntClass> {

	public XSClassPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Class<XSOntClass> getAccessedClass() {
		return XSOntClass.class;
	}

}
