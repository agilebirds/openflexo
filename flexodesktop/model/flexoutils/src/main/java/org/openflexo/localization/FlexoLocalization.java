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
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;

import org.openflexo.antar.binding.BindingEvaluator;

/**
 * This utility class implement localization support <br>
 * 
 * A hierarchical structure of localized delegates are internally used. If no localization is found for a given delegate, then the parent
 * delegate will be recursively invoked. See {@link LocalizedDelegate}.<br>
 * 
 * There are two main ways to use this class:
 * <ul>
 * <li>Either use methods with a supplied localized delegate</li>
 * <li>Or use methods without specifying a localized delegate</li>
 * </ul>
 * In second case, this class MUST have been initialized with a given localizer (localized delegate).
 * 
 * @author sylvain
 */
public class FlexoLocalization {

	private static final Logger logger = Logger.getLogger(FlexoLocalization.class.getPackage().getName());

	private static Language _currentLanguage;

	private static List<WeakReference<LocalizationListener>> localizationListeners = new Vector<WeakReference<LocalizationListener>>();

	private static final WeakHashMap<Component, String> _storedLocalizedForComponents = new WeakHashMap<Component, String>();

	private static final WeakHashMap<JComponent, String> _storedLocalizedForComponentTooltips = new WeakHashMap<JComponent, String>();

	private static final WeakHashMap<TitledBorder, String> _storedLocalizedForBorders = new WeakHashMap<TitledBorder, String>();

	private static final WeakHashMap<TableColumn, String> _storedLocalizedForTableColumn = new WeakHashMap<TableColumn, String>();

	private static final WeakHashMap<Component, String> _storedAdditionalStrings = new WeakHashMap<Component, String>();

	private static LocalizedDelegate mainLocalizer;

	private static boolean uninitalizedLocalizationWarningDone = false;

	/**
	 * Initialize localization given a supplied localization delegate
	 * 
	 * @param delegate
	 */
	public static void initWith(LocalizedDelegate delegate) {
		if (!FlexoLocalization.isInitialized()) {
			mainLocalizer = delegate;
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Locales have already been initialized. Unless you know what you are doing, this should not happen is therefore prevented.");
			}
		}

	}

	/**
	 * Return a flag indicating if localization has been initialized
	 * 
	 * @return
	 */
	public static boolean isInitialized() {
		return mainLocalizer != null;
	}

	/**
	 * Initialize localization given a supplied directory<br>
	 * This directory will be used as the location of a main localizer that will be instanciated here, as an instance of
	 * LocalizedDelegateImpl
	 * 
	 * @param localizedDirectory
	 */
	public static void initWith(File localizedDirectory) {
		mainLocalizer = new LocalizedDelegateImpl(localizedDirectory, null, false);
	}

	/**
	 * This is general and main method to use localized in Flexo.<br>
	 * Applicable language is chosen from the one defined in Preferences.<br>
	 * Use english names for keys, such as 'some_english_words'
	 * 
	 * @param key
	 * @return localized String
	 */
	public static String localizedForKey(String key) {
		if (mainLocalizer == null) {
			if (!uninitalizedLocalizationWarningDone) {
				logger.warning("FlexoLocalization not initialized, returning key as localized key=" + key);
				uninitalizedLocalizationWarningDone = true;
			}
			return key;
		}
		return localizedForKey(mainLocalizer, key);
	}

	public static String localizedForKey(LocalizedDelegate delegate, String key) {
		return localizedForKeyAndLanguage(delegate, key, getCurrentLanguage());
	}

	public static String localizedForKeyAndLanguage(String key, Language language) {
		if (mainLocalizer == null) {
			if (!uninitalizedLocalizationWarningDone) {
				logger.warning("FlexoLocalization not initialized, returning key as localized key=" + key);
				uninitalizedLocalizationWarningDone = true;
			}
			return key;
		}
		return localizedForKeyAndLanguage(mainLocalizer, key, language);
	}

	public static String localizedForKeyAndLanguage(LocalizedDelegate delegate, String key, Language language) {
		return localizedForKeyAndLanguage(delegate, key, language, true);
	}

	public static String localizedForKeyAndLanguage(LocalizedDelegate delegate, String key, Language language,
			boolean createsNewEntriesIfNonExistant) {

		if (key == null) {
			return null;
		}

		if (delegate == null) {
			return key;
		}

		String returned = delegate.getLocalizedForKeyAndLanguage(key, language);
		if (returned == null) {
			// Not found
			if (createsNewEntriesIfNonExistant && delegate.handleNewEntry(key, language)) {
				// We have to register this new entries
				if (delegate.getParent() != null) {
					// A parent exists, we will use its localized values in current localizer
					for (Language l : Language.availableValues()) {
						String value = localizedForKeyAndLanguage(delegate.getParent(), key, l, false);
						delegate.registerNewEntry(key, l, value);
					}
					return localizedForKeyAndLanguage(delegate.getParent(), key, language, false);
				} else {
					// No parent exists, we will use keys
					for (Language l : Language.availableValues()) {
						delegate.registerNewEntry(key, l, key);
					}
					return key;
				}
			} else {
				if (delegate.getParent() != null) {
					return localizedForKeyAndLanguage(delegate.getParent(), key, language, false);
				} else {
					// logger.warning("Not found and not handling new entries for localized for key " + key + " delegate=" + delegate);
					return key;
				}
			}
		} else {
			return returned;
		}
	}

	public static String localizedForKeyWithParams(String key, Object... object) {
		String base = localizedForKey(key);
		return replaceAllParamsInString(base, object);
	}

	public static String localizedForKeyWithParams(LocalizedDelegate delegate, String key, Object... object) {
		String base = localizedForKey(delegate, key);
		return replaceAllParamsInString(base, object);
	}

	/**
	 * Convenient methods localizing an array of keys
	 * 
	 * @param keys
	 * @return
	 */
	public static String[] localizedForKey(String[] keys) {
		String[] values = new String[keys.length];
		for (int i = 0; i < keys.length; i++) {
			values[i] = localizedForKey(keys[i]);
		}
		return values;
	}

	/**
	 * Sets localized related to specified key and language
	 * 
	 * @param key
	 *            , value, language
	 * @return localized String
	 */
	public static void setLocalizedForKeyAndLanguage(String key, String value, Language language) {
		if (mainLocalizer == null) {
			if (!uninitalizedLocalizationWarningDone) {
				logger.warning("FlexoLocalization not initialized, returning key as localized key=" + key);
				uninitalizedLocalizationWarningDone = true;
			}
		} else {
			setLocalizedForKeyAndLanguage(mainLocalizer, key, value, language);
		}
	}

	/**
	 * Sets localized related to specified key and language
	 * 
	 * @param key
	 *            , value, language
	 * @return localized String
	 */
	public static void setLocalizedForKeyAndLanguage(@Nonnull LocalizedDelegate delegate, String key, String value, Language language) {
		if (delegate.handleNewEntry(key, language)) {
			delegate.registerNewEntry(key, language, value);
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

	public static String localizedForKey(LocalizedDelegate delegate, String key, Component component) {
		if (logger.isLoggable(Level.FINE)) {
			logger.finest("localizedForKey called with " + key + " for " + component.getClass().getName());
		}
		_storedLocalizedForComponents.put(component, key);
		return localizedForKey(delegate, key);
	}

	public static String localizedTooltipForKey(String key, JComponent component) {
		return localizedTooltipForKey(mainLocalizer, key, component);
	}

	public static String localizedTooltipForKey(LocalizedDelegate delegate, String key, JComponent component) {
		if (logger.isLoggable(Level.FINE)) {
			logger.finest("localizedForKey called with " + key + " for " + component.getClass().getName());
		}
		_storedLocalizedForComponentTooltips.put(component, key);
		return localizedForKey(delegate, key);
	}

	public static String localizedForKey(String key, Component component) {
		return localizedForKey(mainLocalizer, key, component);
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
		for (Map.Entry<Component, String> e : _storedLocalizedForComponents.entrySet()) {
			Component component = e.getKey();
			String string = e.getValue();
			String text = localizedForKey(string);
			String additionalString = _storedAdditionalStrings.get(component);
			if (additionalString != null) {
				text = text + additionalString;
			}
			if (component instanceof AbstractButton) {
				((AbstractButton) component).setText(text);
			}
			if (component instanceof JLabel) {
				((JLabel) component).setText(text);
			}
			component.setName(text);
			if (component.getParent() instanceof JTabbedPane) {
				if (((JTabbedPane) component.getParent()).indexOfComponent(component) > -1) {
					((JTabbedPane) component.getParent()).setTitleAt(((JTabbedPane) component.getParent()).indexOfComponent(component),
							text);
				}
			}
			if (component.getParent() != null && component.getParent().getParent() instanceof JTabbedPane) {
				if (((JTabbedPane) component.getParent().getParent()).indexOfComponent(component) > -1) {
					((JTabbedPane) component.getParent().getParent()).setTitleAt(
							((JTabbedPane) component.getParent().getParent()).indexOfComponent(component), text);
				}
			}
		}
		for (Map.Entry<JComponent, String> e : _storedLocalizedForComponentTooltips.entrySet()) {
			JComponent component = e.getKey();
			String string = e.getValue();
			String text = localizedForKey(string);
			component.setToolTipText(text);
		}
		for (Map.Entry<TitledBorder, String> e : _storedLocalizedForBorders.entrySet()) {
			String string = e.getValue();
			String text = localizedForKey(string);
			e.getKey().setTitle(text);
		}
		for (Map.Entry<TableColumn, String> e : _storedLocalizedForTableColumn.entrySet()) {
			String string = e.getValue();
			String text = localizedForKey(string);
			e.getKey().setHeaderValue(text);
		}
		for (Frame f : Frame.getFrames()) {
			f.repaint();
		}

	}

	/**
	 * Returns a vector containing all available languages as a Vector of Language objects
	 * 
	 * @return Vector of Language objects
	 */
	public static List<Language> getAvailableLanguages() {
		return Language.getAvailableLanguages();
	}

	public static void setCurrentLanguage(Language language) {
		_currentLanguage = language;
		Iterator<WeakReference<LocalizationListener>> i = localizationListeners.iterator();
		while (i.hasNext()) {
			LocalizationListener l = i.next().get();
			if (l == null) {
				i.remove();
			} else {
				l.languageChanged(language);
			}
		}
	}

	public static Language getCurrentLanguage() {
		if (_currentLanguage == null) {
			_currentLanguage = Language.ENGLISH;
		}
		return _currentLanguage;
	}

	public static LocalizedDelegate getMainLocalizer() {
		return mainLocalizer;
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
	private static String replaceAllParamsInString(String aString, Object... object) {
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("replaceAllParamsInString() with " + aString + " and " + object);
		}
		// Pattern p = Pattern.compile("*\\($[a-zA-Z]\\)*");
		// Pattern p = Pattern.compile("\\p{Punct}\\bJava(\\w*)\\p{Punct}");
		// Pattern p = Pattern.compile("\\(\\bJava(\\w*)\\)");
		// Pattern p = Pattern.compile("\\(\\$(\\w*)\\)");
		Pattern p = Pattern.compile("\\(\\$([_0-9a-zA-Z[\\.]]+)\\)");
		Matcher m = p.matcher(aString);
		StringBuffer returned = new StringBuffer();
		while (m.find()) {
			int nextIndex = m.start();
			String foundPattern = m.group();
			if (logger.isLoggable(Level.FINE)) {
				logger.finest("Found '" + foundPattern + "' at position " + nextIndex);
			}
			String suffix = m.group(1);
			if (logger.isLoggable(Level.FINE)) {
				logger.finest("Suffix is " + suffix);
			}
			try {
				int index = Integer.parseInt(suffix);
				if (object.length > index) {
					if (object[index] != null) {
						m.appendReplacement(returned, object[index].toString());
					} else {
						m.appendReplacement(returned, "");
					}
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Argument index " + index + " is greater than number of arguments");
					}
				}
			} catch (NumberFormatException e) {
				try {
					m.appendReplacement(returned, valueForKeyAndObject(suffix, object[0]));
				} catch (IllegalArgumentException e2) {
					logger.warning("Unexpected IllegalArgumentException " + e2.getMessage());
				}
			}
		}
		m.appendTail(returned);
		if (logger.isLoggable(Level.FINE)) {
			logger.finer("Returning " + returned);
		}
		return returned.toString();
	}

	private static String valueForKeyAndObject(String key, Object object) {

		try {
			return BindingEvaluator.evaluateBinding(key, object).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;
		/*try {

			// Object objectForKey = BindingEvaluator.evaluateBinding(key, object);
			Object objectForKey = KeyValueDecoder.valueForKey(object, key);
			if (objectForKey != null) {
				return objectForKey.toString();
			} else {
				return "";
			}
		} catch (InvalidObjectSpecificationException e) {
			logger.warning(e.getMessage());
			return key;
		}*/
	}

	public static void clearStoredLocalizedForComponents() {
		_storedLocalizedForComponents.clear();
		_storedLocalizedForBorders.clear();
		_storedAdditionalStrings.clear();
		_storedLocalizedForTableColumn.clear();
		localizationListeners.clear();
	}

	public static void addToLocalizationListeners(LocalizationListener l) {
		localizationListeners.add(new WeakReference<LocalizationListener>(l));
	}

	public static void removeFromLocalizationListeners(LocalizationListener l) {
		Iterator<WeakReference<LocalizationListener>> i = localizationListeners.iterator();
		while (i.hasNext()) {
			LocalizationListener l1 = i.next().get();
			if (l1 == null || l1 == l) {
				i.remove();
			}
		}
	}
}
