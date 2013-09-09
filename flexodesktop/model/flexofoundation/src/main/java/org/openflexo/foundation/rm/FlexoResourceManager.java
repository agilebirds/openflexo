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
package org.openflexo.foundation.rm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.utils.DefaultProjectLoadingHandler;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;

/**
 * This class is used to perform synchronization of all resources of a Flexo project. Global consistant states of all the resources of a
 * Flexo project through all application modules is maintained by this scheme.
 * 
 * Only one instance of the FlexoResourceManager is maintained.
 * 
 * The FlexoResourceManager manages one instance of FlexoProject.
 * 
 * @author sguerin
 */
public class FlexoResourceManager {

	protected static final Logger logger = Logger.getLogger(FlexoResourceManager.class.getPackage().getName());

	protected FlexoEditor _editor;

	protected Thread _clockThread;

	protected volatile boolean _stop;

	protected ResourceUpdateHandler _resourceUpdateHandler;

	private boolean isLoadingAProject = true;

	private Object lock;

	private FlexoResourceManager(FlexoEditor editor, ResourceUpdateHandler resourceUpdateHandler) {
		super();
		_editor = editor;
		lock = new Object();
		if (resourceUpdateHandler == null) {
			_resourceUpdateHandler = new DefaultFlexoResourceUpdateHandler();
		} else {
			_resourceUpdateHandler = resourceUpdateHandler;
		}

	}

	public static final long RESOURCE_CHECKING_DELAY = 5000;

	public void startResourcePeriodicChecking() {
		if (!_editor.performResourceScanning()) {
			return;
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("START resource periodic checking for " + "ID=" + _editor.getProject().getID());
		}
		_stop = false;
		_clockThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Thread myThread = Thread.currentThread();
				while (_clockThread == myThread && !_stop) {
					// if (logger.isLoggable(Level.FINER))
					// logger.finer("Checking resources for project " + _editor.getProject());
					try {
						List<FlexoFileResource<? extends FlexoResourceData>> updatedResources = new ArrayList<FlexoFileResource<? extends FlexoResourceData>>();
						for (FlexoFileResource<? extends FlexoResourceData> fileResource : _editor.getProject().getFileResources()) {
							if (fileResource.hasMoreRecentThanExpectedDiskUpdate()) {
								updatedResources.add(fileResource);
								logger.info("File " + fileResource + " update detected on " + _clockThread.getName());
							}
						}
						if (updatedResources.size() > 0 && _resourceUpdateHandler != null) {
							if (updatedResources.size() == 1) {
								_resourceUpdateHandler.handlesResourceUpdate(updatedResources.get(0));
							} else {
								_resourceUpdateHandler.handlesResourcesUpdate(updatedResources);
								// else if (logger.isLoggable(Level.WARNING))
								// logger
								// .warning("Resource update for resource " + resource
								// + " detected but no resource update handler.");
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(RESOURCE_CHECKING_DELAY);
					} catch (InterruptedException e) {

					}
				}
				if (logger.isLoggable(Level.INFO)) {
					logger.info("STOP resource periodic checking for " + getProject().getProjectName() + " ID=" + getProject().getID());
				}
				return;
			}
		}, "RM_CHECKING_" + _editor.getProject().getID());
		_clockThread.setDaemon(true); // Not really useful, but cleaner
		_clockThread.setPriority(Thread.MIN_PRIORITY);
		_clockThread.start();
	}

	public void stopResourcePeriodicChecking() {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Will stop resource periodic checking for project " + getProject().getProjectName() + " ID=" + getProject().getID());
		}
		synchronized (lock) {
			_stop = true;
			if (_clockThread != null) {
				_clockThread.interrupt();// Causes the resource periodic
				// checking to
				// stop immediately (otherwise, sometimes, 2
				// resource periodic checking could run
				// simultaneously and might cause a popup
				// because of some file modification when
				// reloading the same project
				_clockThread = null;
			}
		}
	}

	private static File findResourceManagerFile(File aProjectDirectory) {
		File[] fileArray = aProjectDirectory.listFiles();
		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].getName().endsWith(".rmxml")) {
				return fileArray[i];
			}
		}
		return null;
	}

	public static boolean needsRestructuring(File aProjectDirectory) {
		return findResourceManagerFile(aProjectDirectory) == null;
	}

	public static File getExpectedResourceManagerFile(File aProjectDirectory) {
		File resourceManagerFile = findResourceManagerFile(aProjectDirectory);
		if (resourceManagerFile == null) {
			String projectName = aProjectDirectory.getName();
			if (projectName.endsWith(".prj")) {
				projectName = projectName.substring(0, projectName.length() - 4);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Project directory does not end with '.prj'");
				}
			}
			resourceManagerFile = new File(aProjectDirectory, projectName + ".rmxml");
		}
		return resourceManagerFile;
	}

	public static FlexoEditor initializeExistingProject(File aProjectDirectory, FlexoEditorFactory editorFactory,
			FlexoServiceManager serviceManager) throws ProjectInitializerException, ProjectLoadingCancelledException {
		// Here implement a default handler that attempts to retrieve properly
		return initializeExistingProject(aProjectDirectory, null, editorFactory, new DefaultProjectLoadingHandler(), serviceManager);
	}

	public static FlexoEditor initializeExistingProject(File aProjectDirectory, FlexoProgress progress, FlexoEditorFactory editorFactory,
			ProjectLoadingHandler loadingHandler, FlexoServiceManager serviceManager) throws ProjectInitializerException,
			ProjectLoadingCancelledException {
		FlexoProjectReferenceLoader projectReferenceLoader = serviceManager.getProjectReferenceLoader();
		if (loadingHandler == null) {
			loadingHandler = new DefaultProjectLoadingHandler();
		}
		FlexoProject project = null;
		if (!aProjectDirectory.exists()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.severe("Project directory doesn't exist: " + aProjectDirectory.getAbsolutePath());
			}

		}
		File rmFile = getExpectedResourceManagerFile(aProjectDirectory);
		if (!rmFile.exists()) {
			throw new ProjectInitializerException(
					"There is no rmxml file in project. Cannot load project without one. Use previous versions of Flexo first and then load with this new version.",
					aProjectDirectory);
		} else {
			try {
				FlexoProject.restoreJarsIfNeeded(aProjectDirectory);
			} catch (IOException e) {
				e.printStackTrace();
				throw new ProjectInitializerException(e.getMessage(), aProjectDirectory);
			}
			FlexoRMResource rmRes;
			try {
				rmRes = new FlexoRMResource(rmFile, aProjectDirectory, serviceManager);
				rmRes.setProjectReferenceLoader(projectReferenceLoader);
				project = rmRes.loadProject(progress, loadingHandler, serviceManager);
			} catch (RuntimeException e1) {
				e1.printStackTrace();
				throw new ProjectInitializerException(e1.getMessage(), aProjectDirectory);
			}

		}
		FlexoEditor returned = editorFactory.makeFlexoEditor(project);
		FlexoResourceManager resourceManager = new FlexoResourceManager(returned, returned.getResourceUpdateHandler());
		resourceManager.startResourcePeriodicChecking();
		resourceManager.isLoadingAProject = false;
		project.setResourceManagerInstance(resourceManager);
		checkExternalRepositories(project);
		return returned;
	}

	private static void checkExternalRepositories(FlexoProject project) {
		for (ProjectExternalRepository repository : project.getExternalRepositories()) {
			if (!repository.isConnected()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Found external repository " + repository + " DISCONNECTED, deactivate resources");
				}
				for (FlexoResource<?> resource : repository.getRelatedResources()) {
					resource.deactivate();
				}
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Found external repository " + repository.getDirectory().getAbsolutePath() + " well CONNECTED");
				}
			}
		}
	}

	public static FlexoEditor initializeNewProject(File aProjectDirectory, FlexoProgress progress, FlexoEditorFactory editorFactory,
			FlexoProjectReferenceLoader projectReferenceLoader, FlexoServiceManager serviceManager) throws ProjectInitializerException {
		if (!aProjectDirectory.exists()) {
			aProjectDirectory.mkdirs();
		}
		File rmFile = getExpectedResourceManagerFile(aProjectDirectory);
		FlexoEditor returned = FlexoProject.newProject(rmFile, aProjectDirectory, editorFactory, progress, serviceManager);
		FlexoProject project = returned.getProject();
		FlexoResourceManager resourceManager = new FlexoResourceManager(returned, returned.getResourceUpdateHandler());
		resourceManager.startResourcePeriodicChecking();
		resourceManager.isLoadingAProject = false;
		project.setResourceManagerInstance(resourceManager);
		checkExternalRepositories(project);
		return returned;
	}

	public static FlexoEditor initializeNewProject(File aProjectDirectory, FlexoEditorFactory editorFactory,
			FlexoServiceManager serviceManager) throws ProjectInitializerException {
		return initializeNewProject(aProjectDirectory, null, editorFactory, null, serviceManager);
	}

	public FlexoEditor getEditor() {
		if (_editor == null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Sorry, NO FlexoEditor instance available");
			}
		}
		return _editor;
	}

	public FlexoProject getProject() {
		if (_editor == null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Sorry, NO FlexoEditor instance available");
			}
		} else if (_editor.getProject() == null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Sorry, NO FlexoProject instance available");
			}
		}
		return _editor.getProject();
	}

	private static BackwardSynchronizationHook backwardSynchronizationHook = null;

	public static interface BackwardSynchronizationHook {
		public void notifyBackwardSynchronization(FlexoResource resource1, FlexoResource resource2);
	}

	public static BackwardSynchronizationHook getBackwardSynchronizationHook() {
		return backwardSynchronizationHook;
	}

	public static void setBackwardSynchronizationHook(BackwardSynchronizationHook aBackwardSynchronizationHook) {
		backwardSynchronizationHook = aBackwardSynchronizationHook;
	}

	public boolean isLoadingAProject() {
		return isLoadingAProject;
	}

}
