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
package org.openflexo.foundation.ontology;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.localization.FlexoLocalization;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ImportedOntology extends FlexoOntology {

	private static final Logger logger = Logger.getLogger(ImportedOntology.class.getPackage().getName());

	public ImportedOntology(String anURI, File owlFile, OntologyLibrary library) {
		super(anURI, owlFile, library);
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

	public static ImportedOntology createNewImportedOntology(String anURI, File owlFile, OntologyLibrary library) {
		ImportedOntology returned = new ImportedOntology(anURI, owlFile, library);

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

}
