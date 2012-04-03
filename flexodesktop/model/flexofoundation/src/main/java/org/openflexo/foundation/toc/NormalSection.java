package org.openflexo.foundation.toc;

import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class NormalSection extends TOCEntry {

	private String content;

	public NormalSection(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public NormalSection(TOCData data) {
		super(data);
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

}
