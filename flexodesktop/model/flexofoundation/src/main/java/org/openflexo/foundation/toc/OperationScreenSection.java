package org.openflexo.foundation.toc;

import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class OperationScreenSection extends ModelObjectSection<OperationComponentDefinition> {

	protected static final String DOC_TEMPLATE = "docx_tocentry_screen.xml.vm";

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

	public OperationComponentDefinition getOperationScreen() {
		return getModelObject(true);
	}

	public void setOperationScreen(OperationComponentDefinition operationScreen) {
		setModelObject(operationScreen);
	}

	@Override
	public String getDefaultTemplateName() {
		return DOC_TEMPLATE;
	}

}
