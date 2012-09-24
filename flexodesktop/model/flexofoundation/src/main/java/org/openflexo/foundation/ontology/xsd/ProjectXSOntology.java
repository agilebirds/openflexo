package org.openflexo.foundation.ontology.xsd;

import java.io.File;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.ProjectOntology;
import org.openflexo.foundation.ontology.owl.OWLClass;
import org.openflexo.foundation.ontology.owl.OWLOntology.OntologyNotFoundException;
import org.openflexo.foundation.ontology.owl.OWLProperty;
import org.openflexo.foundation.ontology.owl.OntologyRestrictionClass;
import org.openflexo.foundation.ontology.owl.OntologyRestrictionClass.RestrictionType;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoXMLModelResource;
import org.openflexo.foundation.rm.SaveResourceException;

import com.hp.hpl.jena.ontology.OntModel;

public class ProjectXSOntology extends XSOntology implements ProjectOntology {

	private FlexoXMLModelResource modelResource;
	private FlexoProject project;

	public ProjectXSOntology(String ontologyURI, File xsdFile, OntologyLibrary library) {
		super(ontologyURI, xsdFile, library);
		// TODO Auto-generated constructor stub
	}

	@Override
	public FlexoXMLModelResource getFlexoResource() {
		return modelResource;
	}

	@Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
		if (resource instanceof FlexoXMLModelResource) {
			this.modelResource = (FlexoXMLModelResource) resource;
		}
	}

	@Override
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
	public boolean importOntology(FlexoOntology flexoOntology) throws OntologyNotFoundException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public OntologyRestrictionClass createRestriction(OWLClass subject, OWLProperty property, RestrictionType restrictionType,
			int cardinality, OWLClass object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void describe() {
		// TODO Auto-generated method stub

	}

	@Override
	public OntModel getOntModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VE.PROJECT_ONTOLOGY_INSPECTOR;
	}

	@Override
	public String getClassNameKey() {
		return "XSD_project_ontology";
	}

}
