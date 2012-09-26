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
package org.openflexo.prefs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.GeneralPreferences;
import org.openflexo.inspector.model.TabModel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

/**
 * This class is intented to modelize preferences of Flexo application To be accessed through all the application, all methods are
 * statically defined.
 * 
 * @author sguerin
 */
public class FlexoPreferences extends FlexoAbstractPreferences {

	private static final String FLEXO_PREFS_FILE_NAME = "Flexo.prefs";
	private static final Logger logger = Logger.getLogger(FlexoPreferences.class.getPackage().getName());
	private static File appDataDirectory;

	protected ClassToInstanceMap<ContextPreferences> contextualPreferences;

	protected FlexoPreferences(File preferencesFile) {
		super(preferencesFile);
		contextualPreferences = MutableClassToInstanceMap.create();
	}

	protected <CP extends ContextPreferences> CP get(Class<CP> contextPreferencesClass) {
		CP cp = contextualPreferences.getInstance(contextPreferencesClass);
		if (cp != null) {
			return cp;
		}
		try {
			cp = contextPreferencesClass.newInstance();
			contextualPreferences.putInstance(contextPreferencesClass, cp);
			PreferencesController.register(cp);
		} catch (InstantiationException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		}
		return cp;
	}

	// STATIC METHODS

	private static File getOldPrefsFile() {
		File prefDir = new File(new File(System.getProperty("user.home")), "Library/Flexo");
		return new File(prefDir, FLEXO_PREFS_FILE_NAME);
	}

	public static File getPrefsFile() {
		return new File(FileUtils.getApplicationDataDirectory(), FLEXO_PREFS_FILE_NAME);
	}

	public File getPreferencesFile() {
		return getPrefsFile();
	}

	public static void load() {
		instance();
	}

	protected void saveToFile(boolean warning) {
		try {
			File preferenceFile = getPreferencesFile();
			if (!preferenceFile.exists()) {
				preferenceFile.createNewFile();
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Saving preferences to file: " + preferenceFile.getAbsolutePath());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("properties:" + getPreferencesProperties());
			}
			getPreferencesProperties().store(new FileOutputStream(preferenceFile), "Flexo Preferences");
		} catch (Exception e) {
			if (warning) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Unable to save preferences");
				}
			}
		}
	}

	public static FlexoPreferences instance() {
		if (_instance == null) {
			synchronized (FlexoPreferences.class) {
				if (_instance == null) {
					File prefsFile = getPrefsFile();
					if (!prefsFile.exists()) {
						File oldFile = getOldPrefsFile();
						if (oldFile.exists()) {
							try {
								FileUtils.copyFileToFile(oldFile, prefsFile);
							} catch (IOException e) {
								// Let's log it, but too bad, he's gonna loose his prefs.
								e.printStackTrace();
							}
						}
					}
					_instance = new FlexoPreferences(prefsFile);
					FlexoLocalization.setCurrentLanguage(GeneralPreferences.getLanguage());
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Preferences have been loaded");
					}
				}
			}
		}
		return _instance;
	}

	public static synchronized void savePreferences(boolean warning) {
		instance().saveToFile(warning);
	}

	public static void revertToSaved() {
		instance().reloadFromFile(getPrefsFile());
	}

	/**
	 * Unique instance of FlexoPreferences
	 */
	private static FlexoPreferences _instance;

	@Override
	public Object objectForKey(String key) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("objectForKey for " + key);
		}
		try {
			return super.objectForKey(key);
		} catch (InvalidObjectSpecificationException e) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("FAILED for this");
			}
			for (ContextPreferences next : contextualPreferences.values()) {
				try {
					return next.objectForKey(key);
				} catch (InvalidObjectSpecificationException e2) {
					if (logger.isLoggable(Level.FINE)) {
						logger.finer("FAILED for " + next.getName());
					}
				}
			}
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not find object for key " + key + " in FlexoPreferences");
			}
			return null;
		}
	}

	@Override
	public void setObjectForKey(Object object, String key) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setObjectForKey for " + key + " value " + object);
		}
		try {
			super.setObjectForKey(object, key);
		} catch (InvalidObjectSpecificationException e) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("FAILED for this");
			}
			for (ContextPreferences next : contextualPreferences.values()) {
				try {
					next.setObjectForKey(object, key);
					return;
				} catch (InvalidObjectSpecificationException e2) {
					if (logger.isLoggable(Level.FINE)) {
						logger.finer("FAILED for " + next.getName());
					}
				}
			}
			e.printStackTrace();
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not set value " + object + " for key " + key + " in FlexoPreferences");
			}
		}
	}

	@Override
	public Class<?> getTypeForKey(String key) {
		if (logger.isLoggable(Level.FINE)) {
			logger.finer("getTypeForKey for " + key);
		}
		try {
			return super.getTypeForKey(key);
		} catch (InvalidObjectSpecificationException e) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("FAILED for this");
			}
			for (ContextPreferences next : contextualPreferences.values()) {
				try {
					return next.getTypeForKey(key);
				} catch (InvalidObjectSpecificationException e2) {
					if (logger.isLoggable(Level.FINE)) {
						logger.finer("FAILED for " + next.getName());
					}
				}
			}
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not find object for key " + key + " in FlexoPreferences");
			}
			return null;
		}
	}

	@Override
	public boolean isDeleted() {
		return false;
	}

	@Override
	public String getDeletedProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInspectorTitle() {
		return null;
	}

	@Override
	public Vector<TabModel> inspectionExtraTabs() {
		return null;
	}

	public static File getLogDirectory() {
		File outputDir = new File(System.getProperty("user.home") + "/Library/Logs/OpenFlexo");
		if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
			boolean ok = false;
			String appData = System.getenv("LOCALAPPDATA");
			if (appData != null) {
				File f = new File(appData);
				if (f.isDirectory() && f.canWrite()) {
					outputDir = new File(f, "OpenFlexo/Logs");
					ok = true;
				}
				if (!ok) {
					appData = System.getenv("APPDATA");
					if (appData != null) {
						f = new File(appData);
						if (f.isDirectory() && f.canWrite()) {
							outputDir = new File(f, "OpenFlexo/Logs");
							ok = true;
						}
					}
				}
			}
		} else if (ToolBox.getPLATFORM() == ToolBox.LINUX) {
			outputDir = new File("user.home", ".openflexo/logs");
		}
		return outputDir;
	}

}
