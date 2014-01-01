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
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.toolbox.FileResource;

/**
 * Widget allowing to select a FlexoResourceCenter while browsing in FlexoResourceCenterService
 * 
 * @author sguerin
 * 
 */
public class FIBResourceCenterSelector extends FIBFlexoObjectSelector<FlexoResourceCenter> {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBResourceCenterSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/ResourceCenterSelector.fib");

	private FlexoResourceCenterService rcService;

	public FIBResourceCenterSelector(FlexoResourceCenter editedObject) {
		super(editedObject);
	}

	@Override
	public File getFIBFile() {
		return FIB_FILE;
	}

	@Override
	public Class<FlexoResourceCenter> getRepresentedType() {
		return FlexoResourceCenter.class;
	}

	@Override
	public String renderedString(FlexoResourceCenter editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return "";
	}

	public FlexoResourceCenterService getResourceCenterService() {
		return rcService;
	}

	@CustomComponentParameter(name = "resourceCenterService", type = CustomComponentParameter.Type.MANDATORY)
	public void setResourceCenterService(FlexoResourceCenterService rcService) {
		this.rcService = rcService;
		updateCustomPanel(getEditedObject());
	}

	public Object getRootObject() {
		return getResourceCenterService();
	}

	// Please uncomment this for a live test
	// Never commit this uncommented since it will not compile on continuous build
	// To have icon, you need to choose "Test interface" in the editor (otherwise, flexo controller is not instantiated in EDIT mode)
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

		final ViewPointLibrary viewPointLibrary;

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
		TechnologyAdapterControllerService tacService = DefaultTechnologyAdapterControllerService.getNewInstance();
		serviceManager.registerService(tacService);

		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				FIBResourceCenterSelector selector = new FIBResourceCenterSelector(null);
				selector.setResourceCenterService(serviceManager.getResourceCenterService());
				//try {
				//	selector.setTechnologyAdapter(serviceManager.getTechnologyAdapterService().getTechnologyAdapter(
				//			(Class<TechnologyAdapter>) Class.forName("org.openflexo.technologyadapter.emf.EMFTechnologyAdapter")));
				//} catch (ClassNotFoundException e) {
				//	e.printStackTrace();
				//}
				return makeArray(selector);
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