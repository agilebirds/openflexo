package org.openflexo.foundation.ontology.xsd;

public class XSOntRestriction extends XSOntClass {

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
