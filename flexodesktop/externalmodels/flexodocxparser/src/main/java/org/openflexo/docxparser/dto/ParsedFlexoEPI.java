package org.openflexo.docxparser.dto;

import org.openflexo.docxparser.dto.api.IParsedFlexoEPI;
import org.openflexo.docxparser.flexotag.FlexoEPITag;

public class ParsedFlexoEPI implements IParsedFlexoEPI {

	private FlexoEPITag epiTag;

	private String value;

	private String multilineValue;

	private ParsedHtml styledValue;

	public ParsedFlexoEPI(FlexoEPITag epiTag, String value, String multilineValue, ParsedHtml styledValue) {
		this.epiTag = epiTag;
		this.multilineValue = multilineValue;
		this.styledValue = styledValue;
	}

	@Override
	public String getEditionPatternInstanceID() {
		return epiTag.getEditionPatternInstanceID();
	}

	@Override
	public String getModelObjectReference() {
		return epiTag.getModelObjectReference();
	}

	@Override
	public String getEditionPatternURI() {
		return epiTag.getEditionPatternURI();
	}

	@Override
	public String getBindingPath() {
		return epiTag.getBindingPath();
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getMultilineValue() {
		return multilineValue;
	}

	@Override
	public ParsedHtml getStyledValue() {
		return styledValue;
	}

	public FlexoEPITag getEpiTag() {
		return epiTag;
	}

	public void setEpiTag(FlexoEPITag epiTag) {
		this.epiTag = epiTag;
	}

}
