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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.ApplicationContext;
import org.openflexo.action.SubmitDocumentationAction;
import org.openflexo.components.ProgressWindow;
import org.openflexo.components.SaveProjectsDialog;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoService;
import org.openflexo.foundation.FlexoServiceImpl;
import org.openflexo.foundation.resource.SaveResourceExceptionList;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.utils.OperationCancelledException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.ControllerModel;

/**
 * This service manages modules<br>
 * 
 * Modules are retrieved from service management, and can be loaded and/or unloaded.
 * 
 * @author sguerin
 */
public class ModuleLoader extends FlexoServiceImpl implements FlexoService, HasPropertyChangeSupport {

	private static final Logger logger = Logger.getLogger(ModuleLoader.class.getPackage().getName());

	public static final String ACTIVE_MODULE = "activeModule";

	public static final String MODULE_LOADED = "moduleLoaded";
	public static final String MODULE_UNLOADED = "moduleUnloaded";
	public static final String MODULE_ACTIVATED = "moduleActivated";

	private boolean allowsDocSubmission;

	private FlexoModule activeModule = null;

	private Module activatingModule;

	private WeakReference<FlexoEditor> lastActiveEditor;

	private class ActiveEditorListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(ControllerModel.CURRENT_EDITOR)) {
				FlexoEditor newEditor = (FlexoEditor) evt.getNewValue();
				if (newEditor != null) {
					lastActiveEditor = new WeakReference<FlexoEditor>(newEditor);
				}
			}
		}

	}

	private final ActiveEditorListener activeEditorListener = new ActiveEditorListener();

	private final ApplicationContext applicationContext;

	private final PropertyChangeSupport propertyChangeSupport;

	public ModuleLoader(ApplicationContext applicationContext) {
		super();
		this.applicationContext = applicationContext;
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}

	@Override
	public ApplicationContext getServiceManager() {
		return (ApplicationContext) super.getServiceManager();
	}

	private Map<Class<? extends Module>, Module<?>> knownModules;

	/**
	 * Load all available technology adapters<br>
	 * Retrieve all {@link TechnologyAdapter} available from classpath. <br>
	 * Map contains the TechnologyAdapter class name as key and the TechnologyAdapter itself as value.
	 * 
	 * @return the retrieved TechnologyModuleDefinition map.
	 */
	private void loadAvailableModules() {
		if (knownModules == null) {
			knownModules = new HashMap<Class<? extends Module>, Module<?>>();
			logger.info("Loading available modules...");
			ServiceLoader<Module> loader = ServiceLoader.load(Module.class);
			Iterator<Module> iterator = loader.iterator();
			while (iterator.hasNext()) {
				Module module = iterator.next();
				registerModule(module);
			}
			logger.info("Loading available technology adapters. Done.");
		}

	}

	private void registerModule(Module<?> module) {
		logger.info("Found " + module);
		module.setModuleLoader(this);
		logger.info("Load " + module.getName() + " as " + module.getClass());

		if (knownModules.containsKey(module.getClass())) {
			logger.severe("Cannot include Module with classname '" + module.getClass().getName()
					+ "' because it already exists !!!! A Module name MUST be unique !");
		} else {
			knownModules.put(module.getClass(), module);
		}
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	@Override
	public String getDeletedProperty() {

		return null;
	}

	public boolean allowsDocSubmission() {
		return allowsDocSubmission;
	}

	public void setAllowsDocSubmission(boolean allowsDocSubmission) {
		this.allowsDocSubmission = allowsDocSubmission;
		SubmitDocumentationAction.actionType.setAllowsDocSubmission(allowsDocSubmission);
	}

	public FlexoEditor getLastActiveEditor() {
		if (lastActiveEditor != null) {
			return lastActiveEditor.get();
		}
		return null;
	}

	public FlexoModule getActiveModule() {
		return activeModule;
	}

	/**
	 * Return a collection containing all known modules
	 * 
	 * @return
	 */
	public Collection<Module<?>> getKnownModules() {
		return knownModules.values();
	}

	/**
	 * Return a module with name as supplied
	 * 
	 * @return
	 */
	public Module<?> getModuleNamed(String moduleName) {
		for (Module<?> module : getKnownModules()) {
			if (module.getName().equals(moduleName)) {
				return module;
			}
		}
		return null;
	}

	/**
	 * Return a collection containing all loaded modules as a list of Module instances
	 * 
	 * @return
	 */
	public List<Module<?>> getLoadedModules() {
		List<Module<?>> returned = new ArrayList<Module<?>>();
		for (Module<?> module : getKnownModules()) {
			if (module.isLoaded()) {
				returned.add(module);
			}
		}
		return returned;
	}

	public int getLoadedModuleCount() {
		return getLoadedModules().size();
	}

	/**
	 * Return all unloaded modules but available modules as a list of Module instances
	 * 
	 * @return Vector
	 */
	public List<Module<?>> getUnloadedModules() {
		List<Module<?>> returned = new ArrayList<Module<?>>();
		for (Module<?> module : getKnownModules()) {
			if (!module.isLoaded()) {
				returned.add(module);
			}
		}
		return returned;
	}

	/**
	 * Unload supplied module
	 * 
	 * @param module
	 */
	public void unloadModule(Module<?> module) {
		if (isLoaded(module)) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Unloading module " + module.getName());
			}
			if (activeModule == module.getLoadedModuleInstance()) {
				activeModule = null;
			}
			module.unload();
			getPropertyChangeSupport().firePropertyChange(MODULE_UNLOADED, module, null);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Unable to unload unloaded module " + module.getName());
			}
		}
	}

	/**
	 * Notification of a new Module being loaded
	 * 
	 * @author sylvain
	 * 
	 */
	public class ModuleLoaded implements ServiceNotification {
		private final FlexoModule loadedModule;

		public ModuleLoaded(FlexoModule loadedModule) {
			this.loadedModule = loadedModule;
		}

		public FlexoModule getLoadedModule() {
			return loadedModule;
		}
	}

	/**
	 * Notification of a new Module being loaded
	 * 
	 * @author sylvain
	 * 
	 */
	public class ModuleActivated implements ServiceNotification {
		private final FlexoModule loadedModule;

		public ModuleActivated(FlexoModule loadedModule) {
			this.loadedModule = loadedModule;
		}

		public FlexoModule getLoadedModule() {
			return loadedModule;
		}
	}

	public boolean isLoaded(Module<?> module) {
		return module.isLoaded();
	}

	public boolean isActive(Module<?> module) {
		return getActiveModule() != null && getActiveModule().getModule() == module;
	}

	public boolean isActive(FlexoModule module) {
		return getActiveModule() == module;
	}

	public FlexoModule getModuleInstance(Module<?> module) throws ModuleLoadingException {
		if (module == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Trying to get module instance for module null");
			}
			return null;
		}

		try {
			module.load();
			return module.getLoadedModuleInstance();
		} catch (Exception e) {
			ProgressWindow.hideProgressWindow();
			e.printStackTrace();
			throw new ModuleLoadingException(module);
		}
	}

	private boolean ignoreSwitch = false;

	public FlexoModule switchToModule(final Module module) throws ModuleLoadingException {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						switchToModule(module);
					} catch (ModuleLoadingException e) {
						e.printStackTrace();
					}
				}
			});
			return null;
		}
		if (ignoreSwitch || activeModule != null && activeModule.getModule() == module) {
			return activeModule;
		}
		ignoreSwitch = true;
		activatingModule = module;
		try {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Switch to module " + module.getName());
			}
			FlexoModule moduleInstance = getModuleInstance(module);
			if (moduleInstance != null) {
				FlexoModule old = activeModule;
				if (activeModule != null) {
					activeModule.getController().getControllerModel().getPropertyChangeSupport()
							.removePropertyChangeListener(ControllerModel.CURRENT_EDITOR, activeEditorListener);
					activeModule.setAsInactive();
				}
				activeModule = moduleInstance;
				moduleInstance.setAsActiveModule();
				if (activeModule.getModule().requireProject()) {
					activeModule.getController().getControllerModel().getPropertyChangeSupport()
							.addPropertyChangeListener(ControllerModel.CURRENT_EDITOR, activeEditorListener);
				}
				getPropertyChangeSupport().firePropertyChange(ACTIVE_MODULE, old, activeModule);
				getServiceManager().notify(this, new ModuleActivated(activeModule));
				return moduleInstance;
			}
			throw new ModuleLoadingException(module);
		} finally {
			activatingModule = null;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (isLoaded(module)) {
						module.getLoadedModuleInstance().getFlexoFrame().toFront();
					}
					ModuleLoader.this.ignoreSwitch = false;
				}
			});
		}
	}

	/**
	 * Called for quitting. Ask if saving must be performed, and exit on request.
	 * 
	 * @param askConfirmation
	 *            if flexo must ask confirmation to the user
	 * @throws OperationCancelledException
	 *             whenever user decide to not quit
	 */
	public void quit(boolean askConfirmation) throws OperationCancelledException {
		if (askConfirmation) {
			proceedQuit();
		} else {
			proceedQuitWithoutConfirmation();
		}
	}

	private void proceedQuit() throws OperationCancelledException {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Exiting FLEXO Application Suite...");
		}
		if (applicationContext.getProjectLoader().someProjectsAreModified()) {
			try {
				saveModifiedProjects();
			} catch (SaveResourceExceptionList e) {
				e.printStackTrace();
				if (FlexoController.confirm(FlexoLocalization.localizedForKey("error_during_saving") + "\n"
						+ FlexoLocalization.localizedForKey("would_you_like_to_exit_anyway"))) {
					proceedQuitWithoutConfirmation();
				}
			}
			proceedQuitWithoutConfirmation();
		} else {
			if (FlexoController.confirm(FlexoLocalization.localizedForKey("really_quit"))) {
				proceedQuitWithoutConfirmation();
			} else {
				throw new OperationCancelledException();
			}
		}
	}

	public void saveModifiedProjects() throws OperationCancelledException, SaveResourceExceptionList {
		SaveProjectsDialog dialog = new SaveProjectsDialog(getActiveModule() != null ? getActiveModule().getController() : null,
				applicationContext.getProjectLoader().getModifiedProjects());
		if (dialog.isOk()) {
			applicationContext.getProjectLoader().saveProjects(dialog.getSelectedProject());
		} else { // CANCEL
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Exiting FLEXO Application Suite... CANCELLED");
			}
			throw new OperationCancelledException();
		}
	}

	private void proceedQuitWithoutConfirmation() {
		if (activeModule != null) {
			getServiceManager().getGeneralPreferences().setFavoriteModuleName(activeModule.getModule().getName());
			// FlexoPreferences.savePreferences(true);
		}

		for (Module<?> m : getLoadedModules()) {
			m.getLoadedModuleInstance().closeWithoutConfirmation(false);
		}

		/*if (allowsDocSubmission() && !isAvailable(Module.DRE_MODULE) && DocResourceManager.instance().getSessionSubmissions().size() > 0) {
		if (FlexoController.confirm(FlexoLocalization.localizedForKey("you_have_submitted_documentation_without_having_saved_report")
				+ "\n" + FlexoLocalization.localizedForKey("would_you_like_to_save_your_submissions"))) {
			new ToolsMenu.SaveDocSubmissionAction().actionPerformed(null);
		}
		}
		if (isAvailable(Module.DRE_MODULE)) {
		if (DocResourceManager.instance().needSaving()) {
			if (FlexoController.confirm(FlexoLocalization.localizedForKey("documentation_resource_center_not_saved") + "\n"
					+ FlexoLocalization.localizedForKey("would_you_like_to_save_documenation_resource_center"))) {
				DocResourceManager.instance().save();
			}
		}
		}*/
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Exiting FLEXO Application Suite... DONE");
		}
		System.exit(0);
	}

	public void closeAllModulesWithoutConfirmation() {
		for (Module<?> m : getLoadedModules()) {
			m.getLoadedModuleInstance().closeWithoutConfirmation(false);
		}
	}

	public void closeModule(FlexoModule module) {
		module.close();
	}

	public boolean isLastLoadedModule(Module<?> module) {
		return (getLoadedModuleCount() == 1 && getLoadedModules().contains(module));
	}

	@Override
	public void receiveNotification(FlexoService caller, ServiceNotification notification) {
		logger.fine("ModuleLoader service received notification " + notification + " from " + caller);
	}

	@Override
	public void initialize() {
		loadAvailableModules();
		for (Module module : getKnownModules()) {
			module.initialize();
		}
	}
}
