package org.openflexo.foundation.ontology;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.CustomType;

public class SubPropertyOfProperty implements CustomType {

	public static SubPropertyOfProperty getSubPropertyOfProperty(OntologyProperty anOntologyProperty) {
		if (anOntologyProperty == null) {
			return null;
		}
		if (anOntologyProperty.getOntologyLibrary() != null) {
			if (anOntologyProperty.getOntologyLibrary().subpropertiesOfProperty.get(anOntologyProperty) != null) {
				return anOntologyProperty.getOntologyLibrary().subpropertiesOfProperty.get(anOntologyProperty);
			} else {
				SubPropertyOfProperty returned = new SubPropertyOfProperty(anOntologyProperty);
				anOntologyProperty.getOntologyLibrary().subpropertiesOfProperty.put(anOntologyProperty, returned);
				return returned;
			}
		}
		return null;
	}

	private OntologyProperty ontologyProperty;

	private SubPropertyOfProperty(OntologyProperty anOntologyProperty) {
		this.ontologyProperty = anOntologyProperty;
	}

	public OntologyProperty getOntologyProperty() {
		return ontologyProperty;
	}

	@Override
	public Class getBaseClass() {
		return OntologyProperty.class;
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

		private SubDataPropertyOfProperty(OntologyDataProperty anOntologyProperty) {
			super(anOntologyProperty);
		}

		@Override
		public OntologyDataProperty getOntologyProperty() {
			return (OntologyDataProperty) super.getOntologyProperty();
		}

		@Override
		public Class getBaseClass() {
			return OntologyDataProperty.class;
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

		private SubObjectPropertyOfProperty(OntologyObjectProperty anOntologyProperty) {
			super(anOntologyProperty);
		}

		@Override
		public OntologyObjectProperty getOntologyProperty() {
			return (OntologyObjectProperty) super.getOntologyProperty();
		}

		@Override
		public Class getBaseClass() {
			return OntologyObjectProperty.class;
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
