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
package org.openflexo.oe.fib.dialogs;

import java.io.File;
import java.util.Vector;

import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.action.DeleteOntologyObjects;
import org.openflexo.module.ModuleLoader;
import org.openflexo.oe.OECst;


public class DeleteOntologyObjectsDialogEDITOR {

	
	public static void main(String[] args)
	{
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			public Object[] getData() 
			{
				String URI = "http://www.agilebirds.com/flexo/ontologies/FlexoMethodology/FLXOrganizationalStructure.owl";
				FlexoResourceCenter resourceCenter = ModuleLoader.getFlexoResourceCenter();
				OntologyLibrary ontologyLibrary = resourceCenter.retrieveBaseOntologyLibrary();
				FlexoOntology ontology = ontologyLibrary.getOntology(URI);
				ontology.loadWhenUnloaded();
				Vector<OntologyObject> selection = new Vector<OntologyObject>();
				selection.add(ontologyLibrary.getOntologyObject(URI+"#Actor"));
				selection.add(ontologyLibrary.getOntologyObject(URI+"#Mission"));
				selection.add(ontologyLibrary.getOntologyObject(URI+"#hasMission"));
				selection.add(ontologyLibrary.getOntologyObject(URI+"#description"));
				DeleteOntologyObjects action = DeleteOntologyObjects.actionType.makeNewAction(null, selection,null);
				return makeArray(action);
			}
			public File getFIBFile() {
				return OECst.DELETE_ONTOLOGY_OBJECTS_DIALOG_FIB;
			}
		};
		editor.launch();
	}
}
