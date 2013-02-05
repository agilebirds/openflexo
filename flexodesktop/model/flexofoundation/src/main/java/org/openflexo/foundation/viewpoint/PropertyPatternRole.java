package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.SubPropertyOfProperty;
import org.openflexo.foundation.technologyadapter.FlexoOntologyModelSlot;
import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.ConceptActorReference;
import org.openflexo.foundation.view.EditionPatternInstance;

public abstract class PropertyPatternRole<T extends IFlexoOntologyStructuralProperty> extends OntologicObjectPatternRole<T> {

	private String parentPropertyURI;
	private String domainURI;

	public PropertyPatternRole(VirtualModel.VirtualModelBuilder builder) {
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
		if (getVirtualModel() != null) {
			return getVirtualModel().getOntologyProperty(_getParentPropertyURI());
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
		return getVirtualModel().getOntologyClass(_getDomainURI());
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
	public ActorReference<T> makeActorReference(T object, EditionPatternInstance epi) {
		return new ConceptActorReference<T>(object, this, epi);
	}

	@Override
	public boolean defaultBehaviourIsToBeDeleted() {
		return false;
	}

	@Override
	public FlexoOntologyModelSlot<?, ?> getModelSlot() {
		FlexoOntologyModelSlot<?, ?> returned = super.getModelSlot();
		if (returned == null) {
			if (getVirtualModel() != null && getVirtualModel().getModelSlots(FlexoOntologyModelSlot.class).size() > 0) {
				return getVirtualModel().getModelSlots(FlexoOntologyModelSlot.class).get(0);
			}
		}
		return returned;
	}

}
