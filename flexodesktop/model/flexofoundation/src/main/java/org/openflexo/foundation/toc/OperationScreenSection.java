package org.openflexo.foundation.toc;

import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class OperationScreenSection extends ModelObjectSection<OperationComponentDefinition> {

	public OperationScreenSection(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public OperationScreenSection(TOCData data) {
		super(data);
	}

	@Override
	public ModelObjectType getModelObjectType() {
		return ModelObjectType.OperationScreen;
	}

}
