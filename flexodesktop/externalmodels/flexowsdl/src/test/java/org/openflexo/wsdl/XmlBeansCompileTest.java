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
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.ws.action.AbstractCreateNewWebService;
import org.openflexo.foundation.ws.action.CreateNewWebService;
import org.openflexo.toolbox.FileResource;

public class XmlBeansCompileTest extends FlexoTestCase {

	public XmlBeansCompileTest(String name) {
		super(name);
	}

	public void testCompile() {
		try {
			FlexoEditor editor = FlexoResourceManager.initializeNewProject(new File("/tmp/FlexoTest/xmlBeansTest/XmlBeansTest.prj"),
					EDITOR_FACTORY, null);
			FlexoProject project = editor.getProject();

			CreateNewWebService action = CreateNewWebService.actionType.makeNewAction(project.getFlexoWSLibrary(), null, editor);
			action.setWebServiceType(AbstractCreateNewWebService.EXTERNAL_WS);

			String externalName = "externalWSGroupName";
			File wsdlFile = new FileResource("getJoke.asmx.xml");
			action.setNewWebServiceName(externalName);
			action.setWsdlFile(wsdlFile);

			action.setNewWebServiceName("myNewWebService");
			action.setPortRegistry(project.getLocalFlexoProcess("MyWebService").getPortRegistery());
			action.setProject(project);
			action.doAction();
			// try{
			// project.save();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
