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
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.GeneralPreferences;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;

import org.openflexo.inspector.model.TabModel;
import org.openflexo.localization.FlexoLocalization;

/**
 * This class is intented to modelize preferences of Flexo application To be
 * accessed through all the application, all methods are statically defined.
 *
 * @author sguerin
 */
public class FlexoPreferences extends FlexoAbstractPreferences
{

    private static final Logger logger = Logger.getLogger(FlexoPreferences.class.getPackage().getName());

    protected Vector<ContextPreferences> contextPreferencesVector;

    protected FlexoPreferences(File preferencesFile)
    {
        super(preferencesFile);
        contextPreferencesVector = new Vector<ContextPreferences>();
    }

    protected void register(Class<ContextPreferences> contextPreferencesClass)
    {
        ContextPreferences cp = null;
        try {
            cp = contextPreferencesClass.newInstance();
        } catch (InstantiationException e) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
            e.printStackTrace();
        }
        if (!contextPreferencesVector.contains(cp)) {
            if (logger.isLoggable(Level.FINE))
                logger.fine("Registering context preferences " + cp.getName());
            contextPreferencesVector.add(cp);
            PreferencesController.register(cp);
        }
    }

    // STATIC METHODS

    private static FlexoPreferences loadPreferencesFromFile()
    {
        return new FlexoPreferences(getPrefsFile());
    }

    public static File getPrefsDirectory()
    {
        return new File(new File(System.getProperty("user.home")), "Library/Flexo");
    }

    public static File getPrefsFile()
    {
        File libraryDir = getPrefsDirectory();
        File prefFile = new File(libraryDir, "Flexo.prefs");
        if (!prefFile.exists()) {
            File oldPrefs = new File(System.getProperty("user.home"),"Library/Flexo.prefs");
            if (oldPrefs.exists()) {
                if (!prefFile.getParentFile().exists())
                    prefFile.getParentFile().mkdirs();
                try {
                    FileUtils.copyFileToFile(oldPrefs, prefFile);
                    oldPrefs.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return prefFile;
    }

    @Override
	public File getPreferencesFile()
    {
        return getPrefsFile();
    }

    public static void load()
    {
        instance();
    }

    public static FlexoPreferences instance()
    {
        if (_instance == null) {
            _instance = loadPreferencesFromFile();
            FlexoLocalization.setCurrentLanguage(GeneralPreferences.getLanguage());
            if (logger.isLoggable(Level.INFO))
                logger.info("Preferences have been loaded");
        }
        return _instance;
    }

    public static synchronized void savePreferences(boolean warning)
    {
        instance().saveToFile(warning);
    }

    public static void revertToSaved()
    {
        instance().reloadFromFile(getPrefsFile());
    }

    /**
     * Unique instance of FlexoPreferences
     */
    private static FlexoPreferences _instance;

    /*
     * public String valueForKey(String key) { if
     * (logger.isLoggable(Level.FINE)) logger.finer ("valueForKey for "+key);
     * try { return super.valueForKey(key); } catch
     * (InvalidObjectSpecificationException e) { if
     * (logger.isLoggable(Level.FINE)) logger.finer ("FAILED for this"); for
     * (Enumeration enum=contextPreferencesVector.elements();
     * enum.hasMoreElements();) { ContextPreferences next = null; try { next =
     * (ContextPreferences)enum.nextElement(); return next.valueForKey(key); }
     * catch (InvalidObjectSpecificationException e2) { if
     * (logger.isLoggable(Level.FINE)) logger.finer ("FAILED for
     * "+next.getName()); } } if (logger.isLoggable(Level.WARNING))
     * logger.warning("Could not find value for key "+key+" in
     * FlexoPreferences"); return null; } }
     *
     * public void setValueForKey(String valueAsString, String key) { if
     * (logger.isLoggable(Level.FINE)) logger.finer ("setValueForKey for "+key+"
     * value "+valueAsString); try { super.setValueForKey(valueAsString,key); }
     * catch (InvalidObjectSpecificationException e) { if
     * (logger.isLoggable(Level.FINE)) logger.finer ("FAILED for this"); for
     * (Enumeration enum=contextPreferencesVector.elements();
     * enum.hasMoreElements();) { ContextPreferences next = null; try { next =
     * (ContextPreferences)enum.nextElement();
     * next.setValueForKey(valueAsString,key); return; } catch
     * (InvalidObjectSpecificationException e2) { if
     * (logger.isLoggable(Level.FINE)) logger.finer ("FAILED for
     * "+next.getName()); } } if (logger.isLoggable(Level.WARNING))
     * logger.warning("Could not set value "+valueAsString+" for key "+key+" in
     * FlexoPreferences"); } }
     */

    @Override
	public Object objectForKey(String key)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("objectForKey for " + key);
        try {
            return super.objectForKey(key);
        } catch (InvalidObjectSpecificationException e) {
            if (logger.isLoggable(Level.FINE))
                logger.finer("FAILED for this");
            for (Enumeration<ContextPreferences> en = contextPreferencesVector.elements(); en.hasMoreElements();) {
                ContextPreferences next = null;
                try {
                    next = en.nextElement();
                    return next.objectForKey(key);
                } catch (InvalidObjectSpecificationException e2) {
                    if (logger.isLoggable(Level.FINE))
                        logger.finer("FAILED for " + next.getName());
                }
            }
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Could not find object for key " + key + " in FlexoPreferences");
            return null;
        }
    }

    @Override
	public void setObjectForKey(Object object, String key)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("setObjectForKey for " + key + " value " + object);
        try {
            super.setObjectForKey(object, key);
        } catch (InvalidObjectSpecificationException e) {
            if (logger.isLoggable(Level.FINE))
                logger.finer("FAILED for this");
            for (Enumeration<ContextPreferences> en = contextPreferencesVector.elements(); en.hasMoreElements();) {
                ContextPreferences next = null;
                try {
                    next = en.nextElement();
                    next.setObjectForKey(object, key);
                    return;
                } catch (InvalidObjectSpecificationException e2) {
                    if (logger.isLoggable(Level.FINE))
                        logger.finer("FAILED for " + next.getName());
                }
            }
            e.printStackTrace();
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Could not set value " + object + " for key " + key + " in FlexoPreferences");
        }
    }

    @Override
	public Class<?> getTypeForKey(String key)
    {
        if (logger.isLoggable(Level.FINE))
            logger.finer("getTypeForKey for " + key);
        try {
            return super.getTypeForKey(key);
        } catch (InvalidObjectSpecificationException e) {
            if (logger.isLoggable(Level.FINE))
                logger.finer("FAILED for this");
            for (Enumeration<ContextPreferences> en = contextPreferencesVector.elements(); en.hasMoreElements();) {
                ContextPreferences next = null;
                try {
                    next = en.nextElement();
                    return next.getTypeForKey(key);
                } catch (InvalidObjectSpecificationException e2) {
                    if (logger.isLoggable(Level.FINE))
                        logger.finer("FAILED for " + next.getName());
                }
            }
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Could not find object for key " + key + " in FlexoPreferences");
            return null;
        }
    }

	@Override
	public boolean isDeleted() {
		return false;
	}

	@Override
	public String getInspectorTitle()
	{
		return null;
	}

	@Override
	public Vector<TabModel> inspectionExtraTabs() {
		// TODO Auto-generated method stub
		return null;
	}

}
