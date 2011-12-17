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
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.GeneralPreferences;
import org.openflexo.inspector.model.TabModel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;

/**
 * This class is intented to modelize preferences of Flexo application To be accessed through all the application, all methods are
 * statically defined.
 * 
 * @author sguerin
 */
public class FlexoPreferences extends FlexoAbstractPreferences {

	private static final Logger logger = Logger.getLogger(FlexoPreferences.class.getPackage().getName());
    private static File appDataDirectory;

	protected Vector<ContextPreferences> contextPreferencesVector;

	protected FlexoPreferences(File preferencesFile) {
		super(preferencesFile);
		contextPreferencesVector = new Vector<ContextPreferences>();
	}

	protected void register(Class<ContextPreferences> contextPreferencesClass) {
		ContextPreferences cp = null;
		try {
			cp = contextPreferencesClass.newInstance();
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
		if (!contextPreferencesVector.contains(cp)) {
			contextPreferencesVector.add(cp);
			PreferencesController.register(cp);
		}
	}

	// STATIC METHODS

	private static FlexoPreferences loadPreferencesFromFile() {
		return new FlexoPreferences(getPrefsFile());
	}

	public static File getApplicationDataDirectory() {
        if (appDataDirectory == null) {
            File prefDir = new File(new File(System.getProperty("user.home")), "Library/Flexo");
            if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
                String appData = System.getenv("APPDATA");
                if (appData != null) {
                    File f = new File(appData);
                    if (f.isDirectory() && f.canWrite()) {
                        prefDir = new File(f, "OpenFlexo");
                    }
                }
            }
            appDataDirectory = prefDir;
        }
        return appDataDirectory;
	}

    public static void setAppDataDirectory(File someDir){
        if(appDataDirectory!=null && !appDataDirectory.getAbsoluteFile().equals(someDir.getAbsoluteFile())){
            throw new IllegalStateException("Application Data Directory is already define in :"+appDataDirectory.getAbsolutePath());
        }
        if(appDataDirectory==null){
            appDataDirectory = someDir;
        }
    }

	public static File getPrefsFile() {
		return new File(getApplicationDataDirectory(), "Flexo.prefs");
	}

	@Override
	public File getPreferencesFile() {
		return getPrefsFile();
	}

	public static void load() {
		instance();
	}

	public static FlexoPreferences instance() {
		if (_instance == null) {
			_instance = loadPreferencesFromFile();
			FlexoLocalization.setCurrentLanguage(GeneralPreferences.getLanguage());
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Preferences have been loaded");
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
			for (Enumeration<ContextPreferences> en = contextPreferencesVector.elements(); en.hasMoreElements();) {
				ContextPreferences next = null;
				try {
					next = en.nextElement();
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
			for (Enumeration<ContextPreferences> en = contextPreferencesVector.elements(); en.hasMoreElements();) {
				ContextPreferences next = null;
				try {
					next = en.nextElement();
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
			for (Enumeration<ContextPreferences> en = contextPreferencesVector.elements(); en.hasMoreElements();) {
				ContextPreferences next = null;
				try {
					next = en.nextElement();
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
	public String getInspectorTitle() {
		return null;
	}

	@Override
	public Vector<TabModel> inspectionExtraTabs() {
		return null;
	}

}
