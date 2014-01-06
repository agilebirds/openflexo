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
package org.openflexo.fge.shapes.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.cp.ShapeResizingControlPoint;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEHalfBand;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.shapes.Shape;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;

/**
 * This class represents a shape as a geometric area in a ShapeNode. This is an instance of {@link ShapeSpecification}. As it, it is
 * attached to a {@link ShapeNode}.
 * 
 * This class is a wrapper of {@link FGEShape} which is the geometrical definition of the object as defined in geometrical framework.<br>
 * A {@link ShapeSpecification} has a geometrical definition inside a normalized rectangle as defined by (0.0,0.0,1.0,1.0)<br>
 * 
 * 
 * @author sylvain
 * 
 */
public class ShapeImpl<SS extends ShapeSpecification> implements PropertyChangeListener, Shape<SS> {

	private static final Logger logger = Logger.getLogger(ShapeImpl.class.getPackage().getName());

	// private static final FGEModelFactory SHADOW_FACTORY = FGECoreUtils.TOOLS_FACTORY;

	protected ShapeNode<?> shapeNode;

	private FGEShape<?> shape;
	private FGEShape<?> outline;

	private List<ControlPoint> controlPoints;

	private SS shapeSpecification;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public ShapeImpl(ShapeNode<?> shapeNode) {
		super();
		this.shapeNode = shapeNode;
		shapeSpecification = (SS) shapeNode.getShapeSpecification();
		controlPoints = new ArrayList<ControlPoint>();
	}

	public void delete() {
		if (getShapeSpecification() != null) {
			getShapeSpecification().getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		shapeNode = null;
		controlPoints.clear();
		controlPoints = null;
	}

	@Override
	public SS getShapeSpecification() {
		/*if (shapeNode != null && shapeNode.getGraphicalRepresentation() != null) {
			return (SS) shapeNode.getGraphicalRepresentation().getShapeSpecification();
		}
		return null;*/
		return shapeSpecification;
	}

	/**
	 * Retrieve all control area used to manage this connector
	 * 
	 * @return
	 */
	@Override
	public List<ControlPoint> getControlAreas() {
		return controlPoints;
	}

	/**
	 * Retrieve all control points used to manage this connector
	 * 
	 * @return
	 */
	@Override
	public List<ControlPoint> getControlPoints() {
		return controlPoints;
	}

	@Override
	public FGEShape<?> getShape() {
		if (shape == null) {
			shape = makeShape();
			updateControlPoints();
		}
		return shape;
	}

	@Override
	public FGEShape<?> getOutline() {
		if (outline == null) {
			outline = makeOutline();
		}
		return outline;
	}

	public void updateShape() {

		if (getShapeSpecification() != null) {
			shape = makeShape();
			outline = makeOutline();
			updateControlPoints();
		}
		shapeNode.notifyShapeChanged();
	}

	/**
	 * Recompute all control points for supplied shape node<br>
	 * Return a newly created list of required control points
	 * 
	 * @param shapeNode
	 * @return
	 */
	public List<ControlPoint> updateControlPoints() {

		controlPoints.clear();

		if (shape != null && shape.getControlPoints() != null) {
			for (FGEPoint pt : shape.getControlPoints()) {
				controlPoints.add(new ShapeResizingControlPoint(shapeNode, pt, null));
			}
		}
		return controlPoints;
	}

	@Override
	public ShapeType getShapeType() {
		return getShapeSpecification().getShapeType();
	}

	protected FGEShape<?> makeShape() {
		if (getShapeSpecification() != null) {
			return getShapeSpecification().makeFGEShape(shapeNode);
		}
		return null;
	}

	/**
	 * Return outline for geometric shape of this shape (this is the shape itself, but NOT filled)
	 * 
	 * @return
	 */
	protected final FGEShape<?> makeOutline() {
		FGEShape<?> plainShape = makeShape();
		FGEShape<?> outline = (FGEShape<?>) plainShape.clone();
		outline.setIsFilled(false);
		return outline;
	}

	/**
	 * Return nearest point located on outline, asserting aPoint is related to shape coordinates, and normalized to shape
	 * 
	 * @param aPoint
	 * @return
	 */
	@Override
	public FGEPoint nearestOutlinePoint(FGEPoint aPoint) {
		return getShape().nearestOutlinePoint(aPoint);
	}

	/**
	 * Return flag indicating if position represented is located inside shape, asserting aPoint is related to shape coordinates, and
	 * normalized to shape
	 * 
	 * @param aPoint
	 * @return
	 */
	@Override
	public boolean isPointInsideShape(FGEPoint aPoint) {
		return getShape().containsPoint(aPoint);
	}

	/**
	 * Compute point where supplied line intersects with shape outline trying to minimize distance from "from" point
	 * 
	 * Returns null if no intersection was found
	 * 
	 * @param aLine
	 * @param from
	 * @return
	 */
	@Override
	public final FGEPoint outlineIntersect(FGELine line, FGEPoint from) {
		FGEArea intersection = getShape().intersect(line);
		return intersection.getNearestPoint(from);
	}

	/**
	 * Compute point where a line formed by current shape's center and "from" point intersects with shape outline trying to minimize
	 * distance from "from" point This implementation provide simplified computation with outer bounds (relative coordinates (0,0)-(1,1))
	 * and must be overriden when required
	 * 
	 * Returns null if no intersection was found
	 * 
	 * @param aLine
	 * @param from
	 * @return
	 */
	@Override
	public final FGEPoint outlineIntersect(FGEPoint from) {
		FGELine line = new FGELine(new FGEPoint(0.5, 0.5), from);
		return outlineIntersect(line, from);
	}

	@Override
	public FGEArea getAllowedHorizontalConnectorLocationFromEast() {
		FGEHalfLine north = new FGEHalfLine(1, 0, 2, 0);
		FGEHalfLine south = new FGEHalfLine(1, 1, 2, 1);
		return new FGEHalfBand(north, south);
	}

	/*@Override
	public FGEArea getAllowedHorizontalConnectorLocationFromWest2() {
		double maxY = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		for (ControlPoint cp : getControlPoints()) {
			FGEPoint p = cp.getPoint();
			FGEHalfLine hl = new FGEHalfLine(p.x, p.y, p.x - 1, p.y);
			FGEArea inters = getShape().intersect(hl);
			System.out.println("inters=" + inters);
			if (inters instanceof FGEPoint || inters instanceof FGEEmptyArea) {
				// Consider this point
				if (p.y > maxY) {
					maxY = p.y;
				}
				if (p.y < minY) {
					minY = p.y;
				}
			}
		}
		FGEHalfLine north = new FGEHalfLine(0, minY, -1, minY);
		FGEHalfLine south = new FGEHalfLine(0, maxY, -1, maxY);
		if (north.overlap(south)) {
			System.out.println("Return a " + north.intersect(south));
			return north.intersect(south);
		}
		return new FGEHalfBand(north, south);
	}*/

	@Override
	public FGEArea getAllowedHorizontalConnectorLocationFromWest() {
		FGEHalfLine north = new FGEHalfLine(0, 0, -1, 0);
		FGEHalfLine south = new FGEHalfLine(0, 1, -1, 1);
		return new FGEHalfBand(north, south);
	}

	@Override
	public FGEArea getAllowedVerticalConnectorLocationFromNorth() {
		FGEHalfLine east = new FGEHalfLine(1, 0, 1, -1);
		FGEHalfLine west = new FGEHalfLine(0, 0, 0, -1);
		return new FGEHalfBand(east, west);
	}

	@Override
	public FGEArea getAllowedVerticalConnectorLocationFromSouth() {
		FGEHalfLine east = new FGEHalfLine(1, 1, 1, 2);
		FGEHalfLine west = new FGEHalfLine(0, 1, 0, 2);
		return new FGEHalfBand(east, west);
	}

	// *******************************************************************************
	// * Painting methods *
	// *******************************************************************************

	public void setPaintAttributes(FGEShapeGraphics g) {

		// Background
		if (shapeNode.getIsSelected()) {
			if (shapeNode.getGraphicalRepresentation().getHasSelectedBackground()) {
				g.setDefaultBackground(shapeNode.getGraphicalRepresentation().getSelectedBackground());
			} else if (shapeNode.getGraphicalRepresentation().getHasFocusedBackground()) {
				g.setDefaultBackground(shapeNode.getGraphicalRepresentation().getFocusedBackground());
			} else {
				g.setDefaultBackground(shapeNode.getGraphicalRepresentation().getBackground());
			}
		} else if (shapeNode.getIsFocused() && shapeNode.getGraphicalRepresentation().getHasFocusedBackground()) {
			g.setDefaultBackground(shapeNode.getGraphicalRepresentation().getFocusedBackground());
		} else {
			g.setDefaultBackground(shapeNode.getGraphicalRepresentation().getBackground());
		}

		// Foreground
		if (shapeNode.getIsSelected()) {
			if (shapeNode.getGraphicalRepresentation().getHasSelectedForeground()) {
				g.setDefaultForeground(shapeNode.getGraphicalRepresentation().getSelectedForeground());
			} else if (shapeNode.getGraphicalRepresentation().getHasFocusedForeground()) {
				g.setDefaultForeground(shapeNode.getGraphicalRepresentation().getFocusedForeground());
			} else {
				g.setDefaultForeground(shapeNode.getGraphicalRepresentation().getForeground());
			}
		} else if (shapeNode.getIsFocused() && shapeNode.getGraphicalRepresentation().getHasFocusedForeground()) {
			g.setDefaultForeground(shapeNode.getGraphicalRepresentation().getFocusedForeground());
		} else {
			if (shapeNode.getGraphicalRepresentation().getForeground() == null) {
				logger.info("Ca vient de la: " + shapeNode.getGraphicalRepresentation());
			}
			g.setDefaultForeground(shapeNode.getGraphicalRepresentation().getForeground());
		}

		// Text
		g.setDefaultTextStyle(shapeNode.getGraphicalRepresentation().getTextStyle());
	}

	/*	@Override
		public final void paintShadow(FGEShapeGraphics g) {

			if (g instanceof FGEShapeGraphicsImpl) {

				double deep = shapeNode.getGraphicalRepresentation().getShadowStyle().getShadowDepth();
				int blur = shapeNode.getGraphicalRepresentation().getShadowStyle().getShadowBlur();
				double viewWidth = shapeNode.getViewWidth(1.0);
				double viewHeight = shapeNode.getViewHeight(1.0);
				AffineTransform shadowTranslation = AffineTransform.getTranslateInstance(deep / viewWidth, deep / viewHeight);

				int darkness = shapeNode.getGraphicalRepresentation().getShadowStyle().getShadowDarkness();

				Graphics2D oldGraphics = ((FGEShapeGraphicsImpl) g).cloneGraphics();

				Area clipArea = new Area(new java.awt.Rectangle(0, 0, shapeNode.getViewWidth(g.getScale()), shapeNode.getViewHeight(g
						.getScale())));
				Area a = new Area(getShape());
				a.transform(shapeNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
				clipArea.subtract(a);
				((FGEShapeGraphicsImpl) g).getGraphics().clip(clipArea);

				Color shadowColor = new Color(darkness, darkness, darkness);
				ForegroundStyle foreground = SHADOW_FACTORY.makeForegroundStyle(shadowColor);
				foreground.setUseTransparency(true);
				foreground.setTransparencyLevel(0.5f);
				BackgroundStyle background = SHADOW_FACTORY.makeColoredBackground(shadowColor);
				background.setUseTransparency(true);
				background.setTransparencyLevel(0.5f);
				g.setDefaultForeground(foreground);
				g.setDefaultBackground(background);

				for (int i = blur - 1; i >= 0; i--) {
					float transparency = 0.4f - i * 0.4f / blur;
					foreground.setTransparencyLevel(transparency);
					background.setTransparencyLevel(transparency);
					AffineTransform at = AffineTransform.getScaleInstance((i + 1 + viewWidth) / viewWidth, (i + 1 + viewHeight) / viewHeight);
					at.concatenate(shadowTranslation);
					getShape().transform(at).paint(g);
				}

				((FGEShapeGraphicsImpl) g).releaseClonedGraphics(oldGraphics);
			} else {
				logger.warning("Not support FGEGraphics: " + g);
			}
		}*/

	@Override
	public final void paintShape(FGEShapeGraphics g) {
		setPaintAttributes(g);
		getShape().paint(g);
		// drawLabel(g);
	}

	@Override
	public ShapeImpl clone() {
		try {
			ShapeImpl returned = (ShapeImpl) super.clone();
			// returned._controlPoints = null;
			// returned.graphicalRepresentation = null;
			// returned.updateShape();
			// returned.rebuildControlPoints();
			return returned;
		} catch (CloneNotSupportedException e) {
			// cannot happen since we are clonable
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean equals(Object object) {
		// TODO
		/*if (object instanceof ShapeSpecificationImpl && getShape() != null) {
			return getShape().equals(((ShapeSpecificationImpl) object).getShape())
					&& areDimensionConstrained() == ((ShapeSpecificationImpl) object).areDimensionConstrained();
		}*/
		return super.equals(object);
	}

	@Override
	public int hashCode() {
		// TODO
		/*if (getShape() != null) {
			return getShape().toString().hashCode();
		}*/
		return super.hashCode();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getShapeSpecification()) {
			// System.out.println("Received " + evt + " in ShapeImpl propertyName=" + evt.getPropertyName());
			// Those notifications are forwarded by the shape specification
			updateShape();
		}
	}

	public void notifyObjectResized() {
		updateShape();
	}
}
