package org.openflexo.foundation.ontology.xsd;

import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyProperty;

public abstract class XSOntProperty extends AbstractXSOntObject implements OntologyProperty, XSOntologyURIDefinitions {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSOntProperty.class.getPackage()
			.getName());

	protected XSOntProperty(XSOntology ontology, String name, String uri) {
		super(ontology, name, uri);
	}

	@Override
	public boolean isAnnotationProperty() {
		return false;
	}

	@Override
	public OntologyObject getDomain() {
		// TODO Ask Sylvain
		return null;
	}

}
