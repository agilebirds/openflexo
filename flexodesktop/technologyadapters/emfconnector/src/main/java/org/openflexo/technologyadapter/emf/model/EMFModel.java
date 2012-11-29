package org.openflexo.technologyadapter.emf.model;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologicDataType;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.localization.Language;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;

public class EMFModel extends TemporaryFlexoModelObject implements FlexoModel<EMFModel, EMFMetaModel>, FlexoOntology {

	protected static final Logger logger = Logger.getLogger(EMFModel.class.getPackage().getName());

	private EMFModelResource modelResource;
	private FlexoProject project;

	public EMFModel(String ontologyURI, File xsdFile, OntologyLibrary library) {
		super();
	}

	@Override
	public EMFMetaModel getMetaModel() {
		logger.warning("Access to meta model not implemented yet");
		return null;
	}

	@Override
	public EMFModelResource getFlexoResource() {
		return modelResource;
	}

	@Override
	public void setFlexoResource(@SuppressWarnings("rawtypes") FlexoResource resource) throws DuplicateResourceException {
		if (resource instanceof EMFModelResource) {
			this.modelResource = (EMFModelResource) resource;
		}
	}

	public void setProject(FlexoProject project) {
		this.project = project;
	}

	@Override
	public FlexoProject getProject() {
		return project;
	}

	@Override
	public void save() throws SaveResourceException {
		getFlexoResource().saveResourceData();
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
	public String getHTMLDescription() {
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
	public String getOntologyURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyLibrary getOntologyLibrary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean loadWhenUnloaded() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLoaded() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLoading() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<? extends FlexoOntology> getAllImportedOntologies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends FlexoOntology> getImportedOntologies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends OntologyClass> getClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends OntologyIndividual> getIndividuals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends OntologyDataProperty> getDataProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends OntologyObjectProperty> getObjectProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyObject getOntologyObject(String objectURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyClass getClass(String classURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyIndividual getIndividual(String individualURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyObjectProperty getObjectProperty(String propertyURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyDataProperty getDataProperty(String propertyURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyProperty getProperty(String objectURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends OntologyClass> getAccessibleClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends OntologyIndividual> getAccessibleIndividuals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends OntologyObjectProperty> getAccessibleObjectProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends OntologyDataProperty> getAccessibleDataProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyClass createOntologyClass(String name, OntologyClass superClass) throws DuplicateURIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyClass createOntologyClass(String name) throws DuplicateURIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyIndividual createOntologyIndividual(String name, OntologyClass type) throws DuplicateURIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyObjectProperty createObjectProperty(String name, OntologyObjectProperty superProperty, OntologyClass domain,
			OntologyClass range) throws DuplicateURIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyDataProperty createDataProperty(String name, OntologyDataProperty superProperty, OntologyClass domain,
			OntologicDataType dataType) throws DuplicateURIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyClass getThingConcept() {
		// TODO Auto-generated method stub
		return null;
	}

}
