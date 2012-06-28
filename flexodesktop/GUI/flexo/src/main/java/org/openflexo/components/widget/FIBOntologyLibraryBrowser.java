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
package org.openflexo.components.widget;

import java.util.logging.Logger;

import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.FIBBrowserView;
import org.openflexo.view.controller.FlexoController;

/**
 * Browser allowing to browse through ontology library<br>
 * 
 * @author sguerin
 * 
 */
public class FIBOntologyLibraryBrowser extends FIBBrowserView<OntologyLibrary> {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBOntologyLibraryBrowser.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/FIBOntologyLibraryBrowser.fib");

	public FIBOntologyLibraryBrowser(OntologyLibrary ontologyLibrary, FlexoController controller) {
		super(ontologyLibrary, controller, FIB_FILE);
	}

	// Please uncomment this for a live test
	// Never commit this uncommented since it will not compile on continuous build
	// To have icon, you need to choose "Test interface" in the editor (otherwise, flexo controller is not insanciated in EDIT mode)
	/*public static void main(String[] args) {

		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final FlexoResourceCenter testResourceCenter = LocalResourceCenterImplementation
				.instanciateTestLocalResourceCenterImplementation(new FileResource("TestResourceCenter"));

		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				return makeArray(testResourceCenter.retrieveBaseOntologyLibrary());
			}

			@Override
			public File getFIBFile() {
				return FIB_FILE;
			}

			@Override
			public FIBController makeNewController(FIBComponent component) {
				return new FlexoFIBController<OntologyLibrary>(component);
			}
		};
		editor.launch();
	}*/

}