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

import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.State;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.openflexo.AdvancedPrefs;
import org.openflexo.FlexoCst;
import org.openflexo.GeneralPreferences;
import org.openflexo.action.SubmitDocumentationAction;
import org.openflexo.ch.FCH;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.MultiModuleModeWelcomePanel;
import org.openflexo.components.NewProjectComponent;
import org.openflexo.components.OpenProjectComponent;
import org.openflexo.components.ProgressWindow;
import org.openflexo.components.SaveDialog;
import org.openflexo.components.SingleModuleModeWelcomePanel;
import org.openflexo.components.WelcomeDialog;
import org.openflexo.components.WelcomePanel;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.fib.AskLocalResourceCenterDirectory;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.LocalResourceCenterImplementation;
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
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.xml.FlexoXMLMappings;
import org.openflexo.help.FlexoHelp;
import org.openflexo.inspector.widget.FileEditWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.module.external.ExternalCEDModule;
import org.openflexo.module.external.ExternalDMModule;
import org.openflexo.module.external.ExternalGeneratorModule;
import org.openflexo.module.external.ExternalIEModule;
import org.openflexo.module.external.ExternalModuleDelegater;
import org.openflexo.module.external.ExternalOEModule;
import org.openflexo.module.external.ExternalWKFModule;
import org.openflexo.module.external.IModule;
import org.openflexo.module.external.IModuleLoader;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.rm.ResourceManagerWindow;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.utils.FlexoFileChooserUtils;
import org.openflexo.view.ModuleBar;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.InteractiveFlexoEditor;
import org.openflexo.view.menu.ToolsMenu;
import org.openflexo.view.menu.WindowMenu;

/**
 * This class handles computation of available modules and modules loading. Only one instance of this class is instancied, and available all
 * over Flexo Application Suite. This is the ONLY ONE WAY to access external modules from a given module.
 *
 * @author sguerin
 */
public final class ModuleLoader implements IModuleLoader {

	private static final Logger logger = Logger.getLogger(ModuleLoader.class.getPackage().getName());

	private static boolean allowsDocSubmission;

	private static UserType _userType = null;

	private static boolean multiModuleMode = false;

	private static Module singleModuleModeModule = null;

	private static File _workspaceDirectory;

	private static ModuleLoader _instance = null;

	private static ResourceManagerWindow _rmWindow;

	private static Module switchingToModule = null;

	private static Module activeModule = null;

	private static FlexoProject currentProject;

	private static FlexoAutoSaveThread autoSaveThread = null;

	public static String fileNameToOpen = null;

	/**
	 * Hashtable where are stored Module instances (instance of
	 *
	 * <pre>
	 * FlexoModule
	 * </pre>
	 *
	 * associated to a
	 *
	 * <pre>
	 * Module
	 * </pre>
	 *
	 * instance key.
	 */
	private static Hashtable<Module, FlexoModule> _modules = new Hashtable<Module, FlexoModule>();

	/**
	 * Vector of
	 *
	 * <pre>
	 * Module
	 * </pre>
	 *
	 * instance representing all available modules
	 */
	private static Vector<Module> _availableModules = new Vector<Module>();

	// private static Hashtable _availableModuleConstructors = new Hashtable();

	public static boolean allowsDocSubmission() {
		return allowsDocSubmission;
	}

	public static void setAllowsDocSubmission(boolean allowsDocSubmission) {
		ModuleLoader.allowsDocSubmission = allowsDocSubmission;
		SubmitDocumentationAction.actionType.setAllowsDocSubmission(allowsDocSubmission);
	}

	private ModuleLoader() {
		super();
		ExternalModuleDelegater.registerModuleLoader(this);
	}

	@Override
	public IModule getActiveModule() {
		if (FlexoModule.getActiveModule() != null) {
			return FlexoModule.getActiveModule().getModule();
		} else {
			return null;
		}
	}

	public static FlexoModule getFlexoModule(Module m) {
		return _modules.get(m);
	}

	public static ModuleLoader instance() {
		return _instance;
	}

	/**
	 * Called for a 'Multi-modules' initialization. Choosen module is opened.
	 *
	 * @param userType
	 */
	public static void initializeModules(UserType userType, boolean selectAndOpenProject) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Initializing ModuleLoader...");
		}
		multiModuleMode = true;
		initialize(Module.allKnownModules(), userType, selectAndOpenProject);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Initializing ModuleLoader DONE.");
		}

	}

	/**
	 * Called for a 'Single-module' initialization
	 *
	 * @param module
	 */
	public static void initializeSingleModule(Module module, UserType userType) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Initializing ModuleLoader...");
		}
		multiModuleMode = false;
		singleModuleModeModule = module;
		Vector<Module> modules = new Vector<Module>();
		modules.add(module);
		initialize(modules, userType, true);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Initializing ModuleLoader DONE.");
		}
		switchToModule(module);
	}

	/**
	 * Called for a 'Single-module' initialization
	 *
	 * @param module
	 */
	public static void initializeSingleModule(Module module)
	{
		initializeSingleModule(module,UserType.ANALYST);
	}

	/**
	 * Given a list of modules, check if user type has right to use them and register them consequently
	 *
	 * @param someModules
	 * @param userType
	 * @param selectAndOpenProject
	 */
	private static void initialize(Vector<Module> someModules, UserType userType, boolean selectAndOpenProject) {
		_instance = new ModuleLoader();
		_userType = userType;
		for (Enumeration<Module> e = someModules.elements(); e.hasMoreElements();) {
			Module module = e.nextElement();
			if (userType == null) {
				registerModule(module);
			} else if (module.getModuleClass() != null) {
				registerModule(module);
			}
		}
		try {
			ModuleLoader.setLookAndFeel(AdvancedPrefs.getLookAndFeelString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		if (GeneralPreferences.getLanguage() != null && GeneralPreferences.getLanguage().equals(Language.FRENCH)) {
			Locale.setDefault(Locale.FRANCE);
		} else {
			Locale.setDefault(Locale.US);
		}
		FlexoHelp.configure(GeneralPreferences.getLanguage() != null ? GeneralPreferences.getLanguage().getIdentifier() : "ENGLISH",
				userType.getIdentifier());
		if (selectAndOpenProject) {
			Module firstLaunchedModule = null;
			while (firstLaunchedModule == null || firstLaunchedModule.requireProject() && currentProject == null) {
				firstLaunchedModule = chooseProjectAndStartingModule();
				if (!isLoaded(firstLaunchedModule)) {// It basically means that a module could not be loaded.
					if (currentProject != null) {
						currentProject.close();
					}
					currentProject = null;
					continue;
				}
				if (currentProject == null && firstLaunchedModule.requireProject()) {
					continue;
				}
				if (firstLaunchedModule == null) {
					firstLaunchedModule = getPreferredModule();
				}
				if (multiModuleMode) {
					switchToModule(firstLaunchedModule);
				}
			}
		}
	}

	private static Module getPreferredModule() {
		String preferedModuleName = GeneralPreferences.getFavoriteModuleName();
		if (preferedModuleName == null || preferedModuleName.equals("")) {
			return _availableModules.elementAt(0);
		}
		Enumeration<Module> en = _availableModules.elements();
		Module answer = null;
		Module temp = null;
		while (en.hasMoreElements() && answer == null) {
			temp = en.nextElement();
			if (temp.getName().equals(preferedModuleName)) {
				answer = temp;
			}
		}
		if (answer == null) {
			return _availableModules.elementAt(0);
		}
		return answer;
	}

	/**
	 * Return active openflexo/flexodesktop directory
	 * (dev-mode only)
	 * @return
	 */
	public static File getWorkspaceDirectory()
	{
		if (_workspaceDirectory == null) {
			File returned = new File(System.getProperty("user.dir"));
			while (returned != null && !returned.getName().equals("openflexo")) {
				returned = returned.getParentFile();
			}
			_workspaceDirectory = new File(returned,"flexodesktop");
		}
		return _workspaceDirectory;
	}

	private static Module chooseProjectAndStartingModule() {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Initializing ModuleLoader... DONE.");
		}
		File projectDirectory = fileNameToOpen == null ? null : new File(fileNameToOpen);
		fileNameToOpen = null;
		Module firstLaunchedModule = projectDirectory != null && GeneralPreferences.getFavoriteModuleName() != null ? Module
				.getModule(GeneralPreferences.getFavoriteModuleName()) : null;
				boolean newProject = false;

				while (firstLaunchedModule == null || firstLaunchedModule.requireProject() && projectDirectory == null) {
					// logger.info("firstLaunchedModule="+firstLaunchedModule);
					// logger.info("projectDirectory="+projectDirectory);
					WelcomeDialog welcomeDialog = new WelcomeDialog();
					int result = welcomeDialog.getProjectSelectionChoice();
					firstLaunchedModule = welcomeDialog.getFirstlaunchedModule();
					if (firstLaunchedModule != null) {
						GeneralPreferences.setFavoriteModuleName(firstLaunchedModule.getName());
					}
					FlexoPreferences.savePreferences(true);
					switch (result) {
					case WelcomeDialog.OPEN_LAST_PROJECT:
						projectDirectory = new File(GeneralPreferences.getLastOpenedProject1());
						newProject = false;
						break;
					case WelcomeDialog.OPEN_PROJECT:
						try {
							projectDirectory = OpenProjectComponent.getProjectDirectory();
							if (projectDirectory != null && !projectDirectory.exists()) {
								if (!projectDirectory.getName().toLowerCase().endsWith(".prj")) {
									String newAttempt = projectDirectory.getAbsolutePath() + ".prj";
									File f = new File(newAttempt);
									if (f.exists()) {
										projectDirectory = f;
									}
								}
							}
							if (projectDirectory != null && !projectDirectory.exists()) {
								if (NewProjectComponent.isValidProjectName(projectDirectory.getName())) {
									if (FlexoController.confirm(FlexoLocalization
											.localizedForKey("project_does_not_exist_would_you_like_to_create_it?"))) {
										File newFileDir = projectDirectory.getParentFile();
										AdvancedPrefs.setLastVisitedDirectory(newFileDir);
										if (!projectDirectory.getName().toLowerCase().endsWith(".prj")) {
											projectDirectory = new File(projectDirectory.getAbsolutePath() + ".prj");
										}
										projectDirectory.mkdirs();
										newProject = true;
										break;
									} else {
										projectDirectory = null;
									}
								} else {
									FlexoController.notify(FlexoLocalization.localizedForKey("project_name_cannot_contain_\\___&_#_{_}_[_]_%_~"));
								}
							}

							else if (projectDirectory == null) {
								;// Avoids an NPE
							} else {
								if (!projectDirectory.isDirectory()) {
									projectDirectory = null;
								} else {
									if (!projectDirectory.canWrite()) {
										if (logger.isLoggable(Level.INFO)) {
											logger.info("Write permission denied for " + projectDirectory.getAbsolutePath());
										}
										FlexoController.notify(FlexoLocalization
												.localizedForKey("you_dont_have_writing_permissions_on_this_folder_save_will_not_work"));
									}
								}
							}
							newProject = false;
						} catch (ProjectLoadingCancelledException e1) {
							logger.info("Cancelled");
						}

						break;
					case WelcomeDialog.NEW_PROJECT:
						projectDirectory = NewProjectComponent.getProjectDirectory();
						newProject = true;
						break;
					case WelcomeDialog.OPEN_MODULE:
						break;
					case WelcomeDialog.QUIT:
						ModuleLoader.quit(false);
						break;
					default:
						ModuleLoader.quit();
					}
					if (projectDirectory != null) {
				if (!FlexoFileChooserUtils.PROJECT_FILE_NAME_FILTER.accept(projectDirectory.getParentFile(), projectDirectory.getName())) {
							FlexoController.notify(FlexoLocalization.localizedForKey("chosen_file_is_not_a_flexo_project"));
							projectDirectory = null;
						}
					}
				}
				if (!ProgressWindow.hasInstance()) {
					ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("loading_flexo_application_suite"),
							FlexoCst.LOADING_PROGRESS_STEPS);
				}
				if (firstLaunchedModule.requireProject()) {
					if (newProject) {
						newProject(projectDirectory, firstLaunchedModule);
					} else {
						openProjectWithModule(projectDirectory, firstLaunchedModule);
					}
				} else {
					if (multiModuleMode) {
						activeModule = null;
						switchToModule(firstLaunchedModule);
					}
				}
				return firstLaunchedModule;
	}

	public static boolean isMultiModuleMode() {
		return multiModuleMode;
	}

	public static Module getSingleModuleModeModule() {
		return singleModuleModeModule;
	}

	public static String getReleaseName() {
		if (multiModuleMode) {
			return _userType.getLocalizedName();
		} else {
			return FlexoLocalization.localizedForKey("single_module_release");
		}
	}

	public static UserType getUserType() {
		return _userType;
	}

	public static void setUserType(UserType userType) {
		_userType = userType;
	}

	public static boolean isCustomerRelease() {
		return _userType != null && _userType.equals(UserType.CUSTOMER);
	}

	public static boolean isAnalystRelease() {
		return _userType != null && _userType.equals(UserType.ANALYST);
	}

	public static boolean isDevelopperRelease() {
		return _userType != null && _userType.equals(UserType.DEVELOPER);
	}

	public static boolean isMaintainerRelease() {
		return _userType != null && _userType.equals(UserType.MAINTAINER);
	}

	/**
	 * Internally used to register module with class name
	 *
	 * <pre>
	 * moduleClass
	 * </pre>
	 *
	 * @param moduleClass
	 */
	private static void registerModule(Module module) {
		if (module.register()) {
			_availableModules.add(module);
		}
	}

	/**
	 * Return all loaded modules as an
	 *
	 * <pre>
	 * Enumeration
	 * </pre>
	 *
	 * of
	 *
	 * <pre>
	 * FlexoModule
	 * </pre>
	 *
	 * instances
	 *
	 * @return Enumeration
	 */
	public static Enumeration<FlexoModule> loadedModules() {
		return _modules.elements();
	}

	public static Hashtable<Module, FlexoModule> loadedModulesHashtable() {
		return _modules;
	}

	/**
	 * Return all unloaded modules but available modules as a
	 *
	 * <pre>
	 * Vector
	 * </pre>
	 *
	 * of
	 *
	 * <pre>
	 * Module
	 * </pre>
	 *
	 * instances
	 *
	 * @return Vector
	 */
	public static Vector<Module> unloadedButAvailableModules() {
		Vector<Module> returned = new Vector<Module>();
		returned.addAll(_availableModules);
		for (Enumeration<FlexoModule> e = loadedModules(); e.hasMoreElements();) {
			returned.remove(e.nextElement().getModule());
		}
		return returned;
	}

	/**
	 * Return all loaded modules as a
	 *
	 * <pre>
	 * Vector
	 * </pre>
	 *
	 * of
	 *
	 * <pre>
	 * Module
	 * </pre>
	 *
	 * instances
	 *
	 * @return Vector
	 */
	public static Vector<Module> availableModules() {
		return _availableModules;
	}

	/**
	 * Return all known modules as an
	 *
	 * <pre>
	 * Vector
	 * </pre>
	 *
	 * of
	 *
	 * <pre>
	 * Module
	 * </pre>
	 *
	 * instances
	 *
	 * @return Vector
	 */
	public static Vector<Module> allKnownModules() {
		return Module.allKnownModules();
	}

	public static void unloadModule(Module module) {
		if (isLoaded(module)) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Unloading module " + module.getName());
			}
			_modules.remove(module);
			WindowMenu.notifyModuleUnloaded(module);
			ModuleBar.notifyStaticallyModuleHasBeenUnLoaded(module);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Unable to unload unloaded module " + module.getName());
			}
		}
	}

	private static FlexoModule loadModule(Module module) {
		if (!ProgressWindow.hasInstance()) {
			ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("loading_module") + " " + module.getLocalizedName(), 8);
		}
		ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("loading_module") + " " + module.getLocalizedName());
		FlexoModule returned;
		try {
			returned = module.load();
		} catch (ProjectLoadingCancelledException e) {
			e.printStackTrace();
			ProgressWindow.hideProgressWindow();
			return null;
		} catch (ModuleLoadingException e) {
			e.printStackTrace();
			ProgressWindow.hideProgressWindow();
			FlexoController.showError(FlexoLocalization.localizedForKey("could_not_load_module") + " " + e.getModule().getLocalizedName());
			return null;
		}
		_modules.put(module, returned);
		activeModule = module;
		ProgressWindow.hideProgressWindow();
		WindowMenu.notifyModuleLoaded(module);
		ModuleBar.notifyStaticallyModuleHasBeenLoaded(module);
		return returned;
	}

	public static boolean isAvailable(Module module) {
		return _availableModules.contains(module);
	}

	public static boolean isLoaded(Module module) {
		return _modules.get(module) != null;
	}

	public static boolean isActive(Module module) {
		return instance().getActiveModule() == module;
	}

	public static FlexoModule getModuleInstance(Module module) {
		if (module == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Trying to get module instance for module null");
			}
			return null;
		}
		if (isAvailable(module)) {
			if (isLoaded(module)) {
				return _modules.get(module);
			} else {
				return loadModule(module);
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Sorry, module " + module.getName() + " not available.");
			}
			return null;
		}
	}

	public static ExternalWKFModule getWKFModule() {
		return (ExternalWKFModule) getModuleInstance(Module.WKF_MODULE);
	}

	public static ExternalIEModule getIEModule() {
		return (ExternalIEModule) getModuleInstance(Module.IE_MODULE);
	}

	public static ExternalDMModule getDMModule() {
		return (ExternalDMModule) getModuleInstance(Module.DM_MODULE);
	}

	public static ExternalGeneratorModule getGeneratorModule() {
		return (ExternalGeneratorModule) getModuleInstance(Module.CG_MODULE);
	}

	public static ExternalCEDModule getCEDModule() {
		return (ExternalCEDModule) getModuleInstance(Module.VPM_MODULE);
	}

	public static ExternalOEModule getOEModule() {
		return (ExternalOEModule) getModuleInstance(Module.VE_MODULE);
	}

	public static WelcomePanel getWelcomePanel() {
		if (multiModuleMode) {
			return new MultiModuleModeWelcomePanel();
		} else {
			return new SingleModuleModeWelcomePanel();
		}
	}

	private synchronized static void setSwitchingTo(Module module) {
		switchingToModule = module;
	}

	public static void switchToModule(Module module) {
		if (switchingToModule != null) {
			return;
		}
		if (activeModule == module) {
			return;
		}
		setSwitchingTo(module);
		try {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Switch to module " + module.getName());
			}
			if (instance().getActiveModule() != module) {
				FlexoModule moduleInstance = getModuleInstance(module);
				if (moduleInstance != null) {
					moduleInstance.focusOn();
				}
			}
		} catch (RuntimeException e) {
			switchingToModule = null;
			throw e;
		}
		setSwitchingTo(null);
		activeModule = module;
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Switch to module end");
		}
	}

	public static void notifyNewActiveModule(Module m) {
		if (activeModule != m) {
			getModuleInstance(m).processFocusOn();
			activeModule = m;
		}
	}

	/**
	 * Called for quitting with confirmation.
	 */
	public static void quit() {
		quit(true);
	}

	/**
	 * Called for quitting. Ask if saving must be performed, and exit on request.
	 *
	 * @param askConfirmation
	 */
	public static void quit(boolean askConfirmation) {
		if (askConfirmation) {
			proceedQuit();
		} else {
			proceedQuitWithoutConfirmation();
		}
	}

	private static void proceedQuit() {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Exiting FLEXO Application Suite...");
		}
		if (currentProject != null && currentProject.getUnsavedStorageResources(false).size() > 0) {
			SaveDialog reviewer = new SaveDialog(FlexoController.getActiveFrame(), currentProject);
			if (reviewer.getRetval() == SaveDialog.YES_OPTION) {
				try {
					ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving"), 1);
					reviewer.saveProject(ProgressWindow.instance());
					ProgressWindow.hideProgressWindow();
					proceedQuitWithoutConfirmation();
				} catch (SaveResourcePermissionDeniedException e) {
					ProgressWindow.hideProgressWindow();
					if (FlexoController.confirm(FlexoLocalization.localizedForKey("error_during_saving") + "\n" + FlexoLocalization
							.localizedForKey("would_you_like_to_exit_anyway"))) {
						proceedQuitWithoutConfirmation();
					}
				} catch (SaveResourceException e) {
					e.printStackTrace();
					ProgressWindow.hideProgressWindow();
					if (FlexoController.confirm(FlexoLocalization.localizedForKey("error_during_saving") + "\n" + FlexoLocalization
							.localizedForKey("would_you_like_to_exit_anyway"))) {
						proceedQuitWithoutConfirmation();
					}
				}
			} else if (reviewer.getRetval() == SaveDialog.NO_OPTION) {
				proceedQuitWithoutConfirmation();
			} else { // CANCEL
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Exiting FLEXO Application Suite... CANCELLED");
				}
			}
		} else {
			if (FlexoController.confirm(FlexoLocalization.localizedForKey("really_quit"))) {
				proceedQuitWithoutConfirmation();
			}
		}
	}

	private static void proceedQuitWithoutConfirmation() {
		if (activeModule != null) {
			GeneralPreferences.setFavoriteModuleName(activeModule.getName());
			FlexoPreferences.savePreferences(true);
		}

		for (Enumeration<FlexoModule> en = ModuleLoader.loadedModules(); en.hasMoreElements();) {
			en.nextElement().moduleWillClose();
		}
		if (ModuleLoader.allowsDocSubmission() && !ModuleLoader.isAvailable(Module.DRE_MODULE) && DocResourceManager.instance()
				.getSessionSubmissions().size() > 0) {
			if (FlexoController
					.confirm(FlexoLocalization.localizedForKey("you_have_submitted_documentation_without_having_saved_report") + "\n" + FlexoLocalization
							.localizedForKey("would_you_like_to_save_your_submissions"))) {
				new ToolsMenu.SaveDocSubmissionAction().actionPerformed(null);
			}
		}
		if (ModuleLoader.isAvailable(Module.DRE_MODULE)) {
			if (DocResourceManager.instance().needSaving()) {
				if (FlexoController
						.confirm(FlexoLocalization.localizedForKey("documentation_resource_center_not_saved") + "\n" + FlexoLocalization
								.localizedForKey("would_you_like_to_save_documenation_resource_center"))) {
					DocResourceManager.instance().save();
				}
			}
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Exiting FLEXO Application Suite... DONE");
		}
		System.exit(0);
	}

	private static InteractiveFlexoResourceUpdateHandler _flexoResourceUpdateHandler;

	private static InteractiveFlexoEditor newProject(File projectDirectory, Module moduleToReload) {
		if (!ProgressWindow.hasInstance()) {
			ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("building_new_project"), 10);
		} else {
			ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("building_new_project"));
		}
		if (currentFlexoVersionIsSmallerThanLastVersion(projectDirectory))
		{
			;// This will just create the .version in the project
		}
		preInitialization(projectDirectory);
		_flexoResourceUpdateHandler = new InteractiveFlexoResourceUpdateHandler();
		InteractiveFlexoEditor returned = (InteractiveFlexoEditor) FlexoResourceManager.initializeNewProject(projectDirectory,
				ProgressWindow.instance(), _flexoResourceUpdateHandler, InteractiveFlexoEditor.FACTORY, getFlexoResourceCenter());
		currentProject = returned.getProject();
		conditionalStartOfAutoSaveThread(returned);
		activeModule = null;
		switchToModule(moduleToReload);
		ProgressWindow.hideProgressWindow();
		return returned;
	}

	public static InteractiveFlexoEditor newProject() {
		Module moduleToReload = null;
		if (currentProject != null) {
			moduleToReload = FlexoModule.getActiveModule().getModule();
			if (!saveProject()) {
				return null;
			}
		}
		File projectDirectory = NewProjectComponent.getProjectDirectory();
		if (projectDirectory != null) {
			closeCurrentProject();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Choosen " + projectDirectory.getAbsolutePath());
			}
			return newProject(projectDirectory, moduleToReload);
		}
		return null;
	}

	/**
	 * Check if there is an external repository with some active resources connected In this case, explicitely ask what to do, connect or
	 * let disconnected
	 *
	 * @param project
	 */
	private static void checkExternalRepositories(FlexoProject project) {
		if (getUserType() != UserType.MAINTAINER && getUserType() != UserType.DEVELOPER) {
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
							"what_would_you_like_to_do", CONNECT, choices);
					DirectoryParameter dirParam = new DirectoryParameter("directory", "directory", repository.getDirectory());
					dirParam.setDepends("choice");
					dirParam.setConditional("choice=" + '"' + CONNECT + '"');
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(
							getProject(),
							null,
							FlexoLocalization.localizedForKey("connect_repository_to_local_file_system"),
							repository.getName() + " : " + FlexoLocalization
							.localizedForKey("repository_seems_to_be_not_valid_on_local_file_system"), choiceParam, dirParam);
					System.setProperty("apple.awt.fileDialogForDirectories", "false");
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						if (choiceParam.getValue().equals(CONNECT)) {
							if (dirParam.getValue() != null) {
								if (!dirParam.getValue().exists()) {
									if (FlexoController
											.confirm(FlexoLocalization.localizedForKey("directory") + " " + dirParam.getValue()
													.getAbsolutePath() + " " + FlexoLocalization.localizedForKey("does_not_exist") + "\n" + FlexoLocalization
													.localizedForKey("would_you_like_to_create_it_and_continue?"))) {
										dirParam.getValue().mkdirs();
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

	/**
	 * Loads the project located within <code>projectDirectory</code> and then switches automatically to the given module
	 * <code> moduleToReload </code>.
	 *
	 * @param projectDirectory
	 * @param moduleToReload
	 * @return the editor of the loaded project.
	 */
	protected static InteractiveFlexoEditor openProjectWithModule(File projectDirectory, Module moduleToReload) {
		InteractiveFlexoEditor editor = loadProject(projectDirectory);
		if (editor == null) {
			return null;
		}
		if (moduleToReload == null) {
			moduleToReload = Module.allKnownModules().firstElement();
		}
		if (multiModuleMode) {
			activeModule = null;
			switchToModule(moduleToReload);
		}
		return editor;
	}

	/**
	 * Loads the project located withing <code> projectDirectory </code>. The following method is the default methode to call when opening a
	 * project from a GUI (Interactive mode) so that resource update handling is properly initialized. Additional small stuffs can be
	 * performed in that call so that projects are always opened the same way.
	 *
	 * @param projectDirectory
	 * @return the {@link InteractiveFlexoEditor} editor if the opening succeeded else <code>null</code>
	 */
	public static InteractiveFlexoEditor loadProject(File projectDirectory) {
		boolean restructureProjectHierarchy = false;
		InteractiveFlexoEditor newEditor = null;
		if (projectDirectory == null || !projectDirectory.exists()) {
			return null;
		}
		if (FlexoResourceManager.needsRestructuring(projectDirectory)) {
			restructureProjectHierarchy = FlexoController.ask(FlexoLocalization
					.localizedForKey("do_you_want_the_hierarchy_project_to_be_restructured")) == JOptionPane.YES_OPTION;
		}
		FlexoVersion previousFlexoVersion = getVersion(projectDirectory);
		if (!isProjectOpenable(projectDirectory)) {
			ProgressWindow.hideProgressWindow();
			return null;
		}
		if (autoSaveThread != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Auto-save thread still running. Killing it now. Please ensure that auto-save thread is stopped before loading a project");
			}
			stopAutoSaveThread();
		}
		if (ProgressWindow.hasInstance()) {
			ProgressWindow.hideProgressWindow();
		}
		ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("loading_project"), 14);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Opening " + projectDirectory.getAbsolutePath());
		}
		FlexoProject newProject;
		preInitialization(projectDirectory);
		try {
			_flexoResourceUpdateHandler = new InteractiveFlexoResourceUpdateHandler();
			newEditor = (InteractiveFlexoEditor) FlexoResourceManager.initializeExistingProject(projectDirectory,
					restructureProjectHierarchy, ProgressWindow.instance(), _flexoResourceUpdateHandler, InteractiveFlexoEditor.FACTORY,
					getUserType().getDefaultLoadingHandler(projectDirectory), ModuleLoader.getFlexoResourceCenter());
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
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
			FlexoController.notify(FlexoLocalization.localizedForKey("could_not_open_project_located_at") + projectDirectory
					.getAbsolutePath());
			ProgressWindow.hideProgressWindow();
			return null;
		} catch (ProjectLoadingCancelledException e) {
			// Loading cancelled
			ProgressWindow.hideProgressWindow();
			return null;
		}
		currentProject = newProject;
		conditionalStartOfAutoSaveThread(newEditor);
		ProgressWindow.hideProgressWindow();
		return newEditor;
	}

	public static void conditionalStartOfAutoSaveThread(InteractiveFlexoEditor editor) {
		if (GeneralPreferences.isAutoSavedEnabled()) {
			startAutoSaveThread();
		} else {
			if (editor.isAutoSaveEnabledByDefault()) {
				startAutoSaveThread();
			}
		}
	}

	private static void preInitialization(File projectDirectory) {
		GeneralPreferences.addToLastOpenedProjects(projectDirectory);
		FlexoPreferences.savePreferences(true);
	}

	/**
	 * @param projectDirectory
	 * @return
	 */
	private static boolean currentFlexoVersionIsSmallerThanLastVersion(File projectDirectory) {
		File f = getVersionFile(projectDirectory);
		if (!f.exists()) {
			createVersionFile(projectDirectory);
			return false;
		} else {
			FlexoVersion v = getVersion(projectDirectory);
			// bidouille so that Version will accept 1.0.1RC1 as bigger than
			// 1.0.1beta
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Version is " + v);
			}
			boolean result = FlexoXMLMappings.latestRelease().isLesserThan(v);
			if (!result) {
				createVersionFile(projectDirectory);
			}
			return result;
		}
	}

	/**
	 * @param f
	 * @return
	 */
	private static FlexoVersion getVersion(File projectDirectory) {
		File f = getVersionFile(projectDirectory);
		StringBuilder sb = new StringBuilder();
		byte[] b = new byte[512];
		FileInputStream fis;
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		int i = 0;
		while (i > -1) {
			try {
				i = fis.read(b);
				if (i > -1) {
					sb.append(new String(b, 0, i, "UTF-8"));
				}
			} catch (IOException e) {
				e.printStackTrace();
				try {
					fis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return null;
			}
		}
		try {
			fis.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return new FlexoVersion(sb.toString());
	}

	/**
	 * @param f
	 * @return
	 */
	private static void createVersionFile(File projectDirectory) {
		File f = getVersionFile(projectDirectory);
		if (!f.exists()) {
			boolean create = false;
			try {
				create = f.createNewFile();
			} catch (IOException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("IOException in creation of version file: " + e.getMessage());
				}
				return;
			}
			if (!create) {
				return;
			}
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("FileNotFoundException in creation of version file: " + e.getMessage());
			}
			if (f.exists()) {
				FileUtils.unmakeFileHidden(f);
			} else {
				return;
			}
			try {
				fos = new FileOutputStream(f);
			} catch (FileNotFoundException e1) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("FileNotFoundException in creation of version file: " + e1.getMessage());
				}
				return;
			}
		}
		try {
			fos.write(FlexoXMLMappings.latestRelease().toString().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("IOException in creation of version file: " + e.getMessage());
			}
		} finally {
			try {
				fos.flush();
			} catch (IOException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("IOException in flushing of version file: " + e.getMessage());
				}
			}
			try {
				fos.close();
			} catch (IOException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("IOException in closing of version file: " + e.getMessage());
				}
			}
		}
	}

	/**
	 * @param projectDirectory
	 * @return
	 */
	private static File getVersionFile(File projectDirectory) {
		String versionFileName = ".version";
		File f = new File(projectDirectory, versionFileName);
		return f;
	}

	public static InteractiveFlexoEditor openProject(File projectDirectory) {
		Module moduleToReload = null;
		if (currentProject != null) {
			if (FlexoModule.getActiveModule() != null) {
				moduleToReload = FlexoModule.getActiveModule().getModule();
			}
			if (!saveProject()) {
				return null;
			}
		}
		if (projectDirectory == null) {
			try {
				projectDirectory = OpenProjectComponent.getProjectDirectory();
			} catch (ProjectLoadingCancelledException e1) {
				e1.printStackTrace();
				return null;
			}
		}
		if (projectDirectory != null) {
			if (!isProjectOpenable(projectDirectory)) {
				return null;
			}
			closeCurrentProject();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Choosen " + projectDirectory.getAbsolutePath());
			}
			return openProjectWithModule(projectDirectory, moduleToReload);
		}
		return null;
	}

	/**
	 * @param projectDirectory
	 */
	private static boolean isProjectOpenable(File projectDirectory) {
		FlexoVersion version = getVersion(projectDirectory);
		if (version != null && version.major == 1 && version.minor < 3) {
			FlexoController.notify(FlexoLocalization.localizedForKey("project_is_too_old_please_use_intermediary_versions"));
			return false;
		}
		if (currentFlexoVersionIsSmallerThanLastVersion(projectDirectory)) {
			FlexoController.notify(FlexoLocalization
					.localizedForKey("current_flexo_version_is_smaller_than_last_used_to_open_this_project"));
			return false;
		}
		return true;
	}

	/**
	 *
	 */
	public static void closeCurrentProject() {
		if (currentProject != null) {
			currentProject.close();
		}
		currentProject = null;
		stopAutoSaveThread();
		if (_rmWindow != null) {
			_rmWindow.dispose();
			_rmWindow = null;
		}
		for (Enumeration<FlexoModule> e = new Hashtable<Module, FlexoModule>(loadedModulesHashtable()).elements(); e.hasMoreElements();) {
			FlexoModule mod = e.nextElement();
			mod.closeWithoutConfirmation(false);
		}
		FCH.clearComponentsHashtable();
		FlexoHelp.instance.deleteObservers();
		// FlexoSharedInspectorController.resetInstance();
		FlexoLocalization.clearStoredLocalizedForComponents();
		WindowMenu.reset();
		ModuleBar.reset();
		FlexoProject.cleanUpActionizer();
		System.gc();
	}

	public static FlexoProject getProject() {
		return currentProject;
	}

	public static void setProject(FlexoProject project) {
		currentProject = project;
		if (currentProject != null) {
			flexoResourceCenter = currentProject.getResourceCenter();
		}
	}

	public static InteractiveFlexoEditor reloadProject() {
		Module moduleToReload = null;
		if (currentProject != null) {
			moduleToReload = FlexoModule.getActiveModule().getModule();
			if (!saveProject()) {
				return null;
			}
		}
		File projectDirectory = getProject().getProjectDirectory();
		if (projectDirectory != null) {
			closeCurrentProject();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Choosen " + projectDirectory.getAbsolutePath());
			}
			return openProjectWithModule(projectDirectory, moduleToReload);
		}
		return null;
	}

	private static final String FOR_FLEXO_SERVER = "_forFlexoServer_";

	public static void saveProjectForServer() {
		final String zipFilename = getProject().getProjectDirectory().getName()
				.substring(0, getProject().getProjectDirectory().getName().length() - 4);
		String zipFileNameProposal = zipFilename + FOR_FLEXO_SERVER + new FlexoVersion(1, 0, 0, -1, false, false);
		File[] zips = getProject().getProjectDirectory().getParentFile().listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().toLowerCase().endsWith(".zip") && pathname.getName().startsWith(zipFilename);
			}
		});
		File previousVersion = null;
		if (zips != null) {
			for (int i = 0; i < zips.length; i++) {
				File file = zips[i];
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
		final FileParameter targetZippedProject = new FileParameter("targetZippedProject", "new_zip_file", new File(getProject()
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

		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
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
				ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving"),
						removeScreenshotsAndLibraries.getValue() ? 5 : 2);
				getProject().saveAsZipFile(zipFile, ProgressWindow.instance(), removeScreenshotsAndLibraries.getValue(), true);
				ProgressWindow.hideProgressWindow();
			} catch (SaveResourceException e) {
				e.printStackTrace();
				ProgressWindow.hideProgressWindow();
				FlexoController.notify(FlexoLocalization.localizedForKey("save_as_operation_failed"));
			}
		}
	}

	public static void saveAsProject() {
		Vector<FlexoVersion> availableVersions = new Vector<FlexoVersion>(getProject().getXmlMappings().getReleaseVersions());
		Collections.sort(availableVersions, Collections.reverseOrder(FlexoVersion.comparator));

		final DirectoryParameter targetPrjDirectory = new DirectoryParameter("targetPrjDirectory", "new_project_file", getProject()
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
				availableVersions, availableVersions.firstElement());
		versionParam.setShowReset(false);

		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
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
				projectDirectory.mkdirs();
			}
			/*
			 * else { if (!FlexoController.confirmWithWarning(FlexoLocalization
			 * .localizedForKey("project_already_exists_do_you_want_to_replace_it"))) return; }
			 */
			if (!projectDirectory.canWrite()) {
				FlexoController.notify(FlexoLocalization.localizedForKey("could_not_save_permission_denied"));
				return;
			}
			try {
				ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving"), 1);
				getProject().saveAs(projectDirectory, ProgressWindow.instance(),
						FlexoCst.BUSINESS_APPLICATION_VERSION.equals(versionParam.getValue()) ? null : versionParam.getValue(), true,
								true);
				GeneralPreferences.addToLastOpenedProjects(projectDirectory);
				ProgressWindow.hideProgressWindow();
			} catch (SaveResourceException e) {
				e.printStackTrace();
				ProgressWindow.hideProgressWindow();
				FlexoController.notify(FlexoLocalization.localizedForKey("save_as_operation_failed"));
			}
		} else {
			// Cancelled;
			return;
		}
	}

	public static boolean saveProject(boolean reviewUnsaved) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Saving project");
		}
		if (getProject() != null) {
			if (getProject().getUnsavedStorageResources().size() > 0) {
				if (!reviewUnsaved) {
					try {
						ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving"), 1);
						getProject().save(ProgressWindow.instance());
						ProgressWindow.hideProgressWindow();
						return true;
					} catch (SaveResourceException e) {
						// Warns about the exception
						ProgressWindow.hideProgressWindow();
						if (e instanceof SaveResourcePermissionDeniedException) {
							if (e.getFileResource().getFile().isDirectory()) {
								FlexoController
								.showError(
										FlexoLocalization.localizedForKey("permission_denied"),
										FlexoLocalization
										.localizedForKey("project_was_not_properly_saved_permission_denied_directory") + "\n" + e
										.getFileResource().getFile().getAbsolutePath());
							} else {
								FlexoController
								.showError(
										FlexoLocalization.localizedForKey("permission_denied"),
										FlexoLocalization.localizedForKey("project_was_not_properly_saved_permission_denied_file") + "\n" + e
										.getFileResource().getFile().getAbsolutePath());
							}
						} else {
							FlexoController.showError(FlexoLocalization.localizedForKey("error_during_saving"));
						}
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
						logger.warning(e.getMessage());
						e.printStackTrace();
						return false;
					}
				} else {
					return openResourceReviewerDialog();
				}
			} else {
				// Nothing to do: there are no modified resources !
				return true;
			}
		}
		return false;
	}

	public static boolean openResourceReviewerDialog() {
		SaveDialog reviewer = new SaveDialog(FlexoController.getActiveFrame(), getProject());
		if (reviewer.getRetval() == SaveDialog.YES_OPTION) {
			try {
				ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving"), 2);
				reviewer.saveProject(ProgressWindow.instance());
				ProgressWindow.instance().setVisible(false);
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Saving project... DONE");
				}
				return true;
			} catch (SaveResourcePermissionDeniedException e) {
				ProgressWindow.hideProgressWindow();
				if (e.getFileResource().getFile().isDirectory()) {
					FlexoController.showError(FlexoLocalization.localizedForKey("permission_denied"),
							FlexoLocalization.localizedForKey("project_was_not_properly_saved_permission_denied_directory") + "\n" + e
							.getFileResource().getFile().getAbsolutePath());
				} else {
					FlexoController.showError(FlexoLocalization.localizedForKey("permission_denied"),
							FlexoLocalization.localizedForKey("project_was_not_properly_saved_permission_denied_file") + "\n" + e
							.getFileResource().getFile().getAbsolutePath());
				}
				return false;
			} catch (SaveResourceException e) {
				e.printStackTrace();
				ProgressWindow.hideProgressWindow();
				return FlexoController.confirm(FlexoLocalization.localizedForKey("error_during_saving") + "\n" + FlexoLocalization
						.localizedForKey("would_you_like_to_exit_anyway"));
			}
		} else if (reviewer.getRetval() == SaveDialog.NO_OPTION) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Saving project...NO");
			}
			return true;
		} else { // CANCEL
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Saving project... CANCELLED");
			}
			return false;
		}
	}

	public static boolean saveProject() {
		return saveProject(true);
	}

	public static ResourceManagerWindow getRMWindow(FlexoProject project) {
		if (_rmWindow == null) {
			_rmWindow = new ResourceManagerWindow(project);
		}
		return _rmWindow;
	}

	/**
	 * Overrides getIEModuleInstance
	 *
	 * @see org.openflexo.module.external.IModuleLoader#getIEModuleInstance()
	 */
	@Override
	public ExternalIEModule getIEModuleInstance() {
		return getIEModule();
	}

	/**
	 * Overrides getDMModuleInstance
	 *
	 * @see org.openflexo.module.external.IModuleLoader#getDMModuleInstance()
	 */
	@Override
	public ExternalDMModule getDMModuleInstance() {
		return getDMModule();
	}

	/**
	 * Overrides getWKFModuleInstance
	 *
	 * @see org.openflexo.module.external.IModuleLoader#getWKFModuleInstance()
	 */
	@Override
	public ExternalWKFModule getWKFModuleInstance() {
		return getWKFModule();
	}

	@Override
	public ExternalCEDModule getCEDModuleInstance() {
		return getCEDModule();
	}

	@Override
	public ExternalOEModule getOEModuleInstance() {
		return getOEModule();
	}

	public static InteractiveFlexoResourceUpdateHandler getFlexoResourceUpdateHandler() {
		return _flexoResourceUpdateHandler;
	}

	/**
	 * @param value
	 * @throws ClassNotFoundException
	 * @throws UnsupportedLookAndFeelException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void setLookAndFeel(String value) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
	UnsupportedLookAndFeelException {
		if (UIManager.getLookAndFeel().getClass().getName().equals(value)) {
			return;
		}
		UIManager.setLookAndFeel(value);
		Frame[] frames = Frame.getFrames();
		for (int i = 0; i < frames.length; i++) {
			Frame frame = frames[i];
			Window windows[] = frames[i].getOwnedWindows();
			for (int j = 0; j < windows.length; j++) {
				SwingUtilities.updateComponentTreeUI(windows[j]);
			}
			SwingUtilities.updateComponentTreeUI(frame);
		}
	}

	public static void updateModuleFrameTitles() {
		Enumeration<FlexoModule> en = loadedModules();
		while (en.hasMoreElements()) {
			FlexoModule module = en.nextElement();
			module.getFlexoFrame().updateTitle();
		}
	}

	public static void startAutoSaveThread() {
		if (autoSaveThread != null && autoSaveThread.getProject() != currentProject) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("This is not normal, there was an auto-save thread running but a new project has been loaded. Please always stop the auto-save thread before loading another project.");
			}
			stopAutoSaveThread();
		}
		if (GeneralPreferences.getAutoSaveEnabled() && currentProject != null && autoSaveThread == null) {
			autoSaveThread = new FlexoAutoSaveThread(currentProject);
			autoSaveThread.setNumberOfIntermediateSave(GeneralPreferences.getAutoSaveLimit());
			autoSaveThread.setSleepTime(GeneralPreferences.getAutoSaveInterval() * 60 * 1000);
			autoSaveThread.start();
		}
	}

	public static void stopAutoSaveThread() {
		if (autoSaveThread != null) {
			autoSaveThread.setRun(false);
			if (autoSaveThread.getState() == State.TIMED_WAITING) {
				autoSaveThread.interrupt();
			}
			autoSaveThread = null;
		}
	}

	public static void setAutoSaveLimit(int limit) {
		if (autoSaveThread != null) {
			autoSaveThread.setNumberOfIntermediateSave(limit);
		}
	}

	public static void setAutoSaveSleepTime(int time) {
		if (autoSaveThread != null) {
			autoSaveThread.setSleepTime(time * 60 * 1000);
		}
	}

	public static File getAutoSaveDirectory() {
		if (autoSaveThread != null) {
			return autoSaveThread.getTempDirectory();
		} else {
			return null;
		}
	}

	/**
	 *
	 */
	public static void showTimeTravelerDialog() {
		if (autoSaveThread != null) {
			autoSaveThread.showTimeTravelerDialog();
		}
	}

	public static boolean isTimeTravelingAvailable() {
		return autoSaveThread != null;
	}

	public static boolean isCutAndPasteEnabled() {
		return isDevelopperRelease() || isMaintainerRelease();
	}

	@Override
	public boolean isWKFLoaded() {
		return isLoaded(Module.WKF_MODULE);
	}

	private static FlexoResourceCenter flexoResourceCenter;

	public static void installFlexoResourceCenter(FlexoResourceCenter aResourceCenter) {
		flexoResourceCenter = aResourceCenter;
	}

	public static FlexoResourceCenter getFlexoResourceCenter() {
		return getFlexoResourceCenter(true);
	}

	public static FlexoResourceCenter getFlexoResourceCenter(boolean createIfNotExist) {
		if (flexoResourceCenter == null && createIfNotExist) {
			if (GeneralPreferences.getLocalResourceCenterDirectory() == null || !GeneralPreferences.getLocalResourceCenterDirectory()
					.exists()) {
				if (isDevelopperRelease() || isMaintainerRelease()) {
					AskLocalResourceCenterDirectory data = new AskLocalResourceCenterDirectory();
					data.setLocalResourceDirectory(FlexoProject.getResourceCenterFile());
					FIBDialog dialog = FIBDialog.instanciateComponent(AskLocalResourceCenterDirectory.FIB_FILE, data, null, true);
					switch (dialog.getStatus()) {
					case VALIDATED:
						if (data.getLocalResourceDirectory() != null) {
							if (!data.getLocalResourceDirectory().exists()) {
								data.getLocalResourceDirectory().mkdirs();
							}
							if (!data.getLocalResourceDirectory().exists()) {
								break;
							}
							LocalResourceCenterImplementation rc = LocalResourceCenterImplementation
									.instanciateNewLocalResourceCenterImplementation(data.getLocalResourceDirectory());
							installFlexoResourceCenter(rc);
							GeneralPreferences.setLocalResourceCenterDirectory(data.getLocalResourceDirectory());
						}
						break;
					case CANCELED:
						break;
					case QUIT:
						ModuleLoader.quit(true);
						break;
					default:
						break;
					}
				} else { // Otherwise, dont ask but create resource center in home directory if required
					File resourceCenterDirectory = FlexoProject.getResourceCenterFile();
					if (!resourceCenterDirectory.exists()) {
						logger.info("Create directory " + resourceCenterDirectory);
						resourceCenterDirectory.mkdirs();
						LocalResourceCenterImplementation rc = LocalResourceCenterImplementation
								.instanciateNewLocalResourceCenterImplementation(resourceCenterDirectory);
						installFlexoResourceCenter(rc);
						GeneralPreferences.setLocalResourceCenterDirectory(resourceCenterDirectory);
					} else {
						flexoResourceCenter = new LocalResourceCenterImplementation(resourceCenterDirectory);
					}
				}
			} else if (flexoResourceCenter == null) {
				flexoResourceCenter = new LocalResourceCenterImplementation(GeneralPreferences.getLocalResourceCenterDirectory());
			}
		}
		return flexoResourceCenter;
	}

}