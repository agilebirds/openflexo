package org.openflexo.technologyadapter.emf.model;

import java.util.List;
import java.util.Set;

import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.localization.Language;

// This is a stub, please replace this class
public class EMFInstance implements IFlexoOntologyIndividual {

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
	public IFlexoOntology getFlexoOntology() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSuperConceptOf(IFlexoOntologyConcept concept) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSubConceptOf(IFlexoOntologyConcept concept) {
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
	public Object getPropertyValue(IFlexoOntologyStructuralProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPropertyValue(IFlexoOntologyStructuralProperty property, Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getAnnotationValue(IFlexoOntologyDataProperty property, Language language) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAnnotationValue(Object value, IFlexoOntologyDataProperty property, Language language) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getAnnotationObjectValue(IFlexoOntologyObjectProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAnnotationObjectValue(Object value, IFlexoOntologyObjectProperty property, Language language) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object addPropertyStatement(IFlexoOntologyObjectProperty property, IFlexoOntologyConcept object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addPropertyStatement(IFlexoOntologyStructuralProperty property, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addPropertyStatement(IFlexoOntologyStructuralProperty property, String value, Language language) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addDataPropertyStatement(IFlexoOntologyDataProperty property, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<? extends IFlexoOntologyStructuralProperty> getPropertiesTakingMySelfAsRange() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<? extends IFlexoOntologyStructuralProperty> getPropertiesTakingMySelfAsDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equalsToConcept(IFlexoOntologyConcept o) {
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
	public List<? extends IFlexoOntologyClass> getTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addType(IFlexoOntologyClass type) {
		// TODO Auto-generated method stub
		return null;
	}

}
