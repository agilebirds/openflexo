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

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBLocalizedEntry.FIBLocalizedEntryImpl.class)
@XMLElement(xmlTag = "Localized")
public interface FIBLocalizedEntry extends FIBModelObject {

	@PropertyIdentifier(type = FIBLocalizedDictionary.class)
	public static final String DICTIONARY_KEY = "dictionary";
	@PropertyIdentifier(type = String.class)
	public static final String KEY_KEY = "key";
	@PropertyIdentifier(type = String.class)
	public static final String LANGUAGE_KEY = "language";
	@PropertyIdentifier(type = String.class)
	public static final String VALUE_KEY = "value";

	@Getter(value = DICTIONARY_KEY, inverse = FIBLocalizedDictionary.ENTRIES_KEY)
	public FIBLocalizedDictionary getLocalizedDictionary();

	@Setter(DICTIONARY_KEY)
	public void setLocalizedDictionary(FIBLocalizedDictionary dict);

	@Getter(value = KEY_KEY)
	@XMLAttribute
	public String getKey();

	@Setter(KEY_KEY)
	public void setKey(String key);

	@Getter(value = LANGUAGE_KEY)
	@XMLAttribute(xmlTag = "lang")
	public String getLanguage();

	@Setter(LANGUAGE_KEY)
	public void setLanguage(String language);

	@Getter(value = VALUE_KEY)
	public String getValue();

	@Setter(VALUE_KEY)
	public void setValue(String value);

	public static abstract class FIBLocalizedEntryImpl extends FIBModelObjectImpl implements FIBLocalizedEntry {

		@Override
		public FIBComponent getComponent() {
			return getLocalizedDictionary().getComponent();
		}

	}
}
