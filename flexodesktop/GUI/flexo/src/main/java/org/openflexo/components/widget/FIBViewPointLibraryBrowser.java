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

import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.FIBBrowserView;
import org.openflexo.view.controller.FlexoController;

/**
 * Browser allowing to browse through viewpoint library<br>
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class FIBViewPointLibraryBrowser extends FIBBrowserView<ViewPointLibrary> {
	static final Logger logger = Logger.getLogger(FIBViewPointLibraryBrowser.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/Widget/FIBViewPointLibraryBrowser.fib");

	public FIBViewPointLibraryBrowser(ViewPointLibrary viewPointLibrary, FlexoController controller) {
		super(viewPointLibrary, controller, FIB_FILE);
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

		final FlexoServiceManager serviceManager = new DefaultFlexoServiceManager() {
			@Override
			protected FlexoProjectReferenceLoader createProjectReferenceLoader() {
				return null;
			}

			@Override
			protected FlexoEditor createApplicationEditor() {
				return null;
			}
		};
		final ViewPointLibrary viewPointLibrary = serviceManager.getViewPointLibrary();

		// System.out.println("Resource centers=" + viewPointLibrary.getResourceCenterService().getResourceCenters());
		// System.exit(-1);

		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				return makeArray(viewPointLibrary);
			}

			@Override
			public File getFIBFile() {
				return FIB_FILE;
			}

			@Override
			public FIBController makeNewController(FIBComponent component) {
				return new FlexoFIBController(component);
			}
		};
		editor.launch();

	}*/

}