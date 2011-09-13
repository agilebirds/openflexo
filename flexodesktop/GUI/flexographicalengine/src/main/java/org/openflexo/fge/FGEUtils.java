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
package org.openflexo.fge;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.toolbox.ColorUtils;


public class FGEUtils {

	public static final double PHI = (Math.sqrt(5) + 1) / 2;

	public static final Color NICE_RED = new Color(255, 153, 153);
	public static final Color NICE_BLUE = new Color(153, 153, 255);
	public static final Color NICE_YELLOW = new Color(255, 255, 153);
	public static final Color NICE_PINK = new Color(255, 204, 255);
	public static final Color NICE_GREEN = new Color(153, 255, 153);
	public static final Color NICE_TURQUOISE = new Color(153, 255, 255);
	public static final Color NICE_ORANGE = new Color(255, 204, 102);

	public static final Color NICE_DARK_GREEN = new Color(0, 153, 51);
	public static final Color NICE_BROWN = new Color(186, 112, 0);
	public static final Color NICE_BORDEAU = new Color(153, 0, 51);

	/**
	 * Returns the color that has the best contrast ratio with the oppositeColor. The algorthm used is the one considered by the W3C for
	 * WCAG 2.0.
	 * 
	 * @param oppositeColor
	 *            - the opposite color to consider
	 * @param colors
	 *            - all colors amongst which to choose.
	 * @return the color the best contrast ratio compared to opposite color. See
	 *         http://www.w3.org/TR/2007/WD-WCAG20-TECHS-20070517/Overview.html#G18
	 */
	public static Color chooseBestColor(Color oppositeColor, Color... colors) {
		// int bestContrast = Integer.MIN_VALUE;
		double contrastRatio = 0;
		Color returned = null;
		for (Color c : colors) {
			/*
			 * int colorConstrastDiff = getColorConstrastDiff(oppositeColor,c); int colorBrightnessDiff =
			 * getColorBrightnessDiff(oppositeColor,c); int colorContrast = colorConstrastDiff+colorBrightnessDiff; if (colorContrast >
			 * bestContrast) { bestContrast = colorContrast; returned = c; }
			 */
			double d = ColorUtils.getContrastRatio(oppositeColor, c);
			if (d > contrastRatio) {
				contrastRatio = d;
				returned = c;
			}
		}
		return returned;
	}

	public static Color emphasizedColor(Color c) {
		if (c == null) {
			return c;
		}
		double l = ColorUtils.getRelativeLuminance(c);
		Color test = c, best = c;
		double ratio = 0, bestRatio = -1;
		int count = -1;
		if (l > 0.5) {
			do {
				test = test.darker();
				ratio = ColorUtils.getContrastRatio(test, c);
				if (ratio > bestRatio) {
					bestRatio = ratio;
					best = test;
				}
				count++;
			} while (ratio < 5 && count < 10);
		} else {
			test = new Color(test.getRed() == 0 ? 5 : test.getRed(), test.getGreen() == 0 ? 5 : test.getGreen(), test.getBlue() == 0 ? 5
					: test.getBlue());
			do {
				test = test.brighter();
				ratio = ColorUtils.getContrastRatio(test, c);
				if (ratio > bestRatio) {
					bestRatio = ratio;
					best = test;
				}
				count++;
			} while (ratio < 5 && count < 10);
		}
		return best;
	}

	/**
	 * Return flag indicating if supplied AffineTransform is valid (does not contain Infinity of NaN values)
	 * 
	 * @param at
	 * @return
	 */
	public static boolean checkAffineTransform(AffineTransform at) {
		if (!checkDoubleIsAValue(at.getScaleX())) {
			return false;
		}
		if (!checkDoubleIsAValue(at.getScaleY())) {
			return false;
		}
		if (!checkDoubleIsAValue(at.getTranslateX())) {
			return false;
		}
		if (!checkDoubleIsAValue(at.getTranslateY())) {
			return false;
		}
		if (!checkDoubleIsAValue(at.getShearX())) {
			return false;
		}
		if (!checkDoubleIsAValue(at.getShearY())) {
			return false;
		}
		if (!checkDoubleIsAValue(at.getScaleX())) {
			return false;
		}
		if (!checkDoubleIsAValue(at.getScaleX())) {
			return false;
		}
		return true;
	}

	public static boolean checkDoubleIsAValue(Double v) {
		return !(v.isInfinite() || v.isNaN());
	}

	public static final boolean doubleEquals(double v1, double v2) {
		return Math.abs(v1 - v2) < FGEGeometricObject.EPSILON;
	}

	public static final Color mergeColors(Color color1, Color color2) {
		return new Color((color1.getRed() + color2.getRed()) / 2, (color1.getGreen() + color2.getGreen()) / 2,
				(color1.getBlue() + color2.getBlue()) / 2);
	}

	public static double getSlope(FGEPoint p1, FGEPoint p2) {
		double x = p2.x - p1.x;
		double y = p2.y - p1.y;
		// GPO: In some rare cases, it seems that this returns -PI/2 instead of 3PI/2
		// Example: new FGESegment(0.05369127516778524,0.06451612903225806,0.053691275167785234,-0.5806451612903225)
		// FGESegment.getAngle() has been fixed since but if it re-appears, maybe we should uncomment the code below
		// Mainly, calling methods should take into account that this method can return a value -PI/2 or 3PI/2
		return Math.atan2(/* Math.abs(x)<EPSILON?0:x, Math.abs(y)<EPSILON?0:y */x, y) + Math.PI / 2;
	}

	public static void main(String[] args) {
		Color yellow = new Color(255, 255, 204);
		Color green = new Color(153, 255, 204);
		Color grey = new Color(192, 192, 192);
		System.err.println(emphasizedColor(yellow));
		System.err.println(emphasizedColor(green));
		System.err.println(emphasizedColor(grey));
	}

}
