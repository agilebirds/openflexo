package org.openflexo.docxparser.dto;

import org.openflexo.docxparser.dto.api.IParsedFlexoEPI;
import org.openflexo.docxparser.flexotag.FlexoEPITag;

public class ParsedFlexoEPI implements IParsedFlexoEPI {

	private FlexoEPITag epiTag;

	private String value;

	public ParsedFlexoEPI(FlexoEPITag epiTag, String value) {
		this.epiTag = epiTag;
		this.value = value;
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

	public FlexoEPITag getEpiTag() {
		return epiTag;
	}

	public void setEpiTag(FlexoEPITag epiTag) {
		this.epiTag = epiTag;
	}

}
