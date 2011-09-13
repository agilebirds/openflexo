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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObservable;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.toolbox.FlexoProperties;


/**
 * Class FlexoAbstractPreferences is internally used to be inherited from
 * FlexoPreferences
 *
 * @author sguerin
 */
public abstract class FlexoAbstractPreferences extends FlexoObservable implements InspectableObject
{

    private static final Logger logger = Logger.getLogger(FlexoPreferences.class.getPackage().getName());

    protected FlexoAbstractPreferences()
    {
        super();
        _preferences = new FlexoProperties();
    }

    protected FlexoAbstractPreferences(Properties properties)
    {
        super();
        _preferences = new FlexoProperties(properties);
    }

    protected FlexoAbstractPreferences(File preferencesFile)
    {
        super();
        if (logger.isLoggable(Level.FINE))
            logger.fine("Constructor of FlexoAbstractPreferences with " + preferencesFile.getAbsolutePath());
        _preferences = loadFromFile(preferencesFile);
    }

    protected void reloadFromFile(File preferencesFile)
    {
        Properties reloadedProperties = loadFromFile(preferencesFile);
        for (Enumeration e = reloadedProperties.keys(); e.hasMoreElements();) {
            String reloadedKey = (String) e.nextElement();
            if (logger.isLoggable(Level.FINE))
                logger.fine("Resetting = " + reloadedKey + " with " + reloadedProperties.getProperty(reloadedKey));
            setProperty(reloadedKey, reloadedProperties.getProperty(reloadedKey));
        }
    }

    private static Properties loadFromFile(File preferencesFile)
    {
        Properties returned = new FlexoProperties();
        try {
            File parentDir = preferencesFile.getParentFile();
            if (logger.isLoggable(Level.FINE))
                logger.finest("Parent file = " + parentDir);
            if (!parentDir.exists()) {
                parentDir.mkdir();
            }
            if (!preferencesFile.exists()) {
                preferencesFile.createNewFile();
            }
            returned.load(new FileInputStream(preferencesFile));
        } catch (Exception e) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Unable to read preferences " + e.getClass().getName());
        }
        return returned;
    }

    public void setProperty(String key, String value, String notificationKey) {
        if (logger.isLoggable(Level.FINE))
            logger.finer("setProperty " + key + " with " + value);
        String oldValue = getProperty(key);
        if (value!=null)
        	_preferences.setProperty(key, value);
        else
        	_preferences.remove(key);
        PreferencesHaveChanged modif = new PreferencesHaveChanged(key, oldValue, value);
        if (logger.isLoggable(Level.FINE))
            logger.finer("Notifying about " + modif);
        setChanged();
        notifyObservers(modif);
        if (!key.equals(notificationKey)) {
        	modif = new PreferencesHaveChanged(notificationKey, oldValue, value);
        	setChanged();
        	notifyObservers(modif);
        }
    }

    public void setProperty(String key, String value)
    {
    	setProperty(key, value, key);
    }

    public String getProperty(String key)
    {
        return _preferences.getProperty(key);
    }

    public abstract File getPreferencesFile();

    protected void saveToFile(boolean warning)
    {
        try {
            File preferenceFile = getPreferencesFile();
            if (!preferenceFile.exists()) {
                preferenceFile.createNewFile();
            }
            if (logger.isLoggable(Level.FINE))
                logger.fine("Saving preferences to file: " + preferenceFile.getAbsolutePath());
            if (logger.isLoggable(Level.FINE))
                logger.finer("properties:" + _preferences);
            _preferences.store(new FileOutputStream(preferenceFile), "Flexo Preferences");
        } catch (Exception e) {
            if (warning) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Unable to save preferences");
            }
        }
    }

    // INSPECTABLE OBJECT

    public void setAttributeNamed(String attName, Object value)
    {
        if (logger.isLoggable(Level.FINE))
            logger.finer("setAttributeNamed " + attName + " and " + value + " of class " + value.getClass().getName());
        if (value instanceof String) {
            // setProperty(attName,(String)value);
            setValueForKey((String) value, attName);
        } else {
            if (logger.isLoggable(Level.SEVERE))
                logger.severe("I don't know what to do with a " + value.getClass().getName());
        }
    }

    public Object getAttributeNamed(String attName)
    {
        if (logger.isLoggable(Level.FINE))
            logger.finer("getAttributeNamed " + attName);
        return valueForKey(attName);
        // return getProperty (attName);
    }

    @Override
	public String getInspectorName()
    {
        return "Preferences";
    }

    // PUBLIC GET/SET ACCESSORS

    public Boolean getBooleanProperty(String key)
    {
        String booleanValue = getProperty(key);
        if (booleanValue != null) {
            return Boolean.valueOf(booleanValue);
        } else {
            return null;
        }
    }

    public void setBooleanProperty(String key, Boolean aBoolean)
    {
        if (aBoolean != null) {
            setProperty(key, aBoolean.toString());
        } else {
            setProperty(key, null);
        }
    }

    public Integer getIntegerProperty(String key)
    {
        String integerValue = getProperty(key);
        if (integerValue != null) {
            return Integer.valueOf(integerValue);
        } else {
            return null;
        }
    }

    public void setIntegerProperty(String key, Integer anInteger) {
    	setIntegerProperty(key, anInteger, key);
    }

    public void setIntegerProperty(String key, Integer anInteger, String notificationKey)
    {
        if (anInteger != null) {
            setProperty(key, anInteger.toString(),notificationKey);
        } else {
            setProperty(key, null, notificationKey);
        }
    }

    public File getFileProperty(String key)
    {
        String fileName = getProperty(key);
        if (fileName != null) {
            return new File(fileName);
        } else {
            return null;
        }
    }

    public void setFileProperty(String key, File aFile)
    {
        if (aFile != null) {
            setProperty(key, aFile.getAbsolutePath());
        } else {
            setProperty(key, null);
        }
    }

    public File getFileProperty(String key, boolean mustExist)
    {
        File returned = getFileProperty(key);
        if (returned != null && (returned.exists() || !mustExist)) {
            return returned;
        } else {
            return null;
        }
    }

    public boolean setFileProperty(String key, File aFile, boolean mustExist)
    {
        if (aFile != null && aFile.exists()) {
            setFileProperty(key, aFile);
            return true;
        } else {
            return false;
        }
    }

    public File getDirectoryProperty(String key)
    {
        File returned = getFileProperty(key);
        if (returned != null && returned.isDirectory()) {
            return returned;
        } else {
            return null;
        }
    }

    public void setDirectoryProperty(String key, File aFile)
    {
        if (aFile != null && aFile.isDirectory()) {
            setFileProperty(key, aFile);
        } else {
            setFileProperty(key, null);
        }
    }

    public File getDirectoryProperty(String key, boolean mustExist)
    {
        File returned = getFileProperty(key, mustExist);
        if (returned != null && returned.isDirectory()) {
            return returned;
        } else {
            return null;
        }
    }

    public boolean setDirectoryProperty(String key, File aFile, boolean mustExist)
    {
        if (aFile != null && aFile.exists()) {
            setDirectoryProperty(key, aFile);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Object where are stored the preferences
     */
    private Properties _preferences;

}
