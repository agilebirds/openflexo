package org.openflexo.components.widget;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.TestFlexoServiceManager;
import org.openflexo.foundation.technologyadapter.InformationSpace;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.DefaultTechnologyAdapterControllerService;
import org.openflexo.view.controller.FlexoFIBController;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

public class FIBExcelResourceSelector {

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

		final FlexoServiceManager serviceManager = new TestFlexoServiceManager(new FileResource(
				"C:/Users/Vincent/git/openflexo/packaging/technologyadaptersintegration/src/test/resources/TestResourceCenter"));

		TechnologyAdapterControllerService tacService = DefaultTechnologyAdapterControllerService.getNewInstance();
		serviceManager.registerService(tacService);

		final InformationSpace informationSpace = serviceManager.getInformationSpace();

		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				FIBResourceSelector selector = new FIBResourceSelector(null);
				selector.setInformationSpace(informationSpace);
				try {
					selector.setTechnologyAdapter(serviceManager.getTechnologyAdapterService().getTechnologyAdapter(
							(Class<TechnologyAdapter>) Class.forName("org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter")));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				return makeArray(selector);
			}

			@Override
			public File getFIBFile() {
				return FIBResourceSelector.FIB_FILE;
			}

			@Override
			public FIBController makeNewController(FIBComponent component) {
				return new FlexoFIBController(component);
			}
		};
		editor.launch();
	}
}
