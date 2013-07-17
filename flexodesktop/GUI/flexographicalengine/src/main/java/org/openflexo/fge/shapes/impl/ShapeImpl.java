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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.cp.ShapeResizingControlPoint;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEHalfBand;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.impl.FGEObjectImpl;
import org.openflexo.fge.notifications.ShapeChanged;
import org.openflexo.fge.shapes.Shape;

/**
 * Default implementation of {@link Shape}
 * 
 * @author sylvain
 * 
 */
public abstract class ShapeImpl extends FGEObjectImpl implements Shape {

	private static final Logger logger = Logger.getLogger(ShapeImpl.class.getPackage().getName());

	// private transient ShapeGraphicalRepresentation graphicalRepresentation;

	// private transient Vector<ControlPoint> _controlPoints = null;

	private static final FGEModelFactory SHADOW_FACTORY = FGEUtils.TOOLS_FACTORY;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public ShapeImpl() {
		super();
	}

	// *******************************************************************************
	// * Methods *
	// *******************************************************************************

	@Override
	public void setPaintAttributes(ShapeNode<?> node, FGEShapeGraphics g) {

		// Background
		if (node.getGraphicalRepresentation().getIsSelected()) {
			if (node.getGraphicalRepresentation().getHasSelectedBackground()) {
				g.setDefaultBackground(node.getGraphicalRepresentation().getSelectedBackground());
			} else if (node.getGraphicalRepresentation().getHasFocusedBackground()) {
				g.setDefaultBackground(node.getGraphicalRepresentation().getFocusedBackground());
			} else {
				g.setDefaultBackground(node.getGraphicalRepresentation().getBackground());
			}
		} else if (node.getGraphicalRepresentation().getIsFocused() && node.getGraphicalRepresentation().getHasFocusedBackground()) {
			g.setDefaultBackground(node.getGraphicalRepresentation().getFocusedBackground());
		} else {
			g.setDefaultBackground(node.getGraphicalRepresentation().getBackground());
		}

		// Foreground
		if (node.getGraphicalRepresentation().getIsSelected()) {
			if (node.getGraphicalRepresentation().getHasSelectedForeground()) {
				g.setDefaultForeground(node.getGraphicalRepresentation().getSelectedForeground());
			} else if (node.getGraphicalRepresentation().getHasFocusedForeground()) {
				g.setDefaultForeground(node.getGraphicalRepresentation().getFocusedForeground());
			} else {
				g.setDefaultForeground(node.getGraphicalRepresentation().getForeground());
			}
		} else if (node.getGraphicalRepresentation().getIsFocused() && node.getGraphicalRepresentation().getHasFocusedForeground()) {
			g.setDefaultForeground(node.getGraphicalRepresentation().getFocusedForeground());
		} else {
			if (node.getGraphicalRepresentation().getForeground() == null) {
				logger.info("Ca vient de la: " + node.getGraphicalRepresentation());
			}
			g.setDefaultForeground(node.getGraphicalRepresentation().getForeground());
		}

		// Text
		g.setDefaultTextStyle(node.getGraphicalRepresentation().getTextStyle());
	}

	/**
	 * Must be overriden when shape requires it
	 * 
	 * @return
	 */
	@Override
	public boolean areDimensionConstrained() {
		return false;
	}

	@Override
	public abstract ShapeType getShapeType();

	private Hashtable<ShapeNode<?>, FGEShape<?>> shapes = new Hashtable<ShapeNode<?>, FGEShape<?>>();
	private Hashtable<ShapeNode<?>, FGEShape<?>> outlines = new Hashtable<ShapeNode<?>, FGEShape<?>>();

	/**
	 * Return geometric shape of this shape, in the context of supplied node
	 * 
	 * @return
	 */
	@Override
	public FGEShape<?> getShape(ShapeNode<?> node) {
		if (shapes.get(node) != null) {
			return shapes.get(node);
		} else {
			FGEShape<?> returned = makeShape(node);
			shapes.put(node, returned);
			rebuildControlPoints(node);
			node.notifyShapeChanged();
			return returned;
		}
	}

	protected abstract FGEShape<?> makeShape(ShapeNode<?> node);

	protected void shapeChanged() {
		shapes.clear();
		outlines.clear();
		setChanged();
		notifyObservers(new ShapeChanged());
	}

	/**
	 * Return outline for geometric shape of this shape (this is the shape itself, but NOT filled)
	 * 
	 * @return
	 */
	@Override
	public final FGEShape<?> getOutline(ShapeNode<?> node) {
		if (outlines.get(node) != null) {
			return outlines.get(node);
		} else {
			FGEShape<?> plainShape = getShape(node);
			FGEShape<?> outline = (FGEShape<?>) plainShape.clone();
			outline.setIsFilled(false);
			outlines.put(node, outline);
			return outline;

		}
	}

	/*@Override
	public final ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	@Override
	public final void setGraphicalRepresentation(ShapeGraphicalRepresentation aGR) {
		if (aGR != graphicalRepresentation) {
			// logger.info("Shape "+this+" changed GR from "+graphicalRepresentation+" to "+aGR);
			graphicalRepresentation = aGR;
			updateShape();
		}
	}*/

	/*@Override
	public List<ControlPoint> getControlPoints() {
		if (_controlPoints == null) {
			rebuildControlPoints();
		}
		return _controlPoints;
	}*/

	/**
	 * Recompute all control points for supplied shape node Return a newly created list of required control points
	 * 
	 * @param shapeNode
	 * @return
	 */
	@Override
	public List<ControlPoint> rebuildControlPoints(ShapeNode<?> node) {

		List<ControlPoint> returned = new ArrayList<ControlPoint>();

		// logger.info("For Shape "+this+" rebuildControlPoints()");

		/*if (_controlPoints != null) {
			_controlPoints.clear();
		} else {
			_controlPoints = new Vector<ControlPoint>();
		}

		if (getGraphicalRepresentation() == null) {
			return _controlPoints;
		}*/

		if (getShape(node).getControlPoints() != null) {
			for (FGEPoint pt : getShape(node).getControlPoints()) {
				returned.add(new ShapeResizingControlPoint(node, pt, null));
			}
		}
		return returned;
	}

	/**
	 * Return nearest point located on outline, asserting aPoint is related to shape coordinates, and normalized to shape
	 * 
	 * @param aPoint
	 * @return
	 */
	@Override
	public FGEPoint nearestOutlinePoint(FGEPoint aPoint, ShapeNode<?> node) {
		return getShape(node).nearestOutlinePoint(aPoint);
	}

	/**
	 * Return flag indicating if position represented is located inside shape, asserting aPoint is related to shape coordinates, and
	 * normalized to shape
	 * 
	 * @param aPoint
	 * @return
	 */
	@Override
	public boolean isPointInsideShape(FGEPoint aPoint, ShapeNode<?> node) {
		return getShape(node).containsPoint(aPoint);
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
	public final FGEPoint outlineIntersect(FGELine line, FGEPoint from, ShapeNode<?> node) {
		FGEArea intersection = getShape(node).intersect(line);
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
	public final FGEPoint outlineIntersect(FGEPoint from, ShapeNode<?> node) {
		FGELine line = new FGELine(new FGEPoint(0.5, 0.5), from);
		return outlineIntersect(line, from, node);
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

	@Override
	public final void paintShadow(ShapeNode<?> node, FGEShapeGraphics g) {
		double deep = node.getGraphicalRepresentation().getShadowStyle().getShadowDepth();
		int blur = node.getGraphicalRepresentation().getShadowStyle().getShadowBlur();
		double viewWidth = node.getViewWidth(1.0);
		double viewHeight = node.getViewHeight(1.0);
		AffineTransform shadowTranslation = AffineTransform.getTranslateInstance(deep / viewWidth, deep / viewHeight);

		int darkness = node.getGraphicalRepresentation().getShadowStyle().getShadowDarkness();

		Graphics2D oldGraphics = g.cloneGraphics();

		Area clipArea = new Area(new java.awt.Rectangle(0, 0, node.getViewWidth(g.getScale()), node.getViewHeight(g.getScale())));
		Area a = new Area(node.getGraphicalRepresentation().getShape().getShape(node));
		a.transform(node.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
		clipArea.subtract(a);
		g.getGraphics().clip(clipArea);

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
			getShape(node).transform(at).paint(g);
		}

		g.releaseClonedGraphics(oldGraphics);
	}

	// @Override
	@Override
	public final void paintShape(ShapeNode<?> node, FGEShapeGraphics g) {
		setPaintAttributes(node, g);
		getShape(node).paint(g);
		// drawLabel(g);
	}

	// Override when required
	@Override
	public void notifyObjectResized() {
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
		/*if (object instanceof ShapeImpl && getShape() != null) {
			return getShape().equals(((ShapeImpl) object).getShape())
					&& areDimensionConstrained() == ((ShapeImpl) object).areDimensionConstrained();
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
}
