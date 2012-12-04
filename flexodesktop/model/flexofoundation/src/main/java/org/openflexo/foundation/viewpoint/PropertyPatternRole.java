package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.ConceptActorReference;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public class PropertyPatternRole<T extends OntologyProperty> extends OntologicObjectPatternRole<T> {

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
			return getViewPoint().getOntologyProperty(_getParentPropertyURI());
		}
		return null;
	}

	public void setParentProperty(OntologyProperty ontologyProperty) {
		parentPropertyURI = ontologyProperty != null ? ontologyProperty.getURI() : null;
	}

	public String _getDomainURI() {
		return domainURI;
	}

	public void _setDomainURI(String domainURI) {
		this.domainURI = domainURI;
	}

	public OntologyClass getDomain() {
		getViewPoint().loadWhenUnloaded();
		return getViewPoint().getOntologyClass(_getDomainURI());
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
	public Class<T> getAccessedClass() {
		return (Class<T>) OntologyProperty.class;
	}

	@Override
	public ActorReference<T> makeActorReference(T object, EditionPatternReference epRef) {
		return new ConceptActorReference<T>(object, this, epRef);
	}

	@Override
	public boolean defaultBehaviourIsToBeDeleted() {
		return false;
	}

}
