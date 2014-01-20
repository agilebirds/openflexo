package org.openflexo.technologyadapter.xsd.viewpoint;

import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;

@ModelEntity
@ImplementationClass(XSIndividualPatternRole.XSIndividualPatternRoleImpl.class)
@XMLElement
public interface XSIndividualPatternRole extends IndividualPatternRole<XSOntIndividual> {

	public static abstract class XSIndividualPatternRoleImpl extends IndividualPatternRoleImpl<XSOntIndividual> implements
			XSIndividualPatternRole {

		public XSIndividualPatternRoleImpl() {
			super();
		}

	}
}
