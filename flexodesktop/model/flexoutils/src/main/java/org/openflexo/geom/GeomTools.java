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
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class GeomTools {

	public static Point projection(Point p, Droite d) {
		Point returned = new Point();
		Droite d2 = new Droite(d, p);
		returned.y = (int) (-(d.b * d.c + d.a * d2.c) / (d.a * d.a + d.b * d.b));
		if (d.a != 0) {
			returned.x = (int) (-(d.c + d.b * returned.y) / d.a);
		} else {
			returned.x = (int) (d2.c / d.b);
		}
		return returned;
	}

	public static double distance(Point p1, Point p2) {
		float x2 = p1.x - p2.x;
		x2 = x2 * x2;
		float y2 = p1.y - p2.y;
		y2 = y2 * y2;
		return Math.sqrt(x2 + y2);
	}

	/*
	 * public static double rotationAngle (Point pivot, Point p1, Point p2) {
	 * Point projP1 = GeomTools.projection(p1,new Droite(pivot,p2)); return
	 * Math.atan(distance(p1,projP1)/distance(pivot,projP1)); }
	 */

	public static double rotationAngle(Point pivot, Point p1, Point p2) {
		double vectProduct = (p1.x - pivot.x) * (p2.x - pivot.x) + (p1.y - pivot.y) * (p2.y - pivot.y);
		double returned = Math.acos(vectProduct / (distance(pivot, p1) * distance(pivot, p2)));
		float sign = (p1.x - pivot.x) * (p2.y - pivot.y) - (p2.x - pivot.x) * (p1.y - pivot.y);
		if (sign < 0) {
			returned = -returned;
		}
		return returned;
	}

	public static AffineTransform getRotationTransform(double angle, Point pivot) {
		return AffineTransform.getRotateInstance(angle, pivot.x, pivot.y);
	}

	public static Point rotate(Point pivot, Point p1, Point p2, Point toBeRotated) {
		double angle = rotationAngle(pivot, p1, p2);
		AffineTransform rotation = getRotationTransform(angle, pivot);
		Point2D.Float point = new Point2D.Float(toBeRotated.x, toBeRotated.y);
		Point2D.Float result = new Point2D.Float();
		rotation.transform(point, result);
		return new Point((int) result.x, (int) result.y);
	}

	public static Point transform(Point pivot, Point p1, Point p2, Point toBeTransformed) {
		double angle = rotationAngle(pivot, p1, p2);
		double scale = distance(pivot, p2) / distance(pivot, p1);
		AffineTransform transform = AffineTransform.getTranslateInstance(pivot.x, pivot.y);
		transform.concatenate(AffineTransform.getRotateInstance(angle));
		transform.concatenate(AffineTransform.getScaleInstance(scale, scale));
		transform.concatenate(AffineTransform.getTranslateInstance(-pivot.x, -pivot.y));
		Point2D.Float point = new Point2D.Float(toBeTransformed.x, toBeTransformed.y);
		Point2D.Float result = new Point2D.Float();
		transform.transform(point, result);
		return new Point((int) result.x, (int) result.y);
	}

	public static void main(String[] args) {
		// System.out.println ("Angle1="+rotationAngle(new Point(0,0), new
		// Point(0,3), new Point(2,0))/Math.PI*180);
		// System.out.println ("Angle1="+rotationAngle(new Point(0,0), new
		// Point(2,0), new Point(0,3))/Math.PI*180);
		// System.out.println ("Angle1="+rotationAngle(new Point(1,1), new
		// Point(0,3), new Point(2,0))/Math.PI*180);
		// System.out.println ("Angle1="+rotationAngle(new Point(1,1), new
		// Point(2,0), new Point(0,3))/Math.PI*180);
		// System.out.println ("Angle1="+rotationAngle(new Point(3,2), new
		// Point(6,3), new Point(2,5))/Math.PI*180);

		System.out.println("Return=" + rotate(new Point(0, 0), new Point(0, 3), new Point(2, 0), new Point(0, 3)));
		System.out.println("Return2=" + transform(new Point(0, 0), new Point(0, 3), new Point(2, 0), new Point(0, 3)));
		System.out.println("Return=" + rotate(new Point(3, 2), new Point(3, 5), new Point(5, 2), new Point(3, 5)));
		System.out.println("Return2=" + transform(new Point(3, 2), new Point(3, 5), new Point(5, 2), new Point(3, 5)));
	}

}
