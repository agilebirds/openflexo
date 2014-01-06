package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;

public abstract class ObjectPropertyPatternRole<P extends IFlexoOntologyObjectProperty> extends PropertyPatternRole<P> {

	private String rangeURI;

	public ObjectPropertyPatternRole() {
		super();
	}

	@Override
	public Type getType() {
		if (getParentProperty() == null) {
			return IFlexoOntologyObjectProperty.class;
		}
		return super.getType();
	}

	@Override
	public String getPreciseType() {
		if (getParentProperty() != null) {
			return getParentProperty().getName();
		}
		return "";
	}

	@Override
	public IFlexoOntologyObjectProperty getParentProperty() {
		return (IFlexoOntologyObjectProperty) super.getParentProperty();
	}

	public void setParentProperty(IFlexoOntologyObjectProperty ontologyProperty) {
		super.setParentProperty(ontologyProperty);
	}

	public String _getRangeURI() {
		return rangeURI;
	}

	public void _setRangeURI(String domainURI) {
		this.rangeURI = domainURI;
	}

	public IFlexoOntologyClass getRange() {
		return getVirtualModel().getOntologyClass(_getRangeURI());
	}

	public void setRange(IFlexoOntologyClass c) {
		_setRangeURI(c != null ? c.getURI() : null);
	}

}
