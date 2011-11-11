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
package org.netbeans.lib.cvsclient.response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;

/**
 * Sets the modification time of the next file sent to a specified time.
 * 
 * @author Robert Greig
 */
class ModTimeResponse implements Response {

	/**
	 * The formatter responsible for converting server dates to our own dates
	 */
	protected static final SimpleDateFormat dateFormatter;

	/**
	 * The way the server formats dates
	 */
	protected static final String SERVER_DATE_FORMAT = "dd MMM yyyy HH:mm:ss"; // NOI18N

	static {
		dateFormatter = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);
		dateFormatter.setTimeZone(Entry.getTimeZone());
	}

	/**
	 * Process the data for the response.
	 * 
	 * @param dis
	 *            the data inputstream allowing the client to read the server's response. Note that the actual response name has already
	 *            been read and the input stream is positioned just before the first argument, if any.
	 */
	@Override
	public void process(LoggedDataInputStream dis, ResponseServices services) throws ResponseException {
		try {
			String dateString = dis.readLine();

			// we assume the date is in GMT, this appears to be the case
			// We remove the ending because SimpleDateFormat does not parse
			// +xxxx, only GMT+xxxx and this avoid us having to do String
			// concat
			Date date = dateFormatter.parse(dateString.substring(0, dateString.length() - 6));
			if (date.getTime() < 0) {
				// now we're in trouble - see #18232 issue.
				// we need to adjust the modified time..
				// so that the resulting date.getTime() is not negative.
				// The problem occurs when the sent year has only 2 digits.
				// this happens with old versions of cvs.
				if (date.getYear() < 100 && date.getYear() >= 70) {
					date.setYear(date.getYear() + 1900);
				} else if (date.getYear() >= 0 && date.getYear() < 70) {
					date.setYear(date.getYear() + 2000);
				} else {
					date.setYear(2000 + date.getYear());
					// for values less than zero let's assume
					// that we need to substract the value from 2000
					/*                    throw new ResponseException(
					                       "Cannot adjust negative time value (" + dateString + ")", //NOI18N 
					                       ResponseException.getLocalMessage("ModTimeResponse.badDate", //NOI18N
					                                           new Object[] {dateString}));
					 */
				}
			}
			services.setNextFileDate(date);
		} catch (Exception e) {
			throw new ResponseException(e);
		}
	}

	/**
	 * Is this a terminal response, i.e. should reading of responses stop after this response. This is true for responses such as OK or an
	 * error response
	 */
	@Override
	public boolean isTerminalResponse() {
		return false;
	}
}
