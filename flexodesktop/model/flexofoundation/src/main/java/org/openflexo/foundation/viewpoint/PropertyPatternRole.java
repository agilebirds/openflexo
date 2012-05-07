package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyProperty;

public abstract class PropertyPatternRole extends OntologicObjectPatternRole {

	private String parentPropertyURI;
	private String domainURI;

	public String _getParentPropertyURI() {
		return parentPropertyURI;
	}

	public void _setParentPropertyURI(String parentPropertyURI) {
		this.parentPropertyURI = parentPropertyURI;
	}

	public OntologyProperty getParentProperty() {
		if (getViewPoint() != null) {
			getViewPoint().loadWhenUnloaded();
		}
		if (getOntologyLibrary() != null) {
			return getOntologyLibrary().getProperty(_getParentPropertyURI());
		}
		return null;
	}

	public void setParentProperty(OntologyProperty ontologyProperty) {
		parentPropertyURI = (ontologyProperty != null ? ontologyProperty.getURI() : null);
	}

	public String _getDomainURI() {
		return domainURI;
	}

	public void _setDomainURI(String domainURI) {
		this.domainURI = domainURI;
	}

	public OntologyClass getDomain() {
		getViewPoint().loadWhenUnloaded();
		return getOntologyLibrary().getClass(_getDomainURI());
	}

	public void setDomain(OntologyClass c) {
		_setDomainURI(c != null ? c.getURI() : null);
	}

}
