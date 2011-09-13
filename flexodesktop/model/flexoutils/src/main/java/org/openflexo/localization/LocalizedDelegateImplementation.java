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
package org.openflexo.localization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.toolbox.FlexoProperties;


public class LocalizedDelegateImplementation extends Observable implements LocalizedDelegate {

	private static final Logger logger = Logger.getLogger(LocalizedDelegateImplementation.class.getPackage().getName());

	private File _localizedDirectory;
	private Hashtable<Language,Properties> _localizedDictionaries;

	public LocalizedDelegateImplementation(File localizedDirectory) 
	{
		_localizedDirectory = localizedDirectory;
		loadLocalizedDictionaries();
	}

	private Properties getDictionary(Language language)
    {
		Properties dict = _localizedDictionaries.get(language);
		if (dict == null) {
			dict = createNewDictionary(language);
		}
		return dict;
    }

	private Properties createNewDictionary(Language language)
    {
		Properties newDict = new Properties();
		_localizedDictionaries.put(language,newDict);
		saveDictionary(language, newDict);
		return newDict;
    }

    private File getDictionaryFileForLanguage(Language language)
    {
        return new File(_localizedDirectory, language.getName() + ".dict");
    }

    private void saveDictionary(Language language, Properties dict)
    {
        File dictFile = getDictionaryFileForLanguage(language);
        try {
            if (!dictFile.exists()) {
                dictFile.createNewFile();
            }
            dict.store(new FileOutputStream(dictFile), language.getName());
            logger.info("Saved "+dictFile.getAbsolutePath());
        } catch (IOException e) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Unable to save file " + dictFile.getAbsolutePath() + " " + e.getClass().getName());
            //e.printStackTrace();
        }
    }

    private Properties loadDictionary(Language language)
    {
        File dictFile = getDictionaryFileForLanguage(language);
        Properties loadedDict = new FlexoProperties();
        try {
            loadedDict.load(new FileInputStream(dictFile));
        } catch (IOException e) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Unable to load file " + dictFile.getName());
        }
        return loadedDict;
    }

    private void loadLocalizedDictionaries()
    {
        _localizedDictionaries = new Hashtable<Language,Properties>();

        for (Language language : Language.availableValues()) {
             File dictFile = getDictionaryFileForLanguage(language);
            if (logger.isLoggable(Level.INFO))
                logger.info("Checking dictionary for language " + language.getName());
            if (logger.isLoggable(Level.FINE))
                logger.fine("Looking for file " + dictFile.getAbsolutePath());
            if (!dictFile.exists()) {
                createNewDictionary(language);
            } else {
                Properties loadedDict = loadDictionary(language);
                _localizedDictionaries.put(language, loadedDict);
            }
        }
    }

    private void addEntryInDictionary(Language language, String key, String value, boolean required)
    {
        Properties dict = getDictionary(language);
        if (((!required) && (dict.getProperty(key) == null)) || required) {
            if (logger.isLoggable(Level.INFO))
                logger.info("Adding entry '" + key + "' in " + language + " dictionary, file "+getDictionaryFileForLanguage(language).getAbsolutePath());
            dict.setProperty(key, value);
            //saveDictionary(language, dict);
         }
    }

    public void addEntry(String key)
    {
        // Add in all dictionaries, when required
        for (Language language : Language.availableValues()) {
            addEntryInDictionary(language, key, key, false);
        }
        entries = null;
        setChanged();
        notifyObservers();
    }

    public void removeEntry(String key)
    {
        // Remove from all dictionaries
        for (Language language : Language.availableValues()) {
            Properties dict = getDictionary(language);
            dict.remove(key);
            //saveDictionary(language, dict);
        }
        entries = null;
        setChanged();
        notifyObservers();
    }

    public void saveAllDictionaries()
    {
        for (Language language : Language.availableValues()) {
            Properties dict = getDictionary(language);
            saveDictionary(language, dict);
        }
    }

	@Override
	public boolean handleNewEntry(String key, Language language)
	{
		return true;
	}

	@Override
	public String localizedForKeyAndLanguage(String key, Language language)
	{
		if (_localizedDictionaries == null) {
			loadLocalizedDictionaries();
		}

		Properties currentLanguageDict = getDictionary(language);
		String localized = currentLanguageDict.getProperty(key);

		if (localized == null) {
			addEntry(key);
			return currentLanguageDict.getProperty(key);
		} else
			return localized;
	}
	
	public void setLocalizedForKeyAndLanguage(String key, String value, Language language)
	{
		if (_localizedDictionaries == null) {
			loadLocalizedDictionaries();
		}
		Properties currentLanguageDict = getDictionary(language);
		currentLanguageDict.setProperty(key, value);
		//saveDictionary(language, currentLanguageDict);
	}

	public class Entry
	{
		public String key;
		
		public Entry(String aKey)
		{
			key = aKey;
		}
		
		public String getEnglish()
		{
			return localizedForKeyAndLanguage(key, Language.ENGLISH);
		}
		public void setEnglish(String value)
		{
			setLocalizedForKeyAndLanguage(key, value, Language.ENGLISH);
		}
		public String getFrench()
		{
			return localizedForKeyAndLanguage(key, Language.FRENCH);
		}
		public void setFrench(String value)
		{
			setLocalizedForKeyAndLanguage(key, value, Language.FRENCH);
		}
		public String getDutch()
		{
			return localizedForKeyAndLanguage(key, Language.DUTCH);
		}
		public void setDutch(String value)
		{
			setLocalizedForKeyAndLanguage(key, value, Language.DUTCH);
		}
	}

	private Vector<Entry> entries;
	
	public Vector<Entry> getEntries()
	{
		if (entries == null) {
			if (_localizedDictionaries == null) {
				loadLocalizedDictionaries();
			}
			entries = new Vector<Entry>();
			if (_localizedDictionaries.size() > 0) {
				Enumeration en = _localizedDictionaries.values().iterator().next().propertyNames();
				while (en.hasMoreElements()) {
					entries.add(new Entry((String)en.nextElement()));
				}
			}
			Collections.sort(entries,new Comparator<Entry>() {
				@Override
				public int compare(Entry o1, Entry o2)
				{
					return Collator.getInstance().compare(o1.key,o2.key);
				}
			});
		}
		return entries;
	}
	
	private Entry getEntry(String key)
	{
		if (key==null) return null;
		for (Entry entry : getEntries()) {
			if (key.equals(entry.key)) return entry;
		}
		return null;
	}
	
	public void save()
	{
		saveAllDictionaries();
	}
	
	public void refresh()
	{
		entries = null;
		setChanged();
		notifyObservers();
	}

	public Entry addEntry()
	{
		addEntry("key");
		return getEntry("key");
	}
	
	public void deleteEntry(Entry entry)
	{
		removeEntry(entry.key);
	}
}
