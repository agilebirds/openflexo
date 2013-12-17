package org.openflexo.foundation.resource;

import java.io.File;
import java.util.List;

import org.openflexo.foundation.FlexoService;
import org.openflexo.foundation.FlexoServiceImpl;
import org.openflexo.foundation.FlexoServiceManager.ServiceRegistered;
import org.openflexo.foundation.resource.PamelaResourceImpl.WillWriteFileOnDiskNotification;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FileUtils;

/**
 * Default implementation for the {@link FlexoResourceCenterService} Manage the {@link UserResourceCenter} and the default
 * {@link DirectoryResourceCenter}
 * 
 * @author sylvain
 * 
 */
public abstract class DefaultResourceCenterService extends FlexoServiceImpl implements FlexoResourceCenterService {

	private UserResourceCenter userResourceCenter;

	/**
	 * Instantiate a new DefaultResourceCenterService with only the UserResourceCenter
	 * 
	 * @return
	 */
	public static FlexoResourceCenterService getNewInstance() {
		try {
			ModelFactory factory = new ModelFactory(FlexoResourceCenterService.class);
			factory.setImplementingClassForInterface(DefaultResourceCenterService.class, FlexoResourceCenterService.class);
			DefaultResourceCenterService returned = (DefaultResourceCenterService) factory.newInstance(FlexoResourceCenterService.class);
			returned.addUserResourceCenter();
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Instantiate a new DefaultResourceCenterService with the UserResourceCenter and with some instances of DirectoryResourceCenter
	 * matching all supplied directories
	 * 
	 * @return
	 */
	public static FlexoResourceCenterService getNewInstance(List<File> resourceCenterDirectories) {
		DefaultResourceCenterService returned = (DefaultResourceCenterService) getNewInstance();
		for (File directory : resourceCenterDirectories) {
			if (directory != null && directory.isDirectory() && directory.exists()) {
				returned.addToDirectoryResourceCenter(directory);
			}
		}
		/*if (resourceCenterDirectory != null && resourceCenterDirectory.isDirectory() && resourceCenterDirectory.exists()) {
			returned.addToDirectoryResourceCenter(resourceCenterDirectory);
		} else {
			File defaultRCFile = tryToFindDefaultResourceCenterDirectory();
			if (defaultRCFile != null && defaultRCFile.isDirectory() && defaultRCFile.exists()) {
				returned.addToDirectoryResourceCenter(defaultRCFile);
			}
		}*/
		return returned;
	}

	/*private static File tryToFindDefaultResourceCenterDirectory() {
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
		return file;
	}*/

	public DefaultResourceCenterService() {
	}

	public UserResourceCenter addUserResourceCenter() {
		File userResourceCenterStorageFile = new File(FileUtils.getDocumentDirectory(), "FlexoUserResourceCenter/ResourceCenterData.xml");
		if (!userResourceCenterStorageFile.getParentFile().canWrite()) {
			userResourceCenterStorageFile = new File(System.getProperty("user.home"), "FlexoUserResourceCenter/ResourceCenterData.xml");
		}
		userResourceCenter = new UserResourceCenter(userResourceCenterStorageFile);
		addToResourceCenters(userResourceCenter);
		return userResourceCenter;
	}

	public DirectoryResourceCenter addToDirectoryResourceCenter(File aDirectory) {
		DirectoryResourceCenter returned = DirectoryResourceCenter.instanciateNewDirectoryResourceCenter(aDirectory);
		addToResourceCenters(returned);
		return returned;
	}

	@Override
	public UserResourceCenter getUserResourceCenter() {
		return userResourceCenter;
	}

	@Override
	public void addToResourceCenters(FlexoResourceCenter resourceCenter) {
		performSuperAdder(RESOURCE_CENTERS, resourceCenter);
		if (getServiceManager() != null) {
			getServiceManager().notify(this, new ResourceCenterAdded(resourceCenter));
		}
	}

	@Override
	public void removeFromResourceCenters(FlexoResourceCenter resourceCenter) {
		performSuperRemover(RESOURCE_CENTERS, resourceCenter);
		if (getServiceManager() != null) {
			getServiceManager().notify(this, new ResourceCenterRemoved(resourceCenter));
		}
	}

	/**
	 * Notification of a new ResourceCenter added to the list of referenced resource centers
	 * 
	 * @author sylvain
	 * 
	 */
	public class ResourceCenterAdded implements ServiceNotification {
		private final FlexoResourceCenter addedResourceCenter;

		public ResourceCenterAdded(FlexoResourceCenter addedResourceCenter) {
			this.addedResourceCenter = addedResourceCenter;
		}

		public FlexoResourceCenter getAddedResourceCenter() {
			return addedResourceCenter;
		}
	}

	/**
	 * Notification of a new ResourceCenter removed from the list of referenced resource centers
	 * 
	 * @author sylvain
	 * 
	 */
	public class ResourceCenterRemoved implements ServiceNotification {
		private final FlexoResourceCenter removedResourceCenter;

		public ResourceCenterRemoved(FlexoResourceCenter removedResourceCenter) {
			this.removedResourceCenter = removedResourceCenter;
		}

		public FlexoResourceCenter getRemovedResourceCenter() {
			return removedResourceCenter;
		}
	}

	/**
	 * Save all locations for registered resource centers on disk
	 */
	@Override
	public void storeDirectoryResourceCenterLocations() {
		if (getServiceManager() != null) {
			System.out.println("Saving the directory resource center locations...");
			getServiceManager().notify(this, new ResourceCenterListShouldBeStored());
		}
	}

	/**
	 * Notification of a new ResourceCenter added to the list of referenced resource centers
	 * 
	 * @author sylvain
	 * 
	 */
	public class ResourceCenterListShouldBeStored implements ServiceNotification {
	}

	@Override
	public void initialize() {
		if (getResourceCenters().size() <= 1) {
			if (getServiceManager() != null) {
				System.out.println("Trying to install default packaged resource center");
				getServiceManager().notify(this, new DefaultPackageResourceCenterIsNotInstalled());
			}
		}

	}

	/**
	 * Notification of a new ResourceCenter added to the list of referenced resource centers
	 * 
	 * @author sylvain
	 * 
	 */
	public class DefaultPackageResourceCenterIsNotInstalled implements ServiceNotification {
	}

	@Override
	public void receiveNotification(FlexoService caller, ServiceNotification notification) {
		if (notification instanceof ProjectLoaded) {
			ProjectResourceCenter pRC = ProjectResourceCenter.instanciateProjectResourceCenter(((ProjectLoaded) notification).getProject());
			addToResourceCenters(pRC);
		}
		if (notification instanceof WillWriteFileOnDiskNotification) {
			for (FlexoResourceCenter rc : getResourceCenters()) {
				if (rc instanceof FileSystemBasedResourceCenter) {
					((FileSystemBasedResourceCenter) rc).willWrite(((WillWriteFileOnDiskNotification) notification).getFile());
				}
			}
		}
		if (caller instanceof TechnologyAdapterService) {
			if (notification instanceof ServiceRegistered) {
				for (FlexoResourceCenter rc : getResourceCenters()) {
					rc.initialize((TechnologyAdapterService) caller);
				}
			}
		}
	}

}
