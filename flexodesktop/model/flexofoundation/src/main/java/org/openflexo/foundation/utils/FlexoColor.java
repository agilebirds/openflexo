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
package org.openflexo.foundation.utils;

import java.awt.Color;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FlexoColor extends Color implements StringConvertable {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FlexoColor.class.getPackage().getName());

	public static final StringEncoder.Converter<FlexoColor> flexoColorConverter = new Converter<FlexoColor>(FlexoColor.class) {

		@Override
		public FlexoColor convertFromString(String value) {
			return stringToColor(value);
		}

		@Override
		public String convertToString(FlexoColor value) {
			return value.toString();
		}

	};

	private static final Vector<FlexoColor> COLOR_SET;

	public static final int RAW_COLOR_VALUES[] = { 255, 255, 255, 204, 255, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204,
			255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 255, 204, 255, 255, 204, 204, 255, 204, 204,
			255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 255, 204, 204,
			255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255, 204, 204, 255,
			204, 204, 204, 204, 153, 255, 255, 153, 204, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255,
			153, 153, 255, 153, 153, 255, 204, 153, 255, 255, 153, 255, 255, 153, 204, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255,
			153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 204, 153, 255, 255, 153, 204, 255, 153, 153, 255, 153, 153, 255,
			153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 153, 153, 255, 204, 204, 204, 204, 102, 255, 255,
			102, 204, 255, 102, 153, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 153, 102, 255, 204,
			102, 255, 255, 102, 255, 255, 102, 204, 255, 102, 153, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 102,
			102, 255, 153, 102, 255, 204, 102, 255, 255, 102, 204, 255, 102, 153, 255, 102, 102, 255, 102, 102, 255, 102, 102, 255, 102,
			102, 255, 102, 102, 255, 102, 102, 255, 153, 102, 255, 204, 153, 153, 153, 51, 255, 255, 51, 204, 255, 51, 153, 255, 51, 102,
			255, 51, 51, 255, 51, 51, 255, 51, 51, 255, 102, 51, 255, 153, 51, 255, 204, 51, 255, 255, 51, 255, 255, 51, 204, 255, 51, 153,
			255, 51, 102, 255, 51, 51, 255, 51, 51, 255, 51, 51, 255, 102, 51, 255, 153, 51, 255, 204, 51, 255, 255, 51, 204, 255, 51, 153,
			255, 51, 102, 255, 51, 51, 255, 51, 51, 255, 51, 51, 255, 51, 51, 255, 102, 51, 255, 153, 51, 255, 204, 153, 153, 153, 0, 255,
			255, 0, 204, 255, 0, 153, 255, 0, 102, 255, 0, 51, 255, 0, 0, 255, 51, 0, 255, 102, 0, 255, 153, 0, 255, 204, 0, 255, 255, 0,
			255, 255, 0, 204, 255, 0, 153, 255, 0, 102, 255, 0, 51, 255, 0, 0, 255, 51, 0, 255, 102, 0, 255, 153, 0, 255, 204, 0, 255, 255,
			0, 204, 255, 0, 153, 255, 0, 102, 255, 0, 51, 255, 0, 0, 255, 0, 0, 255, 51, 0, 255, 102, 0, 255, 153, 0, 255, 204, 102, 102,
			102, 0, 204, 204, 0, 204, 204, 0, 153, 204, 0, 102, 204, 0, 51, 204, 0, 0, 204, 51, 0, 204, 102, 0, 204, 153, 0, 204, 204, 0,
			204, 204, 0, 204, 204, 0, 204, 204, 0, 153, 204, 0, 102, 204, 0, 51, 204, 0, 0, 204, 51, 0, 204, 102, 0, 204, 153, 0, 204, 204,
			0, 204, 204, 0, 204, 204, 0, 153, 204, 0, 102, 204, 0, 51, 204, 0, 0, 204, 0, 0, 204, 51, 0, 204, 102, 0, 204, 153, 0, 204,
			204, 102, 102, 102, 0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 102, 153, 0, 51, 153, 0, 0, 153, 51, 0, 153, 102, 0, 153, 153, 0,
			153, 153, 0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 102, 153, 0, 51, 153, 0, 0, 153, 51, 0, 153, 102, 0, 153, 153,
			0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 153, 153, 0, 102, 153, 0, 51, 153, 0, 0, 153, 0, 0, 153, 51, 0, 153, 102, 0, 153,
			153, 0, 153, 153, 51, 51, 51, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 51, 102, 0, 0, 102, 51, 0, 102, 102, 0,
			102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 51, 102, 0, 0, 102, 51, 0, 102, 102,
			0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 102, 102, 0, 51, 102, 0, 0, 102, 0, 0, 102, 51, 0, 102,
			102, 0, 102, 102, 0, 102, 102, 0, 0, 0, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 0, 51, 51, 0, 51, 51, 0, 51,
			51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51,
			0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 0, 51, 0, 0, 51, 51, 0, 51, 51, 0, 51, 51, 0, 51, 51, 51, 51, 51 };

	static {
		COLOR_SET = new Vector<FlexoColor>();
		int i = RAW_COLOR_VALUES.length / 3;
		for (int j = 0; j < i; j++)
			COLOR_SET.add(new FlexoColor(RAW_COLOR_VALUES[j * 3], RAW_COLOR_VALUES[j * 3 + 1], RAW_COLOR_VALUES[j * 3 + 2]));
	}

	public static final FlexoColor BLACK_COLOR = new FlexoColor(Color.BLACK);

	public static final FlexoColor GRAY_COLOR = new FlexoColor(Color.GRAY);

	public static final FlexoColor WHITE_COLOR = new FlexoColor(Color.WHITE);

	public static final FlexoColor LIGHT_GRAY_COLOR = new FlexoColor(new Color(230, 230, 230));

	public FlexoColor(String s) {
		super(redFromString(s), greenFromString(s), blueFromString(s));
	}

	public FlexoColor(int r, int g, int b) {
		super(r, g, b);
	}

	public FlexoColor(Color aColor) {
		super(aColor.getRed(), aColor.getGreen(), aColor.getBlue());
	}

	public int sum() {
		return getRed() + getGreen() + getBlue();
	}

	@Override
	public String toString() {
		return getRed() + "," + getGreen() + "," + getBlue();
	}

	@Override
	public StringEncoder.Converter getConverter() {
		return flexoColorConverter;
	}

	public static FlexoColor stringToColor(String s) {
		return new FlexoColor(s);
	}

	public static FlexoColor getRandomColor(Vector<Color> excludedColors) {
		Vector<FlexoColor> colors = (Vector<FlexoColor>) COLOR_SET.clone();
		colors.removeAll(excludedColors);
		int i = new Random().nextInt(colors.size());
		return colors.get(i);
	}

	public static FlexoColor getColor(int index) {
		Vector<FlexoColor> colors = (Vector<FlexoColor>) COLOR_SET.clone();
		return colors.get(index);
	}

	private static int redFromString(String s) {
		return Integer.parseInt(s.substring(0, s.indexOf(",")));
	}

	private static int greenFromString(String s) {
		return Integer.parseInt(s.substring(s.indexOf(",") + 1, s.lastIndexOf(",")));
	}

	private static int blueFromString(String s) {
		return Integer.parseInt(s.substring(s.lastIndexOf(",") + 1));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Color) {
			Color o1 = (Color) obj;
			return o1.getBlue() == getBlue() && o1.getRed() == getRed() && o1.getGreen() == getGreen();
		}
		return super.equals(obj);
	}
}
