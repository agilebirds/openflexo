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
package org.openflexo.dg.pptx;

/**
 * MOS
 * @author MOSTAFA
 * TODO_MOS
 */

import java.awt.Color;

import org.openflexo.toolbox.ToolBox;
import org.openflexo.toolbox.ColorUtils.LAB;
import org.openflexo.toolbox.ColorUtils.RGB;


public class PptxConstants {

	public enum HighlightColorValues {
		Black,
		Blue,
		Cyan,
		DarkBlue,
		DarkCyan,
		DarkGray,
		DarkGreen,
		DarkMagenta,
		DarkRed,
		DarkYellow,
		Green,
		LightGray,
		Magenta,
		Red,
		White,
		Yellow;

		@Override
		public String toString() {
			return ToolBox.uncapitalize(name());
		};

		public Color getMatchingColor() {
			switch (this) {
			case Blue:
				return Color.BLUE;
			case Black:
				return Color.BLACK;
			case Cyan:
				return Color.CYAN;
			case DarkBlue:
				return Color.BLUE.darker();
			case DarkCyan:
				return Color.CYAN.darker();
			case DarkGray:
				return Color.GRAY.darker();
			case DarkGreen:
				return Color.GREEN.darker();
			case DarkMagenta:
				return Color.MAGENTA.darker();
			case DarkRed:
				return Color.RED.darker();
			case DarkYellow:
				return Color.YELLOW.darker();
			case Green:
				return Color.green;
			case LightGray:
				return Color.LIGHT_GRAY;
			case Magenta:
				return Color.MAGENTA;
			case Red:
				return Color.RED;
			case White:
				return Color.WHITE;
			case Yellow:
				return Color.YELLOW;
			}
			return Color.YELLOW;
		}

		public RGB getMatchingRGB() {
			return new RGB(getMatchingColor());
		}

		public static HighlightColorValues findClosestColor(Color color) {
			HighlightColorValues returned = null;
			double min = Double.MAX_VALUE;
			RGB rgb = new RGB(color);
			LAB lab = rgb.toLAB();
			for (HighlightColorValues value:values()) {
				LAB lab2 = value.getMatchingRGB().toLAB();
				double test = Math.abs(lab.computeDiff(lab2));
				if (returned==null || test<min) {
					returned = value;
					min = test;
				}
			}
			return returned;
		}

	}

	public static void main(String[] args) {
		System.out.println(HighlightColorValues.findClosestColor(new Color(250,250,250)));
	}
}
