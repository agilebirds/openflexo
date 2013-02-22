package org.openflexo.builders.utils;

import java.io.File;

import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.UserResourceCenter;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

public abstract class FlexoBuilderResourceCenterService extends DefaultResourceCenterService {

	private DirectoryResourceCenter openFlexoResourceCenter;
	private UserResourceCenter userResourceCenter;

	public static FlexoResourceCenterService getNewInstance(File workingDir) {
		FlexoBuilderResourceCenterService service = (FlexoBuilderResourceCenterService) _getNewInstance();
		service.openFlexoResourceCenter = DirectoryResourceCenter.instanciateNewDirectoryResourceCenter(workingDir);
		service.userResourceCenter = new UserResourceCenter(new File(workingDir, "UserResourceCenterData.xml"));
		return service;
	}

	public static FlexoResourceCenterService _getNewInstance() {
		try {
			ModelFactory factory = new ModelFactory(FlexoResourceCenterService.class);
			factory.setImplementingClassForInterface(FlexoBuilderResourceCenterService.class, FlexoResourceCenterService.class);
			return factory.newInstance(FlexoResourceCenterService.class);
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public DirectoryResourceCenter getOpenFlexoResourceCenter() {
		return openFlexoResourceCenter;
	}

	@Override
	public UserResourceCenter getUserResourceCenter() {
		return userResourceCenter;
	}

}
