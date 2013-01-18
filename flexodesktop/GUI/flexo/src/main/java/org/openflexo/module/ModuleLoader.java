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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.openflexo.ApplicationContext;
import org.openflexo.GeneralPreferences;
import org.openflexo.action.SubmitDocumentationAction;
import org.openflexo.ch.FCH;
import org.openflexo.components.ProgressWindow;
import org.openflexo.components.SaveProjectsDialog;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.rm.SaveResourceExceptionList;
import org.openflexo.foundation.utils.OperationCancelledException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.external.ExternalCEDModule;
import org.openflexo.module.external.ExternalDMModule;
import org.openflexo.module.external.ExternalIEModule;
import org.openflexo.module.external.ExternalOEModule;
import org.openflexo.module.external.ExternalWKFModule;
import org.openflexo.module.external.IModuleLoader;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.swing.FlexoSwingUtils;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.ControllerModel;
import org.openflexo.view.menu.ToolsMenu;

/**
 * This class handles computation of available modules and modules loading. Only one instance of this class is instancied, and available all
 * over Flexo Application Suite. This is the ONLY ONE WAY to access external modules from a given module.
 * 
 * @author sguerin
 */
public class ModuleLoader implements IModuleLoader, HasPropertyChangeSupport {

	private static final Logger logger = Logger.getLogger(ModuleLoader.class.getPackage().getName());

	public static final String ACTIVE_MODULE = "activeModule";

	public static final String MODULE_LOADED = "moduleLoaded";
	public static final String MODULE_UNLOADED = "moduleUnloaded";
	public static final String MODULE_ACTIVATED = "moduleActivated";

	private boolean allowsDocSubmission;

	private FlexoModule activeModule = null;

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

	private ActiveEditorListener activeEditorListener = new ActiveEditorListener();

	/**
	 * Hashtable where are stored Module instances (instance of FlexoModule associated to a Module instance key.
	 */
	private Map<Module, FlexoModule> _modules = new Hashtable<Module, FlexoModule>();

	private final ApplicationContext applicationContext;

	private PropertyChangeSupport propertyChangeSupport;

	public ModuleLoader(ApplicationContext applicationContext) {
		super();
		this.applicationContext = applicationContext;
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	@Override
	public String getDeletedProperty() {
		// TODO Auto-generated method stub

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

	@Override
	public FlexoModule getActiveModule() {
		return activeModule;
	}

	/**
	 * Return all loaded modules as an Enumeration of FlexoModule instances
	 * 
	 * @return Enumeration
	 */
	public Enumeration<FlexoModule> loadedModules() {
		return new Vector<FlexoModule>(_modules.values()).elements();
	}

	/**
	 * Return all unloaded modules but available modules as a Vector of Module instances
	 * 
	 * @return Vector
	 */
	public List<Module> unloadedButAvailableModules() {
		List<Module> returned = new ArrayList<Module>(getAvailableModules());
		for (Enumeration<FlexoModule> e = loadedModules(); e.hasMoreElements();) {
			returned.remove(e.nextElement().getModule());
		}
		return returned;
	}

	public void unloadModule(Module module) {
		if (isLoaded(module)) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Unloading module " + module.getName());
			}
			FlexoModule flexoModule = _modules.remove(module);
			if (activeModule == flexoModule) {
				activeModule = null;
			}
			getPropertyChangeSupport().firePropertyChange(MODULE_UNLOADED, module, null);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Unable to unload unloaded module " + module.getName());
			}
		}
	}

	private FlexoModule loadModule(Module module) throws Exception {
		boolean createProgress = !ProgressWindow.hasInstance();
		if (createProgress) {
			ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("loading_module") + " " + module.getLocalizedName(), 8);
		}
		ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("loading_module") + " " + module.getLocalizedName());
		FlexoModule flexoModule = doInternalLoadModule(module);
		_modules.put(module, flexoModule);
		if (createProgress) {
			ProgressWindow.hideProgressWindow();
		}
		propertyChangeSupport.firePropertyChange(MODULE_LOADED, null, module);
		return flexoModule;
	}

	private class ModuleLoaderCallable implements Callable<FlexoModule> {

		private final Module module;

		public ModuleLoaderCallable(Module module) {
			this.module = module;
		}

		@Override
		public FlexoModule call() throws Exception {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Loading module " + module.getName());
			}
			FlexoModule flexoModule = module.getConstructor().newInstance(new Object[] { applicationContext });
			FCH.ensureHelpEntryForModuleHaveBeenCreated(flexoModule);
			return flexoModule;
		}

	}

	private FlexoModule doInternalLoadModule(Module module) throws Exception {
		ModuleLoaderCallable loader = new ModuleLoaderCallable(module);
		return FlexoSwingUtils.syncRunInEDT(loader);
	}

	public boolean isAvailable(Module module) {
		return getAvailableModules().contains(module);
	}

	public List<Module> getAvailableModules() {
		return Modules.getInstance().getAvailableModules();
	}

	public boolean isLoaded(Module module) {
		return _modules.get(module) != null;
	}

	public boolean isActive(Module module) {
		return getActiveModule() != null && getActiveModule().getModule() == module;
	}

	public boolean isActive(FlexoModule module) {
		return getActiveModule() == module;
	}

	public FlexoModule getModuleInstance(Module module) throws ModuleLoadingException {
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
				try {
					return loadModule(module);
				} catch (Exception e) {
					ProgressWindow.hideProgressWindow();
					e.printStackTrace();
					throw new ModuleLoadingException(module);
				}
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Sorry, module " + module.getName() + " not available.");
			}
			return null;
		}
	}

	public ExternalWKFModule getWKFModule() throws ModuleLoadingException {
		return (ExternalWKFModule) getModuleInstance(Module.WKF_MODULE);
	}

	public ExternalIEModule getIEModule() throws ModuleLoadingException {
		return (ExternalIEModule) getModuleInstance(Module.IE_MODULE);
	}

	public ExternalDMModule getDMModule() throws ModuleLoadingException {
		return (ExternalDMModule) getModuleInstance(Module.DM_MODULE);
	}

	public ExternalCEDModule getCEDModule() throws ModuleLoadingException {
		return (ExternalCEDModule) getModuleInstance(Module.VPM_MODULE);
	}

	public ExternalOEModule getOEModule() throws ModuleLoadingException {
		return (ExternalOEModule) getModuleInstance(Module.VE_MODULE);
	}
	
	private boolean ignoreSwitch = false;

	public FlexoModule switchToModule(final Module module) throws ModuleLoadingException {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
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
		if (ignoreSwitch||activeModule != null && activeModule.getModule() == module) {
			return activeModule;
		}
		ignoreSwitch = true;
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
			return moduleInstance;
		}
		throw new ModuleLoadingException(module);
		} finally {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
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

		} else {
			if (FlexoController.confirm(FlexoLocalization.localizedForKey("really_quit"))) {
				proceedQuitWithoutConfirmation();
			} else {
				throw new OperationCancelledException();
			}
		}
	}

	public void saveModifiedProjects() throws OperationCancelledException, SaveResourceExceptionList {
		SaveProjectsDialog dialog = new SaveProjectsDialog(applicationContext.getProjectLoader().getModifiedProjects());
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
			GeneralPreferences.setFavoriteModuleName(activeModule.getModule().getName());
			FlexoPreferences.savePreferences(true);
		}

		for (Enumeration<FlexoModule> en = loadedModules(); en.hasMoreElements();) {
			en.nextElement().closeWithoutConfirmation(false);
		}
		if (allowsDocSubmission() && !isAvailable(Module.DRE_MODULE) && DocResourceManager.instance().getSessionSubmissions().size() > 0) {
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
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Exiting FLEXO Application Suite... DONE");
		}
		System.exit(0);
	}

	@Override
	public ExternalIEModule getIEModuleInstance() throws ModuleLoadingException {
		return getIEModule();
	}

	@Override
	public ExternalDMModule getDMModuleInstance() throws ModuleLoadingException {
		return getDMModule();
	}

	@Override
	public ExternalWKFModule getWKFModuleInstance() throws ModuleLoadingException {
		return getWKFModule();
	}

	@Override
	public ExternalCEDModule getVPMModuleInstance() throws ModuleLoadingException {
		return getCEDModule();
	}

	@Override
	public ExternalOEModule getVEModuleInstance() throws ModuleLoadingException {
		return getOEModule();
	}

	/**
	 * @param value
	 *            the look and feel identifier
	 * @throws ClassNotFoundException
	 *             - if the LookAndFeel class could not be found
	 * @throws UnsupportedLookAndFeelException
	 *             - if lnf.isSupportedLookAndFeel() is false
	 * @throws IllegalAccessException
	 *             - if the class or initializer isn't accessible
	 * @throws InstantiationException
	 *             - if a new instance of the class couldn't be created
	 * @throws ClassCastException
	 *             - if className does not identify a class that extends LookAndFeel
	 */
	// todo move this method somewhere else
	public static void setLookAndFeel(String value) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		if (UIManager.getLookAndFeel().getClass().getName().equals(value)) {
			return;
		}
		UIManager.setLookAndFeel(value);
		for (Frame frame : Frame.getFrames()) {
			for (Window window : frame.getOwnedWindows()) {
				SwingUtilities.updateComponentTreeUI(window);
			}
			SwingUtilities.updateComponentTreeUI(frame);
		}
	}

	@Override
	public boolean isWKFLoaded() {
		return isLoaded(Module.WKF_MODULE);
	}

	public void closeAllModulesWithoutConfirmation() {
		for (FlexoModule module : new ArrayList<FlexoModule>(_modules.values())) {
			module.closeWithoutConfirmation(false);
		}
	}

	public void closeModule(FlexoModule module) {
		module.close();
	}

	public boolean isLastLoadedModule(Module module) {
		return _modules.size() == 1 && _modules.containsKey(module);
	}

}
