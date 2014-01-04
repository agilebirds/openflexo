package org.openflexo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.foundation.DefaultFlexoServiceManager;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.FlexoService;
import org.openflexo.foundation.FlexoService.ServiceNotification;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.DefaultResourceCenterService.DefaultPackageResourceCenterIsNotInstalled;
import org.openflexo.foundation.resource.DefaultResourceCenterService.ResourceCenterListShouldBeStored;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.ProjectLoader;
import org.openflexo.prefs.PreferencesService;
import org.openflexo.rest.client.ServerRestService;
import org.openflexo.view.controller.TechnologyAdapterControllerService;

public abstract class ApplicationContext extends DefaultFlexoServiceManager implements FlexoEditorFactory {

	private final FlexoEditor applicationEditor;

	private ServerRestService serverRestService;

	public ApplicationContext() {
		super();
		applicationEditor = createApplicationEditor();
		try {
			ProjectLoader projectLoader = new ProjectLoader(this);
			registerService(projectLoader);
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		ModuleLoader moduleLoader = new ModuleLoader(this);
		registerService(moduleLoader);
		TechnologyAdapterControllerService technologyAdapterControllerService = createTechnologyAdapterControllerService();
		registerService(technologyAdapterControllerService);
	}

	public PreferencesService getPreferencesService() {
		return getService(PreferencesService.class);
	}

	public ModuleLoader getModuleLoader() {
		return getService(ModuleLoader.class);
	}

	public ProjectLoader getProjectLoader() {
		return getService(ProjectLoader.class);
	}

	public final TechnologyAdapterControllerService getTechnologyAdapterControllerService() {
		return getService(TechnologyAdapterControllerService.class);
	}

	public final FlexoEditor getApplicationEditor() {
		return applicationEditor;
	}

	public ServerRestService getServerRestService() {
		if (serverRestService == null) {
			serverRestService = new ServerRestService(getProjectLoader());
		}
		return serverRestService;
	}

	public boolean isAutoSaveServiceEnabled() {
		return false;
	}

	public abstract ProjectLoadingHandler getProjectLoadingHandler(File projectDirectory);

	@Override
	protected abstract FlexoEditor createApplicationEditor();

	protected abstract TechnologyAdapterControllerService createTechnologyAdapterControllerService();

	@Override
	protected FlexoResourceCenterService createResourceCenterService() {
		return DefaultResourceCenterService.getNewInstance(getGeneralPreferences().getDirectoryResourceCenterList());
	}

	@Deprecated
	/*Should be removed when preferences will be a service */
	@Override
	public void notify(FlexoService caller, ServiceNotification notification) {
		super.notify(caller, notification);
		// Little hack to handle rc location saving
		// TODO: Should be removed when preferences will be a service see OPENFLEXO-651
		if (notification instanceof ResourceCenterListShouldBeStored && caller instanceof FlexoResourceCenterService) {
			List<File> rcList = new ArrayList<File>();
			for (FlexoResourceCenter<?> rc : ((FlexoResourceCenterService) caller).getResourceCenters()) {
				if (rc instanceof DirectoryResourceCenter) {
					rcList.add(((DirectoryResourceCenter) rc).getDirectory());
				}
			}
			getGeneralPreferences().setDirectoryResourceCenterList(rcList);
		} else if (notification instanceof DefaultPackageResourceCenterIsNotInstalled && caller instanceof FlexoResourceCenterService) {
			defaultPackagedResourceCenterIsNotInstalled = true;
		}
	}

	private boolean defaultPackagedResourceCenterIsNotInstalled;

	public boolean defaultPackagedResourceCenterIsToBeInstalled() {
		return defaultPackagedResourceCenterIsNotInstalled;
	}

	public GeneralPreferences getGeneralPreferences() {
		return getPreferencesService().getPreferences(GeneralPreferences.class);
	}

	public AdvancedPrefs getAdvancedPrefs() {
		return getPreferencesService().getPreferences(AdvancedPrefs.class);
	}
}
