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

/**
 * @author gpolet
 * 
 */
public class PListHelper {
	public static int getInteger(Object o) throws NullPointerException, NumberFormatException {
		if (o == null) {
			throw new NullPointerException();
		}
		if (o instanceof String) {
			return Integer.parseInt((String) o);
		} else if (o instanceof Number) {
			return ((Number) o).intValue();
		} else {
			System.err.println("Don't know how to convert from " + o.getClass().getName() + " to int");
			return 0;
		}
	}

	public static boolean getBoolean(Object o) throws NullPointerException {
		if (o == null) {
			throw new NullPointerException();
		}
		if (o instanceof String) {
			String s = ((String) o).toLowerCase();
			if (s.equals("true") || s.equals("y") || s.equals("yes")) {
				return true;
			} else {
				return false;
			}
		} else if (o instanceof Boolean) {
			return ((Boolean) o).booleanValue();
		} else {
			System.err.println("Don't know how to convert from " + o.getClass().getName() + " to boolean");
			return false;
		}
	}

	public static Object getObject(Boolean o) throws NullPointerException {
		if (o == null) {
			throw new NullPointerException();
		}
		if (o) {
			return "Y";
		} else {
			return "N";
		}
	}

	public static Object getObject(Integer o) throws NullPointerException {
		if (o == null) {
			throw new NullPointerException();
		}
		return String.valueOf(o);
	}
}
