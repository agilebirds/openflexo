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
package org.openflexo.fge.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.cp.ShapeResizingControlPoint;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEHalfBand;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.kvc.KVCObject;
import org.openflexo.xmlcode.XMLSerializable;


public abstract class Shape extends KVCObject implements XMLSerializable, Cloneable {

	private static final Logger logger = Logger.getLogger(Shape.class.getPackage().getName());

	private transient ShapeGraphicalRepresentation graphicalRepresentation;

	private transient Vector<ControlPoint> _controlPoints = null;
	
	public static final FGEPoint CENTER = new FGEPoint(0.5,0.5);
	public static final FGEPoint NORTH_EAST = new FGEPoint(1,0);
	public static final FGEPoint SOUTH_EAST = new FGEPoint(1,1);
	public static final FGEPoint SOUTH_WEST = new FGEPoint(0,1);
	public static final FGEPoint NORTH_WEST = new FGEPoint(0,0);
	public static final FGEPoint NORTH = new FGEPoint(0.5,0);
	public static final FGEPoint EAST = new FGEPoint(1,0.5);
	public static final FGEPoint SOUTH = new FGEPoint(0.5,1);
	public static final FGEPoint WEST = new FGEPoint(0,0.5);
	

	public static enum ShapeType
	{
		RECTANGLE,
		SQUARE,
		POLYGON,
		TRIANGLE,
		LOSANGE,
		OVAL,
		CIRCLE,
		STAR,
		ARC
	}

	// *******************************************************************************
	// *                               Constructor                                   *
	// *******************************************************************************

	public Shape(ShapeGraphicalRepresentation aGraphicalRepresentation)
	{
		super();
		graphicalRepresentation = aGraphicalRepresentation;
	}
	
	public static Shape makeShape(ShapeType type, ShapeGraphicalRepresentation aGraphicalRepresentation)
	{
		if (type == ShapeType.SQUARE) {
			return new Square(aGraphicalRepresentation);
		}
		else if (type == ShapeType.RECTANGLE) {
			return new Rectangle(aGraphicalRepresentation);
		}
		else if (type == ShapeType.TRIANGLE) {
			return new Triangle(aGraphicalRepresentation);
		}
		else if (type == ShapeType.LOSANGE) {
			return new Losange(aGraphicalRepresentation);
		}
		else if (type == ShapeType.POLYGON) {
			return new Polygon(aGraphicalRepresentation);
		}
		else if (type == ShapeType.OVAL) {
			return new Oval(aGraphicalRepresentation);
		}
		else if (type == ShapeType.CIRCLE) {
			return new Circle(aGraphicalRepresentation);
		}
		else if (type == ShapeType.STAR) {
			return new Star(aGraphicalRepresentation);
		}
		else if (type == ShapeType.ARC) {
			return new Arc(aGraphicalRepresentation);
		}
		return null;
	}
	


	// *******************************************************************************
	// *                                  Methods                                    *
	// *******************************************************************************


	public void setPaintAttributes(FGEShapeGraphics g) 
	{
		g.setDefaultBackground(getGraphicalRepresentation().getBackground());
		g.setDefaultForeground(getGraphicalRepresentation().getForeground());
		g.setDefaultTextStyle(getGraphicalRepresentation().getTextStyle());
	}

	/**
	 * Must be overriden when shape requires it
	 * @return
	 */
	public boolean areDimensionConstrained()
	{
		return false;
	}

	public abstract ShapeType getShapeType();

	/**
	 * Return geometric shape of this shape
	 * 
	 * @return
	 */
	public abstract FGEShape<?> getShape();
	
	/**
	 * Return outline for geometric shape of this shape
	 * (this is the shape itself, but NOT filled)
	 * 
	 * @return
	 */
	public final FGEShape getOutline()
	{
		FGEShape<?> outline = (FGEShape<?>)getShape().clone();
		outline.setIsFilled(false);
		return outline;
	}
	

	public final ShapeGraphicalRepresentation getGraphicalRepresentation()
	{
		return graphicalRepresentation;
	}

	public final void setGraphicalRepresentation(ShapeGraphicalRepresentation aGR)
	{
		if (aGR != graphicalRepresentation) {
			//logger.info("Shape "+this+" changed GR from "+graphicalRepresentation+" to "+aGR);
			graphicalRepresentation = aGR;
			updateShape();
		}
	}

	public List<ControlPoint> getControlPoints()
	{
		if (_controlPoints == null) rebuildControlPoints();
		return _controlPoints;
	}
	
	public List<ControlPoint> rebuildControlPoints()
	{
		//logger.info("For Shape "+this+" rebuildControlPoints()");
		
		if (_controlPoints != null) _controlPoints.clear();
		else _controlPoints = new Vector<ControlPoint>();
	
		if (getGraphicalRepresentation() == null) return _controlPoints;
		
		for (FGEPoint pt : getShape().getControlPoints()) {
			_controlPoints.add(new ShapeResizingControlPoint(getGraphicalRepresentation(),pt,null));
		}
		return _controlPoints;
	}
	
	/**
	 * Return nearest point located on outline, asserting aPoint is related to
	 * shape coordinates, and normalized to shape
	 * @param aPoint
	 * @return
	 */
	public FGEPoint nearestOutlinePoint(FGEPoint aPoint) 
	{
		return getShape().nearestOutlinePoint(aPoint);
	}
	
	/**
	 * Return flag indicating if position represented is
	 * located inside shape, asserting aPoint is related to
	 * shape coordinates, and normalized to shape
	 * @param aPoint
	 * @return
	 */
	public boolean isPointInsideShape(FGEPoint aPoint)
	{
		return getShape().containsPoint(aPoint);
	}

	/**
	 * Compute point where supplied line intersects with shape outline
	 * trying to minimize distance from "from" point
	 * 
	 * Returns null if no intersection was found
	 * 
	 * @param aLine
	 * @param from
	 * @return 
	 */
	public final FGEPoint outlineIntersect(FGELine line, FGEPoint from) 
	{
		FGEArea intersection = getShape().intersect(line);
		return intersection.getNearestPoint(from);	
	}


	/**
	 * Compute point where a line formed by current shape's center
	 * and "from" point intersects with shape outline
	 * trying to minimize distance from "from" point
	 * This implementation provide simplified computation with outer
	 * bounds (relative coordinates (0,0)-(1,1)) and must be overriden
	 * when required
	 * 
	 * Returns null if no intersection was found
	 * 
	 * @param aLine
	 * @param from
	 * @return 
	 */
	public final FGEPoint outlineIntersect(FGEPoint from) 
	{ 
		FGELine line = new FGELine(new FGEPoint(0.5,0.5),from);
		return outlineIntersect(line,from);
	}

	public FGEArea getAllowedHorizontalConnectorLocationFromEast() 
	{
		FGEHalfLine north = new FGEHalfLine(1,0,2,0);
		FGEHalfLine south = new FGEHalfLine(1,1,2,1);
		return new FGEHalfBand(north,south);
	}

	public FGEArea getAllowedHorizontalConnectorLocationFromWest2() 
	{
		double maxY = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		for (ControlPoint cp : getControlPoints()) {
			FGEPoint p = cp.getPoint();
			FGEHalfLine hl = new FGEHalfLine(p.x,p.y,p.x-1,p.y);
			FGEArea inters = getShape().intersect(hl);
			System.out.println("inters="+inters);
			if (inters instanceof FGEPoint || inters instanceof FGEEmptyArea) {
				// Consider this point
				if (p.y > maxY) maxY = p.y;
				if (p.y < minY) minY = p.y;
			}
		}
		FGEHalfLine north = new FGEHalfLine(0,minY,-1,minY);
		FGEHalfLine south = new FGEHalfLine(0,maxY,-1,maxY);
		/*FGEHalfLine north = new FGEHalfLine(0,0,-1,0);
		FGEHalfLine south = new FGEHalfLine(0,1,-1,1);*/
		if (north.overlap(south)) {
			System.out.println("Return a "+north.intersect(south));
			return north.intersect(south);
		}
		return new FGEHalfBand(north,south);
	}
	
	public FGEArea getAllowedHorizontalConnectorLocationFromWest() 
	{
		FGEHalfLine north = new FGEHalfLine(0,0,-1,0);
		FGEHalfLine south = new FGEHalfLine(0,1,-1,1);
		return new FGEHalfBand(north,south);
	}

	public FGEArea getAllowedVerticalConnectorLocationFromNorth() 
	{
		FGEHalfLine east = new FGEHalfLine(1,0,1,-1);
		FGEHalfLine west = new FGEHalfLine(0,0,0,-1);
		return new FGEHalfBand(east,west);
	}

	public FGEArea getAllowedVerticalConnectorLocationFromSouth() 
	{			
		FGEHalfLine east = new FGEHalfLine(1,1,1,2);
		FGEHalfLine west = new FGEHalfLine(0,1,0,2);
		return new FGEHalfBand(east,west);
	}

	
	public final void paintShadow(FGEShapeGraphics g) 
	{
		double deep = getGraphicalRepresentation().getShadowStyle().getShadowDepth();
		int blur = getGraphicalRepresentation().getShadowStyle().getShadowBlur();
		double viewWidth = getGraphicalRepresentation().getViewWidth(1.0);
		double viewHeight = getGraphicalRepresentation().getViewHeight(1.0);
		AffineTransform shadowTranslation = AffineTransform.getTranslateInstance(deep/viewWidth,deep/viewHeight);

		int darkness = getGraphicalRepresentation().getShadowStyle().getShadowDarkness();
					
		Graphics2D oldGraphics = g.cloneGraphics();
		
		Area clipArea = new Area(new java.awt.Rectangle(0,0,getGraphicalRepresentation().getViewWidth(g.getScale()),getGraphicalRepresentation().getViewHeight(g.getScale())));
		Area a = new Area(getGraphicalRepresentation().getShape().getShape());
		a.transform(getGraphicalRepresentation().convertNormalizedPointToViewCoordinatesAT(g.getScale()));
		clipArea.subtract(a);
		g.g2d.clip(clipArea);

		
		Color shadowColor = new Color(darkness,darkness,darkness);
		ForegroundStyle foreground = ForegroundStyle.makeStyle(shadowColor);
		foreground.setUseTransparency(true);
		foreground.setTransparencyLevel(0.5f);
		BackgroundStyle background = BackgroundStyle.makeColoredBackground(shadowColor);
		background.setUseTransparency(true);
		background.setTransparencyLevel(0.5f);
		g.setDefaultForeground(foreground);
		g.setDefaultBackground(background);

		for (int i=blur-1; i>=0; i--) {
			float transparency = 0.4f-i*0.4f/blur;
			foreground.setTransparencyLevel(transparency);
			background.setTransparencyLevel(transparency);
			AffineTransform at = AffineTransform.getScaleInstance((i+1+viewWidth)/viewWidth, (i+1+viewHeight)/viewHeight);
			at.concatenate(shadowTranslation);
			getShape().transform(at).paint(g);
		}
		
		g.releaseClonedGraphics(oldGraphics);
	}

	//@Override
	public final void paintShape(FGEShapeGraphics g) 
	{
		setPaintAttributes(g);
		getShape().paint(g);
		//drawLabel(g);
	}

	// Override when required
	public void notifyObjectResized()
	{
	}

	@Override
	public Shape clone()
	{
		try {			
			Shape returned = (Shape)super.clone();
			returned._controlPoints = null;
			returned.graphicalRepresentation = null;
			returned.updateShape();
			returned.rebuildControlPoints();
			return returned;
		} catch (CloneNotSupportedException e) {
			// cannot happen since we are clonable
			e.printStackTrace();
			return null;
		}
	}

	public abstract void updateShape();
}
