package org.openflexo.foundation.toc;

import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class RoleSection extends ModelObjectSection<Role> {

	public RoleSection(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public RoleSection(TOCData data) {
		super(data);
	}

	@Override
	public ModelObjectType getModelObjectType() {
		return ModelObjectType.Role;
	}

}
