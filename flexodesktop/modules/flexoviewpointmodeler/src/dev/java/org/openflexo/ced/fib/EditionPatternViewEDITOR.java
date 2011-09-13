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
package org.openflexo.ced.fib;

import java.io.File;

import org.openflexo.ced.CEDCst;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.calc.CalcLibrary;
import org.openflexo.foundation.ontology.calc.OntologyCalc;
import org.openflexo.module.ModuleLoader;


public class EditionPatternViewEDITOR {

	
	public static void main(String[] args)
	{
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() 
			{
				FlexoResourceCenter resourceCenter = ModuleLoader.getFlexoResourceCenter(true);
				OntologyLibrary ontologyLibrary = resourceCenter.retrieveBaseOntologyLibrary();
				CalcLibrary calcLibrary = resourceCenter.retrieveCalcLibrary();

				Object[] returned = new Object[12];
				
				OntologyCalc calc1 = calcLibrary.getOntologyCalc("http://www.agilebirds.com/flexo/ontologies/Calcs/Tests/BasicOrganizationTreeEditor.owl");
				calc1.loadWhenUnloaded();
				returned[0] = calc1.getEditionPattern("Employee");
				returned[1] = calc1.getEditionPattern("BOTDepartment");
				
				OntologyCalc calc2 = calcLibrary.getOntologyCalc("http://www.agilebirds.com/flexo/ontologies/Calcs/FlexoMethodology/FLXOrganizationalStructure-A.owl");
				calc2.loadWhenUnloaded();
				returned[2] = calc2.getEditionPattern("RootOrganizationUnit");
				returned[3] = calc2.getEditionPattern("PositionMission");

				OntologyCalc calc3 = calcLibrary.getOntologyCalc("http://www.agilebirds.com/flexo/ontologies/Calcs/Basic/BasicOntology.owl");
				calc3.loadWhenUnloaded();
				returned[4] = calc3.getEditionPattern("Concept");
				returned[5] = calc3.getEditionPattern("IsARelationship");
				returned[6] = calc3.getEditionPattern("HasRelationship");
				
				OntologyCalc calc4 = calcLibrary.getOntologyCalc("http://www.agilebirds.com/flexo/ontologies/Calcs/SKOS/SKOSThesaurusEditor.owl");
				calc4.loadWhenUnloaded();
				returned[7] = calc4.getEditionPattern("Concept");
				
				OntologyCalc calc5 = calcLibrary.getOntologyCalc("http://www.agilebirds.com/flexo/ontologies/Calcs/UML/UseCaseDiagram.owl");
				calc5.loadWhenUnloaded();
				returned[8] = calc5.getEditionPattern("Actor");
				
				OntologyCalc calc6 = calcLibrary.getOntologyCalc("http://www.agilebirds.com/flexo/ontologies/Calcs/FlexoMethodology/FLXOrganizationalStructure-B.owl");
				calc6.loadWhenUnloaded();
				returned[9] = calc6.getEditionPattern("LinkOrganizationUnitToParentOrganizationUnit");
				returned[10] = calc6.getEditionPattern("LinkPositionToOrganizationUnit");

				OntologyCalc calc7 = calcLibrary.getOntologyCalc("http://www.agilebirds.com/flexo/ontologies/Calcs/UML/PackageDiagram.owl");
				calc7.loadWhenUnloaded();
				returned[11] = calc7.getEditionPattern("ImportPackage");
				
				return returned;
			}
			@Override
			public File getFIBFile() {
				return CEDCst.EDITION_PATTERN_VIEW_FIB;
			}
		};
		editor.launch();
	}
}
