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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.UserResourceCenter;
import org.openflexo.foundation.technologyadapter.DefaultTechnologyAdapterService;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.FIBBrowserView;
import org.openflexo.view.controller.DefaultTechnologyAdapterControllerService;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.FlexoFIBController;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

/**
 * Browser allowing to browse through information space<br>
 * The information space is obtained through two services from the {@link FlexoServiceManager}, and results from the merging of the
 * {@link FlexoResourceCenterService} and the {@link TechnologyAdapterService}.<br>
 * For each {@link FlexoResourceCenter} and for each {@link TechnologyAdapter}, a repository of {@link FlexoModel} and
 * {@link FlexoMetaModel} are managed.
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class FIBInformationSpaceBrowser extends FIBBrowserView<FlexoServiceManager> {
	static final Logger logger = Logger.getLogger(FIBInformationSpaceBrowser.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/FIBInformationSpaceBrowser.fib");

	public FIBInformationSpaceBrowser(FlexoServiceManager serviceManager, FlexoController controller) {
		super(serviceManager, controller, FIB_FILE);
	}

	// Please uncomment this for a live test
	// Never commit this uncommented since it will not compile on continuous build
	// To have icon, you need to choose "Test interface" in the editor (otherwise, flexo controller is not insanciated in EDIT mode)
	public static void main(String[] args) {

		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final ViewPointLibrary viewPointLibrary;

		final FlexoServiceManager serviceManager = new FlexoServiceManager();
		FlexoResourceCenterService rcService = DefaultResourceCenterService.getNewInstance();
		rcService.addToResourceCenters(new UserResourceCenter(new FileResource("TestResourceCenter")));
		serviceManager.registerService(rcService);
		viewPointLibrary = new ViewPointLibrary();
		serviceManager.registerService(viewPointLibrary);
		TechnologyAdapterService taService = DefaultTechnologyAdapterService.getNewInstance(rcService);
		serviceManager.registerService(taService);
		TechnologyAdapterControllerService tacService = DefaultTechnologyAdapterControllerService.getNewInstance();
		serviceManager.registerService(tacService);

		// System.out.println("Resource centers=" + viewPointLibrary.getResourceCenterService().getResourceCenters());
		// System.exit(-1);

		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				return makeArray(serviceManager);
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

	}

}