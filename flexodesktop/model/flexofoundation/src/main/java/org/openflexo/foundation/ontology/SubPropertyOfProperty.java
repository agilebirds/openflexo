package org.openflexo.foundation.ontology;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.CustomType;

public class SubPropertyOfProperty implements CustomType {

	public static SubPropertyOfProperty getSubPropertyOfProperty(IFlexoOntologyStructuralProperty anOntologyProperty) {
		if (anOntologyProperty == null) {
			return null;
		}
		return anOntologyProperty.getTechnologyAdapter().getTechnologyContextManager().getSubPropertyOfProperty(anOntologyProperty);
	}

	private IFlexoOntologyStructuralProperty ontologyProperty;

	public SubPropertyOfProperty(IFlexoOntologyStructuralProperty anOntologyProperty) {
		this.ontologyProperty = anOntologyProperty;
	}

	public IFlexoOntologyStructuralProperty getOntologyProperty() {
		return ontologyProperty;
	}

	@Override
	public Class getBaseClass() {
		return IFlexoOntologyStructuralProperty.class;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		// System.out.println("isTypeAssignableFrom " + aType + " (i am a " + this + ")");
		if (aType instanceof SubPropertyOfProperty) {
			return ontologyProperty.isSuperConceptOf(((SubPropertyOfProperty) aType).getOntologyProperty());
		}
		return false;
	}

	@Override
	public String simpleRepresentation() {
		return "Property" + ":" + ontologyProperty.getName();
	}

	@Override
	public String fullQualifiedRepresentation() {
		return "Property" + ":" + ontologyProperty.getURI();
	}

	public static class SubDataPropertyOfProperty extends SubPropertyOfProperty {

		private SubDataPropertyOfProperty(IFlexoOntologyDataProperty anOntologyProperty) {
			super(anOntologyProperty);
		}

		@Override
		public IFlexoOntologyDataProperty getOntologyProperty() {
			return (IFlexoOntologyDataProperty) super.getOntologyProperty();
		}

		@Override
		public Class getBaseClass() {
			return IFlexoOntologyDataProperty.class;
		}

		@Override
		public String simpleRepresentation() {
			return "DataProperty" + ":" + getOntologyProperty().getName();
		}

		@Override
		public String fullQualifiedRepresentation() {
			return "DataProperty" + ":" + getOntologyProperty().getURI();
		}

	}

	public static class SubObjectPropertyOfProperty extends SubPropertyOfProperty {

		private SubObjectPropertyOfProperty(IFlexoOntologyObjectProperty anOntologyProperty) {
			super(anOntologyProperty);
		}

		@Override
		public IFlexoOntologyObjectProperty getOntologyProperty() {
			return (IFlexoOntologyObjectProperty) super.getOntologyProperty();
		}

		@Override
		public Class getBaseClass() {
			return IFlexoOntologyObjectProperty.class;
		}

		@Override
		public String simpleRepresentation() {
			return "ObjectProperty" + ":" + getOntologyProperty().getName();
		}

		@Override
		public String fullQualifiedRepresentation() {
			return "ObjectProperty" + ":" + getOntologyProperty().getURI();
		}

	}

}
