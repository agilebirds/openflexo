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

import java.awt.Color;
import java.util.HashMap;

public class ColorUtils {
	public static class RGB {
		public int r = 0;
		public int g = 0;
		public int b = 0;

		public RGB() {
		}

		public RGB(Color color) {
			r = color.getRed();
			g = color.getGreen();
			b = color.getBlue();
		}

		public double getRelativeLuminance() {
			double rsRGB = (double) r / 255;
			double gsRGB = (double) g / 255;
			double bsRGB = (double) g / 255;
			double r, g, b;
			if (rsRGB <= 0.03928) {
				r = rsRGB / 12.92;
			} else {
				r = Math.pow((rsRGB + 0.055) / 1.055, 2.4);
			}
			if (gsRGB <= 0.03928) {
				g = gsRGB / 12.92;
			} else {
				g = Math.pow((gsRGB + 0.055) / 1.055, 2.4);
			}
			if (bsRGB <= 0.03928) {
				b = bsRGB / 12.92;
			} else {
				b = Math.pow((bsRGB + 0.055) / 1.055, 2.4);
			}
			return 0.2126 * r + 0.7152 * g + 0.0722 * b;
		}

		@Override
		public String toString() {
			return "r=" + r + ",g=" + g + ",b=" + b;
		}

		public XYZ toXYZ() {
			XYZ xyz = new XYZ();
			double red, green, blue;
			red = (double) r / 255;
			green = (double) g / 255;
			blue = (double) b / 255;

			// adjusting values
			if (red > 0.04045) {
				red = (red + 0.055) / 1.055;
				red = Math.pow(red, 2.4);
			} else {
				red = red / 12.92;
			}
			if (green > 0.04045) {
				green = (green + 0.055) / 1.055;
				green = Math.pow(green, 2.4);
			} else {
				green = green / 12.92;
			}
			if (blue > 0.04045) {
				blue = (blue + 0.055) / 1.055;
				blue = Math.pow(blue, 2.4);
			} else {
				blue = blue / 12.92;
			}

			red *= 100;
			green *= 100;
			blue *= 100;

			// applying the matrix
			xyz.x = red * 0.4124 + green * 0.3576 + blue * 0.1805;
			xyz.y = red * 0.2126 + green * 0.7152 + blue * 0.0722;
			xyz.z = red * 0.0193 + green * 0.1192 + blue * 0.9505;

			return xyz;
		}

		public LAB toLAB() {
			return toXYZ().toLAB();
		}
	}

	public static class XYZ {
		public double x = 0;
		public double y = 0;
		public double z = 0;

		public LAB toLAB() {
			LAB lab = new LAB();
			double _x, _y, _z;
			_x = x / 95.047;
			_y = y / 100;
			_z = z / 108.883;

			// adjusting the values
			if (_x > 0.008856) {
				_x = Math.pow(_x, 1.0 / 3);
			} else {
				_x = 7.787 * _x + 16 / 116;
			}
			if (_y > 0.008856) {
				_y = Math.pow(_y, 1.0 / 3);
			} else {
				_y = 7.787 * _y + 16 / 116;
			}
			if (_z > 0.008856) {
				_z = Math.pow(_z, 1.0 / 3);
			} else {
				_z = 7.787 * _z + 16 / 116;
			}

			lab.l = 116 * _y - 16;
			lab.a = 500 * (_x - _y);
			lab.b = 200 * (_y - _z);
			return lab;
		}

		@Override
		public String toString() {
			return "x=" + x + ",y=" + y + ",z=" + z;
		}
	}

	public static class LAB {
		public double l = 0;
		public double a = 0;
		public double b = 0;

		public double cStarAB() {
			return Math.sqrt(a * a + b * b);
		}

		// Computes the difference between two colors according to the algorithm CIEDE2000
		public double computeDiff(LAB lab) {
			double kh = 1, kl = 1, kc = 1;
			double cDistance = (cStarAB() + lab.cStarAB()) / 2;
			double cDistancePower7 = Math.pow(cDistance, 7);
			double g = (1 - Math.sqrt(cDistancePower7 / (cDistancePower7 + Math.pow(25, 7)))) / 2;
			double aPrime1 = (1 + g) * a;
			double aPrime2 = (1 + g) * lab.a;
			double cPrime1 = Math.sqrt(aPrime1 * aPrime1 + b * b);
			double cPrime2 = Math.sqrt(aPrime2 * aPrime2 + lab.b * lab.b);
			double hPrime1, hPrime2;
			hPrime1 = computeHPrime(aPrime1, b);
			hPrime2 = computeHPrime(aPrime2, lab.b);
			double deltaLPrime, deltaCPrime, deltahPrime, deltaHPrime;
			deltaLPrime = lab.l - l;
			deltaCPrime = cPrime2 - cPrime1;
			deltahPrime = 0;
			if (cPrime1 != 0 && cPrime2 != 0) {
				double hPrimeDiff = hPrime2 - hPrime1;
				if (Math.abs(hPrimeDiff) < 180) {
					deltahPrime = hPrimeDiff;
				} else if (hPrimeDiff > 180) {
					deltahPrime = hPrimeDiff - 360;
				} else {
					deltahPrime = hPrimeDiff + 360;
				}
			}
			deltaHPrime = 2 * Math.sqrt(cPrime2 * cPrime1) * Math.sin(Math.toRadians(deltahPrime) / 2);
			double lPrime, cPrime, hPrime, t, deltaTheta, rc, sl, sc, sh, rt;
			lPrime = (l + lab.l) / 2;
			cPrime = (cPrime1 + cPrime2) / 2;
			hPrime = hPrime1 + hPrime2;
			if (cPrime1 != 0 && cPrime2 != 0) {
				double d = Math.abs(hPrime1 - hPrime2);
				if (d <= 180) {
					hPrime /= 2;
				} else {
					if (hPrime < 360) {
						hPrime += 360;
						hPrime /= 2;
					} else {
						hPrime -= 360;
						hPrime /= 2;
					}
				}
			}
			t = 1 - 0.17 * Math.cos(Math.toRadians(hPrime - 30)) + 0.24 * Math.cos(Math.toRadians(2 * hPrime)) + 0.32
					* Math.cos(Math.toRadians(3 * hPrime + 6)) - 0.2 * Math.cos(Math.toRadians(4 * hPrime - 63));
			deltaTheta = 30 * Math.exp(-Math.pow((hPrime - 275) / 25, 2));
			double cPrimePower7 = Math.pow(cPrime, 7);
			rc = 2 * Math.sqrt(cPrimePower7 / (cPrimePower7 + Math.pow(25, 7)));
			double pow = Math.pow(lPrime - 50, 2);
			sl = 1 + 0.015 * pow / Math.sqrt(20 + pow);
			sc = 1 + 0.045 * cPrime;
			sh = 1 + 0.015 * cPrime * t;
			rt = -Math.sin(2 * deltaTheta) * rc;
			return Math.sqrt(Math.pow(deltaLPrime / (kl * sl), 2) + Math.pow(deltaCPrime / (kc * sc), 2)
					+ Math.pow(deltaHPrime / (kh * sh), 2) + rt * deltaCPrime * deltaHPrime / (kc * sc * kh * sh));
		}

		private double computeHPrime(double aPrime1, double b) {
			double hPrime = 0;
			if (aPrime1 != 0 || b != 0) {
				hPrime = Math.atan2(b, aPrime1);
				if (hPrime < 0) {
					hPrime += Math.PI * 2;
				}
			}
			return Math.toDegrees(hPrime);
		}

		@Override
		public String toString() {
			return "L=" + l + ",a=" + a + ",b=" + b;
		}
	}

	public static void main(String[] args) {
		LAB lab1 = new LAB(), lab2 = new LAB();
		lab1.l = 50;
		lab1.a = 2.6772;
		lab1.b = -79.7751;
		lab2.l = 50;
		lab2.a = 0;
		lab2.b = -82.7485;
		System.err.println(lab1.computeDiff(lab2));
	}

	// This may create a memory leak (although it will be limited to the 16.7M colors to the square ;-)) but according to
	// the use we have of it, it should not be too much (only a few dozens of colors) and it should improve a lot the performance.
	public static final HashMap<Color, HashMap<Color, Double>> constrastRatiosCache = new HashMap<Color, HashMap<Color, Double>>();

	/**
	 * Return true if the difference between two colors matches the W3C recommendations for readability See
	 * http://www.wat-c.org/tools/CCA/1.1/
	 */
	public static boolean respectColorConstrastRecommandations(Color c1, Color c2) {
		int colorConstrastDiff = getColorConstrastDiff(c1, c2);
		int colorBrightnessDiff = getColorBrightnessDiff(c1, c2);
		return colorConstrastDiff > 500 && colorBrightnessDiff > 125;
	}

	private static int getColorConstrastDiff(Color c1, Color c2) {
		return Math.abs(c1.getRed() - c2.getRed()) + Math.abs(c1.getGreen() - c2.getGreen()) + Math.abs(c1.getBlue() - c2.getBlue());
	}

	private static int getColorBrightnessDiff(Color c1, Color c2) {
		int bright1 = (c1.getRed() * 299 + c1.getGreen() * 587 + c1.getBlue() * 114) / 1000;
		int bright2 = (c2.getRed() * 299 + c2.getGreen() * 587 + c2.getBlue() * 114) / 1000;
		return Math.abs(bright1 - bright2);
	}

	public static double getContrastRatio(Color c1, Color c2) {
		if (constrastRatiosCache.get(c1) != null && constrastRatiosCache.get(c1).get(c2) != null) {
			return constrastRatiosCache.get(c1).get(c2);
		}
		double l1 = getRelativeLuminance(c1);
		double l2 = getRelativeLuminance(c2);
		double ratio;
		if (l1 > l2) {
			ratio = (l1 + 0.05) / (l2 + 0.05);
		} else {
			ratio = (l2 + 0.05) / (l1 + 0.05);
		}
		if (constrastRatiosCache.get(c1) == null) {
			constrastRatiosCache.put(c1, new HashMap<Color, Double>());
		}
		constrastRatiosCache.get(c1).put(c2, ratio);
		return ratio;
	}

	public static double getRelativeLuminance(Color c) {
		return new RGB(c).getRelativeLuminance();
	}
}
