package org.openflexo.foundation.toc;

import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class EntitySection extends ModelObjectSection<DMEntity> {

	protected static final String DOC_TEMPLATE = "docx_tocentry_eoentity.xml.vm";

	public EntitySection(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public EntitySection(TOCData data) {
		super(data);
	}

	@Override
	public ModelObjectType getModelObjectType() {
		return ModelObjectType.Entity;
	}

	public DMEntity getEntity() {
		return getModelObject(true);
	}

	public void setEntity(DMEntity entity) {
		setModelObject(entity);
	}

	@Override
	public String getDefaultTemplateName() {
		return DOC_TEMPLATE;
	}

	@Override
	public boolean isIndividualEntity() {
		return true;
	}

}
