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
package org.openflexo.fge.geomedit;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;

import org.openflexo.fge.geom.FGEEllips;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;

public class TestShape {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FGERectangle rectangle = new FGERectangle(0, 0, 10, 10);
		System.out.println("Le rectangle: ");
		debugShape(rectangle);
		Area rotatedRectangle = new Area(rectangle);
		rotatedRectangle.transform(AffineTransform.getRotateInstance(Math.PI / 4));
		System.out.println("Le rectangle qu'a tourne: ");
		debugShape(rotatedRectangle);
		FGEEllips circle = new FGEEllips(0, 0, 10, 10, Filling.NOT_FILLED);
		System.out.println("Le cercle: ");
		debugShape(circle);
	}

	private static void debugShape(Shape shape) {
		PathIterator pi = shape.getPathIterator(new AffineTransform());
		while (!pi.isDone()) {
			double[] coords = new double[6];
			int i = pi.currentSegment(coords);
			String pathType = "";
			switch (i) {
			case PathIterator.SEG_LINETO:
				pathType = "SEG_LINETO";
				break;
			case PathIterator.SEG_MOVETO:
				pathType = "SEG_MOVETO";
				break;
			case PathIterator.SEG_CUBICTO:
				pathType = "SEG_CUBICTO";
				break;
			case PathIterator.SEG_QUADTO:
				pathType = "SEG_QUADTO";
				break;
			case PathIterator.SEG_CLOSE:
				pathType = "SEG_CLOSE";
				break;
			default:
				break;
			}
			System.out.println(pathType + " " + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3] + " " + coords[4] + " "
					+ coords[5]);
			pi.next();
		}
	}

}
