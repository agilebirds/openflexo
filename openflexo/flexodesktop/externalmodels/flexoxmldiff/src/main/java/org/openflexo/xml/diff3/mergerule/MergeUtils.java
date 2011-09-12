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
package org.openflexo.xml.diff3.mergerule;

import java.awt.Point;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;


public class MergeUtils {

	

        /** Specify date format */
        protected static final String _dateFormat = new SimpleDateFormat().toPattern();


        public static Date dateFromString(String value)
        {
            try {
                StringTokenizer st = new StringTokenizer(value, ",");
                String dateFormat = _dateFormat;
                String dateAsString = null;
                if (st.hasMoreTokens()) {
                    dateFormat = st.nextToken();
                }
                if (st.hasMoreTokens()) {
                    dateAsString = st.nextToken();
                }
                if (dateAsString != null) {
                    return new SimpleDateFormat(dateFormat).parse(dateAsString);
                }
            } catch (ParseException e) {
            }
            SimpleDateFormat formatter = new SimpleDateFormat(_dateFormat);
            Date currentTime = new Date();
            String dateString = formatter.format(currentTime);
            System.err.println("Supplied value is not parsable as a date. " + " Date format should be for example " + dateString);
            return null;
        }

        public static String dateToString(Date value)
        {
            Date date = value;
            if (date != null) {
                return _dateFormat + "," + new SimpleDateFormat(_dateFormat).format(date);
            } else {
                return null;
            }
        }

        /**
         * Return a string representation of a date, according to valid date
         * format
         */
        public static String getDateRepresentation(Date date)
        {
            if (date != null) {
                return new SimpleDateFormat(_dateFormat).format(date);
            } else {
                return null;
            }
        }

		public static Point pointFromString(String value1) {
			// TODO Auto-generated method stub
			return new Point(Integer.valueOf(value1.substring(0, value1.indexOf(","))),Integer.valueOf(value1.substring(value1.indexOf(",")+1)));
		}
		public static String pointToString(Point p) {
			return p.x+","+p.y;
		}

}
