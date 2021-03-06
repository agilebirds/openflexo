package org.openflexo.foundation.toc;

import org.openflexo.foundation.toc.TOCDataBinding.TOCBindingAttribute;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public abstract class ControlSection extends TOCEntry {

	// private String

	public static enum ControlSectionBindingAttribute implements TOCBindingAttribute {
		iteration, condition, iterationCondition
	}

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
