package org.openflexo.docxparser.dto.api;

import org.openflexo.docxparser.dto.ParsedHtml;

public interface IParsedFlexoEPI {
	public String getEditionPatternInstanceID();

	public String getModelObjectReference();

	public String getEditionPatternURI();

	public String getBindingPath();

	public String getValue();

	public abstract ParsedHtml getStyledValue();

	public abstract String getMultilineValue();

}
