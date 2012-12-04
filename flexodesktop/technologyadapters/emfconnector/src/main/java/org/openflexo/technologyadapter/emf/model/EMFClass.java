package org.openflexo.technologyadapter.emf.model;

import java.util.List;
import java.util.Set;

import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.localization.Language;

// This is a stub, please replace this class
public class EMFClass implements OntologyClass {

	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String aName) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getIsReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FlexoOntology getFlexoOntology() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSuperConceptOf(OntologyObject concept) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSubConceptOf(OntologyObject concept) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDescription(String aDescription) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDisplayableDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHTMLDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyLibrary getOntologyLibrary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPropertyValue(OntologyProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPropertyValue(OntologyProperty property, Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getAnnotationValue(OntologyDataProperty property, Language language) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAnnotationValue(Object value, OntologyDataProperty property, Language language) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getAnnotationObjectValue(OntologyObjectProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAnnotationObjectValue(Object value, OntologyObjectProperty property, Language language) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object addPropertyStatement(OntologyObjectProperty property, OntologyObject object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addPropertyStatement(OntologyProperty property, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addPropertyStatement(OntologyProperty property, String value, Language language) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addDataPropertyStatement(OntologyDataProperty property, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<? extends OntologyProperty> getPropertiesTakingMySelfAsRange() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<? extends OntologyProperty> getPropertiesTakingMySelfAsDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equalsToConcept(OntologyObject o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerEditionPatternReference(EditionPatternInstance editionPatternInstance, PatternRole patternRole) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterEditionPatternReference(EditionPatternInstance editionPatternInstance, PatternRole patternRole) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSuperClassOf(OntologyClass aClass) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<? extends OntologyClass> getSuperClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<? extends OntologyClass> getAllSuperClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addSuperClass(OntologyClass aClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNamedClass() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isThing() {
		// TODO Auto-generated method stub
		return false;
	}

}
