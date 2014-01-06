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
package org.openflexo.fge.geom;

import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

public class GeomUtils {

	static final Logger logger = Logger.getLogger(GeomUtils.class.getPackage().getName());

	public static final double PHI = (Math.sqrt(5) + 1) / 2;

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

	public static double getSlope(FGEPoint p1, FGEPoint p2) {
		double x = p2.x - p1.x;
		double y = p2.y - p1.y;
		// GPO: In some rare cases, it seems that this returns -PI/2 instead of 3PI/2
		// Example: new FGESegment(0.05369127516778524,0.06451612903225806,0.053691275167785234,-0.5806451612903225)
		// FGESegment.getAngle() has been fixed since but if it re-appears, maybe we should uncomment the code below
		// Mainly, calling methods should take into account that this method can return a value -PI/2 or 3PI/2
		return Math.atan2(/* Math.abs(x)<EPSILON?0:x, Math.abs(y)<EPSILON?0:y */x, y) + Math.PI / 2;
	}

}
