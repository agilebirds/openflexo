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
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGEArc;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGEPolylin;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.graphics.FGEGraphics;

public class FGEUnionArea extends FGEOperationArea {

	private static final Logger logger = Logger.getLogger(FGEUnionArea.class.getPackage().getName());

	private Vector<FGEArea> _objects;

	public static FGEArea makeUnion(FGEArea... objects) {
		Vector<FGEArea> v = new Vector<FGEArea>();
		for (FGEArea o : objects) {
			v.add(o.clone());
		}
		return makeUnion(v);
	}

	public static FGEArea makeUnion(List<? extends FGEArea> objects) {
		return makeUnion(objects, true);
	}

	private static FGEArea makeUnion(List<? extends FGEArea> objects, boolean tryToReduceUnionByConcatenation) {
		List<? extends FGEArea> objectsToTakeUnderAccount = reduceUnionByEmbedding(objects);

		if (tryToReduceUnionByConcatenation) {

			List<? extends FGEArea> concatenedObjects = reduceUnionByConcatenation(objectsToTakeUnderAccount);

			if (concatenedObjects.size() == 0) {
				return new FGEEmptyArea();
			} else if (concatenedObjects.size() == 1) {
				return concatenedObjects.get(0).clone();
			}

			// System.out.println("Concatened objects: ");
			// for (FGEArea o : concatenedObjects) System.out.println(" > "+o);

			return new FGEUnionArea(concatenedObjects);
		}

		else {
			if (objectsToTakeUnderAccount.size() == 1) {
				return objectsToTakeUnderAccount.get(0);
			}
			return new FGEUnionArea(objectsToTakeUnderAccount);
		}
	}

	private static List<? extends FGEArea> reduceUnionByEmbedding(List<? extends FGEArea> objects) {
		Vector<FGEArea> objectsToTakeUnderAccount = new Vector<FGEArea>();

		for (int i = 0; i < objects.size(); i++) {
			FGEArea o = objects.get(i);
			if (o instanceof FGEEmptyArea) {
				// Ignore
			} else {
				boolean isAlreadyContained = false;
				for (FGEArea a : objectsToTakeUnderAccount) {
					if (a.containsArea(o)) {
						isAlreadyContained = true;
					}
				}
				if (!isAlreadyContained) {
					Vector<FGEArea> noMoreNecessaryObjects = new Vector<FGEArea>();
					for (FGEArea a2 : objectsToTakeUnderAccount) {
						if (o.containsArea(a2)) {
							noMoreNecessaryObjects.add(a2);
						}
					}
					for (FGEArea removeThat : noMoreNecessaryObjects) {
						objectsToTakeUnderAccount.remove(removeThat);
					}
					objectsToTakeUnderAccount.add(o);
				}

			}
		}

		return objectsToTakeUnderAccount;
	}

	private static List<? extends FGEArea> reduceUnionByConcatenation(List<? extends FGEArea> objectsToTakeUnderAccount) {
		List<? extends FGEArea> listOfObjects = objectsToTakeUnderAccount;

		boolean continueReducing = true;

		while (continueReducing) {

			continueReducing = false;

			Vector<FGEArea> concatenedObjects = new Vector<FGEArea>();
			concatenedObjects.addAll(listOfObjects);

			for (int i = 0; i < listOfObjects.size(); i++) {
				FGEArea a1 = listOfObjects.get(i);
				for (int j = i + 1; j < listOfObjects.size(); j++) {
					FGEArea a2 = listOfObjects.get(j);
					FGEArea concatenation = a1.union(a2);
					if (!(concatenation instanceof FGEUnionArea)) {
						// Those 2 objects are concatenable, do it
						// logger.info("Concatenate "+a1+" and "+a2+" to form "+concatenation);
						concatenedObjects.add(concatenation);
						concatenedObjects.remove(a1);
						concatenedObjects.remove(a2);
						continueReducing = true;
						break;
					}
				}
				if (continueReducing) {
					break;
				}
			}

			listOfObjects = reduceUnionByEmbedding(concatenedObjects);

		}

		return listOfObjects;
	}

	public static void main(String[] args) {
		FGEPoint pt = new FGEPoint(0, 0);
		FGELine line1 = new FGELine(new FGEPoint(0, 0), new FGEPoint(1, 0));
		FGELine line2 = new FGELine(new FGEPoint(0, 0), new FGEPoint(1, 1));
		FGERectangle rectangle = new FGERectangle(new FGEPoint(0, 0), new FGEPoint(1, 1), Filling.FILLED);
		FGEHalfPlane hp = new FGEHalfPlane(line1, new FGEPoint(1, 1));

		System.out.println("Union1: " + makeUnion(line1, line2));
		System.out.println("Union2: " + makeUnion(rectangle, line1, pt));
		System.out.println("Union3: " + makeUnion(pt, line1, rectangle, hp));
		System.out.println("Union4: " + makeUnion(pt, line1, rectangle, hp, line2));
	}

	public FGEUnionArea() {
		super();
		_objects = new Vector<FGEArea>();
	}

	public FGEUnionArea(FGEArea... objects) {
		this();
		for (FGEArea o : objects) {
			addArea(o.clone());
		}
		// logger.info(">>> Creating FGEUnionArea with "+objects);
		if (objects.length == 1) {
			logger.warning("Called constructor for FGEUnionArea with 1 object");
		}

	}

	public FGEUnionArea(List<? extends FGEArea> objects) {
		this();
		for (FGEArea o : objects) {
			addArea(o.clone());
		}
		// logger.info(">>> Creating FGEUnionArea with "+objects);
		if (objects.size() == 1) {
			logger.warning("Called constructor for FGEUnionArea with 1 object");
		}

		// if (objects.size() == 2 && objects.get(0) instanceof FGEArc && objects.get(1) instanceof FGEPoint ) {
		// (new Exception("------------ Ca vient de la ce truc bizarre !!!")).printStackTrace();
		// }
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
			addArea(o.clone());
		}
	}

	public void addToObjects(FGEArea obj) {
		addArea(obj.clone());
	}

	public void removeFromObjects(FGEArea obj) {
		removeArea(obj);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("FGEUnionArea: nObjects=" + _objects.size() + "\n");
		for (int i = 0; i < _objects.size(); i++) {
			sb.append(" " + (i + 1) + " > " + _objects.elementAt(i) + "\n");
		}
		return sb.toString();
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		for (FGEArea a : _objects) {
			if (a.containsPoint(p)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsLine(FGEAbstractLine l) {
		// TODO: what if Union of two objects contains the line ? Implement this...
		for (FGEArea a : _objects) {
			if (a.containsLine(l)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsPolylin(FGEPolylin p) {
		for (FGESegment s : p.getSegments()) {
			if (!containsLine(s)) {
				return false;
			}
		}
		return true;
	}

	public boolean containsPolygon(FGEPolygon p) {
		for (FGESegment s : p.getSegments()) {
			if (!containsLine(s)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public FGEArea transform(AffineTransform t) {
		FGEArea[] all = new FGEArea[_objects.size()];
		for (int i = 0; i < _objects.size(); i++) {
			all[i] = _objects.get(i).transform(t);
		}
		return new FGEUnionArea(all);
	}

	@Override
	public void paint(FGEGraphics g) {
		for (FGEArea a : _objects) {
			a.paint(g);
		}
	}

	public void addArea(FGEArea area) {
		if (area instanceof FGEEmptyArea) {
			return;
		}

		if (containsArea(area)) {
			return;
		}

		if (area instanceof FGEUnionArea) {
			for (FGEArea a : ((FGEUnionArea) area).getObjects()) {
				addArea(a);
			}
			return;
		}

		Vector<FGEArea> uselessObjects = new Vector<FGEArea>();

		for (FGEArea a : _objects) {
			if (area.containsArea(a)) {
				uselessObjects.add(a);
			}
		}

		for (FGEArea a : uselessObjects) {
			_objects.remove(a);
		}

		_objects.add(area);
	}

	public void removeArea(FGEArea area) {
		if (!containsArea(area)) {
			return;
		}

		_objects.remove(area);
	}

	@Override
	public boolean containsArea(FGEArea a) {
		for (FGEArea o : _objects) {
			if (o.containsArea(a)) {
				return true;
			}
		}
		if (a instanceof FGEPoint) {
			return containsPoint((FGEPoint) a);
		}
		if (a instanceof FGEAbstractLine) {
			return containsLine((FGEAbstractLine) a);
		}
		if (a instanceof FGEPolylin) {
			return containsPolylin((FGEPolylin) a);
		}
		if (a instanceof FGEPolygon) {
			return containsPolygon((FGEPolygon) a);
		}
		if (a instanceof FGEShape) {
			return FGEShape.AreaComputation.isShapeContainedInArea((FGEShape) a, this);
		}
		return false;
	}

	public <T> boolean isUnionOf(Class<T> aClass) {
		if (_objects.size() == 0) {
			return false;
		}
		for (FGEArea a : _objects) {
			if (!aClass.isAssignableFrom(a.getClass())) {
				return false;
			}
		}
		return true;
	}

	public boolean isUnionOfFiniteGeometricObjects() {
		return (isUnionOf(FGEGeometricObject.class) && isFinite());
	}

	public boolean isUnionOfPoints() {
		return isUnionOf(FGEPoint.class);
	}

	public boolean isUnionOfSegments() {
		return isUnionOf(FGESegment.class);
	}

	public boolean isUnionOfArcs() {
		return isUnionOf(FGEArc.class);
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		}

		Vector<FGEPoint> pts = new Vector<FGEPoint>();
		for (FGEArea o : getObjects()) {
			pts.add(o.getNearestPoint(aPoint));
		}
		return FGEPoint.getNearestPoint(aPoint, pts);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGEUnionArea) {
			FGEUnionArea u = (FGEUnionArea) obj;
			if (getObjects().size() != u.getObjects().size()) {
				return false;
			}
			for (int i = 0; i < getObjects().size(); i++) {
				FGEArea a = getObjects().get(i);
				// Equals even if not same order
				if (u.getObjects().indexOf(a) == -1) {
					return false;
				}
			}
			return true;
		}
		return super.equals(obj);
	}

	@Override
	public FGEArea intersect(FGEArea area) {
		if (area.containsArea(this)) {
			return this.clone();
		}
		if (containsArea(area)) {
			return area.clone();
		}

		Vector<FGEArea> intersections = new Vector<FGEArea>();

		for (FGEArea o : _objects) {
			intersections.add(o.intersect(area));
		}

		return makeUnion(intersections);
	}

	@Override
	public FGEArea union(FGEArea area) {
		if (containsArea(area)) {
			return clone();
		}
		if (area.containsArea(this)) {
			return area.clone();
		}

		Vector<FGEArea> objects = new Vector<FGEArea>();
		objects.addAll(getObjects());
		objects.add(area);
		return makeUnion(objects, false);
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		/*Vector<FGEArea> newObjects = new Vector<FGEArea>();
		for (FGEArea a : getObjects()) {
			newObjects.add(a.getOrthogonalPerspectiveArea(orientation));
		}
		return new FGEUnionArea(newObjects);*/
		FGEArea anchorArea = getAnchorAreaFrom(orientation);
		if (anchorArea instanceof FGEUnionArea) {
			FGEUnionArea unionAnchorArea = (FGEUnionArea) anchorArea;
			Vector<FGEArea> newObjects = new Vector<FGEArea>();
			for (FGEArea a : unionAnchorArea.getObjects()) {
				newObjects.add(a.getOrthogonalPerspectiveArea(orientation));
			}
			return new FGEUnionArea(newObjects);
		} else {
			return anchorArea.getOrthogonalPerspectiveArea(orientation);
		}
	}

	@Override
	public FGEArea getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		if (isUnionOfSegments()) {
			Vector<FGESegment> segments = new Vector<FGESegment>();
			for (FGEArea s : getObjects()) {
				segments.add((FGESegment) s);
			}
			return FGEPolylin.computeVisibleSegmentsFrom(direction, segments);
		}
		Vector<FGEArea> newObjects = new Vector<FGEArea>();
		for (FGEArea a : getObjects()) {
			newObjects.add(a.getAnchorAreaFrom(direction));
		}
		return new FGEUnionArea(newObjects);
	}

	/**
	 * Return a flag indicating if this area is finite or not An union area is finite if and only if all areas are finite
	 * 
	 * @return
	 */
	@Override
	public final boolean isFinite() {
		for (FGEArea a : _objects) {
			if (!a.isFinite()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * If this area is finite, return embedding bounds as a FGERectangle (this is not guaranteed to be optimal in some cases). For
	 * non-finite areas (if this area contains a least one non-finite area), return null
	 * 
	 * @return
	 */
	@Override
	public final FGERectangle getEmbeddingBounds() {
		FGERectangle returned = null;
		for (FGEArea a : _objects) {
			if (!a.isFinite()) {
				return null;
			}
			FGERectangle r = a.getEmbeddingBounds();
			if (r != null) {
				if (returned == null) {
					returned = r;
				} else {
					returned = returned.rectangleUnion(r);
				}
			}
		}
		return returned;
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
		if (containsPoint(from)) {
			return from.clone();
		}

		Vector<FGEPoint> pts = new Vector<FGEPoint>();
		for (FGEArea o : getObjects()) {
			pts.add(o.nearestPointFrom(from, orientation));
		}
		return FGEPoint.getNearestPoint(from, pts);
	}

	/*public void simplify()
	{
		Vector<FGEArea> newObjects = new Vector<FGEArea>();
		for (FGEArea o : getObjects()) {
			if (o instanceof FGEUnionArea) {
				FGEUnionArea union = (FGEUnionArea)
			}
		}
	}*/

}
