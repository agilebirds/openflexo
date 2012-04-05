package org.openflexo.foundation.toc;

import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class ProcessFolderSection extends ModelObjectSection<ProcessFolder> {

	protected static final String DOC_TEMPLATE = "docx_tocentry_processfolder.xml.vm";

	public ProcessFolderSection(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public ProcessFolderSection(TOCData data) {
		super(data);
	}

	@Override
	public ModelObjectType getModelObjectType() {
		return ModelObjectType.Role;
	}

	public ProcessFolder getProcessFolder() {
		return getModelObject(true);
	}

	public void setProcessFolder(ProcessFolder pf) {
		setModelObject(pf);
	}

	@Override
	public String getDefaultTemplateName() {
		return DOC_TEMPLATE;
	}

}
