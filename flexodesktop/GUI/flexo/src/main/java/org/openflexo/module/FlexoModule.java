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
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.ApplicationContext;
import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.ProjectExitingCancelledException;
import org.openflexo.module.external.IModule;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoController;

/**
 * Abstract class defining a Flexo Module. A Flexo Module is an application component part of the Flexo Application Suite and dedicated to a
 * particular purpose.
 * 
 * @author sguerin
 */
public abstract class FlexoModule implements DataFlexoObserver, IModule {

	static final Logger logger = Logger.getLogger(FlexoModule.class.getPackage().getName());

	private boolean isActive = false;

	private FlexoController controller;

	private final ApplicationContext applicationContext;

	public FlexoModule(ApplicationContext applicationContext) {
		super();
		this.applicationContext = applicationContext;
		controller = createControllerForModule();
	}

	public abstract Module getModule();

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	private ModuleLoader getModuleLoader() {
		return getApplicationContext().getModuleLoader();
	}

	private ProjectLoader getProjectLoader() {
		return getApplicationContext().getProjectLoader();
	}

	protected abstract FlexoController createControllerForModule();

	public FlexoController getController() {
		return controller;
	}

	public FlexoController getFlexoController() {
		return controller;
	}

	public FlexoFrame getFlexoFrame() {
		return controller.getFlexoFrame();
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

	public boolean isActive() {
		return isActive;
	}

	public void activateModule() {
		try {
			getModuleLoader().switchToModule(getModule());
		} catch (ModuleLoadingException e) {
			e.printStackTrace();
		}
	}

	void setAsInactive() {
		isActive = false;
		getFlexoFrame().setRelativeVisible(false);
		if (controller.getConsistencyCheckWindow(false) != null) {
			controller.getConsistencyCheckWindow(false).setVisible(false);
		}
	}

	private FlexoEditor getEditor() {
		return getController().getEditor();
	}

	void setAsActiveModule() {
		isActive = true;
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
		if (getEditor() != null) {
			boolean selectDefaultObject = false;
			if (getDefaultObjectToSelect(getEditor().getProject()) != null
					&& (getFlexoController().getCurrentDisplayedObjectAsModuleView() == null || getFlexoController()
							.getCurrentDisplayedObjectAsModuleView() == getDefaultObjectToSelect(getEditor().getProject()))) {
				if (getFlexoController().getSelectionManager().getFocusedObject() == null) {
					selectDefaultObject = true;
				}
			} else {
				selectDefaultObject = true;
			}
			if (selectDefaultObject) {
				getFlexoController().setCurrentEditedObjectAsModuleView(getDefaultObjectToSelect(getEditor().getProject()));
			} else {
				getFlexoController().getSelectionManager().setSelectedObjects(
						new Vector<FlexoModelObject>(getFlexoController().getSelectionManager().getSelection()));
			}
			getFlexoController().getSelectionManager().fireUpdateSelection();
		}
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
		boolean isLastModule = !getModuleLoader().isLastLoadedModule(getModule());
		if (isLastModule) {
			try {
				getModuleLoader().quit(true);
				return true;
			} catch (ProjectExitingCancelledException e) {
				return false;
			}
		} else { // There are still other modules left
			closeWithoutConfirmation();// Unloads the module
			return true; // Since there is nothing to save and that Flexo
			// has other windows opened to access it, we
			// close the module and that's it!!!
			// }
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
		setAsInactive();
		if (controller != null) {
			controller.dispose();
		} else if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Called twice closeWithoutConfirmation on " + this);
		}
		controller = null;
		if (getModuleLoader().isLoaded(getModule())) {
			getModuleLoader().unloadModule(getModule());
		}
		// Is there some modules loaded ?
		Enumeration<FlexoModule> leftModules = getModuleLoader().loadedModules();
		if (leftModules.hasMoreElements()) {
			try {
				getModuleLoader().switchToModule(leftModules.nextElement().getModule());
			} catch (ModuleLoadingException e) {
				logger.severe("Module is loaded and so this exception CANNOT occur. Please investigate and FIX.");
				e.printStackTrace();
			}
		} else {
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

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {

	}

	public abstract FlexoModelObject getDefaultObjectToSelect(FlexoProject project);

}
