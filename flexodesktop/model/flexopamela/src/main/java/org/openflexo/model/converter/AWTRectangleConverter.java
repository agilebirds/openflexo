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
package org.openflexo.model.converter;

import java.awt.Rectangle;

import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;

/**
 * @author gpolet
 * 
 */
public class AWTRectangleConverter extends Converter<Rectangle> {

	public static final AWTRectangleConverter instance = new AWTRectangleConverter();

	/**
	 * @param aClass
	 */
	public AWTRectangleConverter() {
		super(Rectangle.class);
	}

	/**
	 * Overrides convertFromString
	 * 
	 * @see org.openflexo.xmlcode.StringEncoder.Converter#convertFromString(java.lang.String)
	 */
	@Override
	public Rectangle convertFromString(String value, ModelFactory factory) {
		if (value == null) {
			return null;
		}
		Rectangle r = new Rectangle();
		int i = value.indexOf("x=");
		if (i > -1 && value.indexOf(',', i) > -1) {
			try {
				r.x = Integer.parseInt(value.substring(i + 2, value.indexOf(',', i)));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		i = value.indexOf("y=");
		if (i > -1 && value.indexOf(',', i) > -1) {
			try {
				r.y = Integer.parseInt(value.substring(i + 2, value.indexOf(',', i)));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		i = value.indexOf("width=");
		if (i > -1 && value.indexOf(',', i) > -1) {
			try {
				r.width = Integer.parseInt(value.substring(i + 6, value.indexOf(',', i)));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		i = value.indexOf("height=");
		if (i > -1 && value.indexOf(']', i) > -1) {
			try {
				r.height = Integer.parseInt(value.substring(i + 7, value.indexOf(']', i)));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return r;
	}

	/**
	 * Overrides convertToString
	 * 
	 * @see org.openflexo.xmlcode.StringEncoder.Converter#convertToString(java.lang.Object)
	 */
	@Override
	public String convertToString(Rectangle r) {
		return "[x=" + r.x + ",y=" + r.y + ",width=" + r.width + ",height=" + r.height + "]";
	}

}
