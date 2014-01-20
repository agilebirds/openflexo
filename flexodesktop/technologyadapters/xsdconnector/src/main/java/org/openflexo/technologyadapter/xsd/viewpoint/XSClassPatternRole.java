package org.openflexo.technologyadapter.xsd.viewpoint;

import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;

@ModelEntity
@ImplementationClass(XSClassPatternRole.XSClassPatternRoleImpl.class)
@XMLElement
public interface XSClassPatternRole extends ClassPatternRole<XSOntClass> {

	public static abstract class XSClassPatternRoleImpl extends ClassPatternRoleImpl<XSOntClass> implements XSClassPatternRole {

		public XSClassPatternRoleImpl() {
			super();
		}

	}
}
