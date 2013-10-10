package org.openflexo.foundation.toc;

import org.openflexo.foundation.Format;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class TOCDocumentationPresets extends TOCObject {

	private TOCRepositoryDefinition toc;

	private Format format;

	private String docType;

	/**
	 * Create a new GeneratedCodeRepository.
	 */
	public TOCDocumentationPresets(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public TOCDocumentationPresets(TOCData data) {
		super(data);
	}

	@Override
	public String getFullyQualifiedName() {
		return "DOCUMENTATION_PRESETS." + getName();
	}

	@Override
	public String getClassNameKey() {
		return "documentation_presets";
	}

	public TOCRepositoryDefinition getToc() {
		return toc;
	}

	public void setToc(TOCRepositoryDefinition toc) {
		this.toc = toc;
		setChanged();
		notifyObservers(new TOCModification("toc", null, toc));
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
		setChanged();
		notifyObservers(new TOCModification("format", null, format));
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
		setChanged();
		notifyObservers(new TOCModification("docType", null, docType));
	}

}
