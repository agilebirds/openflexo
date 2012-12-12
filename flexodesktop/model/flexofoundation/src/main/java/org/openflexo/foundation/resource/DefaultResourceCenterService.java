package org.openflexo.foundation.resource;

import java.io.File;
import java.io.IOException;

import org.openflexo.foundation.FlexoService;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.FlexoServiceManager.ServiceRegistered;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FileUtils;

public abstract class DefaultResourceCenterService implements FlexoResourceCenterService {

	private LocalResourceCenterImplementation openFlexoResourceCenter;
	private UserResourceCenter userResourceCenter;

	private FlexoServiceManager serviceManager;

	public static FlexoResourceCenterService getNewInstance() {
		try {
			ModelFactory factory = new ModelFactory(FlexoResourceCenterService.class);
			factory.setImplementingClassForInterface(DefaultResourceCenterService.class, FlexoResourceCenterService.class);
			DefaultResourceCenterService returned = (DefaultResourceCenterService) factory.newInstance(FlexoResourceCenterService.class);
			returned.addToResourceCenters(returned.openFlexoResourceCenter);
			returned.addToResourceCenters(returned.userResourceCenter);
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static FlexoResourceCenterService getNewInstance(File localResourceCenterDir) {
		DefaultResourceCenterService service = (DefaultResourceCenterService) getNewInstance();
		service.openFlexoResourceCenter = LocalResourceCenterImplementation
				.instanciateNewLocalResourceCenterImplementation(localResourceCenterDir);
		return service;
	}

	public DefaultResourceCenterService() {
		File root = FileUtils.getApplicationDataDirectory();
		File file = null;
		boolean ok = false;
		int i = 0;
		String base = "FlexoResourceCenter";
		String attempt = base;
		while (!ok && i < 100) {
			file = new File(root, attempt);
			if (!file.exists()) {
				ok = file.mkdirs();
			} else {
				ok = file.isDirectory() && file.canWrite();
			}
			i++;
			attempt = base + "-" + i;
		}
		i = 0;
		while (!ok && i < 1000) {
			try {
				file = File.createTempFile("FlexoResourceCenter", null);
				file.delete();
				file.mkdirs();
				ok = file.exists() && file.canWrite();
				i++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		openFlexoResourceCenter = new LocalResourceCenterImplementation(file);
		userResourceCenter = new UserResourceCenter(new File(FileUtils.getDocumentDirectory(),
				"FlexoUserResourceCenter/ResourceCenterData.xml"));

	}

	/*	@Override
		public void registerTechnologyAdapterService(TechnologyAdapterService technologyAdapterService) {
			openFlexoResourceCenter.initialize(technologyAdapterService);
			userResourceCenter.initialize(technologyAdapterService);
		}*/

	@Override
	public LocalResourceCenterImplementation getOpenFlexoResourceCenter() {
		return openFlexoResourceCenter;
	}

	@Override
	public FlexoResourceCenter getUserResourceCenter() {
		return userResourceCenter;
	}

	@Override
	public void addToResourceCenters(FlexoResourceCenter resourceCenter) {
		performSuperAdder(RESOURCE_CENTERS, resourceCenter);
		if (serviceManager != null) {
			serviceManager.notify(this, new ResourceCenterAdded(resourceCenter));
		}
	}

	/**
	 * Notification of a new ResourceCenter added to the list of referenced resource centers
	 * 
	 * @author sylvain
	 * 
	 */
	public class ResourceCenterAdded implements ServiceNotification {
		private FlexoResourceCenter addedResourceCenter;

		public ResourceCenterAdded(FlexoResourceCenter addedResourceCenter) {
			this.addedResourceCenter = addedResourceCenter;
		}

		public FlexoResourceCenter getAddedResourceCenter() {
			return addedResourceCenter;
		}
	}

	@Override
	public void receiveNotification(FlexoService caller, ServiceNotification notification) {
		if (caller instanceof TechnologyAdapterService) {
			if (notification instanceof ServiceRegistered) {
				for (FlexoResourceCenter rc : getResourceCenters()) {
					rc.initialize((TechnologyAdapterService) caller);
				}
			}
		}
	}

	@Override
	public void register(FlexoServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	@Override
	public FlexoServiceManager getFlexoServiceManager() {
		return serviceManager;
	}

}
