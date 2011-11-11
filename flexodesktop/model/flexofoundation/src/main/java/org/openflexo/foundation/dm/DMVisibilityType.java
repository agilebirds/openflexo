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
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.kvc.ChoiceList;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

import org.openflexo.foundation.FlexoObject;

/**
 * Represents the visibility modifier of a DMMethod
 * 
 * @author sguerin
 * 
 */
public abstract class DMVisibilityType extends FlexoObject implements StringConvertable, ChoiceList, Serializable {

	private static final Logger logger = Logger.getLogger(DMVisibilityType.class.getPackage().getName());

	public static final DMVisibilityType PUBLIC = new PublicVisibility();

	public static final DMVisibilityType PROTECTED = new ProtectedVisibility();

	public static final DMVisibilityType PRIVATE = new PrivateVisibility();

	public static final DMVisibilityType NONE = new NoneVisibility();

	public static final StringEncoder.Converter<DMVisibilityType> visibilityTypeConverter = new Converter<DMVisibilityType>(
			DMVisibilityType.class) {

		@Override
		public DMVisibilityType convertFromString(String value) {
			return get(value);
		}

		@Override
		public String convertToString(DMVisibilityType value) {
			return value.getName();
		}

	};

	static class PublicVisibility extends DMVisibilityType {
		@Override
		public String getName() {
			return "public";
		}
	}

	static class ProtectedVisibility extends DMVisibilityType {
		@Override
		public String getName() {
			return "protected";
		}
	}

	static class PrivateVisibility extends DMVisibilityType {
		@Override
		public String getName() {
			return "private";
		}
	}

	static class NoneVisibility extends DMVisibilityType {
		@Override
		public String getName() {
			return "none";
		}
	}

	public abstract String getName();

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey("java_modifier_" + getName());
	}

	public static DMVisibilityType get(String cardName) {
		for (Enumeration e = PUBLIC.getAvailableValues().elements(); e.hasMoreElements();) {
			DMVisibilityType temp = (DMVisibilityType) e.nextElement();
			if (temp.getName().equals(cardName)) {
				return temp;
			}
		}

		if (logger.isLoggable(Level.WARNING))
			logger.warning("Could not find VisibilityType named " + cardName);
		return null;
	}

	private Vector<DMVisibilityType> _availableValues = null;

	@Override
	public Vector<DMVisibilityType> getAvailableValues() {
		if (_availableValues == null) {
			_availableValues = new Vector<DMVisibilityType>();
			_availableValues.add(PUBLIC);
			_availableValues.add(PROTECTED);
			_availableValues.add(PRIVATE);
			_availableValues.add(NONE);
		}
		return _availableValues;
	}

	@Override
	public StringEncoder.Converter getConverter() {
		return visibilityTypeConverter;
	}

	public static Vector<DMVisibilityType> availableValues() {
		return PUBLIC.getAvailableValues();
	}

}
