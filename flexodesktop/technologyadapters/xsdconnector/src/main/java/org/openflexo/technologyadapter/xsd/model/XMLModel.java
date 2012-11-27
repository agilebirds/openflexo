package org.openflexo.technologyadapter.xsd.model;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.technologyadapter.xsd.rm.FlexoXMLModelResource;

public class XMLModel extends XSOntology implements FlexoModel<XSDMetaModel> {

	protected static final Logger logger = Logger.getLogger(XMLModel.class.getPackage().getName());

	private FlexoXMLModelResource modelResource;
	private FlexoProject project;

	public XMLModel(String ontologyURI, File xsdFile, OntologyLibrary library) {
		super(ontologyURI, xsdFile, library);
	}

	@Override
	public XSDMetaModel getMetaModel() {
		logger.warning("Access to meta model not implemented yet");
		return null;
	}

	@Override
	public FlexoXMLModelResource getFlexoResource() {
		return modelResource;
	}

	@Override
	public void setFlexoResource(@SuppressWarnings("rawtypes") FlexoResource resource) throws DuplicateResourceException {
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

	@Deprecated
	@Override
	public String getInspectorName() {
		return Inspectors.VE.PROJECT_ONTOLOGY_INSPECTOR;
	}

	@Deprecated
	@Override
	public String getClassNameKey() {
		return "XSD_project_ontology";
	}

}
