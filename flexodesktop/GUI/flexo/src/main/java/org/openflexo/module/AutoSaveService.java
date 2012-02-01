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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.GeneralPreferences;
import org.openflexo.foundation.rm.FlexoProject;

public class AutoSaveService {

	private static final Logger logger = Logger.getLogger(AutoSaveService.class.getPackage().getName());

	private static AutoSaveService autoSaveService;

	private static final Object monitor = new Object();

	private FlexoAutoSaveThread autoSaveThread = null;

	private AutoSaveService() {
		super();
	}

	public static AutoSaveService instance() {
		if (autoSaveService == null) {
			synchronized (monitor) {
				if (autoSaveService == null) {
					autoSaveService = new AutoSaveService();
				}
			}
		}
		return autoSaveService;
	}

	public void startAutoSaveThread() {
		FlexoProject currentProject = getModuleLoader().getProject();
		if (autoSaveThread != null && autoSaveThread.getProject() != currentProject) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("This is not normal, there was an auto-save thread running but a new project has been loaded. Please "
						+ "always stop the auto-save thread before loading another project.");
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

	private ModuleLoader getModuleLoader() {
		return ModuleLoader.instance();
	}

	public boolean hasAutoSaveThread() {
		return autoSaveThread != null;
	}

	public void stopAutoSaveThread() {
		if (autoSaveThread != null) {
			autoSaveThread.setRun(false);
			if (autoSaveThread.getState() == Thread.State.TIMED_WAITING) {
				autoSaveThread.interrupt();
			}
			autoSaveThread = null;
		}
	}

	public void conditionalStartOfAutoSaveThread(boolean isAutoSaveEnabledByDefault) {
		if (GeneralPreferences.isAutoSavedEnabled()) {
			startAutoSaveThread();
		} else {
			if (isAutoSaveEnabledByDefault) {
				startAutoSaveThread();
			}
		}
	}

	public void setAutoSaveLimit(int limit) {
		if (autoSaveThread != null) {
			autoSaveThread.setNumberOfIntermediateSave(limit);
		}
	}

	public void setAutoSaveSleepTime(int time) {
		if (autoSaveThread != null) {
			autoSaveThread.setSleepTime(time * 60 * 1000);
		}
	}

	public File getAutoSaveDirectory() {
		if (autoSaveThread != null) {
			return autoSaveThread.getTempDirectory();
		} else {
			return null;
		}
	}

	public void showTimeTravelerDialog() {
		if (autoSaveThread != null) {
			autoSaveThread.showTimeTravelerDialog();
		}
	}

	public boolean isTimeTravelingAvailable() {
		return autoSaveThread != null;
	}
}
