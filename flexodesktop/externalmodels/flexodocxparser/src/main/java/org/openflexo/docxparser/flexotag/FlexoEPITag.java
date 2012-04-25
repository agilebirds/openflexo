package org.openflexo.docxparser.flexotag;

import java.util.StringTokenizer;

public class FlexoEPITag extends AbstractFlexoTag {

	public static final String EPI_TAG = "__EPI_";

	private String editionPatternURI;
	private String bindingPath;
	private String modelObjectReference;

	public FlexoEPITag(String tagValue) throws FlexoTagFormatException {
		super(tagValue);
	}

	@Override
	protected String getTag() {
		return EPI_TAG;
	}

	// EPI instance ID, EP URI, View ID, bindingpath
	public static String buildFlexoEPITag(String epiInstanceID, String editionPatternURI, String bindingPath, String modelObjectReference) {
		return buildFlexoTag(EPI_TAG, epiInstanceID, "", editionPatternURI + ',' + bindingPath + ',' + modelObjectReference);
	}

	@Override
	protected void parseOptionalInfo(String optionalInfo) {
		super.parseOptionalInfo(optionalInfo);
		if (optionalInfo != null) {
			StringTokenizer st = new StringTokenizer(optionalInfo, ",");
			if (st.hasMoreTokens()) {
				editionPatternURI = st.nextToken();
			}
			if (st.hasMoreTokens()) {
				bindingPath = st.nextToken();
			}
			if (st.hasMoreTokens()) {
				modelObjectReference = st.nextToken();
				while (st.hasMoreTokens()) {
					modelObjectReference += ',' + st.nextToken();
				}
			}

		}
	}

	public String getEditionPatternInstanceID() {
		return getFlexoId();
	}

	public String getEditionPatternURI() {
		return editionPatternURI;
	}

	public String getModelObjectReference() {
		return modelObjectReference;
	}

	public String getBindingPath() {
		return bindingPath;
	}

}
