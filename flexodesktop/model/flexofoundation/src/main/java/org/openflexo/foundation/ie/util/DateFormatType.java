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

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Represents type of an hyperlink
 * 
 * @author sguerin
 * 
 */
public abstract class DateFormatType extends FlexoObject implements StringConvertable, ChoiceList, Serializable {

	private static final Logger logger = Logger.getLogger(DateFormatType.class.getPackage().getName());

	public static final DateFormatType EUDEFAULT = new EUDefaultDateFormatType();
	public static final DateFormatType DEFAULT = new DefaultDateFormatType();
	public static final DateFormatType LONG = new LongDateFormatType();
	public static final DateFormatType SHORT = new ShortDateFormatType();
	public static final DateFormatType DAYLONG = new DayLongDateFormatType();
	public static final DateFormatType DAYSHORT = new DayShortDateFormatType();
	public static final DateFormatType DOTSHORT = new DotShortDateFormatType();
	public static final DateFormatType LONG2 = new Long2DateFormatType();
	public static final DateFormatType SHORT2 = new Short2DateFormatType();
	public static final DateFormatType DAYLONG2 = new DayLong2DateFormatType();
	public static final DateFormatType DAYSHORT2 = new DayShort2DateFormatType();
	public static final DateFormatType DEFAULTSHORT = new DefaultShortFormatType();
	public static final DateFormatType HOURS = new HoursFormatType();
	public static final DateFormatType HOURSAMPM = new HoursAMPMFormatType();
	public static final DateFormatType HOURSSHORT = new HoursShortFormatType();
	public static final DateFormatType HOURSSHORTAMPM = new HoursShortAMPMFormatType();

	public static final StringEncoder.Converter<DateFormatType> DateFormatTypeConverter = new Converter<DateFormatType>(
			DateFormatType.class) {

		@Override
		public DateFormatType convertFromString(String value) {
			return get(value);
		}

		@Override
		public String convertToString(DateFormatType value) {
			return value.getName();
		}

	};

	public static class EUDefaultDateFormatType extends DateFormatType {
		EUDefaultDateFormatType() {
		}

		@Override
		public String getName() {
			return "%d/%m/%y";
		}

		@Override
		public String getDisplayString() {
			return "14/01/05";
		}

		/**
		 * Overrides getJavaPattern
		 * 
		 * @see org.openflexo.foundation.ie.util.DateFormatType#getJavaPattern()
		 */
		@Override
		public String getJavaPattern() {
			return "d/M/yy";
		}
	}

	public static class DefaultDateFormatType extends DateFormatType {
		DefaultDateFormatType() {
		}

		@Override
		public String getName() {
			return "%m/%d/%y";
		}

		@Override
		public String getDisplayString() {
			return "01/14/05";
		}

		/**
		 * Overrides getJavaPattern
		 * 
		 * @see org.openflexo.foundation.ie.util.DateFormatType#getJavaPattern()
		 */
		@Override
		public String getJavaPattern() {
			return "M/d/yy";
		}
	}

	public static class LongDateFormatType extends DateFormatType {
		LongDateFormatType() {
		}

		@Override
		public String getName() {
			return "%B %d, %Y";
		}

		@Override
		public String getDisplayString() {
			return "January 14, 2005";
		}

		/**
		 * Overrides getJavaPattern
		 * 
		 * @see org.openflexo.foundation.ie.util.DateFormatType#getJavaPattern()
		 */
		@Override
		public String getJavaPattern() {
			return "MMMMM d, yyyy";
		}
	}

	public static class ShortDateFormatType extends DateFormatType {
		ShortDateFormatType() {
		}

		@Override
		public String getName() {
			return "%b %d, %Y";
		}

		@Override
		public String getDisplayString() {
			return "Jan 14, 2005";
		}

		/**
		 * Overrides getJavaPattern
		 * 
		 * @see org.openflexo.foundation.ie.util.DateFormatType#getJavaPattern()
		 */
		@Override
		public String getJavaPattern() {
			return "MMM d, yyyy";
		}
	}

	public static class DayLongDateFormatType extends DateFormatType {
		DayLongDateFormatType() {
		}

		@Override
		public String getName() {
			return "%A, %B %d, %Y";
		}

		@Override
		public String getDisplayString() {
			return "Friday, January 14, 2005";
		}

		/**
		 * Overrides getJavaPattern
		 * 
		 * @see org.openflexo.foundation.ie.util.DateFormatType#getJavaPattern()
		 */
		@Override
		public String getJavaPattern() {
			return "EEEE, MMMMM, yyyy";
		}
	}

	public static class DayShortDateFormatType extends DateFormatType {
		DayShortDateFormatType() {
		}

		@Override
		public String getName() {
			return "%A, %b %d, %Y";
		}

		@Override
		public String getDisplayString() {
			return "Friday, Jan 14, 2005";
		}

		/**
		 * Overrides getJavaPattern
		 * 
		 * @see org.openflexo.foundation.ie.util.DateFormatType#getJavaPattern()
		 */
		@Override
		public String getJavaPattern() {
			return "EEEE, MMM d, yyyy";
		}
	}

	public static class DotShortDateFormatType extends DateFormatType {
		DotShortDateFormatType() {
		}

		@Override
		public String getName() {
			return "%d.%m.%y";
		}

		@Override
		public String getDisplayString() {
			return "14.01.05";
		}

		/**
		 * Overrides getJavaPattern
		 * 
		 * @see org.openflexo.foundation.ie.util.DateFormatType#getJavaPattern()
		 */
		@Override
		public String getJavaPattern() {
			return "d.M.yy";
		}
	}

	public static class Long2DateFormatType extends DateFormatType {
		Long2DateFormatType() {
		}

		@Override
		public String getName() {
			return "%d %B %Y";
		}

		@Override
		public String getDisplayString() {
			return "14 January 2005";
		}

		/**
		 * Overrides getJavaPattern
		 * 
		 * @see org.openflexo.foundation.ie.util.DateFormatType#getJavaPattern()
		 */
		@Override
		public String getJavaPattern() {
			return "d MMMMM yyyy";
		}
	}

	public static class Short2DateFormatType extends DateFormatType {
		Short2DateFormatType() {
		}

		@Override
		public String getName() {
			return "%d %b %Y";
		}

		@Override
		public String getDisplayString() {
			return "14 Jan 2005";
		}

		/**
		 * Overrides getJavaPattern
		 * 
		 * @see org.openflexo.foundation.ie.util.DateFormatType#getJavaPattern()
		 */
		@Override
		public String getJavaPattern() {
			return "d MMM yyyy";
		}
	}

	public static class DayLong2DateFormatType extends DateFormatType {
		DayLong2DateFormatType() {
		}

		@Override
		public String getName() {
			return "%A %d %B %Y";
		}

		@Override
		public String getDisplayString() {
			return "Friday 14 January 2005";
		}

		/**
		 * Overrides getJavaPattern
		 * 
		 * @see org.openflexo.foundation.ie.util.DateFormatType#getJavaPattern()
		 */
		@Override
		public String getJavaPattern() {
			return "EEEE d MMMMM yyyy";
		}
	}

	public static class DayShort2DateFormatType extends DateFormatType {
		DayShort2DateFormatType() {
		}

		@Override
		public String getName() {
			return "%A %d %b %Y";
		}

		@Override
		public String getDisplayString() {
			return "Friday 14 Jan 2005";
		}

		/**
		 * Overrides getJavaPattern
		 * 
		 * @see org.openflexo.foundation.ie.util.DateFormatType#getJavaPattern()
		 */
		@Override
		public String getJavaPattern() {
			return "EEEE d MMM yyyy";
		}
	}

	public static class DefaultShortFormatType extends DateFormatType {
		DefaultShortFormatType() {
		}

		@Override
		public String getName() {
			return "%x";
		}

		@Override
		public String getDisplayString() {
			return "Fri Jan 14 2005";
		}

		/**
		 * Overrides getJavaPattern
		 * 
		 * @see org.openflexo.foundation.ie.util.DateFormatType#getJavaPattern()
		 */
		@Override
		public String getJavaPattern() {
			return "EEE MMM d yyyy";
		}
	}

	public static class HoursFormatType extends DateFormatType {
		HoursFormatType() {
		}

		@Override
		public String getName() {
			return "%H:%M:%S";
		}

		@Override
		public String getDisplayString() {
			return "16:45:12";
		}

		/**
		 * Overrides getJavaPattern
		 * 
		 * @see org.openflexo.foundation.ie.util.DateFormatType#getJavaPattern()
		 */
		@Override
		public String getJavaPattern() {
			return "H:m:s";
		}
	}

	public static class HoursAMPMFormatType extends DateFormatType {
		HoursAMPMFormatType() {
		}

		@Override
		public String getName() {
			return "%I:%M:%S %p";
		}

		@Override
		public String getDisplayString() {
			return "04:45:12 PM";
		}

		/**
		 * Overrides getJavaPattern
		 * 
		 * @see org.openflexo.foundation.ie.util.DateFormatType#getJavaPattern()
		 */
		@Override
		public String getJavaPattern() {
			return "h:m:s a";
		}
	}

	public static class HoursShortFormatType extends DateFormatType {
		HoursShortFormatType() {
		}

		@Override
		public String getName() {
			return "%H:%M";
		}

		@Override
		public String getDisplayString() {
			return "16:45";
		}

		/**
		 * Overrides getJavaPattern
		 * 
		 * @see org.openflexo.foundation.ie.util.DateFormatType#getJavaPattern()
		 */
		@Override
		public String getJavaPattern() {
			return "H:m";
		}
	}

	public static class HoursShortAMPMFormatType extends DateFormatType {
		HoursShortAMPMFormatType() {
		}

		@Override
		public String getName() {
			return "%I:%M %p";
		}

		@Override
		public String getDisplayString() {
			return "04:45 PM";
		}

		/**
		 * Overrides getJavaPattern
		 * 
		 * @see org.openflexo.foundation.ie.util.DateFormatType#getJavaPattern()
		 */
		@Override
		public String getJavaPattern() {
			return "h:m a";
		}
	}

	public abstract String getName();

	public abstract String getDisplayString();

	public abstract String getJavaPattern();

	public static DateFormatType get(String typeName) {
		for (Enumeration e = availableValues().elements(); e.hasMoreElements();) {
			DateFormatType temp = (DateFormatType) e.nextElement();
			if (temp.getName().equals(typeName)) {
				return temp;
			}
		}

		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find ClientSideEventType named " + typeName);
		}
		return null;
	}

	private Vector<DateFormatType> _availableValues = null;

	@Override
	public Vector getAvailableValues() {
		if (_availableValues == null) {
			_availableValues = new Vector<DateFormatType>();
			_availableValues.add(EUDEFAULT);
			_availableValues.add(DEFAULT);
			_availableValues.add(LONG);
			_availableValues.add(SHORT);
			_availableValues.add(DAYLONG);
			_availableValues.add(DAYSHORT);
			_availableValues.add(DOTSHORT);
			_availableValues.add(LONG2);
			_availableValues.add(SHORT2);
			_availableValues.add(DAYLONG2);
			_availableValues.add(DAYSHORT2);
			_availableValues.add(DEFAULTSHORT);
			_availableValues.add(HOURS);
			_availableValues.add(HOURSAMPM);
			_availableValues.add(HOURSSHORT);
			_availableValues.add(HOURSSHORTAMPM);
		}
		return _availableValues;
	}

	@Override
	public StringEncoder.Converter getConverter() {
		return DateFormatTypeConverter;
	}

	public static Vector availableValues() {
		return DEFAULT.getAvailableValues();
	}

}
