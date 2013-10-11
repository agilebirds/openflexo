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
package org.openflexo.fge.view.widget;

import java.beans.PropertyChangeSupport;

import javax.swing.JComponent;

import org.openflexo.fge.FGECoreUtils;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.Arc;
import org.openflexo.fge.shapes.Circle;
import org.openflexo.fge.shapes.Losange;
import org.openflexo.fge.shapes.Oval;
import org.openflexo.fge.shapes.Polygon;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.RegularPolygon;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fge.shapes.Square;
import org.openflexo.fge.shapes.Star;
import org.openflexo.fge.shapes.Triangle;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Widget allowing to view and edit a ShapeSpecification
 * 
 * @author sguerin
 * 
 */
// TODO: suppress reference to Swing (when FIB library will be independant from SWING technology)
public interface FIBShapeSelector<C extends JComponent> extends FIBCustomComponent<ShapeSpecification, C> {

	public static FileResource FIB_FILE = new FileResource("Fib/ShapeSelectorPanel.fib");

	public static final FGEModelFactory SHAPE_FACTORY = FGECoreUtils.TOOLS_FACTORY;

	/**
	 * Convenient class use to manipulate BackgroundStyle
	 * 
	 * @author sylvain
	 * 
	 */
	public static class ShapeFactory implements HasPropertyChangeSupport {
		private static final String DELETED = "deleted";

		private ShapeSpecification shape;

		private Rectangle rectangle;
		private Square square;
		private Polygon polygon;
		private RegularPolygon regularPolygon;
		private Losange losange;
		private Triangle triangle;
		private Oval oval;
		private Circle circle;
		private Arc arc;
		private Star star;

		private PropertyChangeSupport pcSupport;

		public ShapeFactory(ShapeSpecification shape) {
			pcSupport = new PropertyChangeSupport(this);
			this.shape = shape;
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		public void delete() {
			pcSupport.firePropertyChange(DELETED, false, true);
			pcSupport = null;
		}

		@Override
		public String getDeletedProperty() {
			return DELETED;
		}

		public ShapeSpecification getShape() {
			return shape;
		}

		public void setShape(ShapeSpecification shape) {
			ShapeSpecification oldShape = this.shape;
			this.shape = shape;
			pcSupport.firePropertyChange("shape", oldShape, shape);
		}

		public ShapeType getShapeType() {
			if (shape != null) {
				return shape.getShapeType();
			}
			return null;
		}

		public void setShapeType(ShapeType shapeType) {
			// logger.info("setBackgroundStyleType with " +
			// backgroundStyleType);
			ShapeType oldShapeType = getShapeType();

			if (oldShapeType != shapeType) {

				// System.out.println("set shape type to " + shapeType);

				switch (shapeType) {
				case RECTANGLE:
					if (rectangle == null) {
						rectangle = (Rectangle) SHAPE_FACTORY.makeShape(shapeType);
					}
					shape = rectangle;
					break;
				case SQUARE:
					if (square == null) {
						square = (Square) SHAPE_FACTORY.makeShape(shapeType);
					}
					shape = square;
					break;
				case CUSTOM_POLYGON:
					if (polygon == null) {
						polygon = SHAPE_FACTORY.makePolygon(null, new FGEPoint(0.1, 0.1), new FGEPoint(0.3, 0.9), new FGEPoint(0.9, 0.3));
					}
					shape = polygon;
					break;
				case POLYGON:
					if (regularPolygon == null) {
						regularPolygon = (RegularPolygon) SHAPE_FACTORY.makeShape(shapeType);
					}
					shape = regularPolygon;
					break;
				case TRIANGLE:
					if (triangle == null) {
						triangle = (Triangle) SHAPE_FACTORY.makeShape(shapeType);
					}
					shape = triangle;
					break;
				case LOSANGE:
					if (losange == null) {
						losange = (Losange) SHAPE_FACTORY.makeShape(shapeType);
					}
					shape = losange;
					break;
				case OVAL:
					if (oval == null) {
						oval = (Oval) SHAPE_FACTORY.makeShape(shapeType);
					}
					shape = oval;
					break;
				case CIRCLE:
					if (circle == null) {
						circle = (Circle) SHAPE_FACTORY.makeShape(shapeType);
					}
					shape = circle;
					break;
				case ARC:
					if (arc == null) {
						arc = (Arc) SHAPE_FACTORY.makeShape(shapeType);
					}
					shape = arc;
					break;
				case STAR:
					if (star == null) {
						star = (Star) SHAPE_FACTORY.makeShape(shapeType);
					}
					shape = star;
					break;
				default:
					shape = SHAPE_FACTORY.makeShape(shapeType);
				}

				pcSupport.firePropertyChange("shapeType", oldShapeType, getShapeType());
			}
		}
	}

}