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
package org.openflexo.foundation.ie.util;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.KVCFlexoObject;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Represents type of dropdown (DROPDOWN,DOMAIN_KEY_VALUE)
 * 
 * @author sguerin
 * 
 */
public abstract class DropDownType extends KVCFlexoObject implements StringConvertable, ChoiceList {

	private static final Logger logger = Logger.getLogger(DropDownType.class.getPackage().getName());

	public static final DropDownType DROPDOWN = new NormalDropDownType();

	public static final DropDownType DOMAIN_KEY_VALUE = new DomainKeyValueType();

	public static final DropDownType STATUS_LIST_TYPE = new StatusListType();

	public static final DropDownType DBOBJECTS_LIST_TYPE = new DBObjectsListType();

	public static final StringEncoder.Converter<DropDownType> dropdownTypeConverter = new Converter<DropDownType>(DropDownType.class) {

		@Override
		public DropDownType convertFromString(String value) {
			return get(value);
		}

		@Override
		public String convertToString(DropDownType value) {
			return value.getName();
		}

	};

	public static class NormalDropDownType extends DropDownType {
		NormalDropDownType() {
		}

		@Override
		public String getName() {
			return "DropDown";
		}
	}

	public static class DomainKeyValueType extends DropDownType {
		DomainKeyValueType() {
		}

		@Override
		public String getName() {
			return "DomainKeyValue";
		}
	}

	public static class StatusListType extends DropDownType {
		StatusListType() {
		}

		@Override
		public String getName() {
			return "StatusList";
		}
	}

	public static class DBObjectsListType extends DropDownType {
		DBObjectsListType() {
		}

		@Override
		public String getName() {
			return "List from DB";
		}
	}

	public abstract String getName();

	public static DropDownType get(String typeName) {
		for (Enumeration e = availableValues().elements(); e.hasMoreElements();) {
			DropDownType temp = (DropDownType) e.nextElement();
			if (temp.getName().equals(typeName)) {
				return temp;
			}
		}

		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find ListType named " + typeName);
		}
		return null;
	}

	public boolean isDBList() {
		return this == DBOBJECTS_LIST_TYPE;
	}

	public boolean isDKV() {
		return this == DOMAIN_KEY_VALUE;
	}

	public boolean isStatus() {
		return this == STATUS_LIST_TYPE;
	}

	public boolean isNormal() {
		return this == DROPDOWN;
	}

	private static final Vector<DropDownType> _availableValues = new Vector<DropDownType>();

	@Override
	public Vector getAvailableValues() {
		if (_availableValues.size() == 0) {
			_availableValues.add(DROPDOWN);
			_availableValues.add(DOMAIN_KEY_VALUE);
			_availableValues.add(STATUS_LIST_TYPE);
			_availableValues.add(DBOBJECTS_LIST_TYPE);
		}
		return _availableValues;
	}

	@Override
	public StringEncoder.Converter getConverter() {
		return dropdownTypeConverter;
	}

	public static Vector availableValues() {
		return DROPDOWN.getAvailableValues();
	}

}
