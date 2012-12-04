package org.openflexo.foundation.resource;

import java.io.File;
import java.io.IOException;

import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FileUtils;

public abstract class DefaultResourceCenterService implements FlexoResourceCenterService {

	private LocalResourceCenterImplementation openFlexoResourceCenter;
	private UserResourceCenter userResourceCenter;

	public static FlexoResourceCenterService getNewInstance() {
		try {
			ModelFactory factory = new ModelFactory().importClass(FlexoResourceCenterService.class);
			factory.setImplementingClassForInterface(DefaultResourceCenterService.class, FlexoResourceCenterService.class);
			return factory.newInstance(FlexoResourceCenterService.class);
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
}
