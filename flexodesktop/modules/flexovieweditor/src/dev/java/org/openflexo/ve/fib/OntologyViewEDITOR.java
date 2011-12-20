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
package org.openflexo.ve.fib;

import java.io.File;

import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.module.ModuleLoader;
import org.openflexo.ve.VECst;


public class OntologyViewEDITOR {

	public static void main(String[] args)
	{
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				FlexoResourceCenter resourceCenter = getFlexoResourceCenterService().getFlexoResourceCenter();
				OntologyLibrary ontologyLibrary = resourceCenter.retrieveBaseOntologyLibrary();
				FlexoOntology ontology1 = ontologyLibrary.getOntology("http://www.agilebirds.com/openflexo/ontologies/FlexoMethodology/FLXOrganizationalStructure.owl");
				ontology1.loadWhenUnloaded();
				FlexoOntology ontology2 = ontologyLibrary.getOntology("http://www.w3.org/2004/02/skos/core");
				ontology2.loadWhenUnloaded();
				FlexoOntology ontology3 = ontologyLibrary.getOntology("http://www.w3.org/2002/07/owl");
				ontology3.loadWhenUnloaded();
				FlexoOntology ontology4 = ontologyLibrary.getOntology("http://www.agilebirds.com/openflexo/ontologies/UML/UML2.owl");
				ontology4.loadWhenUnloaded();
				return makeArray(ontology1,ontology2,ontology3,ontology4);
			}
			@Override
			public File getFIBFile() {
				return VECst.ONTOLOGY_VIEW_FIB;
			}
		};
		editor.launch();
	}

    private static ModuleLoader getModuleLoader(){
        return ModuleLoader.instance();
    }

    private static FlexoResourceCenterService getFlexoResourceCenterService(){
        return FlexoResourceCenterService.instance();
    }
}
