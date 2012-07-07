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
package org.openflexo.vpm.fib;

import java.io.File;

import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.module.FlexoResourceCenterService;
import org.openflexo.vpm.CEDCst;

public class EditionPatternViewEDITOR {

	public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				FlexoResourceCenter resourceCenter = getFlexoResourceCenterService().getFlexoResourceCenter(true);
				OntologyLibrary ontologyLibrary = resourceCenter.retrieveBaseOntologyLibrary();
				ViewPointLibrary calcLibrary = resourceCenter.retrieveViewPointLibrary();

				Object[] returned = new Object[14];

				ViewPoint calc1 = calcLibrary
						.getOntologyCalc("http://www.agilebirds.com/openflexo/ViewPoints/Tests/BasicOrganizationTreeEditor.owl");
				calc1.loadWhenUnloaded();
				returned[0] = calc1.getEditionPattern("Employee");
				returned[1] = calc1.getEditionPattern("BOTDepartment");

				ViewPoint calc2 = calcLibrary
						.getOntologyCalc("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalUnitDefinition.owl");
				calc2.loadWhenUnloaded();
				returned[2] = calc2.getEditionPattern("OrganizationalUnit");
				returned[3] = calc2.getEditionPattern("OrganizationalUnitPosition");
				returned[12] = calc2.getEditionPattern("PositionTask");

				ViewPoint calc3 = calcLibrary.getOntologyCalc("http://www.agilebirds.com/openflexo/ViewPoints/Basic/BasicOntology.owl");
				calc3.loadWhenUnloaded();
				returned[4] = calc3.getEditionPattern("Concept");
				returned[5] = calc3.getEditionPattern("IsARelationship");
				returned[6] = calc3.getEditionPattern("HasRelationship");

				ViewPoint calc4 = calcLibrary
						.getOntologyCalc("http://www.agilebirds.com/openflexo/ViewPoints/SKOS/SKOSThesaurusEditor.owl");
				calc4.loadWhenUnloaded();
				returned[7] = calc4.getEditionPattern("Concept");

				ViewPoint calc5 = calcLibrary.getOntologyCalc("http://www.agilebirds.com/openflexo/ViewPoints/UML/UseCaseDiagram.owl");
				calc5.loadWhenUnloaded();
				returned[8] = calc5.getEditionPattern("Actor");

				ViewPoint calc6 = calcLibrary
						.getOntologyCalc("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalMap.owl");
				calc6.loadWhenUnloaded();
				returned[9] = calc6.getEditionPattern("ContainsPositionLink");
				returned[10] = calc6.getEditionPattern("SubOrganizationUnitLink");

				ViewPoint calc7 = calcLibrary.getOntologyCalc("http://www.agilebirds.com/openflexo/ViewPoints/UML/PackageDiagram.owl");
				calc7.loadWhenUnloaded();
				returned[11] = calc7.getEditionPattern("ImportPackage");

				ViewPoint calc9 = calcLibrary.getOntologyCalc("http://www.thalesgroup.com/ViewPoints/sepel-ng/MappingCapture.owl");
				calc9.loadWhenUnloaded();
				returned[13] = calc9.getEditionPattern("ConceptMapping");

				return returned;
			}

			@Override
			public File getFIBFile() {
				return CEDCst.EDITION_PATTERN_VIEW_FIB;
			}
		};
		editor.launch();
	}

	private static FlexoResourceCenterService getFlexoResourceCenterService() {
		return FlexoResourceCenterService.getInstance();
	}
}
