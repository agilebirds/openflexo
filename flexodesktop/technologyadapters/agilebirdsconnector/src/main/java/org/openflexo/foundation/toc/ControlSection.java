package org.openflexo.foundation.toc;

import org.openflexo.foundation.xml.FlexoTOCBuilder;

public abstract class ControlSection extends TOCEntry {

	public ControlSection(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public ControlSection(TOCData data) {
		super(data);
	}

	@Override
	public String getDefaultTemplateName() {
		return null;
	}

	@Override
	public boolean getRenderSectionTitle() {
		return false;
	}

	@Override
	public boolean getRenderContent() {
		return false;
	}
}
