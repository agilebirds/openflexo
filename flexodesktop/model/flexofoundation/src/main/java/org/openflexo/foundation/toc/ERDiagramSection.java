package org.openflexo.foundation.toc;

import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class ERDiagramSection extends ModelObjectSection<ERDiagram> {

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
	public String getDefaultTemplateName() {
		return null;
	}

}
