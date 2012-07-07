package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public class ObjectPropertyPatternRole extends PropertyPatternRole {

	private String rangeURI;

	public ObjectPropertyPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.ObjectProperty;
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
		return OntologyObjectProperty.class;
	}

	@Override
	public OntologyObjectProperty getParentProperty() {
		return (OntologyObjectProperty) super.getParentProperty();
	}

	public void setParentProperty(OntologyObjectProperty ontologyProperty) {
		super.setParentProperty(ontologyProperty);
	}

	public String _getRangeURI() {
		return rangeURI;
	}

	public void _setRangeURI(String domainURI) {
		this.rangeURI = domainURI;
	}

	public OntologyClass getRange() {
		getViewPoint().loadWhenUnloaded();
		return getViewPoint().getViewpointOntology().getClass(_getRangeURI());
	}

	public void setRange(OntologyClass c) {
		_setRangeURI(c != null ? c.getURI() : null);
	}

}
