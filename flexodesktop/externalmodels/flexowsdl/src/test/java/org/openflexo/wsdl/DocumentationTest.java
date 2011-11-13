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
package org.openflexo.wsdl;

import java.io.File;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.ws.action.CreateNewWebService;
import org.openflexo.toolbox.FileResource;

public class DocumentationTest extends FlexoTestCase {

	public DocumentationTest(String name) {
		super(name);
	}

	public void testDoc() {
		try {
			FlexoEditor editor = FlexoResourceManager.initializeNewProject(new File("/tmp/FlexoTest/TestWSDLProject.prj"), EDITOR_FACTORY,
					null);
			FlexoProject project = editor.getProject();

			CreateNewWebService action = (CreateNewWebService) CreateNewWebService.actionType.makeNewAction(project.getFlexoWSLibrary(),
					null, editor);
			action.setWebServiceType(CreateNewWebService.EXTERNAL_WS);

			String externalName = "quizz";
			File wsdlFile = new FileResource("quiz.wsdl.xml");
			action.setNewWebServiceName(externalName);
			action.setWsdlFile(wsdlFile);

			action.setProject(project);
			action.doAction();
			// try{
			// project.save();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
