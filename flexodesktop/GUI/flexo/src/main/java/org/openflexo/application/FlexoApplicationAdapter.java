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
package org.openflexo.application;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.Flexo;
import org.openflexo.components.AboutDialog;
import org.openflexo.foundation.utils.ProjectExitingCancelledException;
import org.openflexo.module.ModuleLoader;
import org.openflexo.prefs.PreferencesController;

import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

/**
 * Handles application events, such as quit, about and preferences
 * 
 * @author sguerin
 */
public class FlexoApplicationAdapter extends ApplicationAdapter {

	private static final Logger logger = Logger.getLogger(FlexoApplicationAdapter.class.getPackage().getName());

	public void handleAbout(ApplicationEvent event) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("handleAbout");
		}
		event.setHandled(true);
		new AboutDialog();
	}

	public void handlePreferences(ApplicationEvent event) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("handlePreferences");
		}
		if (PreferencesController.hasInstance()) {
			PreferencesController.instance().showPreferences();
		}
	}

	public void handleQuit(ApplicationEvent event) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("handleQuit");
		}
		try {
			getModuleLoader().quit(true);
		} catch (ProjectExitingCancelledException e) {
		}
	}

	public void handleOpenFile(ApplicationEvent arg0) {
		Flexo.setFileNameToOpen(arg0.getFilename());
	}

	private ModuleLoader getModuleLoader() {
		return ModuleLoader.instance();
	}

}
