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
package org.openflexo.technologyadapter.owl.fib.dialog;

import java.io.File;

import org.openflexo.ApplicationContext;
import org.openflexo.TestApplicationContext;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.controller.OWLAdapterController;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OWLOntologyLibrary;
import org.openflexo.technologyadapter.owl.model.action.CreateDataProperty;
import org.openflexo.toolbox.FileResource;

public class CreateDataPropertyDialogEDITOR extends FIBAbstractEditor {

	@Override
	public Object[] getData() {

		ApplicationContext testApplicationContext = new TestApplicationContext(new FileResource("src/test/resources/Ontologies"));
		OWLTechnologyAdapter owlAdapter = testApplicationContext.getTechnologyAdapterService().getTechnologyAdapter(
				OWLTechnologyAdapter.class);
		OWLOntologyLibrary ontologyLibrary = (OWLOntologyLibrary) testApplicationContext.getTechnologyAdapterService()
				.getTechnologyContextManager(owlAdapter);

		OWLOntology ontology = ontologyLibrary
				.getOntology("http://www.agilebirds.com/openflexo/ontologies/ScopeDefinition/OrganizationalUnitScopeDefinition.owl");
		ontology.loadWhenUnloaded();
		org.openflexo.technologyadapter.owl.model.action.CreateDataProperty action = CreateDataProperty.actionType.makeNewAction(ontology,
				null, null);
		return makeArray(action);
	}

	@Override
	public File getFIBFile() {
		return OWLAdapterController.CREATE_DATA_PROPERTY_DIALOG_FIB;
	}

	public static void main(String[] args) {
		main(CreateDataPropertyDialogEDITOR.class);
	}

}
