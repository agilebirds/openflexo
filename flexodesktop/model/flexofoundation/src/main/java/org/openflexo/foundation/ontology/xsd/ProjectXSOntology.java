package org.openflexo.foundation.ontology.xsd;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.ProjectOWLOntology;
import org.openflexo.foundation.ontology.ProjectOntology;
import org.openflexo.foundation.ontology.ProjectOntologyLibrary;
import org.openflexo.foundation.ontology.owl.OWLClass;
import org.openflexo.foundation.ontology.owl.OWLOntology.OntologyNotFoundException;
import org.openflexo.foundation.ontology.owl.OWLProperty;
import org.openflexo.foundation.ontology.owl.OntologyRestrictionClass;
import org.openflexo.foundation.ontology.owl.OntologyRestrictionClass.RestrictionType;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectOntologyResource;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoXMLModelResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.utils.FlexoProjectFile;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ProjectXSOntology extends XSOntology implements ProjectOntology {

	protected static final Logger logger = Logger.getLogger(ProjectXSOntology.class.getPackage().getName());
	
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

	public static ProjectXSOntology createNewProjectOntology(FlexoProject project) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("createNewProjectOntology(), project=" + project);
		}
		logger.info("-------------> Create XSD ontology for " + project.getProjectName());
		File owlFile = ProjectRestructuration.getExpectedProjectOntologyFile(project, project.getProjectName());
		FlexoProjectFile ontologyFile = new FlexoProjectFile(owlFile, project);

		ProjectXSOntology newProjectOntology = createProjectOntology(project.getURI(), owlFile, project.getProjectOntologyLibrary());
		project.getProjectOntologyLibrary().registerOntology(newProjectOntology);

		FlexoXMLModelResource ontologyRes;
		try {
			ontologyRes = new FlexoXMLModelResource(project, newProjectOntology, ontologyFile);
		} catch (InvalidFileNameException e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("This should not happen: invalid file " + ontologyFile);
			}
			return null;
		} catch (DuplicateResourceException e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("This should not happen: DuplicateResourceException for " + ontologyFile);
			}
			return null;
		}
		try {
			project.registerResource(ontologyRes);
		} catch (Exception e1) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}

		try {
			ontologyRes.saveResourceData();
		} catch (Exception e1) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}

		return newProjectOntology;
	}
	
	private static ProjectXSOntology createProjectOntology(String anURI, File xsdFile, ProjectOntologyLibrary ontologyLibrary) {
		ProjectXSOntology returned = new ProjectXSOntology(anURI, xsdFile, ontologyLibrary);

		return returned;
	}
}
