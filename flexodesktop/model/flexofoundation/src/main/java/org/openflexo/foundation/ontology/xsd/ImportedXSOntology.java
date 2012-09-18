package org.openflexo.foundation.ontology.xsd;

import java.io.File;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.ImportedOntology;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.rm.SaveResourceException;

public class ImportedXSOntology extends XSOntology implements ImportedOntology {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(ImportedXSOntology.class
			.getPackage().getName());

	public ImportedXSOntology(String ontologyURI, File xsdFile, OntologyLibrary library) {
		super(ontologyURI, xsdFile, library);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setIsReadOnly(boolean b) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void save() throws SaveResourceException {
		logger.warning("Imported ontologies are not supposed to be saved !!!");
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VE.IMPORTED_ONTOLOGY_INSPECTOR;
	}

	@Override
	public String getClassNameKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
