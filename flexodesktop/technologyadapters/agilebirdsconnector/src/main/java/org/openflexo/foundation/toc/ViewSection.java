package org.openflexo.foundation.toc;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.rm.ViewResource;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class ViewSection extends ModelObjectSection<View> {

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

	public View getView() {
		return getModelObject(true);
	}

	public void setView(View view) {
		setModelObject(view);
	}

	public ViewResource getViewResource() {
		if (getView() != null) {
			return getView().getResource();
		}
		return null;
	}

	public void setViewResource(ViewResource viewResource) {

	}

	@Override
	public void setValue(DataBinding<Object> value) {
		super.setValue(value);
	}

	@Override
	public String getDefaultTemplateName() {
		return DOC_TEMPLATE;
	}

	/*@Override
	public AgileBirdsObject getObject() {
		System.out.println("Je suis la section ViewSection " + getTitle());
		System.out.println("On me demande mon objet");
		AgileBirdsObject returned = getModelObject(false);
		System.out.println("Je repond que c'est " + returned + " of " + (returned != null ? returned.getClass().getSimpleName() : "null"));
		return returned;
	}*/

}
