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
package org.openflexo.module;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.openflexo.FlexoCst;
import org.openflexo.GeneralPreferences;
import org.openflexo.ch.FCH;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.ProgressWindow;
import org.openflexo.components.SaveDialog;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.DirectoryParameter;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.param.FileParameter;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.rm.ProjectExternalRepository;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.SaveResourcePermissionDeniedException;
import org.openflexo.foundation.utils.FlexoProjectUtil;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.UnreadableProjectException;
import org.openflexo.foundation.xml.FlexoXMLMappings;
import org.openflexo.help.FlexoHelp;
import org.openflexo.inspector.widget.FileEditWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.rm.ResourceManagerWindow;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.ModuleBar;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.InteractiveFlexoEditor;
import org.openflexo.view.menu.WindowMenu;

public final class ProjectLoader {

	private static final Logger logger = Logger.getLogger(ModuleLoader.class.getPackage().getName());

	private static final String FOR_FLEXO_SERVER = "_forFlexoServer_";

	private static ProjectLoader _instance;

	private static final Object monitor = new Object();

	private ResourceManagerWindow _rmWindow;

	private InteractiveFlexoResourceUpdateHandler resourceUpdateHandler;

	public static ProjectLoader instance() {
		if (_instance == null) {
			synchronized (monitor) {
				if (_instance == null) {
					_instance = new ProjectLoader();
				}
			}
		}
		return _instance;
	}

	/**
	 * Loads the project located withing <code> projectDirectory </code>. The following method is the default methode to call when opening a
	 * project from a GUI (Interactive mode) so that resource update handling is properly initialized. Additional small stuffs can be
	 * performed in that call so that projects are always opened the same way.
	 * 
	 * @param projectDirectory
	 *            the project directory
	 * @return the {@link InteractiveFlexoEditor} editor if the opening succeeded else <code>null</code>
	 * @throws org.openflexo.foundation.utils.ProjectLoadingCancelledException
	 *             whenever the load procedure is interrupted by the user or by Flexo.
	 * @throws ProjectInitializerException
	 */
	public InteractiveFlexoEditor loadProject(File projectDirectory) throws ProjectLoadingCancelledException, ProjectInitializerException {
		if (projectDirectory == null) {
			throw new IllegalArgumentException("Project directory cannot be null");
		}
		if (!projectDirectory.exists()) {
			throw new ProjectInitializerException("project directory does not exist", projectDirectory);
		}
		FlexoVersion previousFlexoVersion = FlexoProjectUtil.getVersion(projectDirectory);
		try {
			FlexoProjectUtil.isProjectOpenable(projectDirectory);
		} catch (UnreadableProjectException e) {
			FlexoController.notify(e.getMessage());
			ProgressWindow.hideProgressWindow();
			throw new ProjectLoadingCancelledException(e.getMessage());
		}

		if (getAutoSaveService().hasAutoSaveThread()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Auto-save thread still running. Killing it now. Please ensure that auto-save thread is stopped before loading a project");
			}
			getAutoSaveService().stopAutoSaveThread();
		}
		if (ProgressWindow.hasInstance()) {
			ProgressWindow.hideProgressWindow();
		}
		ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("loading_project"), 14);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Opening " + projectDirectory.getAbsolutePath());
		}
		preInitialization(projectDirectory);
		final FlexoProject newProject;
		final InteractiveFlexoEditor newEditor;
		try {
			newEditor = (InteractiveFlexoEditor) FlexoResourceManager.initializeExistingProject(projectDirectory,
					ProgressWindow.instance(), resourceUpdateHandler = new InteractiveFlexoResourceUpdateHandler(),
					InteractiveFlexoEditor.FACTORY, UserType.getCurrentUserType().getDefaultLoadingHandler(projectDirectory),
					getFlexoResourceCenterService().getFlexoResourceCenter());
			newProject = newEditor.getProject();
			checkExternalRepositories(newProject);
			if (previousFlexoVersion != null && previousFlexoVersion.major == 1 && previousFlexoVersion.minor < 3) {
				newProject.validate(); // Let's repair this project
				try {
					newProject.save(ProgressWindow.instance());
				} catch (SaveResourceException e) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not save project after repairing it.");
					}
				}
			}
		} finally {
			ProgressWindow.hideProgressWindow();
		}
		getAutoSaveService().conditionalStartOfAutoSaveThread(newEditor.isAutoSaveEnabledByDefault());
		ProgressWindow.hideProgressWindow();
		return newEditor;
	}

	public InteractiveFlexoEditor newProject(File projectDirectory) {
		if (!ProgressWindow.hasInstance()) {
			ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("building_new_project"), 10);
		} else {
			ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("building_new_project"));
		}
		// This will just create the .version in the project
		FlexoProjectUtil.currentFlexoVersionIsSmallerThanLastVersion(projectDirectory);

		preInitialization(projectDirectory);
		InteractiveFlexoEditor returned;
		returned = (InteractiveFlexoEditor) FlexoResourceManager.initializeNewProject(projectDirectory, ProgressWindow.instance(),
				resourceUpdateHandler = new InteractiveFlexoResourceUpdateHandler(), InteractiveFlexoEditor.FACTORY,
				getFlexoResourceCenterService().getFlexoResourceCenter());
		getAutoSaveService().conditionalStartOfAutoSaveThread(returned.isAutoSaveEnabledByDefault());
		return returned;
	}

	public void closeCurrentProject() {
		if (!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						closeCurrentProject();
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return;
		}

		FlexoProject currentProject = ModuleLoader.instance().getProject();

		if (currentProject != null) {
			currentProject.close();
		}
		getAutoSaveService().stopAutoSaveThread();
		if (_rmWindow != null) {
			_rmWindow.dispose();
			_rmWindow = null;
		}
		for (Enumeration<FlexoModule> e = ModuleLoader.instance().loadedModules(); e.hasMoreElements();) {
			FlexoModule mod = e.nextElement();
			mod.closeWithoutConfirmation(false);
		}
		FCH.clearComponentsHashtable();
		FlexoHelp.instance.deleteObservers();
		FlexoLocalization.clearStoredLocalizedForComponents();
		WindowMenu.reset();
		ModuleBar.reset();
		FlexoProject.cleanUpActionizer();
		System.gc();
	}

	public ResourceManagerWindow getRMWindow(FlexoProject project) {
		if (_rmWindow == null) {
			_rmWindow = new ResourceManagerWindow(project);
		}
		return _rmWindow;
	}

	public void saveProjectForServer(FlexoProject project) {
		final String zipFilename = project.getProjectDirectory().getName()
				.substring(0, project.getProjectDirectory().getName().length() - 4);
		String zipFileNameProposal = zipFilename + FOR_FLEXO_SERVER + new FlexoVersion(1, 0, 0, -1, false, false);
		File[] zips = project.getProjectDirectory().getParentFile().listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().toLowerCase().endsWith(".zip") && pathname.getName().startsWith(zipFilename);
			}
		});
		File previousVersion = null;
		if (zips != null) {
			for (File file : zips) {
				if (previousVersion == null || previousVersion.lastModified() < file.lastModified()) {
					previousVersion = file;
				}
			}
		}
		if (previousVersion != null && previousVersion.getName().indexOf(FOR_FLEXO_SERVER) > -1) {
			String version = previousVersion.getName()
					.substring(previousVersion.getName().indexOf(FOR_FLEXO_SERVER) + FOR_FLEXO_SERVER.length(),
							previousVersion.getName().length() - 4);
			if (FlexoVersion.isValidVersionString(version)) {
				FlexoVersion v = new FlexoVersion(version);
				v.minor++;
				zipFileNameProposal = zipFilename + FOR_FLEXO_SERVER + v;
			}
		}
		final FileParameter targetZippedProject = new FileParameter("targetZippedProject", "new_zip_file", new File(project
				.getProjectDirectory().getParentFile(), zipFileNameProposal + ".zip")) {
			@Override
			public void setValue(File value) {
				if (!value.getName().endsWith(".zip")) {
					value = new File(value.getParentFile(), value.getName() + ".zip");
				}
				super.setValue(value);
			}
		};
		targetZippedProject.setDepends("targetZippedProject");
		targetZippedProject.addParameter(FileEditWidget.TITLE, "select_a_zip_file");
		targetZippedProject.addParameter(FileEditWidget.FILTER, "*.zip");
		targetZippedProject.addParameter(FileEditWidget.MODE, FileEditWidget.SAVE);
		CheckboxParameter removeScreenshotsAndLibraries = new CheckboxParameter("lighten", "remove_screenshots_and_libs", true);

		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(project, null,
				FlexoLocalization.localizedForKey("save_project_as"), FlexoLocalization.localizedForKey("select_a_zip_file_for_project"),
				new AskParametersDialog.ValidationCondition() {
					@Override
					public boolean isValid(ParametersModel model) {
						if (targetZippedProject.getValue() == null) {
							errorMessage = FlexoLocalization.localizedForKey("please_submit_a_zip");
							return false;
						}
						return true;
					}
				}, targetZippedProject, removeScreenshotsAndLibraries);

		System.setProperty("apple.awt.fileDialogForDirectories", "false");
		if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
			File zipFile = targetZippedProject.getValue();
			if (zipFile == null) {
				return;
			}
			if (!zipFile.exists()) {
				try {
					FileUtils.createNewFile(zipFile);
				} catch (IOException e1) {
					e1.printStackTrace();
					FlexoController.notify(FlexoLocalization.localizedForKey("could_not_save_permission_denied"));
					return;
				}
			} else {
				if (!FlexoController.confirm(FlexoLocalization.localizedForKey("file_already_exists.replace_it?"))) {
					return;
				}
			}
			if (!zipFile.canWrite()) {
				FlexoController.notify(FlexoLocalization.localizedForKey("could_not_save_permission_denied"));
				return;
			}
			try {
				ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving"), removeScreenshotsAndLibraries.getValue() ? 5
						: 2);
				project.saveAsZipFile(zipFile, ProgressWindow.instance(), removeScreenshotsAndLibraries.getValue(), true);
				ProgressWindow.hideProgressWindow();
			} catch (SaveResourceException e) {
				e.printStackTrace();
				ProgressWindow.hideProgressWindow();
				FlexoController.notify(FlexoLocalization.localizedForKey("save_as_operation_failed"));
			}
		}
	}

	public void saveAsProject(FlexoProject project) {
		project.getXmlMappings();
		Vector<FlexoVersion> availableVersions = new Vector<FlexoVersion>(FlexoXMLMappings.getReleaseVersions());
		Collections.sort(availableVersions, Collections.reverseOrder(FlexoVersion.comparator));

		final DirectoryParameter targetPrjDirectory = new DirectoryParameter("targetPrjDirectory", "new_project_file", project
				.getProjectDirectory().getParentFile()) {
			@Override
			public void setValue(File value) {
				if (!value.getName().endsWith(".prj")) {
					value = new File(value.getParentFile(), value.getName() + ".prj");
				}
				super.setValue(value);
			}
		};
		targetPrjDirectory.setDepends("targetPrjDirectory");
		targetPrjDirectory.addParameter(FileEditWidget.TITLE, "select_a_prj_directory");
		targetPrjDirectory.addParameter(FileEditWidget.FILTER, "*.prj");
		targetPrjDirectory.addParameter(FileEditWidget.MODE, FileEditWidget.SAVE);
		final DynamicDropDownParameter<FlexoVersion> versionParam = new DynamicDropDownParameter<FlexoVersion>("version", "version",
				availableVersions, availableVersions.get(0));
		versionParam.setShowReset(false);

		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(project, null,
				FlexoLocalization.localizedForKey("save_project_as"),
				FlexoLocalization.localizedForKey("enter_parameters_for_project_saving"), new AskParametersDialog.ValidationCondition() {
					@Override
					public boolean isValid(ParametersModel model) {
						if (versionParam.getValue() == null) {
							errorMessage = FlexoLocalization.localizedForKey("please_submit_a_version");
							return false;
						}
						if (targetPrjDirectory.getValue() == null) {
							errorMessage = FlexoLocalization.localizedForKey("please_submit_a_prj_directory");
							return false;
						}
						if (!(targetPrjDirectory.getValue().getName().endsWith(".prj") && !targetPrjDirectory.getValue().exists())) {
							errorMessage = FlexoLocalization.localizedForKey("please_submit_a_valid_prj_directory");
							return false;
						}
						return true;
					}
				}, targetPrjDirectory, versionParam);

		System.setProperty("apple.awt.fileDialogForDirectories", "false");
		if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
			File projectDirectory = targetPrjDirectory.getValue();
			if (projectDirectory == null) {
				return;
			} else if (!projectDirectory.exists()) {
				if (!projectDirectory.mkdirs()) {
					FlexoController.notify(FlexoLocalization.localizedForKey("could_not_create_prj_directory"));
					return;
				}
			}
			if (!projectDirectory.canWrite()) {
				FlexoController.notify(FlexoLocalization.localizedForKey("could_not_save_permission_denied"));
				return;
			}
			try {
				ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving"), 1);
				project.saveAs(projectDirectory, ProgressWindow.instance(),
						FlexoCst.BUSINESS_APPLICATION_VERSION.equals(versionParam.getValue()) ? null : versionParam.getValue(), true, true);
				GeneralPreferences.addToLastOpenedProjects(projectDirectory);
				ProgressWindow.hideProgressWindow();
			} catch (SaveResourceException e) {
				e.printStackTrace();
				ProgressWindow.hideProgressWindow();
				FlexoController.notify(FlexoLocalization.localizedForKey("save_as_operation_failed"));
			}
		}
	}

	public boolean saveProject(FlexoProject project, boolean reviewUnsaved) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Saving project");
		}
		if (project != null) {
			if (project.getUnsavedStorageResources().size() > 0) {
				if (!reviewUnsaved) {
					try {
						doSaveProject(project);
						return true;
					} catch (SaveResourceException e) {
						informUserAboutSaveResourceException(e);
						return false;
					}
				} else {
					return openResourceReviewerDialog(project);
				}
			} else {
				// Nothing to do: there are no modified resources !
				return true;
			}
		}
		return false;
	}

	static void doSaveProject(FlexoProject project) throws SaveResourceException {
		try {
			ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving"), 1);
			project.save(ProgressWindow.instance());
		} finally {
			ProgressWindow.hideProgressWindow();
		}

	}

	/**
	 * Return boolean indicating if some resources need saving
	 */
	public static boolean someResourcesNeedsSaving(FlexoProject project) {
		return project != null && project.getUnsavedStorageResources(false).size() > 0;
	}

	static void informUserAboutSaveResourceException(SaveResourceException e) {
		if (e instanceof SaveResourcePermissionDeniedException) {
			informUserAboutPermissionDeniedException((SaveResourcePermissionDeniedException) e);
		} else {
			FlexoController.showError(FlexoLocalization.localizedForKey("error_during_saving"));
		}
		logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
		logger.warning(e.getMessage());
		e.printStackTrace();
	}

	private static void informUserAboutPermissionDeniedException(SaveResourcePermissionDeniedException e) {
		if (e.getFileResource().getFile().isDirectory()) {
			FlexoController.showError(FlexoLocalization.localizedForKey("permission_denied"),
					FlexoLocalization.localizedForKey("project_was_not_properly_saved_permission_denied_directory") + "\n"
							+ e.getFileResource().getFile().getAbsolutePath());
		} else {
			FlexoController.showError(FlexoLocalization.localizedForKey("permission_denied"),
					FlexoLocalization.localizedForKey("project_was_not_properly_saved_permission_denied_file") + "\n"
							+ e.getFileResource().getFile().getAbsolutePath());
		}
	}

	private boolean openResourceReviewerDialog(FlexoProject project) {
		SaveDialog reviewer = new SaveDialog(FlexoController.getActiveFrame());
		if (reviewer.getRetval() == JOptionPane.YES_OPTION) {
			try {
				doSaveProject(project);
				return true;
			} catch (SaveResourcePermissionDeniedException e) {
				informUserAboutPermissionDeniedException(e);
				return false;
			} catch (SaveResourceException e) {
				return FlexoController.confirm(FlexoLocalization.localizedForKey("error_during_saving") + "\n"
						+ FlexoLocalization.localizedForKey("would_you_like_to_exit_anyway"));
			}
		} else {
			return reviewer.getRetval() == JOptionPane.NO_OPTION;
		}
	}

	private AutoSaveService getAutoSaveService() {
		return AutoSaveService.instance();
	}

	private static void preInitialization(File projectDirectory) {
		GeneralPreferences.addToLastOpenedProjects(projectDirectory);
		FlexoPreferences.savePreferences(true);
	}

	private FlexoResourceCenterService getFlexoResourceCenterService() {
		return FlexoResourceCenterService.instance();
	}

	/**
	 * Check if there is an external repository with some active resources connected In this case, explicitely ask what to do, connect or
	 * let disconnected
	 * 
	 * @param project
	 *            the project
	 */
	private void checkExternalRepositories(FlexoProject project) {
		// Removed unnecessary blocking dialog. Most users asks that all stay disconnected.
		// The code is left if we want to roll back this change.
		if (true /*!UserType.isMaintainerRelease() && !UserType.isDevelopperRelease()*/) {
			return;
		}
		for (ProjectExternalRepository repository : project.getExternalRepositories()) {
			if (!repository.isConnected()) {
				if (repository.getDirectory() != null) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Found external repository " + repository.getDirectory().getAbsolutePath());
					} else {
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Found external repository " + repository.getIdentifier() + " with no directory");
						}
					}
				}
				if (repository.shouldBeConnected()) {
					String KEEP_DISCONNECTED = FlexoLocalization.localizedForKey("keep_disconnected");
					String KEEP_ALL_DISCONNECTED = FlexoLocalization.localizedForKey("keep_all_disconnected");
					String CONNECT = FlexoLocalization.localizedForKey("connect_to_local_directory");
					String[] choices = { KEEP_DISCONNECTED, KEEP_ALL_DISCONNECTED, CONNECT };
					RadioButtonListParameter<String> choiceParam = new RadioButtonListParameter<String>("choice",
							"what_would_you_like_to_do", KEEP_ALL_DISCONNECTED, choices);
					DirectoryParameter dirParam = new DirectoryParameter("directory", "directory", repository.getDirectory());
					dirParam.setDepends("choice");
					dirParam.setConditional("choice=" + '"' + CONNECT + '"');
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(project, FlexoFrame.getActiveFrame(),
							FlexoLocalization.localizedForKey("connect_repository_to_local_file_system"), repository.getName() + " : "
									+ FlexoLocalization.localizedForKey("repository_seems_to_be_not_valid_on_local_file_system"),
							choiceParam, dirParam);
					System.setProperty("apple.awt.fileDialogForDirectories", "false");
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						if (choiceParam.getValue().equals(CONNECT)) {
							if (dirParam.getValue() != null) {
								if (!dirParam.getValue().exists()) {
									if (FlexoController.confirm(FlexoLocalization.localizedForKey("directory") + " "
											+ dirParam.getValue().getAbsolutePath() + " "
											+ FlexoLocalization.localizedForKey("does_not_exist") + "\n"
											+ FlexoLocalization.localizedForKey("would_you_like_to_create_it_and_continue?"))) {
										if (!dirParam.getValue().mkdirs()) {
											FlexoController.notify(FlexoLocalization.localizedForKey("cannot_create_directory"));
											return;
										}
									}
									repository.setDirectory(dirParam.getValue());
								}
							} else {
								FlexoController.notify(FlexoLocalization.localizedForKey("invalid_directory"));
							}
						} else if (choiceParam.getValue().equals(KEEP_ALL_DISCONNECTED)) {
							return;
						}
					}
				}
			}
		}
	}

	public InteractiveFlexoResourceUpdateHandler getFlexoResourceUpdateHandler() {
		return resourceUpdateHandler;
	}
}
