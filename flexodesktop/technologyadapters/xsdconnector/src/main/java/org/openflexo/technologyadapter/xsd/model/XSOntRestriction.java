package org.openflexo.technologyadapter.xsd.model;

public abstract class XSOntRestriction extends XSOntClass {

	protected XSOntRestriction(XSOntology ontology) {
		super(ontology, null, null);
	}

	public boolean isAttributeRestriction() {
		return false;
	}

	public XSOntAttributeRestriction asAttributeRestriction() {
		if (isAttributeRestriction()) {
			return (XSOntAttributeRestriction) this;
		}
		return null;
	}

}
