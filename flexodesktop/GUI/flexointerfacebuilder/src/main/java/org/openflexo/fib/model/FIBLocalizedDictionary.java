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
package org.openflexo.fib.model;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.toolbox.StringUtils;

public class FIBLocalizedDictionary extends FIBModelObject implements LocalizedDelegate {

	private static final Logger logger = Logger.getLogger(FIBLocalizedDictionary.class.getPackage().getName());

	private FIBComponent _component;
	private Vector<FIBLocalizedEntry> _entries;
	private Hashtable<Language,Hashtable<String,String>> _values;
	private Vector<DynamicEntry> dynamicEntries = null;
	
	private boolean isSearchingNewEntries = false;
	
	public FIBLocalizedDictionary() 
	{
		_entries = new Vector<FIBLocalizedEntry>();
		_values = new Hashtable<Language, Hashtable<String,String>>();
	}

	public FIBComponent getComponent() 
	{
		return _component;
	}

	public void setComponent(FIBComponent component) 
	{
		_component = component;
	}
	
	
	public Vector<FIBLocalizedEntry> getEntries() 
	{
		return _entries;
	}

	public void setEntries(Vector<FIBLocalizedEntry> someEntries) 
	{
		_entries = someEntries;
	}

	public void addToEntries(FIBLocalizedEntry entry) 
	{
		entry.setLocalizedDictionary(this);
		_entries.add(entry);
		//logger.info("Add entry key:"+entry.getKey()+" lang="+entry.getLanguage()+" value:"+entry.getValue());
		Language lang = Language.retrieveLanguage(entry.getLanguage());
		if (lang == null) {
			logger.warning("Undefined language: "+entry.getLanguage());
			return;
		}
		getDictForLang(lang).put(entry.getKey(), entry.getValue());
	}
	
	public void removeFromEntries(FIBLocalizedEntry entry)
	{
		entry.setLocalizedDictionary(null);
		_entries.remove(entry);
	}
	
	public void append(FIBLocalizedDictionary aDict)
	{
		if (aDict == null) return;
		for (FIBLocalizedEntry entry : aDict.getEntries()) {
			addToEntries(entry);
		}
	}

	private FIBLocalizedEntry getEntry(Language language, String key)
	{
		for (FIBLocalizedEntry entry : getEntries()) {
			if (Language.retrieveLanguage(entry.getLanguage()) == language && key.equals(entry.getKey())) return entry;
		}
		return null;
	}
	
	private Hashtable<String,String> getDictForLang(Language lang)
	{
		Hashtable<String,String> dict = _values.get(lang);
		if (dict == null) {
			dict = new Hashtable<String,String>();
			_values.put(lang, dict);
		}		
		return dict;
	}

	public String getDefaultValue(String key, Language language)
	{
		//logger.info("Searched default value for key "+key+" return "+FlexoLocalization.localizedForKey(key));
		return FlexoLocalization.localizedForKeyAndLanguage(key,language,false,false);
	}
	
	@Override
	public String localizedForKeyAndLanguage(String key, Language language) 
	{
		if (key == null || StringUtils.isEmpty(key)) return null;
		//if (isSearchingNewEntries) logger.info("-------> called localizedForKeyAndLanguage() key="+key+" lang="+language);
		String returned = getDictForLang(language).get(key);
		if (returned == null) {
			String defaultValue = getDefaultValue(key,language);
			if (handleNewEntry(key,language)) {
				if (!key.equals(defaultValue)) {
					addToEntries(new FIBLocalizedEntry(this, key, language.getName(), defaultValue));
					logger.fine("FIBLocalizedDictionary: store value "+defaultValue+" for key "+key+" for language "+language);
				}
				else {
					getDictForLang(language).put(key,defaultValue);
					logger.fine("FIBLocalizedDictionary: undefined value for key "+key+" for language "+language);
				}
				//dynamicEntries = null;
			}
			return defaultValue;
		}
		return returned;
	}

	public void setLocalizedForKeyAndLanguage(String key, String value,
			Language language)
	{
		getDictForLang(language).put(key,value);
		FIBLocalizedEntry entry = getEntry(language,key);
		if (entry == null) {
			addToEntries(new FIBLocalizedEntry(this,key,language.getName(),value));
		}
		else {
			entry.setValue(value);
		}
	}

	@Override
	public boolean handleNewEntry(String key, Language language) 
	{
		//logger.warning(">>>>>>>>>>>>>>>>>>>>> Cannot find key "+key+" for language "+language);
		return isSearchingNewEntries;
		//return false;
	}
	
	@Override
	public FIBComponent getRootComponent()
	{
		return getComponent().getRootComponent();
	}

	public class DynamicEntry
	{
		private String key;
		
		public DynamicEntry(String aKey)
		{
			key = aKey;
		}
		
		public String getKey() {
			return key;
		}
		
		public void setKey(String aKey) {
			String englishValue = getEnglish();
			String frenchValue = getFrench();
			String dutchValue = getDutch();
			key = aKey;
			setEnglish(englishValue);
			setFrench(frenchValue);
			setDutch(dutchValue);
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
		@Override
		public String toString()
		{
			return "(key="+key+"{en="+getEnglish()+";fr="+getFrench()+";du="+getDutch()+"})";
		}
	}

	// This method is really not efficient, but only called in the context of locales editor
	// This issue is not really severe.
	private Vector<String> buildAllKeys()
	{
		Vector<String> returned = new Vector<String>();
		for (Language l : _values.keySet()) {
			for (String key : _values.get(l).keySet()) {
				if (!returned.contains(key)) returned.add(key);
			}
		}
		return returned;
	}
	
	// This method is really not efficient, but only called in the context of locales editor
	// Impact of this issue is not really severe.
	public Vector<DynamicEntry> getDynamicEntries()
	{
		if (dynamicEntries == null) {
			dynamicEntries = new Vector<DynamicEntry>();
			for (String key : buildAllKeys()) {
				dynamicEntries.add(new DynamicEntry(key));
			}
			Collections.sort(dynamicEntries,new Comparator<DynamicEntry>() {
				@Override
				public int compare(DynamicEntry o1, DynamicEntry o2)
				{
					return Collator.getInstance().compare(o1.key,o2.key);
				}
			});
		}
		return dynamicEntries;
	}
	
	private DynamicEntry getDynamicEntry(String key)
	{
		if (key==null) return null;
		for (DynamicEntry entry : getDynamicEntries()) {
			if (key.equals(entry.key)) return entry;
		}
		return null;
	}
	
	public void refresh()
	{
		logger.fine("Refresh called on FIBLocalizedDictionary "+Integer.toHexString(hashCode()));
		dynamicEntries = null;
		setChanged();
		notifyObservers();
	}

	public DynamicEntry addEntry()
	{
		String key = "new_entry";
		DynamicEntry newDynamicEntry = new DynamicEntry(key);
		dynamicEntries.add(newDynamicEntry);
		Collections.sort(dynamicEntries,new Comparator<DynamicEntry>() {
			@Override
			public int compare(DynamicEntry o1, DynamicEntry o2)
			{
				return Collator.getInstance().compare(o1.key,o2.key);
			}
		});
		return null;
	}
	
	public void deleteEntry(DynamicEntry entry)
	{
		for (Language l : Language.availableValues()) {
			_values.get(l).remove(entry.key);
			FIBLocalizedEntry e = getEntry(l, entry.key);
			if (e != null) _entries.remove(e);
		}
		refresh();
	}

	public void beginSearchNewLocalizationEntries()
	{
		isSearchingNewEntries = true;
	}

	public void endSearchNewLocalizationEntries()
	{
		isSearchingNewEntries = false;
	}

}
