package org.openflexo.foundation.ontology.xsd;

import org.openflexo.toolbox.StringUtils;

import com.sun.xml.xsom.XSAttributeUse;

public class XSOntAttributeRestriction extends XSOntRestriction {

	private final XSOntDataProperty attributeProperty;
	private final XSAttributeUse attributeUse;

	protected XSOntAttributeRestriction(XSOntology ontology, XSAttributeUse attributeUse) {
		super(ontology);
		String propertyURI = ontology.getFetcher().getURI(attributeUse.getDecl());
		this.attributeProperty = ontology.getDataProperty(propertyURI);
		this.attributeUse = attributeUse;
	}

	@Override
	public boolean isAttributeRestriction() {
		return true;
	}

	public XSAttributeUse getAttributeUse() {
		return attributeUse;
	}

	public XSOntDataProperty getAttributeProperty() {
		return attributeProperty;
	}

	public boolean hasDefaultValue() {
		return StringUtils.isNotEmpty(getDefaultValue());
	}

	public String getDefaultValue() {
		if (attributeUse.getDefaultValue() != null) {
			return attributeUse.getDefaultValue().toString();
		}
		return null;
	}

	public boolean hasFixedValue() {
		return StringUtils.isNotEmpty(getFixedValue());
	}

	public String getFixedValue() {
		if (attributeUse.getFixedValue() != null) {
			return attributeUse.getFixedValue().toString();
		}
		return null;
	}

	public boolean isRequired() {
		return attributeUse.isRequired();
	}

	@Override
	public String getDisplayableDescription() {
		StringBuffer buffer = new StringBuffer("Attribute ");
		buffer.append(attributeUse.getDecl().getName());
		buffer.append(" (").append(attributeProperty.getDataType().toString()).append(") is ");
		if (isRequired()) {
			buffer.append("required");
		} else {
			buffer.append("optional");
		}
		if (hasDefaultValue()) {
			buffer.append(", default: '").append(getDefaultValue()).append("'");
		}
		if (hasFixedValue()) {
			buffer.append(", fixed: '").append(getFixedValue()).append("'");
		}
		return buffer.toString();
	}
}
