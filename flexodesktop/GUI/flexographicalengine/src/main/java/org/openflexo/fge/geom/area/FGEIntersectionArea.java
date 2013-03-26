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
package org.openflexo.fge.geom.area;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.graphics.FGEGraphics;

public class FGEIntersectionArea extends FGEOperationArea {

	private static final Logger logger = Logger.getLogger(FGEIntersectionArea.class.getPackage().getName());

	private Vector<FGEArea> _objects;

	public static FGEArea makeIntersection(FGEArea... objects) {
		return makeIntersection(Arrays.asList(objects));
	}

	public static FGEArea makeIntersection(List<? extends FGEArea> objects) {
		List<FGEArea> nonEmbeddedObjects = new ArrayList<FGEArea>();
		for (int i = 0; i < objects.size(); i++) {
			FGEArea a1 = objects.get(i);
			if (a1 instanceof FGEEmptyArea) {
				return new FGEEmptyArea();
			}
			boolean isAlreadyContained = false;
			for (FGEArea a2 : nonEmbeddedObjects) {
				if (a2.containsArea(a1)) {
					// forget a1;
					isAlreadyContained = true;
				}
			}
			if (!isAlreadyContained) {
				Vector<FGEArea> noMoreNecessaryObjects = new Vector<FGEArea>();
				for (FGEArea a2 : nonEmbeddedObjects) {
					if (a1.containsArea(a2)) {
						noMoreNecessaryObjects.add(a2);
					}
				}
				for (FGEArea removeThat : noMoreNecessaryObjects) {
					nonEmbeddedObjects.remove(removeThat);
				}
				nonEmbeddedObjects.add(a1);
			}
		}

		// Try to make intersection both/both with all objects
		// > reduce intersection as much as possible

		boolean tryToIntersectBothBoth = true;

		while (tryToIntersectBothBoth) {
			tryToIntersectBothBoth = false;
			for (int i = 0; i < nonEmbeddedObjects.size(); i++) {
				FGEArea a1 = nonEmbeddedObjects.get(i);
				for (int j = i + 1; j < nonEmbeddedObjects.size(); j++) {
					FGEArea a2 = nonEmbeddedObjects.get(j);
					FGEArea intersect = a1.intersect(a2);
					if (!(intersect instanceof FGEIntersectionArea)) {
						if (intersect instanceof FGEEmptyArea) {
							return new FGEEmptyArea();
						}
						nonEmbeddedObjects.remove(a1);
						nonEmbeddedObjects.remove(a2);
						nonEmbeddedObjects.add(intersect);
						i = j = nonEmbeddedObjects.size();
						tryToIntersectBothBoth = true;
					}
				}
			}
		}

		if (nonEmbeddedObjects.size() == 0) {
			return new FGEEmptyArea();
		} else if (nonEmbeddedObjects.size() == 1) {
			return nonEmbeddedObjects.get(0).clone();
		} else {
			FGEIntersectionArea returned = new FGEIntersectionArea(nonEmbeddedObjects);

			if (returned.isDevelopable()) {
				return returned.makeDevelopped();
			}

			return returned;
		}
	}

	public static void main(String[] args) {
		FGELine line1 = new FGELine(new FGEPoint(0, 0), new FGEPoint(0, 1));
		FGELine line2 = new FGELine(new FGEPoint(0, 1), new FGEPoint(1, 1));
		FGELine line3 = new FGELine(new FGEPoint(0, 0), new FGEPoint(1, 1));
		FGELine line4 = new FGELine(new FGEPoint(0, 1), new FGEPoint(1, 0));

		System.out.println("Intersection1: " + makeIntersection(line1, line2));
		System.out.println("Intersection2: " + makeIntersection(line3, line2));
		System.out.println("Intersection3: " + makeIntersection(line1, line2, line3));
		System.out.println("Intersection4: " + makeIntersection(line1, line2, line4));
	}

	public FGEIntersectionArea() {
		super();
		_objects = new Vector<FGEArea>();
	}

	public FGEIntersectionArea(FGEArea... objects) {
		this();
		for (FGEArea o : objects) {
			_objects.add(o.clone());
		}
	}

	public FGEIntersectionArea(List<? extends FGEArea> objects) {
		this();
		for (FGEArea o : objects) {
			_objects.add(o.clone());
		}
	}

	public Vector<FGEArea> getObjects() {
		return _objects;
	}

	public void setObjects(Vector<FGEArea> objects) {
		if (_objects != null) {
			_objects.clear();
		} else {
			_objects = new Vector<FGEArea>();
		}
		for (FGEArea o : objects) {
			_objects.add(o.clone());
		}
	}

	public void addToObjects(FGEArea obj) {
		_objects.add(obj.clone());
	}

	public void removeFromObjects(FGEArea obj) {
		_objects.remove(obj);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("FGEIntersectionArea: nObjects=" + _objects.size() + "\n");
		for (int i = 0; i < _objects.size(); i++) {
			sb.append(" " + (i + 1) + " > " + _objects.elementAt(i) + "\n");
		}
		return sb.toString();
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		if (_objects.size() == 0) {
			return false;
		}
		for (FGEArea a : _objects) {
			if (!a.containsPoint(p)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean containsLine(FGEAbstractLine l) {
		if (_objects.size() == 0) {
			return false;
		}
		for (FGEArea a : _objects) {
			if (!a.containsLine(l)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean containsArea(FGEArea a) {
		if (a instanceof FGEPoint) {
			return containsPoint((FGEPoint) a);
		}
		if (a instanceof FGELine) {
			return containsLine((FGELine) a);
		}
		if (a instanceof FGEShape) {
			return FGEShape.AreaComputation.isShapeContainedInArea((FGEShape) a, this);
		}
		return false;
	}

	@Override
	public FGEArea transform(AffineTransform t) {
		FGEArea[] all = new FGEArea[_objects.size()];
		for (int i = 0; i < _objects.size(); i++) {
			all[i] = _objects.get(i).transform(t);
		}
		return new FGEIntersectionArea(all);
	}

	@Override
	public void paint(FGEGraphics g) {
		// TODO
		// Use a finite method, using Java2D to perform shape computation
		// in the area defined by supplied FGEGraphics

		for (FGEArea a : getObjects()) {
			a.paint(g);
		}
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		}

		if (getObjects().size() == 0) {
			logger.warning("getNearestPoint() called for " + this + ": no objects !");
			return null;
		}

		// Little heuristic to find nearest point
		// (not working in all cases !)
		Vector<FGEPoint> potentialPoints = new Vector<FGEPoint>();
		for (int i = 0; i < getObjects().size(); i++) {
			FGEPoint tryThis = _getApproximatedNearestPoint(aPoint, i);
			if (tryThis != null) {
				potentialPoints.add(tryThis);
			}
		}

		if (potentialPoints.size() == 0) {
			logger.warning("getNearestPoint() called for " + this
					+ ": Not implemented yet (tried to compute heuristic, but failing to obtain a result)");
			return null;
		}

		else {
			double bestDistance = Double.POSITIVE_INFINITY;
			FGEPoint bestPoint = null;

			for (FGEPoint p : potentialPoints) {
				double dist = FGEPoint.distance(p, aPoint);
				if (dist < bestDistance) {
					bestPoint = p;
					bestDistance = dist;
				}
			}

			return bestPoint;
		}

	}

	/**
	 * Little heuristic to find nearest point for a formal intersection (not working in all cases !)
	 * 
	 * @param aPoint
	 * @param firstTriedObjectIndex
	 * @return
	 */
	private FGEPoint _getApproximatedNearestPoint(FGEPoint aPoint, int firstTriedObjectIndex) {
		int MAX_TRIES = 10;
		int tries = 0;
		FGEPoint returned = aPoint.clone();

		// System.out.println("_getApproximatedNearestPoint() called for "+aPoint+" on "+this);

		while (!containsPoint(returned) && tries < MAX_TRIES) {
			FGEArea current = getObjects().elementAt(firstTriedObjectIndex);
			firstTriedObjectIndex++;
			if (firstTriedObjectIndex >= getObjects().size()) {
				firstTriedObjectIndex = 0;
			}
			// System.out.println("> Try on "+current);
			// System.out.println("  obtained "+current.getNearestPoint(returned)+" from "+returned);
			returned = current.getNearestPoint(returned);
			tries++;
		}

		if (containsPoint(returned)) {
			// System.out.println("Found this: "+returned);
			return returned;
		}

		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGEIntersectionArea) {
			FGEIntersectionArea inters = (FGEIntersectionArea) obj;
			if (getObjects().size() != inters.getObjects().size()) {
				return false;
			}
			for (int i = 0; i < getObjects().size(); i++) {
				FGEArea a = getObjects().get(i);
				// Equals even if not same order
				if (inters.getObjects().indexOf(a) == -1) {
					return false;
				}
			}
			return true;
		}
		return super.equals(obj);
	}

	/**
	 * Return a flag indicating if this area is finite or not An union area is finite if and only if at least on area is finite
	 * 
	 * @return
	 */
	@Override
	public final boolean isFinite() {
		if (_objects.size() == 0) {
			return true;
		}
		for (FGEArea a : _objects) {
			if (a.isFinite()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If this area is finite, return embedding bounds as a FGERectangle (this is not guaranteed to be optimal in some cases). For
	 * non-finite areas (if this area contains a least one non-finite area), return null
	 * 
	 * @return
	 */
	@Override
	public final FGERectangle getEmbeddingBounds() {
		if (!isFinite()) {
			return null;
		}
		FGERectangle returned = null;
		for (FGEArea a : _objects) {
			FGERectangle r = a.getEmbeddingBounds();
			if (r != null) {
				if (returned == null) {
					returned = r;
				} else {
					FGEArea intersect = returned.intersect(r);
					if (intersect instanceof FGERectangle) {
						returned = (FGERectangle) intersect;
					} else if (!(intersect instanceof FGEIntersectionArea) && intersect.isFinite()) {
						returned = intersect.getEmbeddingBounds();
					} else {
						logger.warning("Cannot compute embedding bounds for " + this);
						return null;
					}
				}
			}
		}
		return returned;
	}

	public boolean isDevelopable() {
		if (getObjects().size() <= 1) {
			return false;
		}

		return _findFirstUnionArea(this) != null || _findFirstSubstractionArea(this) != null;

	}

	private static FGEUnionArea _findFirstUnionArea(FGEIntersectionArea intersection) {
		for (FGEArea o : intersection.getObjects()) {
			if (o instanceof FGEUnionArea) {
				return (FGEUnionArea) o;
			}
		}

		return null;
	}

	private static FGESubstractionArea _findFirstSubstractionArea(FGEIntersectionArea intersection) {
		for (FGEArea o : intersection.getObjects()) {
			if (o instanceof FGESubstractionArea) {
				return (FGESubstractionArea) o;
			}
		}

		return null;
	}

	public FGEArea makeDevelopped() {
		if (!isDevelopable()) {
			return clone();
		}

		FGEArea returned = developUnions();

		if (returned instanceof FGEIntersectionArea) {
			FGEIntersectionArea intersection = (FGEIntersectionArea) returned;
			if (!intersection.isDevelopable()) {
				return clone();
			}
			returned = developSubstractions();
		}

		return returned;
	}

	private FGEArea developUnions() {
		if (_findFirstUnionArea(this) == null) {
			return clone();
		}

		FGEUnionArea union = _findFirstUnionArea(this);
		FGEArea area = null;
		Vector<FGEArea> others = new Vector<FGEArea>();
		for (FGEArea o : getObjects()) {
			if (o != union) {
				if (area == null) {
					area = o;
				} else {
					others.add(o);
				}
			}
		}
		if (area == null) {
			logger.warning("Inconsistent data while computing developUnions() in FGEIntersectionArea");
			return clone();
		} else {

			// logger.info("develop "+this);

			FGEArea developedArea = _developAsIntersection(union, area);
			if (others.size() > 0) {
				others.add(developedArea);
				FGEIntersectionArea result = new FGEIntersectionArea(others);
				if (result.isDevelopable()) {
					return result.developUnions();
				}
			}

			// logger.info("obtain "+developedArea);

			return developedArea;
		}
	}

	private FGEArea _developAsIntersection(FGEUnionArea union, FGEArea area) {
		Vector<FGEArea> unionObjects = new Vector<FGEArea>();

		for (FGEArea o : union.getObjects()) {
			unionObjects.add(o.intersect(area));
		}

		return FGEUnionArea.makeUnion(unionObjects);
	}

	private FGEArea developSubstractions() {
		if (_findFirstSubstractionArea(this) == null) {
			return clone();
		}

		FGESubstractionArea sub = _findFirstSubstractionArea(this);
		FGEArea area = null;
		Vector<FGEArea> others = new Vector<FGEArea>();
		for (FGEArea o : getObjects()) {
			if (o != sub) {
				if (area == null) {
					area = o;
				} else {
					others.add(o);
				}
			}
		}
		if (area == null) {
			logger.warning("Inconsistent data while computing developSubstractions() in FGEIntersectionArea");
			return clone();
		} else {

			// logger.info("develop "+this);

			FGEArea developedArea = _developAsSubstraction(sub, area);
			if (others.size() > 0) {
				others.add(developedArea);
				FGEIntersectionArea result = new FGEIntersectionArea(others);
				if (result.isDevelopable()) {
					return result.developSubstractions();
				}
			}

			// logger.info("obtain "+developedArea);

			return developedArea;
		}
	}

	private FGEArea _developAsSubstraction(FGESubstractionArea sub, FGEArea area) {
		return FGESubstractionArea.makeSubstraction(sub.getContainerArea().intersect(area), sub.getSubstractedArea().intersect(area),
				sub.isStrict(), false);
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
		FGEHalfLine hl = FGEHalfLine.makeHalfLine(from, orientation);
		FGEArea intersect = intersect(hl);

		if (intersect instanceof FGEIntersectionArea) {
			// Avoid infinite loop
			logger.warning("Cannot find nearest from " + from + " from " + orientation);
			return null;
		}

		return intersect.nearestPointFrom(from, orientation);
	}

}
