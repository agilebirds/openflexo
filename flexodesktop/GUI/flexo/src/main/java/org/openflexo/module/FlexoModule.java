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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.openflexo.components.SaveDialog;
import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoResourceData;
import org.openflexo.foundation.rm.ResourceRemoved;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.utils.ProjectExitingCancelledException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.ModuleBar;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.InteractiveFlexoEditor;
import org.openflexo.view.controller.SelectionManagingController;
import org.openflexo.view.menu.WindowMenu;

/**
 * Abstract class defining a Flexo Module. A Flexo Module is an application component part of the Flexo Application Suite and dedicated to a
 * particular purpose.
 * 
 * @author sguerin
 */
public abstract class FlexoModule implements DataFlexoObserver {

	static final Logger logger = Logger.getLogger(FlexoModule.class.getPackage().getName());

	protected FlexoController _controller = null;

	protected FlexoFrame _frame = null;

	private InteractiveFlexoEditor _editor = null;

	private boolean isActive = false;

	private static FlexoModule _activeModule = null;

	/**
	 * Hashtable where all the resources used by this module are stored, with associated key which is a String identifying the resource
	 * (resourceIdentifier)
	 */
	private final Map<String, FlexoResource<? extends FlexoResourceData>> usedResources;

	public FlexoModule(InteractiveFlexoEditor projectEditor, FlexoController controller) {
		super();
		_editor = projectEditor;
		projectEditor.getProject().addObserver(this);
		_controller = controller;
		_controller.setModule(this);
		_frame = controller.getFlexoFrame();
		_frame.setTitle(getName());
		_frame.setModule(this);
		usedResources = new Hashtable<String, FlexoResource<? extends FlexoResourceData>>();
		_controller.initInspectors();
		if (projectEditor.getProject() != null) {
			retain(projectEditor.getProject());
		}
	}

	public FlexoModule(InteractiveFlexoEditor projectEditor) {
		super();
		_editor = projectEditor;
		if (projectEditor.getProject() != null) {
			projectEditor.getProject().addObserver(this);
		}
		usedResources = new Hashtable<String, FlexoResource<? extends FlexoResourceData>>();
		if (projectEditor.getProject() != null) {
			retain(projectEditor.getProject());
		}
	}

	protected void setFlexoController(FlexoController controller) {
		_controller = controller;
		_controller.setModule(this);
		_frame = controller.getFlexoFrame();
		_frame.setModule(this);
		_controller.initInspectors();
	}

	public FlexoController getFlexoController() {
		return _controller;
	}

	public FlexoFrame getFlexoFrame() {
		return _frame;
	}

	public abstract InspectorGroup[] getInspectorGroups();

	public final String getName() {
		return getModule().getLocalizedName();
	}

	public final String getShortName() {
		return getModule().getShortName();
	}

	public final String getDescription() {
		return getModule().getLocalizedDescription();
	}

	public static FlexoModule getActiveModule() {
		return _activeModule;
	}

	public static boolean isRunningTest() {
		return getActiveModule() != null && getActiveModule().getEditor() != null && getActiveModule().getEditor().isTestEditor();
	}

	public boolean isActive() {
		return isActive;
	}

	public void focusOn() {
		processFocusOn();
		int state = getFlexoFrame().getExtendedState();
		state &= ~Frame.ICONIFIED;
		getFlexoFrame().setExtendedState(state);
		getFlexoFrame().setVisible(true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getFlexoFrame().toFront();
			}
		});
	}

	public void notifyFocusOn() {
		if (!isActive()) {
			processFocusOn();
			getFlexoFrame().setRelativeVisible(true);
		}
	}

	public void notifyFocusGained() {
		if (ignoreFocusGainedNotifications) {
			if (System.currentTimeMillis() - dateWhenFocusGainedWasLocked > 3000) {
				// After 3 seconds, FocusGained locking time-out and is
				// inconditionnaly reset
				ignoreFocusGainedNotifications = false;
			}
		}
		if (!ignoreFocusGainedNotifications) {
			notifyFocusOn();
		}
		tryToUnlock();
	}

	/*
	 * public void notifyRelativeWindowFocusGained() { tryToUnlock(); }
	 */

	private void tryToUnlock() {
		if (activatingModule == this) {
			if (desactivatingModule != null) {
				desactivatingModule.ignoreFocusGainedNotifications = false;
			}
			activatingModule = null;
			desactivatingModule = null;
		}
	}

	boolean ignoreFocusGainedNotifications = false;

	public void focusOff() {
		_activeModule = null;
		isActive = false;
		ignoreFocusGainedNotifications = true;
		dateWhenFocusGainedWasLocked = System.currentTimeMillis();
		getFlexoFrame().setRelativeVisible(false);
		if (_controller.getConsistencyCheckWindow(false) != null) {
			_controller.getConsistencyCheckWindow(false).setVisible(false);
		}
	}

	private FlexoModule activatingModule;

	private FlexoModule desactivatingModule;

	private long dateWhenFocusGainedWasLocked = 0;

	public void processFocusOn() {
		if (_activeModule != null && _activeModule != this) {
			desactivatingModule = _activeModule;
			_activeModule.focusOff();
		}
		if (_activeModule != this) {
			setAsActiveModule();
		}
		boolean selectDefaultObject = false;
		if (getDefaultObjectToSelect() != null
				&& (getFlexoController().getCurrentDisplayedObjectAsModuleView() == null || getFlexoController()
						.getCurrentDisplayedObjectAsModuleView() == getDefaultObjectToSelect())) {
			if (getFlexoController() instanceof SelectionManagingController) {
				if (((SelectionManagingController) getFlexoController()).getSelectionManager().getFocusedObject() == null) {
					selectDefaultObject = true;
				}
			} else {
				selectDefaultObject = true;
			}
		}
		if (selectDefaultObject) {
			getFlexoController().setCurrentEditedObjectAsModuleView(getDefaultObjectToSelect());
		} else if (getFlexoController() instanceof SelectionManagingController) {
			((SelectionManagingController) getFlexoController()).getSelectionManager()
					.setSelectedObjects(
							new Vector<FlexoModelObject>(((SelectionManagingController) getFlexoController()).getSelectionManager()
									.getSelection()));
		}
	}

	private void setAsActiveModule() {
		WindowMenu.notifySwitchToModule(getModule());
		ModuleBar.notifyStaticallySwitchToModule(getModule());
		isActive = true;
		_activeModule = this;
		activatingModule = this;
		if (getFlexoController() instanceof SelectionManagingController) {
			((SelectionManagingController) getFlexoController()).getSelectionManager().fireUpdateSelection();
		}

	}

	private ModuleLoader getModuleLoader() {
		return ModuleLoader.instance();
	}

	/**
	 * Close Module after asking confirmation Review unsaved and save Unload in module loader
	 * 
	 * @return boolean indicating if close was performed
	 */
	public boolean close()
	/*
	 * This method is used to request the closing of module (either because it has been hit in the menu or the red-cross of the current
	 * window has been pressed. To perform the real closing of the module, closeWithoutConfirmation() must be called. Once
	 * closeWithoutConfirmation has been called, there are no way back. Therefore any call to closeWithoutConfirmation() must be followed by
	 * a "return true"
	 */
	{
		boolean isLastModule = !getModuleLoader().isThereAnyLoadedModuleWithAProjectExcept(getModule());
		if (isLastModule) {
			if (ProjectLoader.someResourcesNeedsSaving(getProject())) {
				return showSaveDialogAndClose();
			} else {
				if (FlexoController.confirm(FlexoLocalization.localizedForKey("really_quit"))) {
					closeWithoutConfirmation();
					return true;
				} else {
					return false;
				}
			}
		} else { // There are still other modules left
			closeWithoutConfirmation();// Unloads the module
			return true; // Since there is nothing to save and that Flexo
			// has other windows opened to access it, we
			// close the module and that's it!!!
			// }
		}
	}

	public boolean showSaveDialogAndClose() {
		try {
			SaveDialog reviewer = new SaveDialog(getFlexoFrame());
			if (reviewer.getRetval() == JOptionPane.YES_OPTION) {
				ProjectLoader.doSaveProject(_controller.getProject());
				closeWithoutConfirmation();
				return true;
			} else if (reviewer.getRetval() == JOptionPane.NO_OPTION) {
				closeWithoutConfirmation();
				return true;
			} else {
				return false;
			}

		} catch (SaveResourceException e) {
			e.printStackTrace();
			if (FlexoController.confirm(FlexoLocalization.localizedForKey("error_during_saving") + "\n"
					+ FlexoLocalization.localizedForKey("would_you_like_to_close_anyway"))) {
				closeWithoutConfirmation();
				return true;
			} else {
				return false;
			}
		}
	}

	public void closeWithoutConfirmation() {
		closeWithoutConfirmation(true);
	}

	public void closeWithoutConfirmation(boolean quitIfNoModuleLeft) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Closing module " + getName());
		}
		moduleWillClose();
		focusOff();
		if (_controller != null) {
			_controller.dispose();
		} else if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Called twice closeWithoutConfirmation on " + this);
		}
		_controller = null;
		for (FlexoResource<? extends FlexoResourceData> r : new ArrayList<FlexoResource<? extends FlexoResourceData>>(
				usedResources.values())) {
			releaseResource(r);
		}
		if (getModuleLoader().isLoaded(getModule())) {
			getModuleLoader().unloadModule(getModule());
		}
		// Is there some modules loaded ?
		Enumeration<FlexoModule> leftModules = getModuleLoader().loadedModules();
		if (leftModules.hasMoreElements()) {
			try {
				getModuleLoader().switchToModule(leftModules.nextElement().getModule(), ModuleLoader.instance().getProject());
			} catch (ModuleLoadingException e) {
				logger.severe("Module is loaded and so this exception CANNOT occurs. Please investigate and FIX.");
				e.printStackTrace();
			}
		} else {
			_activeModule = null;
			if (quitIfNoModuleLeft) {
				try {
					getModuleLoader().quit(false);
				} catch (ProjectExitingCancelledException e) {
				}
			}
		}

	}

	public void moduleWillClose() {
	}

	public Module getModule() {
		return getModuleLoader().getModule(getClass());
	}

	/**
	 * Called to "tell" to this module is using this resource, and that this resource must consequently be retained by this module
	 * 
	 * @param resource
	 */
	public void retainResource(FlexoResource<? extends FlexoResourceData> resource) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(getModule().getName() + " now retains " + resource);
		}
		if (!usedResources.containsValue(resource)) {
			usedResources.put(resource.getResourceIdentifier(), resource);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Resource " + resource.getResourceIdentifier() + " is retained by module " + getName());
			}
		}
	}

	public boolean isRetaining(FlexoResource<? extends FlexoResourceData> resource) {
		if (usedResources == null || resource == null || resource.getResourceIdentifier() == null) {
			return false;
		}
		return usedResources.get(resource.getResourceIdentifier()) != null;
	}

	/**
	 * Called to "tell" to this module is using this resource data, and that this corresponding resource must consequently be retained by
	 * this module
	 * 
	 * @param resourceData
	 */
	public void retain(FlexoResourceData resourceData) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(getModule().getName() + " now retains " + resourceData);
		}
		if (resourceData != null) {
			retainResource(resourceData.getFlexoResource());
		} else {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("try to retain an null resource data");
			}
		}
	}

	/**
	 * Called to "tell" that this module is no more using this resource, and that this resource can consequently be released by this module
	 * 
	 * @param resource
	 */
	public void releaseResource(FlexoResource resource) {
		if (usedResources.containsValue(resource)) {
			usedResources.remove(resource.getResourceIdentifier());
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Resource " + resource.getResourceIdentifier() + " has been released by module " + getName());
			}
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("update with " + dataModification);
		}
		if (dataModification instanceof ResourceRemoved) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Release resource " + ((ResourceRemoved) dataModification).getRemovedResource());
			}
			releaseResource(((ResourceRemoved) dataModification).getRemovedResource());
		}
	}

	public FlexoProject getProject() {
		return _editor.getProject();
	}

	public abstract FlexoModelObject getDefaultObjectToSelect();

	public InteractiveFlexoEditor getEditor() {
		return _editor;
	}

}
