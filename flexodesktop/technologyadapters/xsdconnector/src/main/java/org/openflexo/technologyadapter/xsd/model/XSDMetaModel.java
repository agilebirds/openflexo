package org.openflexo.technologyadapter.xsd.model;

import java.io.File;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;

public class XSDMetaModel extends XSOntology implements FlexoMetaModel {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSDMetaModel.class.getPackage()
			.getName());

	public XSDMetaModel(String ontologyURI, File xsdFile, OntologyLibrary library) {
		super(ontologyURI, xsdFile, library);
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public void setIsReadOnly(boolean b) {
	}

	@Override
	public void save() throws SaveResourceException {
		logger.warning("Imported ontologies are not supposed to be saved !!!");
	}

	@Deprecated
	@Override
	public String getInspectorName() {
		return Inspectors.VE.IMPORTED_ONTOLOGY_INSPECTOR;
	}

	@Deprecated
	@Override
	public String getClassNameKey() {
		return null;
	}

}
