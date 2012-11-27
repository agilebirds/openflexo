/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.technologyadapter.owl.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;

public class OWLMetaModel extends OWLOntology implements FlexoMetaModel {

	private static final Logger logger = Logger.getLogger(OWLMetaModel.class.getPackage().getName());

	public OWLMetaModel(String anURI, File owlFile, OntologyLibrary library) {
		super(anURI, owlFile, library);
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public String getClassNameKey() {
		return "imported_ontology";
	}

	@Override
	public String getDescription() {
		return FlexoLocalization.localizedForKey("no_description_available");
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VE.IMPORTED_ONTOLOGY_INSPECTOR;
	}

	@Override
	public String getDisplayableDescription() {
		return "Ontology " + getName();
	}

	public static OWLMetaModel createNewImportedOntology(String anURI, File owlFile, OntologyLibrary library) {
		OWLMetaModel returned = new OWLMetaModel(anURI, owlFile, library);

		Model base = ModelFactory.createDefaultModel();
		returned.ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, library, base);
		returned.ontModel.createOntology(anURI);
		returned.ontModel.setDynamicImports(true);
		returned.isLoaded = true;
		return returned;

	}

	@Override
	public void save() throws SaveResourceException {
		logger.warning("Imported ontologies are not supposed to be saved !!!");
		super.save();
	}

	public static void main(String[] args) {
		String uri = "http://this.is.a.test.owl";
		Model base = ModelFactory.createDefaultModel();
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null, base);
		// returned.ontModel.
		// ontModel.setNsPrefix("base", uri);
		ontModel.createOntology(uri);
		ontModel.setDynamicImports(true);
		File aFile = null;
		try {
			aFile = File.createTempFile("Zobi.owl", null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("File: " + aFile.getAbsolutePath());
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(aFile);
			RDFWriter writer = ontModel.getWriter("RDF/XML-ABBREV");
			writer.setProperty("xmlbase", uri);
			writer.write(ontModel.getBaseModel(), out, uri);
			// ontModel.write(out, "RDF/XML-ABBREV", uri); // "RDF/XML-ABBREV"
			System.out.println("Wrote " + aFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.warning("FileNotFoundException: " + e.getMessage());
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.warning("IOException: " + e.getMessage());
			}
		}

		try {
			System.out.println("Contents=\n" + FileUtils.fileContents(aFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
