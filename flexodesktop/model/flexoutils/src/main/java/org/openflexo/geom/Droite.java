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
package org.openflexo.geom;

import java.awt.Point;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class Droite {

	public float a;

	public float b;

	public float c;

	public Droite(Point p1, Point p2) {
		if (p1.x != p2.x) {
			a = (float) (p2.y - p1.y) / (float) (p1.x - p2.x);
			c = -p1.y - a * p1.x;
			b = 1;
		} else {
			b = 0;
			a = 1;
			c = -p1.x;
		}
	}

	public Droite(Droite perpendiculaire, Point p) {
		a = -perpendiculaire.b;
		b = perpendiculaire.a;
		c = -(a * p.x + b * p.y);
	}

	@Override
	public String toString() {
		return "Droite: " + a + ".x+" + b + ".y+" + c + "=0";
	}

}
