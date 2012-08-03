package org.openflexo.foundation.ontology.xsd;

import org.openflexo.foundation.ontology.OntologyProperty;

public abstract class XSOntProperty extends XSOntObject implements OntologyProperty, XSOntologyURIDefinitions {

	protected XSOntProperty(XSOntology ontology, String name, String uri) {
		super(ontology, name, uri);
	}

	@Override
	public boolean isAnnotationProperty() {
		return false;
	}

}
