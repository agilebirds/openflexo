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
package org.openflexo.ve.fib.dialogs;

import java.io.File;

import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.LocalResourceCenterImplementation;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.action.CreateOntologyIndividual;
import org.openflexo.module.FlexoResourceCenterService;
import org.openflexo.ve.VECst;

public class CreateOntologyIndividualDialogEDITOR {

	public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				FlexoResourceCenter resourceCenter = LocalResourceCenterImplementation
						.instanciateTestLocalResourceCenterImplementation(new FileResource("TestResourceCenter"));
				OntologyLibrary ontologyLibrary = resourceCenter.retrieveBaseOntologyLibrary();
				FlexoOntology ontology = ontologyLibrary
						.getOntology("http://www.agilebirds.com/openflexo/ontologies/ScopeDefinition/OrganizationalUnitScopeDefinition.owl");
				ontology.loadWhenUnloaded();
				CreateOntologyIndividual action = CreateOntologyIndividual.actionType.makeNewAction(ontology, null, null);
				return makeArray(action);
			}

			@Override
			public File getFIBFile() {
				return VECst.CREATE_ONTOLOGY_INDIVIDUAL_FIB;
			}
		};
		editor.launch();
	}

}
