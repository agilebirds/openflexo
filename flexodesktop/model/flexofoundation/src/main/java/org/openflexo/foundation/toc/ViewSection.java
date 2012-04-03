package org.openflexo.foundation.toc;

import org.openflexo.foundation.view.View;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class ViewSection extends ModelObjectSection<View> {

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

}
