package org.openflexo.foundation.toc;

import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class ViewSection extends ModelObjectSection<ViewDefinition> {

	private static final String DOC_TEMPLATE = "docx_tocentry_view.xml.vm";

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

	@Override
	public String getDefaultTemplateName() {
		return DOC_TEMPLATE;
	}

	/*@Override
	public FlexoModelObject getObject() {
		System.out.println("Je suis la section ViewSection " + getTitle());
		System.out.println("On me demande mon objet");
		FlexoModelObject returned = getModelObject(false);
		System.out.println("Je repond que c'est " + returned + " of " + (returned != null ? returned.getClass().getSimpleName() : "null"));
		return returned;
	}*/

}
