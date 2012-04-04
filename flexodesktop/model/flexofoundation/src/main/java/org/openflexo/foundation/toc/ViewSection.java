package org.openflexo.foundation.toc;

import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class ViewSection extends ModelObjectSection<ViewDefinition> {

	public ViewSection(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public ViewSection(TOCData data) {
		super(data);
	}

	@Override
	public ModelObjectType getModelObjectType() {
		return ModelObjectType.View;
	}

	public ViewDefinition getViewDefinition() {
		return getModelObject(true);
	}

	public void setViewDefinition(ViewDefinition view) {
		setModelObject(view);
	}

}
