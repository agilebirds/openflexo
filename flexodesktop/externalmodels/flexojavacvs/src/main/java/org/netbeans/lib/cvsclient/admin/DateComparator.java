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
package org.netbeans.lib.cvsclient.admin;

/**
 * A class for comparing the file's date and the CVS/Entries date.
 * 
 * @author Thomas Singer
 * @version Sep 29, 2001
 */
public class DateComparator {

	private static final long SECONDS_PER_HOUR = 3600;

	private static final DateComparator singleton = new DateComparator();

	/**
	 * Returns an instance of a DateComparator.
	 */
	public static DateComparator getInstance() {
		return singleton;
	}

	/**
	 * This class is a singleton. There is no need to subclass or instantiate it outside.
	 */
	private DateComparator() {
	}

	/**
	 * Compares the specified dates, whether they should be treated as equal. Returns true to indicate equality.
	 */
	public boolean equals(final long fileTime, final long entryTime) {
		final long fileTimeSeconds = fileTime / 1000;
		final long entryTimeSeconds = entryTime / 1000;
		final long difference = Math.abs(fileTimeSeconds - entryTimeSeconds);
		// differences smaller than 1 second are treated as equal to catch rounding errors
		if (difference < 1) {
			return true;
		}

		// 1-hour-differences (caused by daylight-saving) are treated as equal
		if (difference >= SECONDS_PER_HOUR - 1 && difference <= SECONDS_PER_HOUR + 1) {
			return true;
		}
		return false;
	}
}
