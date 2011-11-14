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
package org.openflexo.foundation.dm.eo;

import java.util.Enumeration;
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
 * Represents the delete rule type for a relationship
 * 
 * @author sguerin
 * 
 */
public abstract class DeleteRuleType extends FlexoObject implements StringConvertable, ChoiceList {

	private static final Logger logger = Logger.getLogger(DeleteRuleType.class.getPackage().getName());

	private static final String DeleteRuleNullifyString = "EODeleteRuleNullify";

	private static final String DeleteRuleCascadeString = "EODeleteRuleCascade";

	private static final String DeleteRuleDenyString = "EODeleteRuleDeny";

	private static final String DeleteRuleNoActionString = "EODeleteRuleNoAction";

	public static final DeleteRuleType NULLIFY = new DeleteRuleNullify();

	public static final DeleteRuleType CASCADE = new DeleteRuleCascade();

	public static final DeleteRuleType DENY = new DeleteRuleDeny();

	public static final DeleteRuleType NO_ACTION = new DeleteRuleNoAction();

	public static final StringEncoder.Converter<DeleteRuleType> deleteRuleTypeConverter = new Converter<DeleteRuleType>(
			DeleteRuleType.class) {

		@Override
		public DeleteRuleType convertFromString(String value) {
			return get(value);
		}

		@Override
		public String convertToString(DeleteRuleType value) {
			return value.getName();
		}

	};

	private static class DeleteRuleNullify extends DeleteRuleType {
		DeleteRuleNullify() {
			super();
		}

		@Override
		public String getName() {
			return "nullify";
		}

		@Override
		public String getEOCode() {
			return DeleteRuleNullifyString;
		}
	}

	private static class DeleteRuleCascade extends DeleteRuleType {

		DeleteRuleCascade() {
			super();
		}

		@Override
		public String getName() {
			return "cascade_delete";
		}

		@Override
		public String getEOCode() {
			return DeleteRuleCascadeString;
		}
	}

	private static class DeleteRuleDeny extends DeleteRuleType {
		DeleteRuleDeny() {
			super();
		}

		@Override
		public String getName() {
			return "deny";
		}

		@Override
		public String getEOCode() {
			return DeleteRuleDenyString;
		}
	}

	private static class DeleteRuleNoAction extends DeleteRuleType {
		DeleteRuleNoAction() {
			super();
		}

		@Override
		public String getName() {
			return "no_action";
		}

		@Override
		public String getEOCode() {
			return DeleteRuleNoActionString;
		}
	}

	public abstract String getName();

	public abstract String getEOCode();

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	public static DeleteRuleType get(String deleteRuleName) {
		if (deleteRuleName == null) {
			return null;
		}
		for (Enumeration e = NULLIFY.getAvailableValues().elements(); e.hasMoreElements();) {
			DeleteRuleType temp = (DeleteRuleType) e.nextElement();
			if (temp.getName().equals(deleteRuleName)) {
				return temp;
			}
		}

		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find DeleteRule named " + deleteRuleName);
		}
		return null;
	}

	public static DeleteRuleType getDeleteRule(String deleteRuleEOEcode) {
		if (deleteRuleEOEcode == null) {
			return null;
		}
		for (Enumeration e = NULLIFY.getAvailableValues().elements(); e.hasMoreElements();) {
			DeleteRuleType temp = (DeleteRuleType) e.nextElement();
			if (temp.getEOCode().equals(deleteRuleEOEcode)) {
				return temp;
			}
		}

		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find DeleteRule coded as " + deleteRuleEOEcode);
		}
		return null;
	}

	private Vector<DeleteRuleType> _availableValues = null;

	@Override
	public Vector getAvailableValues() {
		if (_availableValues == null) {
			_availableValues = new Vector<DeleteRuleType>();
			_availableValues.add(NULLIFY);
			_availableValues.add(CASCADE);
			_availableValues.add(DENY);
			_availableValues.add(NO_ACTION);
		}
		return _availableValues;
	}

	@Override
	public StringEncoder.Converter getConverter() {
		return deleteRuleTypeConverter;
	}

	public static Vector availableValues() {
		return NULLIFY.getAvailableValues();
	}

}
