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
package org.openflexo.foundation.wkf.node;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Represents type of action node
 * 
 * @author sguerin
 * 
 */
public abstract class ActionType extends FlexoObject implements StringConvertable, ChoiceList, Serializable {

	private static final Logger logger = Logger.getLogger(ActionType.class.getPackage().getName());

	// protected static final ImageIcon FLEXO_ACTION_ICON = new ImageIcon((new
	// FileResource("Resources/WKF/FlexoActionNew.gif")).getAbsolutePath());

	public static final ActionType FLEXO_ACTION = new FlexoActionType();

	public static final ActionType DISPLAY_ACTION = new DisplayActionType();

	/*
	* public static final ActionType MAIL_IN = new MailInActionType(); public
	* static final ActionType MAIL_OUT = new MailOutActionType(); public static
	* final ActionType TIMER_IN = new TimerInActionType(); public static final
	* ActionType TIMER_OUT = new TimerOutActionType(); public static final
	* ActionType ERROR_IN = new ErrorInActionType(); public static final
	* ActionType ERROR_OUT = new ErrorOutActionType(); public static final
	* ActionType CANCEL_IN = new CancelInActionType(); public static final
	* ActionType CANCEL_OUT = new CancelOutActionType();
	*/

	public static final StringEncoder.Converter<ActionType> actionTypeConverter = new Converter<ActionType>(ActionType.class) {

		@Override
		public ActionType convertFromString(String value) {
			return get(value);
		}

		@Override
		public String convertToString(ActionType value) {
			return value.getName();
		}

	};

	public static class FlexoActionType extends ActionType {
		FlexoActionType() {
		}

		@Override
		public String getName() {
			return "FLEXO_ACTION";
		}

		@Override
		public ImageIcon getImageIcon() {
			return null;// FLEXO_ACTION_ICON;
		}
	}

	public static class DisplayActionType extends ActionType {

		DisplayActionType() {
		}

		@Override
		public String getName() {
			return "DISPLAY_ACTION";
		}

		@Override
		public ImageIcon getImageIcon() {
			return null;// DISPLAY_ICON;
		}
	}

	public abstract ImageIcon getImageIcon();

	public abstract String getName();

	public static ActionType get(String typeName) {
		for (Enumeration e = availableValues().elements(); e.hasMoreElements();) {
			ActionType temp = (ActionType) e.nextElement();
			if (temp.getName().equals(typeName)) {
				return temp;
			}
		}
		if ("NEXT_PAGE".equals(typeName)) {
			return DISPLAY_ACTION;
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find ActionType named " + typeName);
		}
		return null;
	}

	private Vector<ActionType> _availableValues = null;

	@Override
	public Vector getAvailableValues() {
		if (_availableValues == null) {
			_availableValues = new Vector<ActionType>();
			_availableValues.add(FLEXO_ACTION);
			_availableValues.add(DISPLAY_ACTION);
			/*
			 * _availableValues.add(MAIL_IN); _availableValues.add(MAIL_OUT);
			 * _availableValues.add(TIMER_IN); _availableValues.add(TIMER_OUT);
			 * _availableValues.add(ERROR_IN); _availableValues.add(ERROR_OUT);
			 * _availableValues.add(CANCEL_IN);
			 * _availableValues.add(CANCEL_OUT);
			 */
		}
		return _availableValues;
	}

	@Override
	public StringEncoder.Converter getConverter() {
		return actionTypeConverter;
	}

	public static Vector availableValues() {
		return FLEXO_ACTION.getAvailableValues();
	}

}
