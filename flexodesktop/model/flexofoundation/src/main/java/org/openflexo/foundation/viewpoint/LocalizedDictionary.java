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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.viewpoint.inspector.InspectorEntry;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(LocalizedDictionary.LocalizedDictionaryImpl.class)
@XMLElement(xmlTag = "LocalizedDictionary")
public interface LocalizedDictionary extends ViewPointObject, LocalizedDelegate {

	@PropertyIdentifier(type = ViewPoint.class)
	public static final String OWNER_KEY = "owner";
	@PropertyIdentifier(type = List.class)
	public static final String ENTRIES_KEY = "entries";

	@Getter(value = ENTRIES_KEY, cardinality = Cardinality.LIST, inverse = LocalizedEntry.LOCALIZED_DICTIONARY_KEY)
	public List<LocalizedEntry> getEntries();

	@Setter(ENTRIES_KEY)
	public void setEntries(List<LocalizedEntry> entries);

	@Adder(ENTRIES_KEY)
	public void addToEntries(LocalizedEntry aEntrie);

	@Remover(ENTRIES_KEY)
	public void removeFromEntries(LocalizedEntry aEntrie);

	@Getter(value = OWNER_KEY, inverse = ViewPoint.LOCALIZED_DICTIONARY_KEY)
	public ViewPoint getOwner();

	@Setter(OWNER_KEY)
	public void setOwner(ViewPoint owner);

	public static abstract class LocalizedDictionaryImpl extends ViewPointObjectImpl implements LocalizedDictionary {

		private static final Logger logger = Logger.getLogger(LocalizedDictionary.class.getPackage().getName());

		private Vector<LocalizedEntry> _entries;
		private final Hashtable<Language, Hashtable<String, String>> _values;
		private Vector<DynamicEntry> dynamicEntries = null;

		public LocalizedDictionaryImpl() {
			super();
			_entries = new Vector<LocalizedEntry>();
			_values = new Hashtable<Language, Hashtable<String, String>>();
			/*if (builder != null) {
				owner = builder.getVirtualModel();
			}*/
		}

		/*public LocalizedDictionaryImpl() {
			super();
			_entries = new Vector<LocalizedEntry>();
			_values = new Hashtable<Language, Hashtable<String, String>>();
			if (builder != null) {
				owner = builder.getViewPoint();
			}
		}*/

		@Override
		public ViewPoint getViewPoint() {
			return getOwner();
		}

		@Override
		public Vector<LocalizedEntry> getEntries() {
			return _entries;
		}

		public void setEntries(Vector<LocalizedEntry> someEntries) {
			_entries = someEntries;
		}

		@Override
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

		@Override
		public void removeFromEntries(LocalizedEntry entry) {
			entry.setLocalizedDictionary(null);
			_entries.remove(entry);
		}

		private LocalizedEntry getEntry(Language language, String key) {
			for (LocalizedEntry entry : getEntries()) {
				if (Language.retrieveLanguage(entry.getLanguage()) == language && key.equals(entry.getKey())) {
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
				LocalizedEntry newEntry = getViewPoint().getFactory().newInstance(LocalizedEntry.class);
				newEntry.setLanguage(language.getName());
				newEntry.setKey(key);
				newEntry.setValue(value);
				addToEntries(newEntry);
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

			LocalizedEntry newEntry = getViewPoint().getFactory().newInstance(LocalizedEntry.class);
			newEntry.setLanguage(FlexoLocalization.getCurrentLanguage().getName());
			newEntry.setKey(newKey);
			newEntry.setValue(newKey);
			addToEntries(newEntry);
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
			for (VirtualModel vm : getViewPoint().getVirtualModels()) {
				for (EditionPattern ep : vm.getEditionPatterns()) {
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
			}
			dynamicEntries = null;
			getViewPoint().setChanged();
			getViewPoint().notifyObservers();
		}

		private void checkAndRegisterLocalized(String key) {
			handleNewEntry = true;
			FlexoLocalization.localizedForKey(this, key);
			// getLocalizedForKeyAndLanguage(key, FlexoLocalization.getCurrentLanguage());
			handleNewEntry = false;
		}

		@Override
		public BindingModel getBindingModel() {
			return getViewPoint().getBindingModel();
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

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			return "<not_implemented:" + getStringRepresentation() + ">";
		}

		@Override
		public Collection<? extends Validable> getEmbeddedValidableObjects() {
			return getEntries();
		}
	}
}
