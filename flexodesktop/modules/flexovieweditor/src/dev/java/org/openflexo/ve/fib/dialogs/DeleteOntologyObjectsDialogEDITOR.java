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
import java.util.Vector;

import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.owl.OWLObject;
import org.openflexo.foundation.ontology.owl.OWLOntology;
import org.openflexo.foundation.ontology.owl.action.DeleteOntologyObjects;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.ve.VECst;

public class DeleteOntologyObjectsDialogEDITOR extends FIBAbstractEditor {

	@Override
	public Object[] getData() {
		String URI = "http://www.agilebirds.com/openflexo/ontologies/FlexoMethodology/FLXOrganizationalStructure.owl";
		FlexoResourceCenter resourceCenter = DefaultResourceCenterService.getNewInstance().getOpenFlexoResourceCenter();
		OntologyLibrary ontologyLibrary = resourceCenter.retrieveBaseOntologyLibrary();
		OWLOntology ontology = (OWLOntology) ontologyLibrary.getOntology(URI);
		ontology.loadWhenUnloaded();
		Vector<OWLObject> selection = new Vector<OWLObject>();
		selection.add(ontology.getOntologyObject(URI + "#Actor"));
		selection.add(ontology.getOntologyObject(URI + "#Mission"));
		selection.add(ontology.getOntologyObject(URI + "#hasMission"));
		selection.add(ontology.getOntologyObject(URI + "#description"));
		DeleteOntologyObjects action = DeleteOntologyObjects.actionType.makeNewAction(null, selection, null);
		return makeArray(action);
	}

	@Override
	public File getFIBFile() {
		return VECst.DELETE_ONTOLOGY_OBJECTS_DIALOG_FIB;
	}

	public static void main(String[] args) {
		main(DeleteOntologyObjectsDialogEDITOR.class);
	}

}
