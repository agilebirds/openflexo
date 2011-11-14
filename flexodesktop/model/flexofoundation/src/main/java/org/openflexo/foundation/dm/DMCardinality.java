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
package org.openflexo.foundation.dm;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Represents the cardinality of a property
 * 
 * @author sguerin
 * 
 */
public abstract class DMCardinality extends FlexoObject implements StringConvertable, ChoiceList, Serializable {

	private static final Logger logger = Logger.getLogger(DMCardinality.class.getPackage().getName());

	public static final DMCardinality SINGLE = new SingleCardinality();

	public static final DMCardinality VECTOR = new VectorCardinality();

	public static final DMCardinality HASHTABLE = new HashtableCardinality();

	// public static final DMCardinality ARRAY = new ArrayCardinality();
	// public static final DMCardinality PROPERTIES = new
	// PropertiesCardinality();

	public static final StringEncoder.Converter<DMCardinality> cardinalityConverter = new Converter<DMCardinality>(DMCardinality.class) {

		@Override
		public DMCardinality convertFromString(String value) {
			return get(value);
		}

		@Override
		public String convertToString(DMCardinality value) {
			return value.getName();
		}

	};

	static class SingleCardinality extends DMCardinality {
		@Override
		public String getName() {
			return "SINGLE";
		}

		@Override
		public boolean isMultiple() {
			return false;
		}
	}

	static class VectorCardinality extends DMCardinality {
		@Override
		public String getName() {
			return "VECTOR";
		}

		@Override
		public boolean isMultiple() {
			return true;
		}
	}

	static class HashtableCardinality extends DMCardinality {
		@Override
		public String getName() {
			return "HASHTABLE";
		}

		@Override
		public boolean isMultiple() {
			return true;
		}
	}

	/*
	 * private static class ArrayCardinality extends DMCardinality { public
	 * String getName() { return "ARRAY"; } }
	 * 
	 * private static class PropertiesCardinality extends DMCardinality { public
	 * String getName() { return "PROPERTIES"; } }
	 */

	public abstract String getName();

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	public abstract boolean isMultiple();

	public static DMCardinality get(String cardName) {
		for (Enumeration e = SINGLE.getAvailableValues().elements(); e.hasMoreElements();) {
			DMCardinality temp = (DMCardinality) e.nextElement();
			if (temp.getName().equalsIgnoreCase(cardName)) {
				return temp;
			}
		}

		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find Cardinality named " + cardName);
		}
		return null;
	}

	private Vector<DMCardinality> _availableValues = null;

	@Override
	public Vector getAvailableValues() {
		if (_availableValues == null) {
			_availableValues = new Vector<DMCardinality>();
			_availableValues.add(SINGLE);
			_availableValues.add(VECTOR);
			_availableValues.add(HASHTABLE);
		}
		return _availableValues;
	}

	@Override
	public StringEncoder.Converter getConverter() {
		return cardinalityConverter;
	}

	public static DMCardinality get(Class aClass) {
		if (Vector.class.isAssignableFrom(aClass)) {
			return VECTOR;
		} else if (Hashtable.class.isAssignableFrom(aClass)) {
			return HASHTABLE;
		}
		return SINGLE;
	}

	public static Vector availableValues() {
		return SINGLE.getAvailableValues();
	}

	public static DMCardinality get(Type returnType) {
		// TODO Implements this later !!!
		return SINGLE;
	}

}
