package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.SubPropertyOfProperty;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.ConceptActorReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;

@ModelEntity
@ImplementationClass(PropertyPatternRole.PropertyPatternRoleImpl.class)
public interface PropertyPatternRole<T extends IFlexoOntologyStructuralProperty> extends OntologicObjectPatternRole<T> {

	@PropertyIdentifier(type = String.class)
	public static final String PARENT_PROPERTY_URI_KEY = "parentPropertyURI";
	@PropertyIdentifier(type = String.class)
	public static final String DOMAIN_URI = "domainURI";

	@Getter(PARENT_PROPERTY_URI_KEY)
	@XMLAttribute
	public String _getParentPropertyURI();

	@Setter(PARENT_PROPERTY_URI_KEY)
	public void _setParentPropertyURI(String parentPropertyURI);

	@Getter(DOMAIN_URI)
	@XMLAttribute
	public String _getDomainURI();

	@Setter(DOMAIN_URI)
	public void _setDomainURI(String domainURI);

	public IFlexoOntologyStructuralProperty getParentProperty();

	public void setParentProperty(IFlexoOntologyStructuralProperty ontologyProperty);

	public IFlexoOntologyClass getDomain();

	public void setDomain(IFlexoOntologyClass c);

	public abstract class PropertyPatternRoleImpl<T extends IFlexoOntologyStructuralProperty> extends OntologicObjectPatternRoleImpl<T>
			implements PropertyPatternRole<T> {

		private String parentPropertyURI;
		private String domainURI;

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			FMLRepresentationOutput out = new FMLRepresentationOutput(context);
			out.append("PatternRole " + getName() + " as Property " + " from " + getModelSlot().getMetaModelURI() + " ;", context);
			return out.toString();
		}

		@Override
		public Type getType() {
			if (getParentProperty() == null) {
				return IFlexoOntologyStructuralProperty.class;
			}
			return SubPropertyOfProperty.getSubPropertyOfProperty(getParentProperty());
		}

		@Override
		public String _getParentPropertyURI() {
			return parentPropertyURI;
		}

		@Override
		public void _setParentPropertyURI(String parentPropertyURI) {
			this.parentPropertyURI = parentPropertyURI;
		}

		@Override
		public IFlexoOntologyStructuralProperty getParentProperty() {
			if (getVirtualModel() != null) {
				return getVirtualModel().getOntologyProperty(_getParentPropertyURI());
			}
			return null;
		}

		@Override
		public void setParentProperty(IFlexoOntologyStructuralProperty ontologyProperty) {
			parentPropertyURI = ontologyProperty != null ? ontologyProperty.getURI() : null;
		}

		@Override
		public String _getDomainURI() {
			return domainURI;
		}

		@Override
		public void _setDomainURI(String domainURI) {
			this.domainURI = domainURI;
		}

		@Override
		public IFlexoOntologyClass getDomain() {
			return getVirtualModel().getOntologyClass(_getDomainURI());
		}

		@Override
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
		public TypeAwareModelSlot<?, ?> getModelSlot() {
			TypeAwareModelSlot<?, ?> returned = super.getModelSlot();
			if (returned == null) {
				if (getVirtualModel() != null && getVirtualModel().getModelSlots(TypeAwareModelSlot.class).size() > 0) {
					return getVirtualModel().getModelSlots(TypeAwareModelSlot.class).get(0);
				}
			}
			return returned;
		}
	}
}
