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

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.kvc.ChoiceList;

/**
 * Represents a language in Flexo Application
 * 
 * @author sguerin
 * 
 */
public abstract class Language implements ChoiceList {

	private static final Logger logger = Logger.getLogger(Language.class.getPackage().getName());

	/**
	 * Those are available langages, represented as a
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
	 */
	private static Vector<Language> availableLanguages = null;

	public static final Language ENGLISH = new EnglishLanguage();

	public static final Language FRENCH = new FrenchLanguage();

	public static final Language DUTCH = new DutchLanguage();

	private static final Language[] knownsLanguages = { ENGLISH, FRENCH, DUTCH };

	private static class EnglishLanguage extends Language {
		EnglishLanguage() {
			super();
		}

		@Override
		public String getName() {
			return "English";
		}

		@Override
		public String getIdentifier() {
			return "ENGLISH";
		}

		@Override
		public String getTag() {
			return "EN";
		}
	}

	private static class FrenchLanguage extends Language {
		FrenchLanguage() {
			super();
		}

		@Override
		public String getName() {
			return "French";
		}

		@Override
		public String getIdentifier() {
			return "FRENCH";
		}

		@Override
		public String getTag() {
			return "FR";
		}
	}

	private static class DutchLanguage extends Language {
		DutchLanguage() {
			super();
		}

		@Override
		public String getName() {
			return "Dutch";
		}

		@Override
		public String getIdentifier() {
			return "DUTCH";
		}

		@Override
		public String getTag() {
			return "NL";
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
	public static Vector<Language> getAvailableLanguages() {
		if (availableLanguages == null) {
			availableLanguages = new Vector<Language>();
			for (int i = 0; i < knownsLanguages.length; i++) {
				availableLanguages.add(knownsLanguages[i]);
			}
		}
		return availableLanguages;
	}

	public Language[] getKnownsLanguages() {
		return knownsLanguages;
	}

	/**
	 * Return a Vector of possible values (which must be of the same type as the one declared as class implemented this interface)
	 * 
	 * @return a Vector of ChoiceList
	 */
	@Override
	public Vector<Language> getAvailableValues() {
		return getAvailableLanguages();
	}

	public static Language get(String languageAsString) {
		if (languageAsString == null) {
			return ENGLISH;
		}
		for (Language next : getAvailableLanguages()) {
			if (next.getName().equalsIgnoreCase(languageAsString)) {
				return next;
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Cannot find language " + languageAsString);
		}
		if (getAvailableLanguages().size() > 0) {
			return getAvailableLanguages().firstElement();
		}
		return ENGLISH;
	}

	public static Language retrieveLanguage(String languageAsString) {
		if (languageAsString == null) {
			return ENGLISH;
		}
		for (Language next : getAvailableLanguages()) {
			if (next.getName().equalsIgnoreCase(languageAsString)) {
				return next;
			}
			if (next.getTag().equalsIgnoreCase(languageAsString)) {
				return next;
			}
		}
		return ENGLISH;
	}

	public abstract String getName();

	public abstract String getIdentifier();

	public abstract String getTag();

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	public static Vector<Language> availableValues() {
		return getAvailableLanguages();
	}

	@Override
	public String toString() {
		return getName();
	}
}
