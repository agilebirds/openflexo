package org.openflexo.foundation.toc;

import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class RoleSection extends ModelObjectSection<Role> {

	protected static final String DOC_TEMPLATE = "docx_tocentry_individualrole.xml.vm";

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

	public Role getRole() {
		return getModelObject(true);
	}

	public void setRole(Role role) {
		setModelObject(role);
	}

	@Override
	public String getDefaultTemplateName() {
		return DOC_TEMPLATE;
	}

}
