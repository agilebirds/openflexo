package org.openflexo.foundation.ontology.xsd;

import java.util.Set;
import java.util.logging.Level;

import org.openflexo.foundation.ontology.AbstractOntologyObject;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.localization.Language;

public abstract class XSOntObject extends AbstractOntologyObject implements OntologyObject, XSOntologyURIDefinitions {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSOntObject.class.getPackage()
			.getName());

	private String uri;
	private String name;
	private XSOntology ontology;

	protected XSOntObject(XSOntology ontology, String name, String uri) {
		this.name = name;
		this.uri = uri;
		this.ontology = ontology;
	}

	@Override
	public String getURI() {
		return uri;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String aName) {
		name = aName;
		// TODO the following warning and check if read only?
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("The ontology object name changed, renaming of the object URI not implemented yet");
		}
	}

	@Override
	public boolean getIsReadOnly() {
		// TODO
		return false;
	}

	@Override
	public FlexoOntology getFlexoOntology() {
		return ontology;
	}

	@Override
	public Object getPropertyValue(OntologyProperty property) {
		// Stub
		return null;
	}

	@Override
	public void setPropertyValue(OntologyProperty property, Object newValue) {
		// Stub
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
		// TODO How does the localization works?
		return null;
	}

	@Override
	public void setDescription(String aDescription) {
		// TODO How does the localization works?

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
	public String getInspectorName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClassNameKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addPropertyStatement(OntologyObjectProperty property, OntologyObject object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object addPropertyStatement(OntologyProperty property, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object addPropertyStatement(OntologyProperty property, String value, Language language) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object addDataPropertyStatement(OntologyDataProperty property, Object value) {
		throw new UnsupportedOperationException();
	}
}
