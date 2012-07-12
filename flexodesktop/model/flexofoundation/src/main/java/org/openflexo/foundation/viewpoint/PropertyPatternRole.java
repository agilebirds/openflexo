package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public class PropertyPatternRole extends OntologicObjectPatternRole {

	private String parentPropertyURI;
	private String domainURI;

	public PropertyPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	public String _getParentPropertyURI() {
		return parentPropertyURI;
	}

	public void _setParentPropertyURI(String parentPropertyURI) {
		this.parentPropertyURI = parentPropertyURI;
	}

	public OntologyProperty getParentProperty() {
		if (getViewPoint() != null) {
			getViewPoint().loadWhenUnloaded();
			if (getViewPoint().getViewpointOntology() != null) {
				return getViewPoint().getViewpointOntology().getProperty(_getParentPropertyURI());
			}
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
		return getViewPoint().getViewpointOntology().getClass(_getDomainURI());
	}

	public void setDomain(OntologyClass c) {
		_setDomainURI(c != null ? c.getURI() : null);
	}

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.Property;
	}

	@Override
	public String getPreciseType() {
		if (getParentProperty() != null) {
			return getParentProperty().getName();
		}
		return "";
	}

	@Override
	public Class<?> getAccessedClass() {
		return OntologyProperty.class;
	}

}
