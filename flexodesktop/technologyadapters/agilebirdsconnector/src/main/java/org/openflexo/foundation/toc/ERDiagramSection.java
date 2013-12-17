package org.openflexo.foundation.toc;

import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class ERDiagramSection extends ModelObjectSection<ERDiagram> {

	protected static final String DOC_TEMPLATE = "docx_tocentry_diagram.xml.vm";

	public ERDiagramSection(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public ERDiagramSection(TOCData data) {
		super(data);
	}

	@Override
	public ModelObjectType getModelObjectType() {
		return ModelObjectType.ERDiagram;
	}

	public ERDiagram getDiagram() {
		return getModelObject(true);
	}

	public void setDiagram(ERDiagram diagram) {
		setModelObject(diagram);
	}

	@Override
	public boolean isERDiagram() {
		return true;
	}

}
