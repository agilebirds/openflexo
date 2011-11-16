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

import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;

import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FlexoProperties;
import org.openflexo.toolbox.Localized;
import org.openflexo.xmlcode.KeyValueDecoder;

/**
 * This class is used to retrieve localized string in Flexo application. Localized are defined in language-specific dictionaries stored in
 * Config.Localization This software part automatically add entries in all languages for all new entries, so provides an efficient and soft
 * localized managing.
 * 
 * @author sguerin
 */
public class FlexoLocalization {

	static {
		Localized.setInstance(new Localized() {
			@Override
			protected String getLocalizedForKey(String key) {
				return FlexoLocalization.localizedForKey(key);
			}
		});
	}

	private static final Logger logger = Logger.getLogger(FlexoLocalization.class.getPackage().getName());

	public static final String LOCALIZATION_DIRNAME = "Localized";

	private static Language _currentLanguage;

	private static Vector<LocalizationListener> localizationListeners = new Vector<LocalizationListener>();

	/**
	 * Directory where localized dictionnaries are stored
	 */
	private static File _localizedDirectory = null;

	/**
	 * This hashtable stores all the loaded dictionary for localized used among Flexo application. Value are
	 * 
	 * <pre>
	 * Property
	 * </pre>
	 * 
	 * instances while keys are
	 * 
	 * <pre>
	 * Language&lt;pre&gt; instances.
	 * 
	 */
	private static Hashtable<Language, Properties> _localizedDictionaries = null;

	private static final WeakHashMap<Component, String> _storedLocalizedForComponents = new WeakHashMap<Component, String>();

	private static final WeakHashMap<TitledBorder, String> _storedLocalizedForBorders = new WeakHashMap<TitledBorder, String>();

	private static final WeakHashMap<TableColumn, String> _storedLocalizedForTableColumn = new WeakHashMap<TableColumn, String>();

	private static final WeakHashMap<Component, String> _storedAdditionalStrings = new WeakHashMap<Component, String>();

	private static final Vector<LocalizedDelegate> _delegates = new Vector<LocalizedDelegate>();

	private static void createNewDictionary(Language language) {
		Properties newDict = new FlexoProperties();
		saveDictionary(language, newDict);
	}

	public static File getDictionaryFileForLanguage(Language language) {
		return new File(getLocalizedDirectory(), language.getName() + ".dict");
	}

	private static File getLocalizedDirectory() {
		if (_localizedDirectory == null) {
			_localizedDirectory = new FileResource(LOCALIZATION_DIRNAME);

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Setting localized directory" + _localizedDirectory.getAbsolutePath());
			}
		}
		return _localizedDirectory;
	}

	private static void saveDictionary(Language language, Properties dict) {
		File dictFile = getDictionaryFileForLanguage(language);
		try {
			if (!dictFile.exists()) {
				dictFile.createNewFile();
			}
			dict.store(new FileOutputStream(dictFile), language.getName());
		} catch (IOException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Unable to save file " + dictFile.getAbsolutePath() + " " + e.getClass().getName());
				// e.printStackTrace();
			}
		}
	}

	private static Properties loadDictionary(Language language) {
		File dictFile = getDictionaryFileForLanguage(language);
		Properties loadedDict = new FlexoProperties();
		try {
			loadedDict.load(new FileInputStream(dictFile));
		} catch (IOException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Unable to load file " + dictFile.getName());
			}
		}
		return loadedDict;
	}

	private static void loadLocalizedDictionaries() {
		_localizedDictionaries = new Hashtable<Language, Properties>();

		for (Enumeration e = getAvailableLanguages().elements(); e.hasMoreElements();) {
			Language language = (Language) e.nextElement();
			File dictFile = getDictionaryFileForLanguage(language);
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Checking dictionary for language " + language.getName());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Looking for file " + dictFile.getAbsolutePath());
			}
			if (!dictFile.exists()) {
				createNewDictionary(language);
			} else {
				Properties loadedDict = loadDictionary(language);
				_localizedDictionaries.put(language, loadedDict);
			}
		}
	}

	private static void addEntryInDictionnary(Language language, String key, String value, boolean required) {
		Properties dict = _localizedDictionaries.get(language);
		if (((!required) && (dict.getProperty(key) == null)) || required) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Adding entry '" + key + "' in " + language + " dictionary");
			}
			dict.setProperty(key, value);
			saveDictionary(language, dict);
		}
	}

	public static void addEntry(String key) {
		// Add in all dictionaries, when required
		for (Enumeration e = getAvailableLanguages().elements(); e.hasMoreElements();) {
			Language nextLanguage = (Language) e.nextElement();
			addEntryInDictionnary(nextLanguage, key, key, false);
		}
	}

	public static void removeEntry(String key) {
		// Remove from all dictionaries
		for (Enumeration e = getAvailableLanguages().elements(); e.hasMoreElements();) {
			Language language = (Language) e.nextElement();
			Properties dict = _localizedDictionaries.get(language);
			dict.remove(key);
			saveDictionary(language, dict);
		}
	}

	public static void saveAllDictionaries() {
		for (Enumeration e = _localizedDictionaries.keys(); e.hasMoreElements();) {
			Language language = (Language) e.nextElement();
			Properties dict = _localizedDictionaries.get(language);
			saveDictionary(language, dict);
		}
	}

	/**
	 * This is general and main method to use localized in Flexo. Applicable language is chosen from the one defined in Preferences. Use
	 * english names for keys, such as some_english_words
	 * 
	 * @param key
	 * @return localized String
	 */
	public static String localizedForKey(String key) {
		return localizedForKeyAndLanguage(key, getCurrentLanguage());
	}

	public static String localizedForKey(LocalizedDelegate delegate, String key) {
		return delegate.localizedForKeyAndLanguage(key, getCurrentLanguage());
	}

	public static String[] localizedForKey(String[] keys) {
		String[] values = new String[keys.length];
		for (int i = 0; i < keys.length; i++) {
			values[i] = localizedForKey(keys[i]);
		}
		return values;
	}

/**
     * Same as {@link localizedForKey(String) plus an additional String (not
     * localized)
     *
     * @param key
     * @param additional
     *            String
     * @return localized String
     */
	public static String localizedForKey(String key, String additionalString) {
		return localizedForKeyAndLanguage(key, getCurrentLanguage()) + additionalString;
	}

	/**
	 * Return localized related to specified key and language
	 * 
	 * @param key
	 *            , language
	 * @return localized String
	 */
	public static String localizedForKeyAndLanguage(String key, Language language) {
		return localizedForKeyAndLanguage(key, language, true, true);
	}

	/**
	 * Return localized related to specified key and language
	 * 
	 * @param key
	 *            , language
	 * @return localized String
	 */
	public static String localizedForKeyAndLanguage(String key, Language language, boolean createsEntry, boolean useDelegates) {
		if (key == null) {
			return null;
		}
		Properties currentLanguageDict;
		String localized;

		if (_localizedDictionaries == null) {
			loadLocalizedDictionaries();
		}

		// First look in delegates
		if (useDelegates) {
			for (LocalizedDelegate d : _delegates) {
				localized = d.localizedForKeyAndLanguage(key, language);
				// System.out.println("Search for "+key+" in "+d+" return "+localized);
				if (localized != null) {
					return localized;
				}
			}
		}

		// Try in common dict
		currentLanguageDict = _localizedDictionaries.get(language);
		localized = currentLanguageDict.getProperty(key);

		/* if (localized == null) {
		 	// Not found, might be in delegates
		 	for (LocalizedDelegate d : _delegates) {
		 		localized = d.localizedForKeyAndLanguage(key, language);
		 		if (localized != null) break;
		 	}
		 }*/

		if (localized == null) {
			// Add in all dictionaries, when required (only when no delegate handle it)
			if (useDelegates) {
				for (LocalizedDelegate d : _delegates) {
					if (d.handleNewEntry(key, language)) {
						return d.localizedForKeyAndLanguage(key, language);
					}
				}
			}
			if (createsEntry) {
				addEntry(key);
				return currentLanguageDict.getProperty(key);
			} else {
				return key;
			}
		} else {
			return localized;
		}
	}

	/**
	 * Sets localized related to specified key and language
	 * 
	 * @param key
	 *            , value, language
	 * @return localized String
	 */
	public static void setLocalizedForKeyAndLanguage(String key, String value, Language language) {
		Properties currentLanguageDict;
		if (_localizedDictionaries == null) {
			loadLocalizedDictionaries();
		}
		currentLanguageDict = _localizedDictionaries.get(language);
		Object old = currentLanguageDict.setProperty(key, value);
		if (language == Language.ENGLISH) {
			if (key.equals(_localizedDictionaries.get(Language.DUTCH).get(key))
					|| (old != null && old.equals(_localizedDictionaries.get(Language.DUTCH).get(key)))
					|| (old == null && _localizedDictionaries.get(Language.DUTCH).get(key) == null)) {
				_localizedDictionaries.get(Language.DUTCH).setProperty(key, value);
			}
		}
	}

	/**
	 * Sets localized related to specified key
	 * 
	 * @param key
	 *            , value, language
	 * @return localized String
	 */
	public static void setLocalizedForKey(String key, String value) {
		setLocalizedForKeyAndLanguage(key, value, getCurrentLanguage());

	}

	public static String localizedForKey(String key, Component component) {
		if (logger.isLoggable(Level.FINE)) {
			logger.finest("localizedForKey called with " + key + " for " + component.getClass().getName());
		}
		_storedLocalizedForComponents.put(component, key);
		return localizedForKey(key);
	}

	public static String localizedForKey(String key, TitledBorder border) {
		if (logger.isLoggable(Level.FINE)) {
			logger.finest("localizedForKey called with " + key + " for border " + border.getClass().getName());
		}
		_storedLocalizedForBorders.put(border, key);
		return localizedForKey(key);
	}

	public static String localizedForKey(String key, TableColumn column) {
		if (logger.isLoggable(Level.FINE)) {
			logger.finest("localizedForKey called with " + key + " for border " + column.getClass().getName());
		}
		_storedLocalizedForTableColumn.put(column, key);
		return localizedForKey(key);
	}

	public static String localizedForKey(String key, String additionalString, Component component) {
		if (key == null) {
			return null;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.finest("localizedForKey called with " + key + " for " + component.getClass().getName());
		}
		_storedLocalizedForComponents.put(component, key);
		_storedAdditionalStrings.put(component, additionalString);
		return localizedForKey(key) + additionalString;
	}

	public static void updateGUILocalized() {
		for (Component next : _storedLocalizedForComponents.keySet()) {
			if (next == null) {
				continue;
			}
			String string = _storedLocalizedForComponents.get(next);
			if (string == null) {
				continue;
			}
			String text = localizedForKey(string);
			String additionalString = _storedAdditionalStrings.get(next);
			if (additionalString != null) {
				text = text + additionalString;
			}
			if (next instanceof AbstractButton) {
				((AbstractButton) next).setText(text);
			}
			if (next instanceof JLabel) {
				((JLabel) next).setText(text);
			}
			next.setName(text);
			if (next.getParent() instanceof JTabbedPane) {
				if (((JTabbedPane) next.getParent()).indexOfComponent(next) > -1) {
					((JTabbedPane) next.getParent()).setTitleAt(((JTabbedPane) next.getParent()).indexOfComponent(next), text);
				}
			}
			if (next.getParent() != null && next.getParent().getParent() instanceof JTabbedPane) {
				if (((JTabbedPane) next.getParent().getParent()).indexOfComponent(next) > -1) {
					((JTabbedPane) next.getParent().getParent()).setTitleAt(
							((JTabbedPane) next.getParent().getParent()).indexOfComponent(next), text);
				}
			}
		}
		for (TitledBorder next : _storedLocalizedForBorders.keySet()) {
			if (next == null) {
				continue;
			}
			String string = _storedLocalizedForBorders.get(next);
			if (string == null) {
				continue;
			}
			String text = localizedForKey(string);
			next.setTitle(text);
		}
		for (TableColumn next : _storedLocalizedForTableColumn.keySet()) {
			if (next == null) {
				continue;
			}
			String string = _storedLocalizedForTableColumn.get(next);
			if (string == null) {
				continue;
			}
			String text = localizedForKey(string);
			next.setHeaderValue(text);
		}
		for (Frame f : Frame.getFrames()) {
			f.repaint();
		}

	}

	/**
	 * Returns a vector containing all available languages as a
	 * 
	 * <pre>
	 * Vector
	 * </pre>
	 * 
	 * of
	 * 
	 * <pre>
	 * Language
	 * </pre>
	 * 
	 * objects
	 * 
	 * @return Vector of
	 * 
	 *         <pre>
	 * Language
	 * </pre>
	 * 
	 *         objects
	 */
	public static Vector getAvailableLanguages() {
		return Language.getAvailableLanguages();
	}

	public static Vector<String> buildAllKeys() {
		Vector<String> returned = new Vector<String>();
		for (Enumeration e1 = _localizedDictionaries.elements(); e1.hasMoreElements();) {
			Properties next = (Properties) e1.nextElement();
			for (Enumeration e2 = next.keys(); e2.hasMoreElements();) {
				String nextKey = (String) e2.nextElement();
				if (!returned.contains(nextKey)) {
					returned.add(nextKey);
				}
			}
		}
		return returned;
	}

	public static Vector<String> buildAllKeys(char aChar) {
		char lc = Character.toLowerCase(aChar);
		char uc = Character.toUpperCase(aChar);
		Vector<String> returned = new Vector<String>();
		for (Enumeration e1 = _localizedDictionaries.elements(); e1.hasMoreElements();) {
			Properties next = (Properties) e1.nextElement();
			for (Enumeration e2 = next.keys(); e2.hasMoreElements();) {
				String nextKey = (String) e2.nextElement();
				if (nextKey.length() > 0) {
					if ((!returned.contains(nextKey)) && ((nextKey.charAt(0) == lc) || (nextKey.charAt(0) == uc))) {
						returned.add(nextKey);
					}
				}
			}
		}
		return returned;
	}

	public static Vector<String> buildAllWarningKeys() {
		Vector<String> returned = new Vector<String>();
		for (Enumeration e1 = _localizedDictionaries.elements(); e1.hasMoreElements();) {
			Properties next = (Properties) e1.nextElement();
			for (Enumeration e2 = next.keys(); e2.hasMoreElements();) {
				String nextKey = (String) e2.nextElement();
				if (nextKey.length() > 0) {
					if (!returned.contains(nextKey)) {
						String value = next.getProperty(nextKey);
						if (!LocalizedEditorModel.isKeyValid(nextKey)) {
							returned.add(nextKey);
						} else if (LocalizedEditorModel.isValueValid(nextKey, value)) {
						} else {
							returned.add(nextKey);
						}
					}
				}
			}
		}
		return returned;
	}

	public static Vector<Character> getAllFirstChar() {
		Vector<Character> returned = new Vector<Character>();
		for (Enumeration e1 = _localizedDictionaries.elements(); e1.hasMoreElements();) {
			Properties next = (Properties) e1.nextElement();
			for (Enumeration e2 = next.keys(); e2.hasMoreElements();) {
				String nextKey = (String) e2.nextElement();
				if (nextKey.length() > 0) {
					char firstChar = Character.toUpperCase(nextKey.charAt(0));
					if (!returned.contains(firstChar)) {
						returned.add(firstChar);
					}
				}
			}
		}
		return returned;
	}

	/*public static LocalizedEditorModel getEditorModel()
	{
	    if (_editorModel == null) {
	        _editorModel = new LocalizedEditorModel();
	    }
	    return _editorModel;
	}*/

	public static void showLocalizedEditor() {
		if (localizedEditor == null) {
			localizedEditor = new LocalizedEditor();
		}
		localizedEditor.setVisible(true);
	}

	private static LocalizedEditor localizedEditor = null;

	public static void setCurrentLanguage(Language language) {
		_currentLanguage = language;
		for (LocalizationListener l : localizationListeners) {
			l.languageChanged(language);
		}
	}

	public static Language getCurrentLanguage() {
		if (_currentLanguage == null) {
			_currentLanguage = Language.ENGLISH;
		}
		return _currentLanguage;
	}

	/**
	 * @param _message
	 * @param issue
	 * @return
	 */
	public static String localizedForKeyWithParams(String key, Object object) {
		String base = localizedForKey(key);
		return replaceAllParamsInString(base, object);
	}

	/**
	 * @param _message
	 * @param issue
	 * @return
	 */
	public static String localizedForKeyWithParams(LocalizedDelegate delegate, String key, Object object) {
		String base = localizedForKey(delegate, key);
		return replaceAllParamsInString(base, object);
	}

	/**
	 * @param _message
	 * @param issue
	 * @return
	 */
	public static String localizedForKeyWithParams(String key, String... params) {
		String base = localizedForKey(key);
		return replaceAllParamsInString(base, params);
	}

	/**
	 * @param _message
	 * @param issue
	 * @return
	 */
	public static String localizedForKeyWithParams(LocalizedDelegate delegate, String key, String... params) {
		String base = localizedForKey(delegate, key);
		return replaceAllParamsInString(base, params);
	}

	public static void main(String[] args) {
		System.out.println("Testing localizedForKeyWithParams(String,String...): "
				+ localizedForKeyWithParams(
						"Must display first param here ($0) and second one: ($1) and third one:($2) then repeat second one ($1)", "foo1",
						"foo2", "foo3"));
	}

	/**
	 * @param returned
	 * @param object
	 */
	private static String replaceAllParamsInString(String aString, Object object) {
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("replaceAllParamsInString() with " + aString + " and " + object);
		}
		// Pattern p = Pattern.compile("*\\($[a-zA-Z]\\)*");
		// Pattern p = Pattern.compile("\\p{Punct}\\bJava(\\w*)\\p{Punct}");
		// Pattern p = Pattern.compile("\\(\\bJava(\\w*)\\)");
		// Pattern p = Pattern.compile("\\(\\$(\\w*)\\)");
		Pattern p = Pattern.compile("\\(\\$([a-zA-Z[\\.]]*)\\)");
		Matcher m = p.matcher(aString);
		String returned = "";
		int lastIndex = 0;
		while (m.find()) {
			int nextIndex = m.start(0);
			String foundPattern = m.group(0);
			if (logger.isLoggable(Level.FINE)) {
				logger.finest("Found '" + foundPattern + "' at position " + nextIndex);
			}
			if (m.start(1) < m.end(1)) {
				String suffix = m.group(1);
				if (logger.isLoggable(Level.FINE)) {
					logger.finest("Suffix is " + suffix);
				}
				String replacementString = valueForKeyAndObject(suffix, object);
				returned += aString.substring(lastIndex, nextIndex) + replacementString;
				lastIndex = nextIndex + foundPattern.length();
			}
		}
		returned += aString.substring(lastIndex, aString.length());
		if (logger.isLoggable(Level.FINE)) {
			logger.finer("Returning " + returned);
		}
		return returned;
	}

	/**
	 * @param returned
	 * @param object
	 */
	private static String replaceAllParamsInString(String aString, String... params) {
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("replaceAllParamsInString() with " + aString + " and " + params);
		}
		Pattern p = Pattern.compile("\\(\\$([0-9]*)\\)");
		Matcher m = p.matcher(aString);
		String returned = "";
		int lastIndex = 0;
		while (m.find()) {
			int nextIndex = m.start(0);
			String foundPattern = m.group(0);
			if (logger.isLoggable(Level.FINEST)) {
				logger.finest("Found '" + foundPattern + "' at position " + nextIndex);
			}
			if (m.start(1) < m.end(1)) {
				String suffix = m.group(1);
				if (logger.isLoggable(Level.FINE)) {
					logger.finest("Suffix is " + suffix);
				}
				int suffixValue = -1;
				try {
					suffixValue = Integer.valueOf(suffix);
					String replacementString = params[suffixValue];
					returned += aString.substring(lastIndex, nextIndex) + replacementString;
					lastIndex = nextIndex + foundPattern.length();
				} catch (NumberFormatException e) {
					logger.warning("Could not parse " + suffix + " as integer");
				} catch (IndexOutOfBoundsException e) {
					logger.warning("Could not access index " + suffixValue + " for " + aString + " : " + e.getMessage());
				}

			}
		}
		returned += aString.substring(lastIndex, aString.length());
		if (logger.isLoggable(Level.FINE)) {
			logger.finer("Returning " + returned);
		}
		return returned;
	}

	private static String valueForKeyAndObject(String key, Object object) {

		return (String) KeyValueDecoder.objectForKey(object, key);
	}

	public static void clearStoredLocalizedForComponents() {
		_storedLocalizedForComponents.clear();
		_storedLocalizedForBorders.clear();
		_storedAdditionalStrings.clear();
		_storedLocalizedForTableColumn.clear();
	}

	public static void addToLocalizedDelegates(LocalizedDelegate d) {
		if (d == null) {
			throw new NullPointerException();
		}
		_delegates.add(d);
	}

	public static void removeFromLocalizedDelegates(LocalizedDelegate d) {
		if (d == null) {
			throw new NullPointerException();
		}
		_delegates.remove(d);
	}

	public static void addToLocalizationListeners(LocalizationListener l) {
		localizationListeners.add(l);
	}

	public static void removeFromLocalizationListeners(LocalizationListener l) {
		localizationListeners.remove(l);
	}
}
