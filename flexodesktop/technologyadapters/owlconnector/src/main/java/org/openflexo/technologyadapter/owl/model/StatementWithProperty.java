package org.openflexo.technologyadapter.owl.model;

import java.lang.reflect.Type;

import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.viewpoint.TechnologySpecificCustomType;

public class StatementWithProperty implements TechnologySpecificCustomType {

	public static StatementWithProperty getStatementWithProperty(IFlexoOntologyStructuralProperty aProperty) {
		if (aProperty == null) {
			return null;
		}
		return ((OWLOntologyLibrary) aProperty.getTechnologyAdapter().getTechnologyContextManager()).getStatementWithProperty(aProperty);
	}

	private IFlexoOntologyStructuralProperty property;

	public StatementWithProperty(IFlexoOntologyStructuralProperty aProperty) {
		this.property = aProperty;
	}

	public IFlexoOntologyStructuralProperty getProperty() {
		return property;
	}

	@Override
	public Class getBaseClass() {
		if (property != null) {
			return property.getClass();
		}
		return IFlexoOntologyStructuralProperty.class;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		// System.out.println("isTypeAssignableFrom " + aType + " (i am a " + this + ")");
		if (aType instanceof StatementWithProperty) {
			return property.isSuperConceptOf(((StatementWithProperty) aType).getProperty());
		}
		return false;
	}

	@Override
	public String simpleRepresentation() {
		return "Statement:" + property.getName();
	}

	@Override
	public String fullQualifiedRepresentation() {
		return simpleRepresentation();
	}

	@Override
	public String toString() {
		return simpleRepresentation();
	}

	@Override
	public TechnologyAdapter getTechnologyAdapter() {
		if (getProperty() != null) {
			return getProperty().getTechnologyAdapter();
		}
		return null;
	}

}
