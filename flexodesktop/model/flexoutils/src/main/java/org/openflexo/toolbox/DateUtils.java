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
package org.openflexo.toolbox;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateUtils {
	private static final String[] ACCEPTABLEDATEFORMAT = { "d/M/y", "d/M/y H'h'm", "d/M/y H:m", "d-M-y", "d-M-y H'h'm", "d-M-y H:m",
			"M/d/y", "M/d/y H'h'm", "M/d/y H:m", "M-d-y", "M-d-y H'h'm", "M-d-y H:m" };

	/**
	 * Parse all specified String values and returns them as a date array. This method try to detect the date format to use and ensure the
	 * same will be used for all values. <br>
	 * If a common date format cannot be found, null is returned.
	 * 
	 * @param values
	 * @return all specified String values as Date, null if they cannot be converted.
	 */
	public static Date[] parseDate(String[] values) {
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.setLenient(false);
		ParsePosition pos = new ParsePosition(0);
		for (String pattern : ACCEPTABLEDATEFORMAT) {
			dateFormat.applyPattern(pattern);
			List<Date> results = new ArrayList<Date>();

			for (String value : values) {
				pos.setIndex(0);
				Date date = dateFormat.parse(value.trim(), pos);
				if (date == null || pos.getIndex() != value.length()) {
					break;
				}
				results.add(date);
			}

			if (results.size() == values.length) {
				return results.toArray(new Date[0]);
			}
		}

		return null;
	}

	public static Date parseDate(String value) {
		Date[] dates = parseDate(new String[] { value });
		if (dates != null && dates.length > 0) {
			return dates[0];
		}
		return null;
	}
}
