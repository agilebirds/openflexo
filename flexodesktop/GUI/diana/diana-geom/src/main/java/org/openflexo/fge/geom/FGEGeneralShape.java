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

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.geom.area.FGEIntersectionArea;
import org.openflexo.fge.geom.area.FGESubstractionArea;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graphics.AbstractFGEGraphics;

public class FGEGeneralShape<O extends FGEGeneralShape<O>> implements FGEGeometricObject<O>, FGEShape<O> {

	private static final Logger logger = Logger.getLogger(FGEGeneralShape.class.getPackage().getName());

	private Vector<GeneralShapePathElement<?>> pathElements;
	private Closure closure;
	private GeneralPath _generalPath;
	private FGEPoint currentPoint;

	private Vector<FGEPoint> _controlPoints;

	public static enum Closure {
		OPEN_NOT_FILLED, CLOSED_NOT_FILLED, OPEN_FILLED, CLOSED_FILLED;
	}

	public static interface GeneralShapePathElement<E extends GeneralShapePathElement<E>> extends FGEGeometricObject<E> {
		public FGEPoint getP1();

		public FGEPoint getP2();
	}

	public FGEGeneralShape() {
		this(Closure.OPEN_NOT_FILLED);
	}

	public FGEGeneralShape(Closure aClosure) {
		super();
		closure = aClosure;
		pathElements = new Vector<GeneralShapePathElement<?>>();
		_generalPath = new GeneralPath();
		_controlPoints = new Vector<FGEPoint>();
	}

	public FGEGeneralShape(Closure aClosure, GeneralPath generalPath) {
		this(aClosure);
		logger.warning("FGEGeneralShape from generalPath not implemented yet");
	}

	public Vector<GeneralShapePathElement<?>> getPathElements() {
		return pathElements;
	}

	public void setPathElements(Vector<GeneralShapePathElement<?>> elements) {
		currentPoint = null;
		for (GeneralShapePathElement<?> e : elements) {
			addToPathElements(e);
		}
	}

	public void addToPathElements(GeneralShapePathElement<?> element) {
		if (element instanceof FGESegment) {
			if (currentPoint == null) {
				beginAtPoint(((FGESegment) element).getP1());
			}
			addSegment(((FGESegment) element).getP2());
		} else if (element instanceof FGEQuadCurve) {
			if (currentPoint == null) {
				beginAtPoint(((FGEQuadCurve) element).getP1());
			}
			addQuadCurve(((FGEQuadCurve) element).getCtrlPoint(), ((FGEQuadCurve) element).getP2());
		} else if (element instanceof FGECubicCurve) {
			if (currentPoint == null) {
				beginAtPoint(((FGECubicCurve) element).getP1());
			}
			addCubicCurve(((FGECubicCurve) element).getCtrlP1(), ((FGECubicCurve) element).getCtrlP2(), ((FGECubicCurve) element).getP2());
		}
	}

	public void removeFromPathElements(GeneralShapePathElement<?> element) {
		logger.warning("Not implemented yet");
	}

	public GeneralShapePathElement<?> getElementAt(int index) {
		return pathElements.get(index);
	}

	public void beginAtPoint(FGEPoint p) {
		pathElements.clear();
		currentPoint = p;
	}

	public void addSegment(Point2D p) {
		addSegment(new FGEPoint(p));
	}

	public void addSegment(FGEPoint p) {
		if (currentPoint == null) {
			throw new IllegalArgumentException("No current point defined");
		}
		pathElements.add(new FGESegment(currentPoint, p));
		updateGeneralPath();
	}

	public void addQuadCurve(Point2D cp, Point2D p) {
		addQuadCurve(new FGEPoint(cp), new FGEPoint(p));
	}

	public void addQuadCurve(FGEPoint cp, FGEPoint p) {
		if (currentPoint == null) {
			throw new IllegalArgumentException("No current point defined");
		}
		pathElements.add(new FGEQuadCurve(currentPoint, cp, p));
		updateGeneralPath();
	}

	public void addCubicCurve(Point2D cp1, Point2D cp2, Point2D p) {
		addCubicCurve(new FGEPoint(cp1), new FGEPoint(cp2), new FGEPoint(p));
	}

	public void addCubicCurve(FGEPoint cp1, FGEPoint cp2, FGEPoint p) {
		if (currentPoint == null) {
			throw new IllegalArgumentException("No current point defined");
		}
		pathElements.add(new FGECubicCurve(currentPoint, cp1, cp2, p));
		currentPoint = p;
		updateGeneralPath();
	}

	public Closure getClosure() {
		return closure;
	}

	public void setClosure(Closure aClosure) {
		if (aClosure != closure) {
			this.closure = aClosure;
			updateGeneralPath();
		}
	}

	@Override
	public boolean getIsFilled() {
		return getClosure() == Closure.OPEN_NOT_FILLED || getClosure() == Closure.CLOSED_NOT_FILLED;
	}

	@Override
	public void setIsFilled(boolean filled) {
		if (filled) {
			if (getClosure() == Closure.OPEN_NOT_FILLED) {
				setClosure(Closure.OPEN_FILLED);
			} else if (getClosure() == Closure.CLOSED_NOT_FILLED) {
				setClosure(Closure.CLOSED_FILLED);
			}
		} else {
			if (getClosure() == Closure.OPEN_FILLED) {
				setClosure(Closure.OPEN_NOT_FILLED);
			} else if (getClosure() == Closure.CLOSED_FILLED) {
				setClosure(Closure.CLOSED_NOT_FILLED);
			}
		}
	}

	public void refresh() {
		updateGeneralPath();
	}

	private void updateGeneralPath() {
		_controlPoints.clear();
		_generalPath = new GeneralPath();
		FGEPoint current = null;
		for (GeneralShapePathElement<?> e : pathElements) {
			if (e instanceof FGESegment) {
				if (current == null) {
					current = ((FGESegment) e).getP1();
					_generalPath.moveTo((float) current.x, (float) current.y);
					_controlPoints.add(current);
				}
				current = ((FGESegment) e).getP2();
				_controlPoints.add(current);
				_generalPath.lineTo((float) current.x, (float) current.y);
			} else if (e instanceof FGEQuadCurve) {
				if (current == null) {
					current = ((FGEQuadCurve) e).getP1();
					_generalPath.moveTo((float) current.x, (float) current.y);
					_controlPoints.add(current);
				}
				FGEPoint cp = ((FGEQuadCurve) e).getCtrlPoint();
				current = ((FGEQuadCurve) e).getP2();
				_controlPoints.add(current);
				_generalPath.quadTo((float) cp.x, (float) cp.y, (float) current.x, (float) current.y);
			} else if (e instanceof FGECubicCurve) {
				if (current == null) {
					current = ((FGECubicCurve) e).getP1();
					_generalPath.moveTo((float) current.x, (float) current.y);
					_controlPoints.add(current);
				}
				FGEPoint cp1 = ((FGECubicCurve) e).getCtrlP1();
				FGEPoint cp2 = ((FGECubicCurve) e).getCtrlP2();
				current = ((FGECubicCurve) e).getP2();
				_controlPoints.add(current);
				_generalPath.curveTo((float) cp1.x, (float) cp1.y, (float) cp2.x, (float) cp2.y, (float) current.x, (float) current.y);
			}
		}
		if (closure == Closure.CLOSED_FILLED || closure == Closure.CLOSED_NOT_FILLED) {
			_generalPath.closePath();
		}
		_generalPath.setWindingRule(Path2D.WIND_NON_ZERO);
	}

	@Override
	public List<FGEPoint> getControlPoints() {
		return _controlPoints;
	}

	@Override
	public String getStringRepresentation() {
		return "FGEGeneralShape: " + pathElements;
	}

	@Override
	public boolean containsArea(FGEArea a) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsLine(FGEAbstractLine<?> l) {
		/*if (l instanceof FGESegment) {
			return containsPoint(((FGESegment)l).getP1()) && containsPoint(((FGESegment)l).getP2());
		}*/
		// Obviously false: might be concave
		return false;
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		return contains(p.x, p.y);
	}

	@Override
	public FGEArea exclusiveOr(FGEArea area) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public FGEArea getAnchorAreaFrom(org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection direction) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		// TODO Auto-generated method stub
		return aPoint;
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection orientation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void paint(AbstractFGEGraphics g) {
		if (closure == Closure.OPEN_FILLED || closure == Closure.CLOSED_FILLED) {
			g.useDefaultBackgroundStyle();
			g.fillGeneralShape(this);
		}
		g.useDefaultForegroundStyle();
		g.drawGeneralShape(this);
	}

	@Override
	public FGEGeneralShape transform(AffineTransform t) {
		// TODO
		return this;
	}

	@Override
	public FGEArea intersect(FGEArea area) {
		FGEIntersectionArea returned = new FGEIntersectionArea(this, area);
		if (returned.isDevelopable()) {
			return returned.makeDevelopped();
		} else {
			return returned;
		}
	}

	@Override
	public FGEArea substract(FGEArea area, boolean isStrict) {
		return new FGESubstractionArea(this, area, isStrict);
	}

	@Override
	public FGEArea union(FGEArea area) {
		if (containsArea(area)) {
			return clone();
		}
		if (area.containsArea(this)) {
			return area.clone();
		}

		return new FGEUnionArea(this, area);
	}

	@Override
	public FGERectangle getBoundingBox() {
		Rectangle2D r = getGeneralPath().getBounds2D();
		return new FGERectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight(), Filling.FILLED);
	}

	@Override
	public FGEPoint getCenter() {
		return getBoundingBox().getCenter();
	}

	@Override
	public FGEPoint nearestOutlinePoint(FGEPoint aPoint) {
		// TODO
		return aPoint;
	}

	/**
	 * Return nearest point from point "from" following supplied orientation
	 * 
	 * Returns null if no intersection was found
	 * 
	 * @param from
	 *            point from which we are coming to area
	 * @param orientation
	 *            orientation we are coming from
	 * @return
	 */
	@Override
	public FGEPoint nearestPointFrom(FGEPoint from, SimplifiedCardinalDirection orientation) {
		@SuppressWarnings("unused")
		FGEHalfLine hl = FGEHalfLine.makeHalfLine(from, orientation);

		// TODO not implemented
		return null;
	}

	@Override
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean contains(Rectangle2D r) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(double x, double y) {
		return _generalPath.contains(x, y);
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return _generalPath.contains(x, y, w, h);
	}

	@Override
	public Rectangle getBounds() {
		return _generalPath.getBounds();
	}

	@Override
	public Rectangle2D getBounds2D() {
		return _generalPath.getBounds2D();
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return _generalPath.getPathIterator(at);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return _generalPath.getPathIterator(at, flatness);
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return _generalPath.intersects(r);
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return _generalPath.intersects(x, y, w, h);
	}

	/**
	 * Creates a new object of the same class and with the same contents as this object.
	 * 
	 * @return a clone of this instance.
	 * @exception OutOfMemoryError
	 *                if there is not enough memory.
	 * @see java.lang.Cloneable
	 * @since 1.2
	 */
	@Override
	public FGEGeneralShape<O> clone() {
		try {
			return (FGEGeneralShape<O>) super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	public GeneralPath getGeneralPath() {
		return _generalPath;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGEGeneralShape) {
			FGEGeneralShape p = (FGEGeneralShape) obj;
			if (getClosure() != p.getClosure()) {
				return false;
			}
			if (getPathElements().size() != p.getPathElements().size()) {
				return false;
			}
			for (int i = 0; i < getPathElements().size(); i++) {
				if (!getElementAt(i).equals(p.getElementAt(i))) {
					return false;
				}
			}
			return true;
		}
		return super.equals(obj);
	}

	/**
	 * This area is finite, so always return true
	 */
	@Override
	public final boolean isFinite() {
		return true;
	}

	/**
	 * This area is finite, so always return null
	 */
	@Override
	public final FGERectangle getEmbeddingBounds() {
		return getBoundingBox();
	}

}
