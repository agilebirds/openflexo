package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.SubPropertyOfProperty;
import org.openflexo.foundation.technologyadapter.FlexoOntologyModelSlot;
import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.ConceptActorReference;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public abstract class PropertyPatternRole<T extends IFlexoOntologyStructuralProperty> extends OntologicObjectPatternRole<T> {

	private String parentPropertyURI;
	private String domainURI;

	public PropertyPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Type getType() {
		if (getParentProperty() == null) {
			return IFlexoOntologyStructuralProperty.class;
		}
		return SubPropertyOfProperty.getSubPropertyOfProperty(getParentProperty());
	}

	public String _getParentPropertyURI() {
		return parentPropertyURI;
	}

	public void _setParentPropertyURI(String parentPropertyURI) {
		this.parentPropertyURI = parentPropertyURI;
	}

	public IFlexoOntologyStructuralProperty getParentProperty() {
		if (getViewPoint() != null) {
			return getViewPoint().getOntologyProperty(_getParentPropertyURI());
		}
		return null;
	}

	public void setParentProperty(IFlexoOntologyStructuralProperty ontologyProperty) {
		parentPropertyURI = ontologyProperty != null ? ontologyProperty.getURI() : null;
	}

	public String _getDomainURI() {
		return domainURI;
	}

	public void _setDomainURI(String domainURI) {
		this.domainURI = domainURI;
	}

	public IFlexoOntologyClass getDomain() {
		return getViewPoint().getOntologyClass(_getDomainURI());
	}

	public void setDomain(IFlexoOntologyClass c) {
		_setDomainURI(c != null ? c.getURI() : null);
	}

	@Override
	public String getPreciseType() {
		if (getParentProperty() != null) {
			return getParentProperty().getName();
		}
		return "";
	}

	@Override
	public ActorReference<T> makeActorReference(T object, EditionPatternReference epRef) {
		return new ConceptActorReference<T>(object, this, epRef);
	}

	@Override
	public boolean defaultBehaviourIsToBeDeleted() {
		return false;
	}

	@Override
	public FlexoOntologyModelSlot<?, ?> getModelSlot() {
		FlexoOntologyModelSlot<?, ?> returned = super.getModelSlot();
		if (returned == null) {
			if (getViewPoint() != null && getViewPoint().getModelSlots(FlexoOntologyModelSlot.class).size() > 0) {
				return getViewPoint().getModelSlots(FlexoOntologyModelSlot.class).get(0);
			}
		}
		return returned;
	}

}
