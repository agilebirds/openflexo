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
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.InformationSpace;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.toolbox.FileResource;

/**
 * Widget allowing to select a Model while browsing in Information Space
 * 
 * @author sguerin
 * 
 */
public class FIBModelSelector extends FIBModelObjectSelector<FlexoModelResource> {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBModelSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/ModelSelector.fib");

	private InformationSpace informationSpace;
	private TechnologyAdapter<?, ?> technologyAdapter;
	private FlexoResourceCenter resourceCenter;

	public FIBModelSelector(FlexoModelResource editedObject) {
		super(editedObject);
	}

	@Override
	public File getFIBFile() {
		return FIB_FILE;
	}

	@Override
	public Class<FlexoModelResource> getRepresentedType() {
		return FlexoModelResource.class;
	}

	@Override
	public String renderedString(FlexoModelResource editedObject) {
		if (editedObject != null) {
			return editedObject.getURI();
		}
		return "";
	}

	public InformationSpace getInformationSpace() {
		return informationSpace;
	}

	@CustomComponentParameter(name = "informationSpace", type = CustomComponentParameter.Type.MANDATORY)
	public void setInformationSpace(InformationSpace informationSpace) {
		this.informationSpace = informationSpace;
		updateCustomPanel(getEditedObject());
	}

	public TechnologyAdapter<?, ?> getTechnologyAdapter() {
		return technologyAdapter;
	}

	@CustomComponentParameter(name = "technologyAdapter", type = CustomComponentParameter.Type.OPTIONAL)
	public void setTechnologyAdapter(TechnologyAdapter<?, ?> technologyAdapter) {
		this.technologyAdapter = technologyAdapter;
		updateCustomPanel(getEditedObject());
	}

	public Object getRootObject() {
		if (getTechnologyAdapter() != null) {
			return getTechnologyAdapter();
		} else {
			return getInformationSpace();
		}
	}

	public FlexoResourceCenter getResourceCenter() {
		return resourceCenter;
	}

	public void setResourceCenter(FlexoResourceCenter resourceCenter) {
		this.resourceCenter = resourceCenter;
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
		final InformationSpace informationSpace = serviceManager.getInformationSpace();

		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				FIBModelSelector selector = new FIBModelSelector(null);
				selector.setInformationSpace(informationSpace);
				//try {
				//	selector.setTechnologyAdapter(serviceManager.getTechnologyAdapterService().getTechnologyAdapter(
				//			(Class<TechnologyAdapter<?, ?>>) Class.forName("org.openflexo.technologyadapter.emf.EMFTechnologyAdapter")));
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