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
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.ve.VECst;

public class OntologyViewEDITOR extends FIBAbstractEditor {

	@Override
	public Object[] getData() {
		FlexoResourceCenter resourceCenter = DefaultResourceCenterService.getNewInstance().getOpenFlexoResourceCenter();
		OntologyLibrary ontologyLibrary = resourceCenter.retrieveBaseOntologyLibrary();
		IFlexoOntology ontology1 = ontologyLibrary.getFlexoConceptOntology();
		ontology1.loadWhenUnloaded();
		IFlexoOntology ontology2 = ontologyLibrary.getOntology("http://www.w3.org/2004/02/skos/core");
		ontology2.loadWhenUnloaded();
		IFlexoOntology ontology3 = ontologyLibrary.getOntology("http://www.w3.org/2002/07/owl");
		ontology3.loadWhenUnloaded();
		IFlexoOntology ontology4 = ontologyLibrary.getOntology("http://www.agilebirds.com/openflexo/ontologies/UML/UML2.owl");
		ontology4.loadWhenUnloaded();
		return makeArray(ontology2, ontology3, ontology4);
	}

	@Override
	public File getFIBFile() {
		return VECst.ONTOLOGY_VIEW_FIB;
	}

	public static void main(String[] args) {
		main(OntologyViewEDITOR.class);
	}

}
