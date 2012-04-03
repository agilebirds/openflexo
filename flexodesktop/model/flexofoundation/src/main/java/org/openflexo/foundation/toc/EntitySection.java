package org.openflexo.foundation.toc;

import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class EntitySection extends ModelObjectSection<DMEntity> {

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

}
