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
import java.util.ArrayList;
import java.util.List;

import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.vpm.CEDCst;

public class EditionPatternViewEDITOR extends FIBAbstractEditor {

	@Override
	public Object[] getData() {
		FlexoResourceCenter resourceCenter = DefaultResourceCenterService.getNewInstance().getOpenFlexoResourceCenter();
		OntologyLibrary ontologyLibrary = resourceCenter.retrieveBaseOntologyLibrary();
		ViewPointLibrary viewPointLibrary = resourceCenter.retrieveViewPointLibrary();

		List<EditionPattern> objects = new ArrayList<EditionPattern>();

		ViewPoint viewPoint2 = viewPointLibrary
				.getOntologyCalc("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalUnitDefinition.owl");
		viewPoint2.loadWhenUnloaded();
		objects.add(viewPoint2.getEditionPattern("OrganizationalUnit"));
		objects.add(viewPoint2.getEditionPattern("OrganizationalUnitPosition"));
		objects.add(viewPoint2.getEditionPattern("PositionTask"));

		ViewPoint viewPoint4 = viewPointLibrary
				.getOntologyCalc("http://www.agilebirds.com/openflexo/ViewPoints/SKOS/SKOSThesaurusEditor.owl");
		viewPoint4.loadWhenUnloaded();
		objects.add(viewPoint4.getEditionPattern("Concept"));

		ViewPoint viewPoint5 = viewPointLibrary.getOntologyCalc("http://www.agilebirds.com/openflexo/ViewPoints/UML/UseCaseDiagram.owl");
		viewPoint5.loadWhenUnloaded();
		objects.add(viewPoint5.getEditionPattern("Actor"));

		ViewPoint viewPoint6 = viewPointLibrary
				.getOntologyCalc("http://www.agilebirds.com/openflexo/ViewPoints/ScopeDefinition/OrganizationalMap.owl");
		viewPoint6.loadWhenUnloaded();
		objects.add(viewPoint6.getEditionPattern("ContainsPositionLink"));
		objects.add(viewPoint6.getEditionPattern("SubOrganizationUnitLink"));

		ViewPoint viewPoint7 = viewPointLibrary.getOntologyCalc("http://www.agilebirds.com/openflexo/ViewPoints/UML/PackageDiagram.owl");
		viewPoint7.loadWhenUnloaded();
		objects.add(viewPoint7.getEditionPattern("ImportPackage"));

		// ViewPoint viewPoint9 = viewPointLibrary.getOntologyCalc("http://www.thalesgroup.com/ViewPoints/sepel-ng/MappingCapture.owl");
		// viewPoint9.loadWhenUnloaded();
		// returned[13] = viewPoint9.getEditionPattern("ConceptMapping");

		return objects.toArray();
	}

	@Override
	public File getFIBFile() {
		return CEDCst.EDITION_PATTERN_VIEW_FIB;
	}

	public static void main(String[] args) {
		main(EditionPatternViewEDITOR.class);
	}

}
