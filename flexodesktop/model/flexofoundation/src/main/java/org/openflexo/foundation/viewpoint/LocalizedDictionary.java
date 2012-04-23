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
package org.openflexo.foundation.viewpoint;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.viewpoint.inspector.InspectorEntry;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizedDelegate;

public class LocalizedDictionary extends ViewPointObject implements LocalizedDelegate {

	private static final Logger logger = Logger.getLogger(LocalizedDictionary.class.getPackage().getName());

	private ViewPoint _calc;
	private Vector<LocalizedEntry> _entries;
	private final Hashtable<Language, Hashtable<String, String>> _values;
	private Vector<DynamicEntry> dynamicEntries = null;

	public LocalizedDictionary() {
		_entries = new Vector<LocalizedEntry>();
		_values = new Hashtable<Language, Hashtable<String, String>>();
	}

	@Override
	public ViewPoint getCalc() {
		return _calc;
	}

	public void setCalc(ViewPoint calc) {
		_calc = calc;
	}

	public Vector<LocalizedEntry> getEntries() {
		return _entries;
	}

	public void setEntries(Vector<LocalizedEntry> someEntries) {
		_entries = someEntries;
	}

	public void addToEntries(LocalizedEntry entry) {
		entry.setLocalizedDictionary(this);
		_entries.add(entry);
		logger.fine("Add entry key:" + entry.getKey() + " lang=" + entry.getLanguage() + " value:" + entry.getValue());
		Language lang = Language.retrieveLanguage(entry.getLanguage());
		if (lang == null) {
			logger.warning("Undefined language: " + entry.getLanguage());
			return;
		}
		getDictForLang(lang).put(entry.getKey(), entry.getValue());
	}

	public void removeFromEntries(LocalizedEntry entry) {
		entry.setLocalizedDictionary(null);
		_entries.remove(entry);
	}

	private LocalizedEntry getEntry(Language language, String key) {
		for (LocalizedEntry entry : getEntries()) {
			if ((Language.retrieveLanguage(entry.getLanguage()) == language) && key.equals(entry.getKey())) {
				return entry;
			}
		}
		return null;
	}

	private Hashtable<String, String> getDictForLang(Language lang) {
		Hashtable<String, String> dict = _values.get(lang);
		if (dict == null) {
			dict = new Hashtable<String, String>();
			_values.put(lang, dict);
		}
		return dict;
	}

	@Override
	public String getInspectorName() {
		// TODO Auto-generated method stub
		return null;
	}

	/*public String getDefaultValue(String key, Language language) {
		// logger.info("Searched default value for key "+key+" return "+FlexoLocalization.localizedForKey(key));
		if (getParent() != null) {
			return FlexoLocalization.localizedForKeyAndLanguage(getParent(), key, language);
		}
		return key;
	}*/

	@Override
	public String getLocalizedForKeyAndLanguage(String key, Language language) {
		// if (isSearchingNewEntries) logger.info("-------> called localizedForKeyAndLanguage() key="+key+" lang="+language);
		return getDictForLang(language).get(key);

		/*String returned = getDictForLang(language).get(key);
		if (returned == null) {
			String defaultValue = getDefaultValue(key, language);
			if (handleNewEntry(key, language)) {
				if (!key.equals(defaultValue)) {
					addToEntries(new LocalizedEntry(this, key, language.getName(), defaultValue));
					logger.fine("Calc LocalizedDictionary: store value " + defaultValue + " for key " + key + " for language " + language);
				} else {
					getDictForLang(language).put(key, defaultValue);
					logger.fine("Calc LocalizedDictionary: undefined value for key " + key + " for language " + language);
				}
				// dynamicEntries = null;
			}
			return defaultValue;
		}
		return returned;*/
	}

	public void setLocalizedForKeyAndLanguage(String key, String value, Language language) {
		getDictForLang(language).put(key, value);
		LocalizedEntry entry = getEntry(language, key);
		if (entry == null) {
			addToEntries(new LocalizedEntry(this, key, language.getName(), value));
		} else {
			entry.setValue(value);
		}
	}

	private boolean handleNewEntry = false;

	@Override
	public boolean handleNewEntry(String key, Language language) {
		return handleNewEntry;
	}

	public class DynamicEntry {
		private String key;

		public DynamicEntry(String aKey) {
			key = aKey;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String aNewKey) {
			String oldKey = key;
			for (Language l : Language.availableValues()) {
				String oldValue = _values.get(l).get(oldKey);
				_values.get(l).remove(oldKey);
				if (oldValue != null) {
					_values.get(l).put(aNewKey, oldValue);
				}
				LocalizedEntry e = getEntry(l, oldKey);
				if (e != null) {
					e.setKey(aNewKey);
				}
			}
			key = aNewKey;
		}

		public String getEnglish() {
			return getLocalizedForKeyAndLanguage(key, Language.ENGLISH);
		}

		public void setEnglish(String value) {
			setLocalizedForKeyAndLanguage(key, value, Language.ENGLISH);
		}

		public String getFrench() {
			return getLocalizedForKeyAndLanguage(key, Language.FRENCH);
		}

		public void setFrench(String value) {
			setLocalizedForKeyAndLanguage(key, value, Language.FRENCH);
		}

		public String getDutch() {
			return getLocalizedForKeyAndLanguage(key, Language.DUTCH);
		}

		public void setDutch(String value) {
			setLocalizedForKeyAndLanguage(key, value, Language.DUTCH);
		}

		@Override
		public String toString() {
			return "(key=" + key + "{en=" + getEnglish() + ";fr=" + getFrench() + ";du=" + getDutch() + "})";
		}

	}

	// This method is really not efficient, but only called in the context of locales editor
	// This issue is not really severe.
	private Vector<String> buildAllKeys() {
		Vector<String> returned = new Vector<String>();
		for (Language l : _values.keySet()) {
			for (String key : _values.get(l).keySet()) {
				if (!returned.contains(key)) {
					returned.add(key);
				}
			}
		}
		return returned;
	}

	// This method is really not efficient, but only called in the context of locales editor
	// Impact of this issue is not really severe.
	public Vector<DynamicEntry> getDynamicEntries() {
		if (dynamicEntries == null) {
			dynamicEntries = new Vector<DynamicEntry>();
			for (String key : buildAllKeys()) {
				dynamicEntries.add(new DynamicEntry(key));
			}
			Collections.sort(dynamicEntries, new Comparator<DynamicEntry>() {
				@Override
				public int compare(DynamicEntry o1, DynamicEntry o2) {
					return Collator.getInstance().compare(o1.key, o2.key);
				}
			});
		}
		return dynamicEntries;
	}

	private DynamicEntry getDynamicEntry(String key) {
		if (key == null) {
			return null;
		}
		for (DynamicEntry entry : getDynamicEntries()) {
			if (key.equals(entry.key)) {
				return entry;
			}
		}
		return null;
	}

	public void refresh() {
		logger.fine("Refresh called on FIBLocalizedDictionary " + Integer.toHexString(hashCode()));
		dynamicEntries = null;
		setChanged();
		notifyObservers();
	}

	public DynamicEntry addEntry() {
		String newKey = "key";
		Vector<String> allKeys = buildAllKeys();
		boolean keyAlreadyExists = allKeys.contains(newKey);
		int index = 1;
		while (keyAlreadyExists) {
			newKey = "key" + index;
			keyAlreadyExists = allKeys.contains(newKey);
			index++;
		}
		addToEntries(new LocalizedEntry(this, newKey, FlexoLocalization.getCurrentLanguage().getName(), newKey));
		refresh();
		return getDynamicEntry(newKey);
	}

	public void deleteEntry(DynamicEntry entry) {
		for (Language l : Language.availableValues()) {
			_values.get(l).remove(entry.getKey());
			LocalizedEntry e = getEntry(l, entry.getKey());
			if (e != null) {
				_entries.remove(e);
			}
		}
		refresh();
	}

	public void searchNewEntries() {
		logger.info("Search new entries");
		for (EditionPattern ep : getCalc().getEditionPatterns()) {
			checkAndRegisterLocalized(ep.getName());
			for (EditionScheme es : ep.getEditionSchemes()) {
				checkAndRegisterLocalized(es.getLabel());
				checkAndRegisterLocalized(es.getDescription());
				for (EditionSchemeParameter p : es.getParameters()) {
					checkAndRegisterLocalized(p.getLabel());
				}
				for (InspectorEntry entry : ep.getInspector().getEntries()) {
					checkAndRegisterLocalized(entry.getLabel());
				}
			}
		}
		dynamicEntries = null;
		getCalc().setChanged();
		getCalc().notifyObservers();
	}

	private void checkAndRegisterLocalized(String key) {
		handleNewEntry = true;
		FlexoLocalization.localizedForKey(this, key);
		// getLocalizedForKeyAndLanguage(key, FlexoLocalization.getCurrentLanguage());
		handleNewEntry = false;
	}

	@Override
	public BindingModel getBindingModel() {
		return getCalc().getBindingModel();
	}

	@Override
	public boolean registerNewEntry(String key, Language language, String value) {
		System.out.println("Register entry key=" + key + " lang=" + language + " value=" + value);
		setLocalizedForKeyAndLanguage(key, value, language);
		return true;
	}

	@Override
	public LocalizedDelegate getParent() {
		return FlexoLocalization.getMainLocalizer();
	}
}
