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
package org.openflexo.vpm.fib.dialogs;

import java.io.File;

import org.openflexo.TestApplicationContext;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramConnector;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.action.DeclareConnectorInEditionPattern;
import org.openflexo.toolbox.FileResource;
import org.openflexo.vpm.VPMCst;

public class DeclareConnectorInEditionPatternDialogEDITOR extends FIBAbstractEditor {

	@Override
	public Object[] getData() {
		TestApplicationContext testApplicationContext = new TestApplicationContext(
				new FileResource("src/test/resources/TestResourceCenter"));
		ViewPointLibrary viewPointLibrary = testApplicationContext.getViewPointLibrary();
		ViewPoint calc1 = viewPointLibrary.getViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/Basic/BasicOntology.owl");

		ExampleDiagram shema = calc1.getDefaultDiagramSpecification().getExampleDiagrams().get(0);
		ExampleDiagramConnector connector = (ExampleDiagramConnector) shema.getChilds().get(2);
		DeclareConnectorInEditionPattern action = DeclareConnectorInEditionPattern.actionType.makeNewAction(connector, null, null);
		return makeArray(action);
	}

	@Override
	public File getFIBFile() {
		return VPMCst.DECLARE_CONNECTOR_IN_EDITION_PATTERN_DIALOG_FIB;
	}

	public static void main(String[] args) {
		main(DeclareConnectorInEditionPatternDialogEDITOR.class);
	}

}
