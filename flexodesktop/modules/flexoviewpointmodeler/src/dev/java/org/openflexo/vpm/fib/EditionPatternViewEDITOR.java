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

import org.openflexo.TestApplicationContext;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.toolbox.FileResource;
import org.openflexo.vpm.VPMCst;

public class EditionPatternViewEDITOR extends FIBAbstractEditor {

	@Override
	public Object[] getData() {

		TestApplicationContext testApplicationContext = new TestApplicationContext(
				new FileResource("src/test/resources/TestResourceCenter"));
		ViewPointLibrary viewPointLibrary = testApplicationContext.getViewPointLibrary();

		Object[] returned = new Object[11];

		/*ViewPoint viewPoint1 = viewPointLibrary
				.getViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/Tests/BasicOrganizationTreeEditor.owl");
		returned[0] = viewPoint1.getEditionPattern("Employee");
		returned[1] = viewPoint1.getEditionPattern("BOTDepartment");*/

		ViewPoint viewPoint2 = viewPointLibrary
				.getViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalUnitDefinition.owl");
		returned[0] = viewPoint2.getDefaultDiagramSpecification().getEditionPattern("OrganizationalUnit");
		returned[1] = viewPoint2.getDefaultDiagramSpecification().getEditionPattern("OrganizationalUnitPosition");
		returned[2] = viewPoint2.getDefaultDiagramSpecification().getEditionPattern("PositionTask");

		ViewPoint viewPoint3 = viewPointLibrary.getViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/Basic/BasicOntology.owl");
		returned[3] = viewPoint3.getDefaultDiagramSpecification().getEditionPattern("Concept");
		returned[4] = viewPoint3.getDefaultDiagramSpecification().getEditionPattern("IsARelationship");
		returned[5] = viewPoint3.getDefaultDiagramSpecification().getEditionPattern("HasRelationship");

		ViewPoint viewPoint4 = viewPointLibrary.getViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/SKOS/SKOSThesaurusEditor.owl");
		returned[6] = viewPoint4.getDefaultDiagramSpecification().getEditionPattern("Concept");

		ViewPoint viewPoint5 = viewPointLibrary.getViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/UML/UseCaseDiagram.owl");
		returned[7] = viewPoint5.getDefaultDiagramSpecification().getEditionPattern("Actor");

		ViewPoint viewPoint6 = viewPointLibrary
				.getViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalMap.owl");
		returned[8] = viewPoint6.getDefaultDiagramSpecification().getEditionPattern("ContainsPositionLink");
		returned[9] = viewPoint6.getDefaultDiagramSpecification().getEditionPattern("SubOrganizationUnitLink");

		ViewPoint viewPoint7 = viewPointLibrary.getViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/UML/PackageDiagram.owl");
		returned[10] = viewPoint7.getDefaultDiagramSpecification().getEditionPattern("ImportPackage");

		// ViewPoint viewPoint9 = viewPointLibrary.getOntologyCalc("http://www.thalesgroup.com/ViewPoints/sepel-ng/MappingCapture.owl");
		// viewPoint9.loadWhenUnloaded();
		// returned[13] = viewPoint9.getEditionPattern("ConceptMapping");

		return returned;
	}

	@Override
	public File getFIBFile() {
		return VPMCst.EDITION_PATTERN_VIEW_FIB;
	}

	public static void main(String[] args) {
		main(EditionPatternViewEDITOR.class);
	}

}
