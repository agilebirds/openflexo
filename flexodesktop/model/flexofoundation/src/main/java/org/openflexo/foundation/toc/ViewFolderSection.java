package org.openflexo.foundation.toc;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.foundation.view.ViewFolder;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class ViewFolderSection extends ModelObjectSection<ViewFolder> {

	private static final String DOC_TEMPLATE = "docx_tocentry_view_folder.xml.vm";

	public ViewFolderSection(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public ViewFolderSection(TOCData data) {
		super(data);
	}

	@Override
	public ModelObjectType getModelObjectType() {
		return ModelObjectType.ViewFolder;
	}

	public ViewFolder getViewFolder() {
		return getModelObject(true);
	}

	public void setViewFolder(ViewFolder viewFolder) {
		setModelObject(viewFolder);
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
	public FlexoModelObject getObject() {
		System.out.println("Je suis la section ViewSection " + getTitle());
		System.out.println("On me demande mon objet");
		FlexoModelObject returned = getModelObject(false);
		System.out.println("Je repond que c'est " + returned + " of " + (returned != null ? returned.getClass().getSimpleName() : "null"));
		return returned;
	}*/

}
