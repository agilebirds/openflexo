package org.openflexo.technologyadapter.emf.model;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.technologyadapter.emf.rm.FlexoEMFModelResource;

public class EMFModel extends TemporaryFlexoModelObject implements FlexoModel<EMFMetaModel>, FlexoOntology {

	protected static final Logger logger = Logger.getLogger(EMFModel.class.getPackage().getName());

	private FlexoEMFModelResource modelResource;
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
	public FlexoEMFModelResource getFlexoResource() {
		return modelResource;
	}

	@Override
	public void setFlexoResource(@SuppressWarnings("rawtypes") FlexoResource resource) throws DuplicateResourceException {
		if (resource instanceof FlexoEMFModelResource) {
			this.modelResource = (FlexoEMFModelResource) resource;
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

}
