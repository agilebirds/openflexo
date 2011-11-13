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

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.BackgroundStyle.Texture.TextureType;
import org.openflexo.fge.graphics.ForegroundStyle.DashStyle;

public class FGERectPolylin extends FGEPolylin {

	static final Logger logger = Logger.getLogger(FGERectPolylin.class.getPackage().getName());

	private FGEArea startArea;
	private SimplifiedCardinalDirection startOrientation;
	private FGEArea endArea;
	private SimplifiedCardinalDirection endOrientation;

	private double overlapX = 10;
	private double overlapY = 10;

	private boolean straightWhenPossible = false;

	private FGEArea resultingStartArea;
	private FGEArea resultingEndArea;

	// TODO: debug only remove this
	protected static ForegroundStyle focusedForegroundStyle;
	protected static BackgroundStyle focusedBackgroundStyle;

	private boolean respectAllConstraints = true;

	// TODO: debug only remove this
	static {
		focusedForegroundStyle = ForegroundStyle.makeStyle(Color.RED, 0.5f, DashStyle.MEDIUM_DASHES);
		focusedBackgroundStyle = BackgroundStyle.makeTexturedBackground(TextureType.TEXTURE1, Color.RED, Color.WHITE);
		focusedBackgroundStyle.setUseTransparency(true);
		focusedBackgroundStyle.setTransparencyLevel(0.1f);
	}

	public FGERectPolylin() {
		super();
	}

	public FGERectPolylin(List<FGEPoint> points) {
		super(points);
	}

	public FGERectPolylin(FGEPoint... points) {
		super(makeList(points));
	}

	private static List<FGEPoint> makeList(FGEPoint... points) {
		Vector<FGEPoint> returned = new Vector<FGEPoint>();
		for (FGEPoint pt : points)
			returned.add(pt);
		return returned;
	}

	public FGERectPolylin(List<FGEPoint> points, boolean straightWhenPossible, double overlapX, double overlapY) {
		super(points);

		this.straightWhenPossible = straightWhenPossible;

		if (overlapX < 0) {
			logger.warning("Called FGERectPolylin with negative overlapX: " + overlapX);
			overlapX = 0;
		}
		this.overlapX = overlapX;

		if (overlapY < 0) {
			logger.warning("Called FGERectPolylin with negative overlapY: " + overlapY);
			overlapY = 0;
		}
		this.overlapY = overlapY;
	}

	/**
	 * Build and return a FGERectPolylin given supplied start and end orientation, and a bunch of parameters
	 * 
	 * @param startArea
	 * @param startOrientation
	 * @param endArea
	 * @param endOrientation
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public FGERectPolylin(FGEArea aStartArea, SimplifiedCardinalDirection startOrientation, FGEArea anEndArea,
			SimplifiedCardinalDirection endOrientation, boolean straightWhenPossible, double overlapX, double overlapY) {
		super();
		this.startArea = aStartArea.clone();
		if (startArea instanceof FGEShape)
			((FGEShape) startArea).setIsFilled(false);
		this.startOrientation = startOrientation;
		this.endArea = anEndArea.clone();
		if (endArea instanceof FGEShape)
			((FGEShape) endArea).setIsFilled(false);
		this.endOrientation = endOrientation;
		this.straightWhenPossible = straightWhenPossible;

		// logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  FGERectPolylin BEGIN");

		if (overlapX < 0) {
			logger.warning("Called FGERectPolylin with negative overlapX: " + overlapX);
			overlapX = 0;
		}
		this.overlapX = overlapX;

		if (overlapY < 0) {
			logger.warning("Called FGERectPolylin with negative overlapY: " + overlapY);
			overlapY = 0;
		}
		this.overlapY = overlapY;

		computeResultingOrthogonalPerspectiveAreas();
		restoreDefaultLayout();

		respectAllConstraints = true;

		if (getOrientationOfSegment(0) != startOrientation && overlapX > 0 && overlapY > 0 && getFirstSegment() != null
				&& getFirstSegment().getLength() > 0) {
			if (getOrientationOfSegment(0) != null)
				logger.info("Inconsistant start orientation invariant... " + getOrientationOfSegment(0) + " != " + startOrientation);
			if (logger.isLoggable(Level.FINE)) {
				logger.info("startArea=" + startArea);
				logger.info("startOrientation=" + startOrientation);
				logger.info("endArea=" + endArea);
				logger.info("endOrientation=" + endOrientation);
				logger.info("polylin=" + this);
			}
			respectAllConstraints = false;
		}
		if (endOrientation != null && getOrientationOfSegment(getSegmentNb() - 1) != endOrientation.getOpposite() && overlapX > 0
				&& overlapY > 0 && getLastSegment() != null && getLastSegment().getLength() > 0) {
			if (getOrientationOfSegment(getSegmentNb() - 1) != null)
				logger.info("Inconsistant end orientation invariant... " + getOrientationOfSegment(getSegmentNb() - 1) + " != "
						+ endOrientation.getOpposite());
			if (logger.isLoggable(Level.FINE)) {
				logger.info("startArea=" + startArea);
				logger.info("startOrientation=" + startOrientation);
				logger.info("endArea=" + endArea);
				logger.info("endOrientation=" + endOrientation);
				logger.info("polylin=" + this);
			}
			respectAllConstraints = false;
		}
		if (getPointsNb() == 0) {
			logger.info("Inconsistant invariant getPointsNb() == 0");
			if (logger.isLoggable(Level.FINE)) {
				logger.info("startArea=" + startArea);
				logger.info("startOrientation=" + startOrientation);
				logger.info("endArea=" + endArea);
				logger.info("endOrientation=" + endOrientation);
				logger.info("resultingStartArea=" + resultingStartArea);
				logger.info("resultingEndArea=" + resultingEndArea);
			}
			respectAllConstraints = false;
		}
		// logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< FGERectPolylin END");
	}

	/**
	 * Return and build a FGERectPolylin, given some parameters. All orientation solutions are examined, and best solution is returned: -
	 * 1st: try to minimize number of points - 2nd: try to minimize total length of path
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static FGERectPolylin makeShortestRectPolylin(FGEArea startArea, FGEArea endArea, boolean straightWhenPossible, double overlapX,
			double overlapY) {
		return makeShortestRectPolylin(startArea, endArea, straightWhenPossible, overlapX, overlapY,
				(Vector<SimplifiedCardinalDirection>) null, (Vector<SimplifiedCardinalDirection>) null);
	}

	/**
	 * Return and build a FGERectPolylin, given some parameters. All orientation solutions (except those supplied as to be excluded) are
	 * examined, and best solution is returned: - 1st: try to minimize number of points - 2nd: try to minimize total length of path
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static FGERectPolylin makeShortestRectPolylin(FGEArea startArea, FGEArea endArea, boolean straightWhenPossible, double overlapX,
			double overlapY, SimplifiedCardinalDirection startOrientation, SimplifiedCardinalDirection endOrientation) {
		return makeShortestRectPolylin(startArea, endArea, straightWhenPossible, overlapX, overlapY,
				SimplifiedCardinalDirection.allDirectionsExcept(startOrientation),
				SimplifiedCardinalDirection.allDirectionsExcept(endOrientation));
	}

	/**
	 * Return and build a FGERectPolylin, given some parameters. All orientation solutions (except those supplied as to be excluded) are
	 * examined, and best solution is returned: - 1st: try to minimize number of points - 2nd: try to minimize total length of path
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static FGERectPolylin makeShortestRectPolylin(FGEArea startArea, FGEArea endArea, boolean straightWhenPossible, double overlapX,
			double overlapY, Vector<SimplifiedCardinalDirection> excludedStartOrientations,
			Vector<SimplifiedCardinalDirection> excludedEndOrientations) {
		FGERectPolylin returned = null;
		int bestNbOfPoints = Integer.MAX_VALUE;
		double bestLength = Double.POSITIVE_INFINITY;

		for (SimplifiedCardinalDirection startOrientation : SimplifiedCardinalDirection.values()) {
			if (excludedStartOrientations == null || !excludedStartOrientations.contains(startOrientation)) {
				for (SimplifiedCardinalDirection endOrientation : SimplifiedCardinalDirection.values()) {
					if (excludedEndOrientations == null || !excludedEndOrientations.contains(endOrientation)) {
						FGERectPolylin tried = new FGERectPolylin(startArea, startOrientation, endArea, endOrientation,
								straightWhenPossible, overlapX, overlapY);
						if (tried.doesRespectAllConstraints() && tried.getPointsNb() < bestNbOfPoints) {
							returned = tried;
							bestNbOfPoints = tried.getPointsNb();
							bestLength = tried.getLength();
						} else if (tried.doesRespectAllConstraints() && tried.getPointsNb() == bestNbOfPoints) {
							if (tried.getLength() < bestLength) {
								returned = tried;
								bestNbOfPoints = tried.getPointsNb();
								bestLength = tried.getLength();
							}
						}
					}
				}
			}
		}
		return returned;
	}

	/**
	 * Return and build a FGERectPolylin, given some parameters. All orientation solutions (except those supplied as to be excluded) are
	 * examined, and best solution is returned regarding distance between returned polylin and supplied point
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static FGERectPolylin makeShortestRectPolylin(FGEArea startArea, FGEArea endArea, boolean straightWhenPossible, double overlapX,
			double overlapY, FGEPoint minimizeDistanceToThisPoint, Vector<SimplifiedCardinalDirection> excludedStartOrientations,
			Vector<SimplifiedCardinalDirection> excludedEndOrientations) {
		FGERectPolylin returned = null;
		double bestDistance = Double.POSITIVE_INFINITY;

		for (SimplifiedCardinalDirection startOrientation : SimplifiedCardinalDirection.values()) {
			if (excludedStartOrientations == null || !excludedStartOrientations.contains(startOrientation)) {
				for (SimplifiedCardinalDirection endOrientation : SimplifiedCardinalDirection.values()) {
					if (excludedEndOrientations == null || !excludedEndOrientations.contains(endOrientation)) {
						FGERectPolylin tried = new FGERectPolylin(startArea, startOrientation, endArea, endOrientation,
								straightWhenPossible, overlapX, overlapY);
						double distance = FGEPoint
								.distance(tried.getNearestPoint(minimizeDistanceToThisPoint), minimizeDistanceToThisPoint);
						if (tried.doesRespectAllConstraints() && distance < bestDistance) {
							returned = tried;
							bestDistance = distance;
						}
					}
				}
			}
		}
		return returned;
	}

	/**
	 * Return and build a FGERectPolylin linking a start and an end area, and crossing supplied point. All orientation solutions are
	 * examined, and best solution is returned regarding distance between returned polylin and supplied point
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static FGERectPolylin makeRectPolylinCrossingPoint(FGEArea startArea, FGEArea endArea, FGEPoint crossedPoint,
			boolean straightWhenPossible, double overlapX, double overlapY) {
		return makeRectPolylinCrossingPoint(startArea, endArea, crossedPoint, straightWhenPossible, overlapX, overlapY, null, null);
	}

	/**
	 * Return and build a FGERectPolylin linking a start and an end area, and crossing supplied point, using supplied start and end
	 * orientation
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static FGERectPolylin makeRectPolylinCrossingPoint(FGEArea startArea, FGEArea endArea, FGEPoint crossedPoint,
			SimplifiedCardinalDirection startOrientation, SimplifiedCardinalDirection endOrientation, boolean straightWhenPossible,
			double overlapX, double overlapY) {
		return makeRectPolylinCrossingPoint(startArea, endArea, crossedPoint, straightWhenPossible, overlapX, overlapY,
				SimplifiedCardinalDirection.allDirectionsExcept(startOrientation),
				SimplifiedCardinalDirection.allDirectionsExcept(endOrientation));
	}

	/**
	 * Return and build a FGERectPolylin linking a start and an end area, and crossing supplied point. All orientation solutions (except
	 * those supplied as to be excluded) are examined, and best solution is returned regarding distance between returned polylin and
	 * supplied point
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	/*public static FGERectPolylin makeRectPolylinCrossingPoint (
			FGEArea startArea, 
			FGEArea endArea, 
			FGEPoint crossedPoint,
			boolean straightWhenPossible,
			double overlap,
			Vector<SimplifiedCardinalDirection> excludedStartOrientations,
			Vector<SimplifiedCardinalDirection> excludedEndOrientations)
	{
		System.out.println("excludedStartOrientations="+excludedStartOrientations);
		System.out.println("excludedEndOrientations="+excludedEndOrientations);

		FGERectPolylin polylin = makeShortestRectPolylin(
				startArea, 
				endArea, 
				straightWhenPossible,
				overlap,
				crossedPoint, 
				excludedStartOrientations,
				excludedEndOrientations);
		FGESegment projectionSegment = polylin.getProjectionSegment(crossedPoint);
		if (projectionSegment == null) projectionSegment = polylin.getNearestSegment(crossedPoint);
		SimplifiedCardinalDirection orientation = projectionSegment.getApproximatedOrientation();
		Vector<SimplifiedCardinalDirection> polylin1ExcludedEndOrientations 
		= SimplifiedCardinalDirection.allDirectionsExcept(orientation.getOpposite());
		Vector<SimplifiedCardinalDirection> polylin2ExcludedStartOrientations 
		= SimplifiedCardinalDirection.allDirectionsExcept(orientation);

		FGERectPolylin polylin1 = makeShortestRectPolylin(
				startArea, 
				crossedPoint, 
				straightWhenPossible,
				0, 
				excludedStartOrientations,
				polylin1ExcludedEndOrientations);
		FGERectPolylin polylin2 = makeShortestRectPolylin(
				crossedPoint, 
				endArea, 
				straightWhenPossible,
				0,
				polylin2ExcludedStartOrientations,
				excludedEndOrientations);
		FGERectPolylin returned = mergePolylins(polylin1, 0, polylin1.getPointsNb()-2, polylin2, 1, polylin2.getPointsNb()-1);
		if (returned.hasExtraPoints()) returned.removeExtraPoints();
		return returned;
	}*/

	/**
	 * Return and build a FGERectPolylin linking a start and an end area, and crossing supplied point. All orientation solutions (except
	 * those supplied as to be excluded) are examined, and best solution is returned regarding distance between returned polylin and
	 * supplied point
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static FGERectPolylin makeRectPolylinCrossingPoint(FGEArea startArea, FGEArea endArea, FGEPoint crossedPoint,
			boolean straightWhenPossible, double overlapX, double overlapY, Vector<SimplifiedCardinalDirection> excludedStartOrientations,
			Vector<SimplifiedCardinalDirection> excludedEndOrientations) {
		FGERectPolylin returned = null;
		int bestNbOfPoints = Integer.MAX_VALUE;
		double bestLength = Double.POSITIVE_INFINITY;
		boolean isCurrentlyChoosenPolylinWithCrossedPointAsCorner = false;

		SimplifiedCardinalDirection bestStartOrientation = null;
		SimplifiedCardinalDirection bestMiddleOrientation = null;
		SimplifiedCardinalDirection bestEndOrientation = null;

		Hashtable<SimplifiedCardinalDirection, FGERectPolylin> polylins1 = new Hashtable<SimplifiedCardinalDirection, FGERectPolylin>();
		Hashtable<SimplifiedCardinalDirection, FGERectPolylin> polylins2 = new Hashtable<SimplifiedCardinalDirection, FGERectPolylin>();

		// Following regards performances optimization
		// Replace further commented code

		for (SimplifiedCardinalDirection orientation1 : SimplifiedCardinalDirection.values()) {
			FGERectPolylin polylin = makeShortestRectPolylin(startArea, crossedPoint, true, overlapX, overlapY, excludedStartOrientations,
					SimplifiedCardinalDirection.allDirectionsExcept(orientation1));
			if (polylin != null)
				polylins1.put(orientation1, polylin);
		}

		for (SimplifiedCardinalDirection orientation2 : SimplifiedCardinalDirection.values()) {
			FGERectPolylin polylin = makeShortestRectPolylin(crossedPoint, endArea, true, overlapX, overlapY,
					SimplifiedCardinalDirection.allDirectionsExcept(orientation2), excludedEndOrientations);
			if (polylin != null)
				polylins2.put(orientation2, polylin);
		}

		for (SimplifiedCardinalDirection orientation1 : SimplifiedCardinalDirection.values()) {
			for (SimplifiedCardinalDirection orientation2 : SimplifiedCardinalDirection.values()) {
				if (orientation1 != orientation2) {

					/*Vector<SimplifiedCardinalDirection> polylin1ExcludedEndOrientations 
					= SimplifiedCardinalDirection.allDirectionsExcept(orientation1);
					Vector<SimplifiedCardinalDirection> polylin2ExcludedStartOrientations 
					= SimplifiedCardinalDirection.allDirectionsExcept(orientation2);

					FGERectPolylin polylin1 = makeShortestRectPolylin(
							startArea, 
							crossedPoint, 
							true,
							overlapX,
							overlapY,
							excludedStartOrientations,
							polylin1ExcludedEndOrientations);
					FGERectPolylin polylin2 = makeShortestRectPolylin(
							crossedPoint, 
							endArea, 
							true,
							overlapX,
							overlapY,
							polylin2ExcludedStartOrientations,
							excludedEndOrientations);*/

					// Performances, see above
					FGERectPolylin polylin1 = polylins1.get(orientation1);
					FGERectPolylin polylin2 = polylins2.get(orientation2);

					if (polylin1 != null && polylin1.doesRespectAllConstraints() && polylin2 != null
							&& polylin2.doesRespectAllConstraints()) {
						FGERectPolylin tried;
						boolean cornerChoosen;
						if (orientation1 == orientation2.getOpposite()) {
							// In this case, crossedPoint is belonging to a segment
							cornerChoosen = false;
							tried = mergePolylins(polylin1, 0, polylin1.getPointsNb() - 2, polylin2, 1, polylin2.getPointsNb() - 1);
						} else {
							// In this case, crossedPoint is a corner, take all points of polylin1 and concatenate it
							// with all points of polylin2 except the first one (which is also crossedPoint)
							cornerChoosen = true;
							tried = mergePolylins(polylin1, 0, polylin1.getPointsNb() - 1, polylin2, 1, polylin2.getPointsNb() - 1);
						}
						if (tried.hasExtraPoints())
							tried.removeExtraPoints();

						// First of all, a polylin not crossing itself is better than any other
						if (returned != null && returned.crossedItSelf() && !tried.crossedItSelf()) {
							returned = tried;
							bestNbOfPoints = tried.getPointsNb();
							bestLength = tried.getLength();
							isCurrentlyChoosenPolylinWithCrossedPointAsCorner = cornerChoosen;
							bestStartOrientation = polylin1.getStartOrientation();
							bestMiddleOrientation = polylin1.getEndOrientation();
							bestEndOrientation = polylin2.getEndOrientation();
						}

						// Then, try to minimize number of points
						else if (tried.getPointsNb() < bestNbOfPoints) {
							returned = tried;
							bestNbOfPoints = tried.getPointsNb();
							bestLength = tried.getLength();
							isCurrentlyChoosenPolylinWithCrossedPointAsCorner = cornerChoosen;
							bestStartOrientation = polylin1.getStartOrientation();
							bestMiddleOrientation = polylin1.getEndOrientation();
							bestEndOrientation = polylin2.getEndOrientation();
						}

						// Then, minimise total length
						else if (tried.getPointsNb() == bestNbOfPoints) {
							if (tried.getLength() < bestLength - EPSILON) {
								returned = tried;
								bestNbOfPoints = tried.getPointsNb();
								bestLength = tried.getLength();
								isCurrentlyChoosenPolylinWithCrossedPointAsCorner = cornerChoosen;
								bestStartOrientation = polylin1.getStartOrientation();
								bestMiddleOrientation = polylin1.getEndOrientation();
								bestEndOrientation = polylin2.getEndOrientation();
							}
							// In case of same length, try to choose layout where crossed point is a corner
							else if (cornerChoosen && !isCurrentlyChoosenPolylinWithCrossedPointAsCorner) {
								returned = tried;
								bestNbOfPoints = tried.getPointsNb();
								bestLength = tried.getLength();
								isCurrentlyChoosenPolylinWithCrossedPointAsCorner = cornerChoosen;
								bestStartOrientation = polylin1.getStartOrientation();
								bestMiddleOrientation = polylin1.getEndOrientation();
								bestEndOrientation = polylin2.getEndOrientation();
							}
						}
					}
				}
			}
		}

		// logger.info(" Choosen polylin "+bestStartOrientation+","+bestMiddleOrientation+","+bestEndOrientation);

		return returned;
	}

	/**
	 * Return and build a FGERectPolylin linking a start and an end area, and crossing supplied point. All orientation solutions (except
	 * those supplied as to be excluded) are examined, and best solution is returned regarding distance between returned polylin and
	 * supplied point
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	/*public static FGERectPolylin makeRectPolylinCrossingPoint (
			FGEArea startArea, 
			FGEArea endArea, 
			FGEPoint crossedPoint,
			boolean straightWhenPossible,
			double overlapX,
			double overlapY,
			Vector<SimplifiedCardinalDirection> excludedStartOrientations,
			Vector<SimplifiedCardinalDirection> excludedEndOrientations)
	{
		FGERectPolylin returned = null;
		int bestNbOfPoints = Integer.MAX_VALUE;
		double bestLength = Double.POSITIVE_INFINITY;

		for (SimplifiedCardinalDirection orientation : SimplifiedCardinalDirection.values()) {
			Vector<SimplifiedCardinalDirection> polylin1ExcludedEndOrientations 
			= SimplifiedCardinalDirection.allDirectionsExcept(orientation.getOpposite());
			Vector<SimplifiedCardinalDirection> polylin2ExcludedStartOrientations 
			= SimplifiedCardinalDirection.allDirectionsExcept(orientation);
			FGERectPolylin polylin1 = makeShortestRectPolylin(
					startArea, 
					crossedPoint, 
					true,
					overlapX,
					overlapY,
					excludedStartOrientations,
					polylin1ExcludedEndOrientations);
			FGERectPolylin polylin2 = makeShortestRectPolylin(
					crossedPoint, 
					endArea, 
					true,
					overlapX,
					overlapY,
					polylin2ExcludedStartOrientations,
					excludedEndOrientations);
			FGERectPolylin tried = mergePolylins(polylin1, 0, polylin1.getPointsNb()-2, polylin2, 1, polylin2.getPointsNb()-1);
			if (tried.hasExtraPoints()) tried.removeExtraPoints();
			if (returned != null && returned.crossedItSelf() && !tried.crossedItSelf()) {
				returned = tried;
				bestNbOfPoints = tried.getPointsNb();
				bestLength = tried.getLength();
			}
			else if (tried.getPointsNb() < bestNbOfPoints) {
				returned = tried;
				bestNbOfPoints = tried.getPointsNb();
				bestLength = tried.getLength();
			}
			else if (tried.getPointsNb() == bestNbOfPoints) {
				if (tried.getLength() < bestLength-EPSILON) {
					returned = tried;
					bestNbOfPoints = tried.getPointsNb();
					bestLength = tried.getLength();
				}
			}
		}		
		return returned;
	}
	 */
	public boolean crossedItSelf() {
		for (FGESegment s1 : getSegments()) {
			for (FGESegment s2 : getSegments()) {
				if (s1 != s2
						&& (s1.overlap(s2) || (s1.intersectsInsideSegment(s2) && !s1.getLineIntersection(s2).equals(s1.getP1())
								&& !s1.getLineIntersection(s2).equals(s1.getP2()) && !s1.getLineIntersection(s2).equals(s2.getP1()) && !s1
								.getLineIntersection(s2).equals(s2.getP2()))))
					return true;
			}
		}
		return false;
	}

	/**
	 * Merge polylin given supplied polylin and indexes. Ignore extra points...
	 * 
	 * @param p1
	 * @param startIndex1
	 * @param endIndex1
	 * @param p2
	 * @param startIndex2
	 * @param endIndex2
	 * @return
	 */
	public static FGERectPolylin mergePolylins(FGERectPolylin p1, int startIndex1, int endIndex1, FGERectPolylin p2, int startIndex2,
			int endIndex2) {
		FGEPoint previous = null;
		FGERectPolylin returned = new FGERectPolylin();
		returned.setOverlapX(p1.getOverlapX());
		returned.setOverlapY(p1.getOverlapY());
		for (int i = startIndex1; i <= endIndex1; i++) {
			if (previous != null && previous.equals(p1.getPointAt(i))) {
				// System.out.println("ignore point: "+previous);
			} else {
				returned.addToPoints(p1.getPointAt(i));
			}
			previous = p1.getPointAt(i);
		}
		for (int i = startIndex2; i <= endIndex2; i++) {
			if (previous != null && previous.equals(p2.getPointAt(i))) {
				// System.out.println("ignore point: "+previous);
			} else {
				returned.addToPoints(p2.getPointAt(i));
			}
			previous = p2.getPointAt(i);
		}
		return returned;
	}

	private void computeResultingOrthogonalPerspectiveAreas() {
		if (startOrientation != null)
			resultingStartArea = startArea.getOrthogonalPerspectiveArea(startOrientation);
		else
			resultingStartArea = new FGEEmptyArea();

		if (endOrientation != null) {
			resultingEndArea = endArea.getOrthogonalPerspectiveArea(endOrientation);
			// FGEHalfPlane north2 = new FGEHalfPlane(FGELine.makeHorizontalLine(new FGEPoint(0,getMinYFor(endArea))),new
			// FGEPoint(0,getMinYFor(endArea)-1));
			/*System.out.println("resultingEndArea="+resultingEndArea);
			System.out.println("north2="+north2);
			System.out.println("result="+resultingEndArea.intersect(north2));*/
			// resultingEndArea = resultingEndArea.intersect(north2);
		} else
			resultingEndArea = new FGEEmptyArea();

		/*if (startArea instanceof FGEPoint) {
			resultingStartArea = ((FGEPoint)startArea).getOrthogonalPerspectiveArea(startOrientation);
		}
		else if (startArea instanceof FGESegment) {
			resultingStartArea = ((FGESegment)startArea).getOrthogonalPerspectiveArea(startOrientation);
		}
		else if (startArea instanceof FGERectangle) {
			resultingStartArea = ((FGERectangle)startArea).getOrthogonalPerspectiveArea(startOrientation);
		}
		else {
			logger.warning("What to do with a "+startArea+" ?");
		}

		if (endArea instanceof FGEPoint) {
			resultingEndArea = ((FGEPoint)endArea).getOrthogonalPerspectiveArea(endOrientation);
		}
		else if (endArea instanceof FGESegment) {
			resultingEndArea = ((FGESegment)endArea).getOrthogonalPerspectiveArea(endOrientation);
		}
		else if (endArea instanceof FGERectangle) {
			resultingEndArea = ((FGERectangle)endArea).getOrthogonalPerspectiveArea(endOrientation);
		}
		else {
			logger.warning("What to do with a "+endArea+" ?");
		}*/
	}

	public void debugPaint(FGEGraphics g) {
		ForegroundStyle fg = g.getDefaultForeground();
		BackgroundStyle bg = g.getDefaultBackground();
		g.setDefaultForeground(focusedForegroundStyle);
		g.setDefaultBackground(focusedBackgroundStyle);
		if (resultingStartArea != null)
			resultingStartArea.paint(g);
		if (resultingEndArea != null)
			resultingEndArea.paint(g);
		g.setDefaultForeground(fg);
		g.setDefaultBackground(bg);
		super.paint(g);
		getMiddle().paint(g);
	}

	public void paintWithRounds(FGEGraphics g, int arcSize) {
		g.useDefaultForegroundStyle();

		FGEPoint arcP1 = g.getGraphicalRepresentation().convertViewCoordinatesToNormalizedPoint(new Point(0, 0), 1.0);
		FGEPoint arcP2 = g.getGraphicalRepresentation().convertViewCoordinatesToNormalizedPoint(new Point(arcSize, arcSize), 1.0);
		double requestedArcWidth = arcP2.x - arcP1.x;
		double requestedArcHeight = arcP2.y - arcP1.y;

		FGEPoint current = null;

		for (int i = 0; i < _segments.size(); i++) {
			FGESegment s = _segments.get(i);
			FGESegment next = (i < _segments.size() - 1 ? _segments.get(i + 1) : null);
			if (next == null) {
				if (current == null) {
					s.paint(g);
				} else {
					(new FGESegment(current, s.getP2())).paint(g);
				}
			} else {
				FGEPoint p = s.getP2();
				SimplifiedCardinalDirection currentOrientation;
				SimplifiedCardinalDirection nextOrientation;
				double angleStart = 0;
				boolean clockWise = false;
				FGEPoint circleCenter = null;
				boolean displayArc;

				double arcWidth = requestedArcWidth;
				double arcHeight = requestedArcHeight;

				// Prevent rounded radius exceed half of segment length
				double arcRatio = requestedArcWidth / requestedArcHeight;
				if (s.isVertical() && next.isHorizontal()) {
					if (s.getLength() < requestedArcHeight * 2) {
						arcHeight = s.getLength() / 2;
						arcWidth = arcHeight * arcRatio;
					}
					if (next.getLength() < arcWidth * 2) {
						arcWidth = next.getLength() / 2;
						arcHeight = arcWidth / arcRatio;
					}
				} else if (s.isHorizontal() && next.isVertical()) {
					if (s.getLength() < requestedArcWidth * 2) {
						arcWidth = s.getLength() / 2;
						arcHeight = arcWidth / arcRatio;
					}
					if (next.getLength() < arcHeight * 2) {
						arcHeight = next.getLength() / 2;
						arcWidth = arcHeight * arcRatio;
					}
				}

				if (s.isVertical() && next.isHorizontal()) {
					displayArc = true;
					if (next.getP1().x < next.getP2().x) {
						if (s.getP1().y < s.getP2().y) {
							currentOrientation = SimplifiedCardinalDirection.SOUTH;
							nextOrientation = SimplifiedCardinalDirection.EAST;
							circleCenter = new FGEPoint(p.x + arcWidth, p.y - arcHeight);
							angleStart = -180;
							clockWise = false;
						} else {
							currentOrientation = SimplifiedCardinalDirection.NORTH;
							nextOrientation = SimplifiedCardinalDirection.EAST;
							circleCenter = new FGEPoint(p.x + arcWidth, p.y + arcHeight);
							angleStart = 90;
							clockWise = true;
						}
					} else {
						if (s.getP1().y < s.getP2().y) {
							currentOrientation = SimplifiedCardinalDirection.SOUTH;
							nextOrientation = SimplifiedCardinalDirection.WEST;
							circleCenter = new FGEPoint(p.x - arcWidth, p.y - arcHeight);
							angleStart = -90;
							clockWise = true;
						} else {
							currentOrientation = SimplifiedCardinalDirection.NORTH;
							nextOrientation = SimplifiedCardinalDirection.WEST;
							circleCenter = new FGEPoint(p.x - arcWidth, p.y + arcHeight);
							angleStart = 0;
							clockWise = false;
						}
					}
				} else if (s.isHorizontal() && next.isVertical()) {
					displayArc = true;
					if (next.getP1().y < next.getP2().y) {
						if (s.getP1().x < s.getP2().x) {
							currentOrientation = SimplifiedCardinalDirection.EAST;
							nextOrientation = SimplifiedCardinalDirection.SOUTH;
							circleCenter = new FGEPoint(p.x - arcWidth, p.y + arcHeight);
							angleStart = 0;
							clockWise = true;
						} else {
							currentOrientation = SimplifiedCardinalDirection.WEST;
							nextOrientation = SimplifiedCardinalDirection.SOUTH;
							circleCenter = new FGEPoint(p.x + arcWidth, p.y + arcHeight);
							angleStart = 90;
							clockWise = false;
						}
					} else {
						if (s.getP1().x < s.getP2().x) {
							currentOrientation = SimplifiedCardinalDirection.EAST;
							nextOrientation = SimplifiedCardinalDirection.NORTH;
							circleCenter = new FGEPoint(p.x - arcWidth, p.y - arcHeight);
							angleStart = -90;
							clockWise = false;
						} else {
							currentOrientation = SimplifiedCardinalDirection.WEST;
							nextOrientation = SimplifiedCardinalDirection.NORTH;
							circleCenter = new FGEPoint(p.x + arcWidth, p.y - arcHeight);
							angleStart = -180;
							clockWise = true;
						}
					}
				} else {
					logger.warning("Unexpected situation while drawing rounded RectPolylin connectors");
					displayArc = false;
					// return;
				}

				if (displayArc) {

					FGEArc arc = new FGEArc(circleCenter, new FGEDimension(arcWidth * 2, arcHeight * 2), angleStart, 90);
					FGEPoint startRound = arc.getPointAtAngle(clockWise ? angleStart + 90 : angleStart);
					FGEPoint endRound = arc.getPointAtAngle(clockWise ? angleStart : angleStart + 90);

					// DEBUG
					/*g.setDefaultForeground(ForegroundStyle.makeStyle(Color.CYAN));
					(new FGEEllips(circleCenter,new FGEDimension(arcWidth*2,arcHeight*2),Filling.NOT_FILLED)).paint(g);
					startRound.paint(g);
					endRound.paint(g);*/

					g.useDefaultForegroundStyle();

					if (current == null) {
						(new FGESegment(s.getP1(), startRound)).paint(g);
					} else {
						(new FGESegment(current, startRound)).paint(g);
					}
					arc.paint(g);
					current = endRound;
				}

				else {
					// For some reasons (for example 2 continuous colinear segments)
					// cannot display round

					g.useDefaultForegroundStyle();
					s.paint(g);
					current = s.getP2();
				}
			}
		}

		/*for (FGESegment s : _segments) {
			s.paint(g);
		}*/
	}

	private void restoreDefaultLayout() {
		clearPoints();

		/*FGEHalfPlane north1 = new FGEHalfPlane(FGELine.makeHorizontalLine(new FGEPoint(0,getMinYFor(startArea))),new FGEPoint(0,getMinYFor(startArea)-1));
		//resultingStartArea = resultingStartArea.intersect(north1);

		FGEHalfPlane north2 = new FGEHalfPlane(FGELine.makeHorizontalLine(new FGEPoint(0,getMinYFor(endArea))),new FGEPoint(0,getMinYFor(endArea)-1));
		System.out.println("resultingEndArea="+resultingEndArea);
		System.out.println("north2="+north2);
		System.out.println("result="+resultingEndArea.intersect(north2));
		resultingEndArea = resultingEndArea.intersect(north2);*/

		FGEArea intersect = resultingStartArea.intersect(resultingEndArea);

		/*logger.info("startOrientation="+startOrientation+" endOrientation="+endOrientation);
			logger.info("resultingStartArea="+resultingStartArea);
			logger.info("resultingEndArea="+resultingEndArea);
			logger.info("Intersect="+intersect);*/

		if (intersect instanceof FGEPoint) {
			FGEPoint p = (FGEPoint) intersect;

			// FGEPoint p_start = startArea.getNearestPoint(p);
			// FGEPoint p_end = endArea.getNearestPoint(p);

			FGEPoint p_start = (startOrientation.isHorizontal() ? nearestPointOnHorizontalLine(p, startArea) : nearestPointOnVerticalLine(
					p, startArea));

			FGEPoint p_end = (endOrientation.isHorizontal() ? nearestPointOnHorizontalLine(p, endArea) : nearestPointOnVerticalLine(p,
					endArea));

			addToPoints(p_start);
			addToPoints(p);
			addToPoints(p_end);
			return;
		}

		else if (intersect instanceof FGESegment) {
			FGEPoint p = ((FGESegment) intersect).getMiddle();

			// FGEPoint p_start = startArea.getNearestPoint(p);
			// FGEPoint p_end = endArea.getNearestPoint(p);

			FGEPoint p_start = (startOrientation.isHorizontal() ? nearestPointOnHorizontalLine(p, startArea) : nearestPointOnVerticalLine(
					p, startArea));

			FGEPoint p_end = (endOrientation.isHorizontal() ? nearestPointOnHorizontalLine(p, endArea) : nearestPointOnVerticalLine(p,
					endArea));

			if (FGEPoint.areAligned(p_start, p, p_end) && straightWhenPossible) {
				addToPoints(p_start);
				addToPoints(p_end);
				return;
			} else {
				addToPoints(p_start);
				addToPoints(p);
				addToPoints(p_end);
				return;
			}
		}

		else if ((intersect instanceof FGEShape) || (intersect.isFinite() && intersect.getEmbeddingBounds() != null)) {

			FGEPoint center;

			/*logger.info("startOrientation="+startOrientation);
			logger.info("endOrientation="+endOrientation);
			logger.info("startArea="+startArea);
			logger.info("endArea="+endArea);
			logger.info("resultingStartArea="+resultingStartArea);
			logger.info("resultingEndArea="+resultingEndArea);
			logger.info("Shape is "+intersect);*/

			if (intersect instanceof FGEShape) {
				center = ((FGEShape) intersect).getCenter();
			} else { // intersect is finite with non-null bounds
				center = intersect.getEmbeddingBounds().getCenter();
			}

			FGEPoint p_start = (startOrientation.isHorizontal() ? nearestPointOnHorizontalLine(center, startArea)
					: nearestPointOnVerticalLine(center, startArea));

			FGEPoint p_end = (endOrientation.isHorizontal() ? nearestPointOnHorizontalLine(center, endArea) : nearestPointOnVerticalLine(
					center, endArea));

			// FGEPoint p_start = FGEPoint.getNearestPoint(center,startArea.nearestPointFrom(center,
			// startOrientation.getOpposite()),startArea.nearestPointFrom(center, startOrientation));
			// FGEPoint p_end = FGEPoint.getNearestPoint(center,endArea.nearestPointFrom(center,
			// endOrientation.getOpposite()),endArea.nearestPointFrom(center, endOrientation));

			/*logger.info("p_start="+p_start);
			logger.info("p_end="+p_end);
			logger.info("startArea="+startArea);
			logger.info("endArea="+endArea);
			logger.info("center="+center);*/

			/*if (p_start == null) {
				p_start = startArea.getNearestPoint(center);
				logger.warning("Cound not find nearest point on start area along axis, selecting nearest.");
			}

			if (p_end == null) {
				p_end = endArea.getNearestPoint(center);
				logger.warning("Cound not find nearest point on end area along axis, selecting nearest.");
			}*/

			// This test is added to handle cases where intersection is disjointed.
			// In this case, center can be outside resulting areas, and causes
			// orientations not to be correct
			if (resultingStartArea.containsPoint(center) && resultingEndArea.containsPoint(center)) {

				if (FGEPoint.areAligned(p_start, center, p_end) && straightWhenPossible) {
					addToPoints(p_start);
					addToPoints(p_end);
					return;
				} else {
					addToPoints(p_start);
					addToPoints(center);
					addToPoints(p_end);
					return;
				}
			}

		}

		// logger.info("*********** For "+startOrientation+"/"+endOrientation+" CONTINUE");

		if (startOrientation == SimplifiedCardinalDirection.EAST && endOrientation == SimplifiedCardinalDirection.EAST) {
			restoreDefaultLayoutForEastEast();
		} else if (startOrientation == SimplifiedCardinalDirection.WEST && endOrientation == SimplifiedCardinalDirection.WEST) {
			restoreDefaultLayoutForWestWest();
		} else if (startOrientation == SimplifiedCardinalDirection.NORTH && endOrientation == SimplifiedCardinalDirection.NORTH) {
			restoreDefaultLayoutForNorthNorth();
		} else if (startOrientation == SimplifiedCardinalDirection.SOUTH && endOrientation == SimplifiedCardinalDirection.SOUTH) {
			restoreDefaultLayoutForSouthSouth();
		} else if (startOrientation == SimplifiedCardinalDirection.EAST && endOrientation == SimplifiedCardinalDirection.WEST) {
			restoreDefaultLayoutForEastWest();
		} else if (startOrientation == SimplifiedCardinalDirection.SOUTH && endOrientation == SimplifiedCardinalDirection.NORTH) {
			restoreDefaultLayoutForSouthNorth();
		} else if (startOrientation == SimplifiedCardinalDirection.EAST && endOrientation == SimplifiedCardinalDirection.NORTH) {
			restoreDefaultLayoutForEastNorth();
		} else if (startOrientation == SimplifiedCardinalDirection.EAST && endOrientation == SimplifiedCardinalDirection.SOUTH) {
			restoreDefaultLayoutForEastSouth();
		} else if (startOrientation == SimplifiedCardinalDirection.WEST && endOrientation == SimplifiedCardinalDirection.NORTH) {
			restoreDefaultLayoutForWestNorth();
		} else if (startOrientation == SimplifiedCardinalDirection.WEST && endOrientation == SimplifiedCardinalDirection.SOUTH) {
			restoreDefaultLayoutForWestSouth();
		} else if (startOrientation == SimplifiedCardinalDirection.WEST && endOrientation == SimplifiedCardinalDirection.EAST) {
			computeAs(new FGERectPolylin(endArea, endOrientation, startArea, startOrientation, straightWhenPossible, overlapX, overlapY));
		} else if (startOrientation == SimplifiedCardinalDirection.NORTH && endOrientation == SimplifiedCardinalDirection.SOUTH) {
			computeAs(new FGERectPolylin(endArea, endOrientation, startArea, startOrientation, straightWhenPossible, overlapX, overlapY));
		} else if (startOrientation == SimplifiedCardinalDirection.NORTH && endOrientation == SimplifiedCardinalDirection.EAST) {
			computeAs(new FGERectPolylin(endArea, endOrientation, startArea, startOrientation, straightWhenPossible, overlapX, overlapY));
		} else if (startOrientation == SimplifiedCardinalDirection.SOUTH && endOrientation == SimplifiedCardinalDirection.EAST) {
			computeAs(new FGERectPolylin(endArea, endOrientation, startArea, startOrientation, straightWhenPossible, overlapX, overlapY));
		} else if (startOrientation == SimplifiedCardinalDirection.NORTH && endOrientation == SimplifiedCardinalDirection.WEST) {
			computeAs(new FGERectPolylin(endArea, endOrientation, startArea, startOrientation, straightWhenPossible, overlapX, overlapY));
		} else if (startOrientation == SimplifiedCardinalDirection.SOUTH && endOrientation == SimplifiedCardinalDirection.WEST) {
			computeAs(new FGERectPolylin(endArea, endOrientation, startArea, startOrientation, straightWhenPossible, overlapX, overlapY));
		} else {
			logger.warning("Unexpected case: startOrientation=" + startOrientation + " endOrientation=" + endOrientation);
			(new Exception("???")).printStackTrace();
		}

	}

	private void computeAs(FGERectPolylin poly) {
		for (int i = poly.getPointsNb() - 1; i >= 0; i--)
			addToPoints(poly.getPointAt(i));
	}

	private FGEPoint nearestPointOnHorizontalLine(FGEPoint p, FGEArea area) {
		FGEPoint returned = FGEPoint.getNearestPoint(p, area.nearestPointFrom(p, SimplifiedCardinalDirection.EAST),
				area.nearestPointFrom(p, SimplifiedCardinalDirection.WEST));
		if (returned == null) {
			returned = area.getNearestPoint(p);
			logger.warning("Cound not find nearest point on area along horizontal axis, selecting nearest, area=" + area);
		}
		return returned;

	}

	private FGEPoint nearestPointOnVerticalLine(FGEPoint p, FGEArea area) {
		FGEPoint returned = FGEPoint.getNearestPoint(p, area.nearestPointFrom(p, SimplifiedCardinalDirection.NORTH),
				area.nearestPointFrom(p, SimplifiedCardinalDirection.SOUTH));
		if (returned == null) {
			returned = area.getNearestPoint(p);
			logger.warning("Cound not find nearest point on area along horizontal axis, selecting nearest, area=" + area);
		}
		return returned;

	}

	private void restoreDefaultLayoutForEastEast() {
		if (logger.isLoggable(Level.FINE))
			logger.fine("restoreDefaultForEastEast()");

		FGELine line = FGELine.makeVerticalLine(new FGEPoint(Math.max(getMaxXFor(startArea), getMaxXFor(endArea)) + overlapX, 0));

		FGEPoint p1 = getSignificativeAnchorAreaLocationFor(resultingStartArea.intersect(line), SimplifiedCardinalDirection.EAST);
		FGEPoint p2 = getSignificativeAnchorAreaLocationFor(resultingEndArea.intersect(line), SimplifiedCardinalDirection.EAST);

		// FGEPoint p_start = startArea.getNearestPoint(p1);
		// FGEPoint p_end = endArea.getNearestPoint(p2);
		FGEPoint p_start = nearestPointOnHorizontalLine(p1, startArea);
		FGEPoint p_end = nearestPointOnHorizontalLine(p2, endArea);
		addToPoints(p_start);
		addToPoints(p1);
		addToPoints(p2);
		addToPoints(p_end);

	}

	private void restoreDefaultLayoutForWestWest() {
		if (logger.isLoggable(Level.FINE))
			logger.fine("restoreDefaultForWestWest()");

		FGELine line = FGELine.makeVerticalLine(new FGEPoint(Math.min(getMinXFor(startArea), getMinXFor(endArea)) - overlapX, 0));

		FGEPoint p1 = getSignificativeAnchorAreaLocationFor(resultingStartArea.intersect(line), SimplifiedCardinalDirection.WEST);
		FGEPoint p2 = getSignificativeAnchorAreaLocationFor(resultingEndArea.intersect(line), SimplifiedCardinalDirection.WEST);

		// FGEPoint p_start = startArea.getNearestPoint(p1);
		// FGEPoint p_end = endArea.getNearestPoint(p2);
		FGEPoint p_start = nearestPointOnHorizontalLine(p1, startArea);
		FGEPoint p_end = nearestPointOnHorizontalLine(p2, endArea);
		addToPoints(p_start);
		addToPoints(p1);
		addToPoints(p2);
		addToPoints(p_end);

	}

	private void restoreDefaultLayoutForNorthNorth() {
		if (logger.isLoggable(Level.FINE))
			logger.fine("restoreDefaultForNorthNorth()");

		FGELine line = FGELine.makeHorizontalLine(new FGEPoint(0, Math.min(getMinYFor(startArea), getMinYFor(endArea)) - overlapY));

		FGEPoint p1 = getSignificativeAnchorAreaLocationFor(resultingStartArea.intersect(line), SimplifiedCardinalDirection.NORTH);
		FGEPoint p2 = getSignificativeAnchorAreaLocationFor(resultingEndArea.intersect(line), SimplifiedCardinalDirection.NORTH);

		// FGEPoint p_start = startArea.getNearestPoint(p1);
		// FGEPoint p_end = endArea.getNearestPoint(p2);
		FGEPoint p_start = nearestPointOnVerticalLine(p1, startArea);
		FGEPoint p_end = nearestPointOnVerticalLine(p2, endArea);
		addToPoints(p_start);
		addToPoints(p1);
		addToPoints(p2);
		addToPoints(p_end);

	}

	private void restoreDefaultLayoutForSouthSouth() {
		if (logger.isLoggable(Level.FINE))
			logger.fine("restoreDefaultForSouthSouth()");

		FGELine line = FGELine.makeHorizontalLine(new FGEPoint(0, Math.max(getMaxYFor(startArea), getMaxYFor(endArea)) + overlapY));

		FGEPoint p1 = getSignificativeAnchorAreaLocationFor(resultingStartArea.intersect(line), SimplifiedCardinalDirection.SOUTH);
		FGEPoint p2 = getSignificativeAnchorAreaLocationFor(resultingEndArea.intersect(line), SimplifiedCardinalDirection.SOUTH);

		// FGEPoint p_start = startArea.getNearestPoint(p1);
		// FGEPoint p_end = endArea.getNearestPoint(p2);
		FGEPoint p_start = nearestPointOnVerticalLine(p1, startArea);
		FGEPoint p_end = nearestPointOnVerticalLine(p2, endArea);
		addToPoints(p_start);
		addToPoints(p1);
		addToPoints(p2);
		addToPoints(p_end);

	}

	private void restoreDefaultLayoutForEastWest() {
		if (logger.isLoggable(Level.FINE))
			logger.fine("restoreDefaultForEastWest()");

		FGEPoint significativeStartLocation = getSignificativeAnchorAreaLocationFor(startArea, SimplifiedCardinalDirection.EAST);
		FGEPoint significativeEndLocation = getSignificativeAnchorAreaLocationFor(endArea, SimplifiedCardinalDirection.WEST);

		FGEArea startAnchorArea = startArea.getAnchorAreaFrom(SimplifiedCardinalDirection.EAST);
		FGEArea endAnchorArea = endArea.getAnchorAreaFrom(SimplifiedCardinalDirection.WEST);

		if (getMaxXFor(startAnchorArea) > getMinXFor(endAnchorArea)) {
			// if (getMinXFor(startAnchorArea) > getMaxXFor(endAnchorArea)) { /* XXX */

			double middleY = (significativeStartLocation.y <= significativeEndLocation.y ? (getMaxYFor(startArea) + getMinYFor(endArea)) / 2
					: (getMinYFor(startArea) + getMaxYFor(endArea)) / 2);
			FGELine line = FGELine.makeHorizontalLine(new FGEPoint(0, middleY));
			// FGELine line1 = FGELine.makeVerticalLine(new FGEPoint(getMaxXFor(startAnchorArea)+overlapX,0));
			// FGELine line2 = FGELine.makeVerticalLine(new FGEPoint(getMinXFor(endAnchorArea)-overlapX,0));
			FGELine line1 = FGELine.makeVerticalLine(new FGEPoint(getMaxXFor(startAnchorArea) + overlapX, 0));
			FGELine line2 = FGELine.makeVerticalLine(new FGEPoint(getMinXFor(endAnchorArea) - overlapX, 0));

			FGEPoint p1 = getLocationFor(resultingStartArea.intersect(line1));
			FGEPoint p2 = line1.getLineIntersection(line);
			FGEPoint p3 = line2.getLineIntersection(line);
			FGEPoint p4 = getLocationFor(resultingEndArea.intersect(line2));

			// FGEPoint p_start = startArea.getNearestPoint(p1);
			// FGEPoint p_end = endArea.getNearestPoint(p4);
			FGEPoint p_start = nearestPointOnHorizontalLine(p1, startArea);
			FGEPoint p_end = nearestPointOnHorizontalLine(p4, endArea);
			addToPoints(p_start);
			addToPoints(p1);
			addToPoints(p2);
			addToPoints(p3);
			addToPoints(p4);
			addToPoints(p_end);
		}

		else {
			FGELine line = FGELine.makeVerticalLine(new FGEPoint(
					(significativeStartLocation.x <= significativeEndLocation.x ? (getMaxXFor(startArea) + getMinXFor(endArea)) / 2
							: (getMinXFor(startArea) + getMaxXFor(endArea)) / 2), 0));

			FGEPoint p1 = getLocationFor(resultingStartArea.intersect(line));
			FGEPoint p2 = getLocationFor(resultingEndArea.intersect(line));

			// FGEPoint p_start = startArea.getNearestPoint(p1);
			// FGEPoint p_end = endArea.getNearestPoint(p2);
			FGEPoint p_start = nearestPointOnHorizontalLine(p1, startArea);
			FGEPoint p_end = nearestPointOnHorizontalLine(p2, endArea);
			addToPoints(p_start);
			addToPoints(p1);
			addToPoints(p2);
			addToPoints(p_end);

		}
	}

	private void restoreDefaultLayoutForSouthNorth() {
		if (logger.isLoggable(Level.FINE))
			logger.fine("restoreDefaultForSouthNorth()");

		FGEPoint significativeStartLocation = getSignificativeAnchorAreaLocationFor(startArea, SimplifiedCardinalDirection.SOUTH);
		FGEPoint significativeEndLocation = getSignificativeAnchorAreaLocationFor(endArea, SimplifiedCardinalDirection.NORTH);

		FGEArea startAnchorArea = startArea.getAnchorAreaFrom(SimplifiedCardinalDirection.SOUTH);
		FGEArea endAnchorArea = endArea.getAnchorAreaFrom(SimplifiedCardinalDirection.NORTH);

		if (getMaxYFor(startAnchorArea) > getMinYFor(endAnchorArea)) {
			// if (getMinYFor(startAnchorArea) > getMaxYFor(endAnchorArea)) { /* XXX */

			double middleX = (significativeStartLocation.x <= significativeEndLocation.x ? (getMaxXFor(startArea) + getMinXFor(endArea)) / 2
					: (getMinXFor(startArea) + getMaxXFor(endArea)) / 2);
			FGELine line = FGELine.makeVerticalLine(new FGEPoint(middleX, 0));
			// FGELine line1 = FGELine.makeHorizontalLine(new FGEPoint(0,getMinYFor(startAnchorArea)+overlapY)); /* XXX */
			// FGELine line2 = FGELine.makeHorizontalLine(new FGEPoint(0,getMaxYFor(endAnchorArea)-overlapY)); /* XXX */
			FGELine line1 = FGELine.makeHorizontalLine(new FGEPoint(0, getMaxYFor(startAnchorArea) + overlapY));
			FGELine line2 = FGELine.makeHorizontalLine(new FGEPoint(0, getMinYFor(endAnchorArea) - overlapY));

			FGEPoint p1 = getLocationFor(resultingStartArea.intersect(line1));
			FGEPoint p2 = line1.getLineIntersection(line);
			FGEPoint p3 = line2.getLineIntersection(line);
			FGEPoint p4 = getLocationFor(resultingEndArea.intersect(line2));

			// FGEPoint p_start = startArea.getNearestPoint(p1);
			// FGEPoint p_end = endArea.getNearestPoint(p4);
			FGEPoint p_start = nearestPointOnVerticalLine(p1, startArea);
			FGEPoint p_end = nearestPointOnVerticalLine(p4, endArea);
			addToPoints(p_start);
			addToPoints(p1);
			addToPoints(p2);
			addToPoints(p3);
			addToPoints(p4);
			addToPoints(p_end);
		}

		else {

			FGELine line = FGELine.makeHorizontalLine(new FGEPoint(0,
					(significativeStartLocation.y <= significativeEndLocation.y ? (getMaxYFor(startArea) + getMinYFor(endArea)) / 2
							: (getMinYFor(startArea) + getMaxYFor(endArea)) / 2)));
			FGELine alternativeLine = FGELine.makeHorizontalLine(new FGEPoint(0,
					(significativeStartLocation.y + significativeEndLocation.y) / 2));

			FGEArea i1 = resultingStartArea.intersect(line);
			FGEArea i2 = resultingEndArea.intersect(line);

			if (i1 instanceof FGEEmptyArea)
				i1 = resultingStartArea.intersect(alternativeLine);
			if (i2 instanceof FGEEmptyArea)
				i2 = resultingEndArea.intersect(alternativeLine);

			if (i1 instanceof FGEEmptyArea) {
				logger.warning("Unexpected empty area for intersection: " + resultingStartArea + " and " + alternativeLine);
			}
			if (i2 instanceof FGEEmptyArea) {
				logger.warning("Unexpected empty area for intersection: " + resultingEndArea + " and " + alternativeLine);
			}

			FGEPoint p1 = getLocationFor(i1);
			FGEPoint p2 = getLocationFor(i2);

			// FGEPoint p_start = startArea.getNearestPoint(p1);
			// FGEPoint p_end = endArea.getNearestPoint(p2);
			FGEPoint p_start = nearestPointOnVerticalLine(p1, startArea);
			FGEPoint p_end = nearestPointOnVerticalLine(p2, endArea);
			addToPoints(p_start);
			addToPoints(p1);
			addToPoints(p2);
			addToPoints(p_end);

		}
	}

	private void restoreDefaultLayoutForEastNorth() {
		if (logger.isLoggable(Level.FINE))
			logger.fine("restoreDefaultForEastNorth()");

		FGEPoint significativeStartLocation = getSignificativeAnchorAreaLocationFor(startArea, SimplifiedCardinalDirection.EAST);
		FGEPoint significativeEndLocation = getSignificativeAnchorAreaLocationFor(endArea, SimplifiedCardinalDirection.NORTH);

		/*System.out.println("startArea="+startArea);
		System.out.println("endArea"+endArea);
		System.out.println("significativeStartLocation="+significativeStartLocation);
		System.out.println("significativeEndLocation"+significativeEndLocation);*/

		CardinalQuadrant quadrant = FGEPoint.getCardinalQuadrant(significativeStartLocation, significativeEndLocation);
		FGELine line1 = null;
		FGELine line2 = null;
		FGELine alternativeLine1 = null;
		FGELine alternativeLine2 = null;

		boolean useAlternativeLine1 = false;
		boolean useAlternativeLine2 = false;

		if (quadrant == CardinalQuadrant.SOUTH_EAST) {
			logger.warning("Unexpected call to restoreDefaultForEastNorth() while quadrant is SOUTH_EAST");
			return;
		}

		else if (quadrant == CardinalQuadrant.NORTH_EAST) {
			useAlternativeLine1 = getMaxXFor(startArea) > getMinXFor(endArea);
			line1 = FGELine.makeVerticalLine(new FGEPoint((getMaxXFor(startArea) + getMinXFor(endArea)) / 2, 0));
			alternativeLine1 = FGELine.makeVerticalLine(new FGEPoint((significativeStartLocation.x + significativeEndLocation.x) / 2, 0));
			line2 = FGELine.makeHorizontalLine(new FGEPoint(0, getMinYFor(endArea) - overlapY));
		}

		else if (quadrant == CardinalQuadrant.SOUTH_WEST) {
			line1 = FGELine.makeVerticalLine(new FGEPoint(getMaxXFor(startArea) + overlapX, 0));
			useAlternativeLine2 = getMaxYFor(startArea) > getMinYFor(endArea);
			line2 = FGELine.makeHorizontalLine(new FGEPoint(0, (getMaxYFor(startArea) + getMinYFor(endArea)) / 2));
			alternativeLine2 = FGELine.makeHorizontalLine(new FGEPoint(0, (significativeStartLocation.y + significativeEndLocation.y) / 2));
		}

		else if (quadrant == CardinalQuadrant.NORTH_WEST) {
			line1 = FGELine.makeVerticalLine(new FGEPoint(getMaxXFor(startArea) + overlapX, 0));
			line2 = FGELine.makeHorizontalLine(new FGEPoint(0, getMinYFor(endArea) - overlapY));
		}

		if (useAlternativeLine1)
			line1 = alternativeLine1;
		if (useAlternativeLine2)
			line2 = alternativeLine2;

		FGEArea i1 = resultingStartArea.intersect(line1);
		FGEArea i2 = resultingEndArea.intersect(line2);

		if (i1 instanceof FGEEmptyArea && alternativeLine1 != null) {
			// Special case, use alternative line 1
			line1 = alternativeLine1;
			i1 = resultingStartArea.intersect(line1);
		}
		if (i2 instanceof FGEEmptyArea && alternativeLine2 != null) {
			// Special case, use alternative line 1
			line2 = alternativeLine2;
			i2 = resultingEndArea.intersect(line2);
		}

		FGEPoint p1 = getLocationFor(i1);
		FGEPoint p2 = line1.getLineIntersection(line2);
		FGEPoint p3 = getLocationFor(i2);

		// FGEPoint p_start = startArea.getNearestPoint(p1);
		// FGEPoint p_end = endArea.getNearestPoint(p3);
		FGEPoint p_start = nearestPointOnHorizontalLine(p1, startArea);
		FGEPoint p_end = nearestPointOnVerticalLine(p3, endArea);
		addToPoints(p_start);
		addToPoints(p1);
		addToPoints(p2);
		addToPoints(p3);
		addToPoints(p_end);

	}

	private void restoreDefaultLayoutForEastSouth() {
		if (logger.isLoggable(Level.FINE))
			logger.fine("restoreDefaultForEastSouth()");

		FGEPoint significativeStartLocation = getSignificativeAnchorAreaLocationFor(startArea, SimplifiedCardinalDirection.EAST);
		FGEPoint significativeEndLocation = getSignificativeAnchorAreaLocationFor(endArea, SimplifiedCardinalDirection.SOUTH);

		CardinalQuadrant quadrant = FGEPoint.getCardinalQuadrant(significativeStartLocation, significativeEndLocation);

		FGELine line1 = null;
		FGELine line2 = null;
		FGELine alternativeLine1 = null;
		FGELine alternativeLine2 = null;

		boolean useAlternativeLine1 = false;
		boolean useAlternativeLine2 = false;

		if (quadrant == CardinalQuadrant.NORTH_EAST) {
			logger.warning("Unexpected call to restoreDefaultForEastSouth() while quadrant is NORTH_EAST");
			return;
		}

		else if (quadrant == CardinalQuadrant.SOUTH_EAST) {
			useAlternativeLine1 = getMaxXFor(startArea) > getMinXFor(endArea);
			line1 = FGELine.makeVerticalLine(new FGEPoint((getMaxXFor(startArea) + getMinXFor(endArea)) / 2, 0));
			alternativeLine1 = FGELine.makeVerticalLine(new FGEPoint((significativeStartLocation.x + significativeEndLocation.x) / 2, 0));
			line2 = FGELine.makeHorizontalLine(new FGEPoint(0, getMaxYFor(endArea) + overlapY));
		}

		else if (quadrant == CardinalQuadrant.NORTH_WEST) {
			line1 = FGELine.makeVerticalLine(new FGEPoint(getMaxXFor(startArea) + overlapX, 0));
			useAlternativeLine2 = getMinYFor(startArea) < getMaxYFor(endArea);
			line2 = FGELine.makeHorizontalLine(new FGEPoint(0, (getMinYFor(startArea) + getMaxYFor(endArea)) / 2));
			alternativeLine2 = FGELine.makeHorizontalLine(new FGEPoint(0, (significativeStartLocation.y + significativeEndLocation.y) / 2));
		}

		else if (quadrant == CardinalQuadrant.SOUTH_WEST) {
			line1 = FGELine.makeVerticalLine(new FGEPoint(getMaxXFor(startArea) + overlapX, 0));
			line2 = FGELine.makeHorizontalLine(new FGEPoint(0, getMaxYFor(endArea) + overlapY));
		}

		if (useAlternativeLine1)
			line1 = alternativeLine1;
		if (useAlternativeLine2)
			line2 = alternativeLine2;

		FGEArea i1 = resultingStartArea.intersect(line1);
		FGEArea i2 = resultingEndArea.intersect(line2);

		if (i1 instanceof FGEEmptyArea && alternativeLine1 != null) {
			// Special case, use alternative line 1
			line1 = alternativeLine1;
			i1 = resultingStartArea.intersect(line1);
		}
		if (i2 instanceof FGEEmptyArea && alternativeLine2 != null) {
			// Special case, use alternative line 1
			line2 = alternativeLine2;
			i2 = resultingEndArea.intersect(line2);
		}

		FGEPoint p1 = getLocationFor(i1);
		FGEPoint p2 = line1.getLineIntersection(line2);
		FGEPoint p3 = getLocationFor(i2);

		// FGEPoint p_start = startArea.getNearestPoint(p1);
		// FGEPoint p_end = endArea.getNearestPoint(p3);
		FGEPoint p_start = nearestPointOnHorizontalLine(p1, startArea);
		FGEPoint p_end = nearestPointOnVerticalLine(p3, endArea);
		addToPoints(p_start);
		addToPoints(p1);
		addToPoints(p2);
		addToPoints(p3);
		addToPoints(p_end);
	}

	private void restoreDefaultLayoutForWestNorth() {
		if (logger.isLoggable(Level.FINE))
			logger.fine("restoreDefaultForWestNorth()");

		FGEPoint significativeStartLocation = getSignificativeAnchorAreaLocationFor(startArea, SimplifiedCardinalDirection.WEST);
		FGEPoint significativeEndLocation = getSignificativeAnchorAreaLocationFor(endArea, SimplifiedCardinalDirection.NORTH);

		CardinalQuadrant quadrant = FGEPoint.getCardinalQuadrant(significativeStartLocation, significativeEndLocation);
		FGELine line1 = null;
		FGELine line2 = null;
		FGELine alternativeLine1 = null;
		FGELine alternativeLine2 = null;

		boolean useAlternativeLine1 = false;
		boolean useAlternativeLine2 = false;

		if (quadrant == CardinalQuadrant.SOUTH_WEST) {
			logger.warning("Unexpected call to restoreDefaultForWestNorth() while quadrant is SOUTH_WEST");
			return;
		}

		else if (quadrant == CardinalQuadrant.NORTH_WEST) {
			useAlternativeLine1 = getMinXFor(startArea) < getMaxXFor(endArea);
			line1 = FGELine.makeVerticalLine(new FGEPoint((getMinXFor(startArea) + getMaxXFor(endArea)) / 2, 0));
			alternativeLine1 = FGELine.makeVerticalLine(new FGEPoint((significativeStartLocation.x + significativeEndLocation.x) / 2, 0));
			line2 = FGELine.makeHorizontalLine(new FGEPoint(0, getMinYFor(endArea) - overlapY));
		}

		else if (quadrant == CardinalQuadrant.SOUTH_EAST) {
			line1 = FGELine.makeVerticalLine(new FGEPoint(getMinXFor(startArea) - overlapX, 0));
			useAlternativeLine2 = getMaxYFor(startArea) > getMinYFor(endArea);
			line2 = FGELine.makeHorizontalLine(new FGEPoint(0, (getMaxYFor(startArea) + getMinYFor(endArea)) / 2));
			alternativeLine2 = FGELine.makeHorizontalLine(new FGEPoint(0, (significativeStartLocation.y + significativeEndLocation.y) / 2));
		}

		else if (quadrant == CardinalQuadrant.NORTH_EAST) {
			line1 = FGELine.makeVerticalLine(new FGEPoint(getMinXFor(startArea) - overlapX, 0));
			line2 = FGELine.makeHorizontalLine(new FGEPoint(0, getMinYFor(endArea) - overlapY));
		}

		if (useAlternativeLine1)
			line1 = alternativeLine1;
		if (useAlternativeLine2)
			line2 = alternativeLine2;

		FGEArea i1 = resultingStartArea.intersect(line1);
		FGEArea i2 = resultingEndArea.intersect(line2);

		if (i1 instanceof FGEEmptyArea && alternativeLine1 != null) {
			// Special case, use alternative line 1
			line1 = alternativeLine1;
			i1 = resultingStartArea.intersect(line1);
		}
		if (i2 instanceof FGEEmptyArea && alternativeLine2 != null) {
			// Special case, use alternative line 1
			line2 = alternativeLine2;
			i2 = resultingEndArea.intersect(line2);
		}

		FGEPoint p1 = getLocationFor(i1);
		FGEPoint p2 = line1.getLineIntersection(line2);
		FGEPoint p3 = getLocationFor(i2);

		// FGEPoint p_start = startArea.getNearestPoint(p1);
		// FGEPoint p_end = endArea.getNearestPoint(p3);
		FGEPoint p_start = nearestPointOnHorizontalLine(p1, startArea);
		FGEPoint p_end = nearestPointOnVerticalLine(p3, endArea);
		addToPoints(p_start);
		addToPoints(p1);
		addToPoints(p2);
		addToPoints(p3);
		addToPoints(p_end);

	}

	private void restoreDefaultLayoutForWestSouth() {
		if (logger.isLoggable(Level.FINE))
			logger.fine("restoreDefaultForWestSouth()");

		FGEPoint significativeStartLocation = getSignificativeAnchorAreaLocationFor(startArea, SimplifiedCardinalDirection.WEST);
		FGEPoint significativeEndLocation = getSignificativeAnchorAreaLocationFor(endArea, SimplifiedCardinalDirection.SOUTH);

		CardinalQuadrant quadrant = FGEPoint.getCardinalQuadrant(significativeStartLocation, significativeEndLocation);

		FGELine line1 = null;
		FGELine line2 = null;
		FGELine alternativeLine1 = null;
		FGELine alternativeLine2 = null;

		boolean useAlternativeLine1 = false;
		boolean useAlternativeLine2 = false;

		if (quadrant == CardinalQuadrant.NORTH_WEST) {
			logger.warning("Unexpected call to restoreDefaultForWestSouth() while quadrant is NORTH_WEST");
			return;
		}

		else if (quadrant == CardinalQuadrant.SOUTH_WEST) {
			useAlternativeLine1 = getMinXFor(startArea) < getMaxXFor(endArea);
			line1 = FGELine.makeVerticalLine(new FGEPoint((getMinXFor(startArea) + getMaxXFor(endArea)) / 2, 0));
			alternativeLine1 = FGELine.makeVerticalLine(new FGEPoint((significativeStartLocation.x + significativeEndLocation.x) / 2, 0));
			line2 = FGELine.makeHorizontalLine(new FGEPoint(0, getMaxYFor(endArea) + overlapY));
		}

		else if (quadrant == CardinalQuadrant.NORTH_EAST) {
			line1 = FGELine.makeVerticalLine(new FGEPoint(getMinXFor(startArea) - overlapX, 0));
			useAlternativeLine2 = getMinYFor(startArea) < getMaxYFor(endArea);
			line2 = FGELine.makeHorizontalLine(new FGEPoint(0, (getMinYFor(startArea) + getMaxYFor(endArea)) / 2));
			alternativeLine2 = FGELine.makeHorizontalLine(new FGEPoint(0, (significativeStartLocation.y + significativeEndLocation.y) / 2));
		}

		else if (quadrant == CardinalQuadrant.SOUTH_EAST) {
			line1 = FGELine.makeVerticalLine(new FGEPoint(getMinXFor(startArea) - overlapX, 0));
			line2 = FGELine.makeHorizontalLine(new FGEPoint(0, getMaxYFor(endArea) + overlapY));
		}

		if (useAlternativeLine1)
			line1 = alternativeLine1;
		if (useAlternativeLine2)
			line2 = alternativeLine2;

		FGEArea i1 = resultingStartArea.intersect(line1);
		FGEArea i2 = resultingEndArea.intersect(line2);

		if (i1 instanceof FGEEmptyArea && alternativeLine1 != null) {
			// Special case, use alternative line 1
			line1 = alternativeLine1;
			i1 = resultingStartArea.intersect(line1);
		}
		if (i2 instanceof FGEEmptyArea && alternativeLine2 != null) {
			// Special case, use alternative line 1
			line2 = alternativeLine2;
			i2 = resultingEndArea.intersect(line2);
		}

		FGEPoint p1 = getLocationFor(i1);
		FGEPoint p2 = line1.getLineIntersection(line2);
		FGEPoint p3 = getLocationFor(i2);

		// FGEPoint p_start = startArea.getNearestPoint(p1);
		// FGEPoint p_end = endArea.getNearestPoint(p3);
		FGEPoint p_start = nearestPointOnHorizontalLine(p1, startArea);
		FGEPoint p_end = nearestPointOnVerticalLine(p3, endArea);
		addToPoints(p_start);
		addToPoints(p1);
		addToPoints(p2);
		addToPoints(p3);
		addToPoints(p_end);
	}

	private FGEPoint getLocationFor(FGEArea a) {
		if (a instanceof FGEEmptyArea) {
			logger.warning("Unexpected empty area while computing getLocationFor(FGEArea)");
		}

		if (a instanceof FGEPoint)
			return ((FGEPoint) a).clone();
		if (a instanceof FGESegment) {
			return ((FGESegment) a).getMiddle();
		}
		if (a instanceof FGEPolylin) {
			return ((FGEPolylin) a).getMiddle();
		}
		if (a instanceof FGEArc) {
			return ((FGEArc) a).getMiddle();
		}
		if (a instanceof FGEUnionArea && ((FGEUnionArea) a).isUnionOfPoints()) {
			return (FGEPoint) ((FGEUnionArea) a).getObjects().firstElement();
		}
		if (a instanceof FGEUnionArea && ((FGEUnionArea) a).isUnionOfFiniteGeometricObjects()) {
			return a.getNearestPoint(a.getEmbeddingBounds().getCenter());
		}
		if (a.isFinite() && !(a instanceof FGEEmptyArea)) {
			FGERectangle r = a.getEmbeddingBounds();
			if (r != null) {
				return r.getCenter();
			}
		}

		logger.warning("Unexpected " + a);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Infos:" + "\nstartArea=" + startArea + "\nstartOrientation=" + startOrientation + "\nendArea=" + endArea
					+ "\nendOrientation=" + endOrientation + "\noverlapX=" + overlapX + "\noverlapY=" + overlapY);
		}

		return new FGEPoint(0, 0);
	}

	private FGEPoint getSignificativeAnchorAreaLocationFor(FGEArea a, SimplifiedCardinalDirection direction) {
		FGEArea anchorArea = a.getAnchorAreaFrom(direction);

		/*if (anchorArea instanceof FGEUnionArea && ((FGEUnionArea)anchorArea).isUnionOfSegments()) {
			anchorArea = ((FGEUnionArea)anchorArea).getObjects().firstElement();
		}

		logger.info("Anchor area for "+a+" is "+anchorArea);*/

		if (anchorArea instanceof FGEPoint)
			return ((FGEPoint) anchorArea).clone();
		if (anchorArea instanceof FGESegment) {
			return ((FGESegment) anchorArea).getMiddle();
		}
		if (anchorArea instanceof FGEPolylin) {
			return ((FGEPolylin) anchorArea).getMiddle();
		}
		if (anchorArea instanceof FGEArc) {
			return ((FGEArc) anchorArea).getMiddle();
		}
		if (anchorArea instanceof FGEShape)
			return getSignificativeAnchorAreaLocationFor(((FGEShape) anchorArea).getBoundingBox(), direction);
		if (anchorArea instanceof FGEUnionArea && ((FGEUnionArea) anchorArea).isUnionOfPoints())
			return getSignificativeAnchorAreaLocationFor(((FGEUnionArea) anchorArea).getObjects().firstElement(), direction);
		if (anchorArea instanceof FGEUnionArea && ((FGEUnionArea) anchorArea).isUnionOfFiniteGeometricObjects()) {
			return anchorArea.getNearestPoint(anchorArea.getEmbeddingBounds().getCenter());
		}
		logger.warning("Unexpected " + anchorArea + " for " + a);
		return new FGEPoint(0, 0);
	}

	private double getMaxXFor(FGEArea a) {
		if (a instanceof FGEPoint)
			return ((FGEPoint) a).x;
		if (a instanceof FGESegment)
			return Math.max(((FGESegment) a).getP1().x, ((FGESegment) a).getP2().x);
		if (a instanceof FGEShape)
			return ((FGEShape) a).getBoundingBox().x + ((FGEShape) a).getBoundingBox().width;
		if (a instanceof FGEPolylin)
			return ((FGEPolylin) a).getBoundingBox().x + ((FGEPolylin) a).getBoundingBox().width;
		if (a instanceof FGEArc)
			return ((FGEArc) a).getBoundingBox().x + ((FGEArc) a).getBoundingBox().width;
		if (a instanceof FGEUnionArea && ((FGEUnionArea) a).isUnionOfPoints()) {
			double returned = Double.NEGATIVE_INFINITY;
			for (FGEArea p : ((FGEUnionArea) a).getObjects()) {
				FGEPoint pt = (FGEPoint) p;
				if (pt.x > returned)
					returned = pt.x;
			}
			return returned;
		}
		if (a instanceof FGEUnionArea && ((FGEUnionArea) a).isUnionOfFiniteGeometricObjects()) {
			return a.getEmbeddingBounds().getMaxX();
		}
		logger.warning("Unexpected " + a);
		return 0;
	}

	private double getMinXFor(FGEArea a) {
		if (a instanceof FGEPoint)
			return ((FGEPoint) a).x;
		if (a instanceof FGESegment)
			return Math.min(((FGESegment) a).getP1().x, ((FGESegment) a).getP2().x);
		if (a instanceof FGEShape)
			return ((FGEShape) a).getBoundingBox().x;
		if (a instanceof FGEPolylin)
			return ((FGEPolylin) a).getBoundingBox().x;
		if (a instanceof FGEArc)
			return ((FGEArc) a).getBoundingBox().x;
		if (a instanceof FGEUnionArea && ((FGEUnionArea) a).isUnionOfPoints()) {
			double returned = Double.POSITIVE_INFINITY;
			for (FGEArea p : ((FGEUnionArea) a).getObjects()) {
				FGEPoint pt = (FGEPoint) p;
				if (pt.x < returned)
					returned = pt.x;
			}
			return returned;
		}
		if (a instanceof FGEUnionArea && ((FGEUnionArea) a).isUnionOfFiniteGeometricObjects()) {
			return a.getEmbeddingBounds().getMinX();
		}
		logger.warning("Unexpected " + a);
		return 0;
	}

	private double getMaxYFor(FGEArea a) {
		if (a instanceof FGEPoint)
			return ((FGEPoint) a).y;
		if (a instanceof FGESegment)
			return Math.max(((FGESegment) a).getP1().y, ((FGESegment) a).getP2().y);
		if (a instanceof FGEShape)
			return ((FGEShape) a).getBoundingBox().y + ((FGEShape) a).getBoundingBox().height;
		if (a instanceof FGEPolylin)
			return ((FGEPolylin) a).getBoundingBox().y + ((FGEPolylin) a).getBoundingBox().height;
		if (a instanceof FGEArc)
			return ((FGEArc) a).getBoundingBox().y + ((FGEArc) a).getBoundingBox().height;
		if (a instanceof FGEUnionArea && ((FGEUnionArea) a).isUnionOfPoints()) {
			double returned = Double.NEGATIVE_INFINITY;
			for (FGEArea p : ((FGEUnionArea) a).getObjects()) {
				FGEPoint pt = (FGEPoint) p;
				if (pt.y > returned)
					returned = pt.y;
			}
			return returned;
		}
		if (a instanceof FGEUnionArea && ((FGEUnionArea) a).isUnionOfFiniteGeometricObjects()) {
			return a.getEmbeddingBounds().getMaxY();
		}
		logger.warning("Unexpected " + a);
		return 0;
	}

	private double getMinYFor(FGEArea a) {
		if (a instanceof FGEPoint)
			return ((FGEPoint) a).y;
		if (a instanceof FGESegment)
			return Math.min(((FGESegment) a).getP1().y, ((FGESegment) a).getP2().y);
		if (a instanceof FGEShape)
			return ((FGEShape) a).getBoundingBox().y;
		if (a instanceof FGEPolylin)
			return ((FGEPolylin) a).getBoundingBox().y;
		if (a instanceof FGEArc)
			return ((FGEArc) a).getBoundingBox().y + ((FGEArc) a).getBoundingBox().height;
		if (a instanceof FGEUnionArea && ((FGEUnionArea) a).isUnionOfPoints()) {
			double returned = Double.POSITIVE_INFINITY;
			for (FGEArea p : ((FGEUnionArea) a).getObjects()) {
				FGEPoint pt = (FGEPoint) p;
				if (pt.y < returned)
					returned = pt.y;
			}
			return returned;
		}
		if (a instanceof FGEUnionArea && ((FGEUnionArea) a).isUnionOfFiniteGeometricObjects()) {
			return a.getEmbeddingBounds().getMinY();
		}
		logger.warning("Unexpected " + a);
		return 0;
	}

	public SimplifiedCardinalDirection getEndOrientation() {
		if (endOrientation == null && getSegmentNb() > 0) {
			endOrientation = getApproximatedOrientationOfSegment(getSegments().size() - 1);
		}
		return endOrientation;
	}

	public SimplifiedCardinalDirection getStartOrientation() {
		if (startOrientation == null && getSegmentNb() > 0) {
			startOrientation = getApproximatedOrientationOfSegment(0);
		}
		return startOrientation;
	}

	/**
	 * Return the segment on which middle is located
	 * 
	 * @return
	 */
	public FGESegment getMiddleSegment() {
		double middleDistancePath = getLength() / 2;
		double distance = 0;
		for (FGESegment s : getSegments()) {
			if ((distance <= middleDistancePath) && (distance + s.getLength() >= middleDistancePath)) {
				return s;
			}
			distance += s.getLength();
		}
		logger.warning("Unexpected situation while computing middle segment of rect polylin");
		return null;
	}

	public FGEPoint getNearestPointLocatedOnRoundedRepresentation(FGEPoint p, double arcWidth, double arcHeight) {
		FGEArc arc = getArcForNearestPointLocatedOnRoundedRepresentation(p, arcWidth, arcHeight);
		if (arc == null) {
			return getNearestPoint(p); // Point located outside arc
		} else {
			return arc.getNearestPoint(p);
		}
	}

	public FGEArc getArcForNearestPointLocatedOnRoundedRepresentation(FGEPoint p, double arcWidth, double arcHeight) {
		FGEPoint point = getNearestPoint(p);
		FGESegment segment = getProjectionSegment(point);
		// FGEPoint point = segment.getNearestPointOnSegment(p);
		int segmentId = getSegmentIndex(segment);
		SimplifiedCardinalDirection orientation = getApproximatedOrientationOfSegment(segmentId);
		FGEArc arc = null;
		FGEArc startArc = null;
		FGEArc endArc = null;
		if (segmentId > 0) {
			FGESegment previousSegment = getSegmentAt(segmentId - 1);
			startArc = getRoundedArc(previousSegment, segment, arcWidth, arcHeight);
		}
		if (segmentId < getSegmentNb() - 1) {
			FGESegment nextSegment = getSegmentAt(segmentId + 1);
			endArc = getRoundedArc(segment, nextSegment, arcWidth, arcHeight);
		}
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			if (segment.y1 - point.y < arcHeight)
				arc = startArc;
			if (point.y - segment.y2 < arcHeight)
				arc = endArc;
		}
		if (orientation == SimplifiedCardinalDirection.SOUTH) {
			if (point.y - segment.y1 < arcHeight)
				arc = startArc;
			if (segment.y2 - point.y < arcHeight)
				arc = endArc;
		}
		if (orientation == SimplifiedCardinalDirection.WEST) {
			if (segment.x1 - point.x < arcWidth)
				arc = startArc;
			if (point.x - segment.x2 < arcWidth)
				arc = endArc;
		}
		if (orientation == SimplifiedCardinalDirection.EAST) {
			if (point.x - segment.x1 < arcWidth)
				arc = startArc;
			if (segment.x2 - point.x < arcWidth)
				arc = endArc;
		}
		return arc;
	}

	protected FGEArc getRoundedArc(FGESegment s1, FGESegment s2, double arcWidth, double arcHeight) {
		if (s1 == null || s2 == null)
			return null;

		SimplifiedCardinalDirection orientation1 = s1.getApproximatedOrientation();
		SimplifiedCardinalDirection orientation2 = s2.getApproximatedOrientation();

		// Prevent rounded radius exceed half of segment length
		double arcRatio = arcWidth / arcHeight;
		if (s1.isVertical() && s2.isHorizontal()) {
			if (s1.getLength() < arcHeight * 2) {
				arcHeight = s1.getLength() / 2;
				arcWidth = arcHeight * arcRatio;
			}
			if (s2.getLength() < arcWidth * 2) {
				arcWidth = s2.getLength() / 2;
				arcHeight = arcWidth / arcRatio;
			}
		} else if (s1.isHorizontal() && s2.isVertical()) {
			if (s1.getLength() < arcWidth * 2) {
				arcWidth = s1.getLength() / 2;
				arcHeight = arcWidth / arcRatio;
			}
			if (s2.getLength() < arcHeight * 2) {
				arcHeight = s2.getLength() / 2;
				arcWidth = arcHeight * arcRatio;
			}
		}

		if (orientation1 == SimplifiedCardinalDirection.NORTH) {
			if (orientation2 == SimplifiedCardinalDirection.EAST)
				return new FGEArc(new FGEPoint(s1.x2 + arcWidth, s1.y2 + arcHeight), new FGEDimension(arcWidth * 2, arcHeight * 2), 180,
						-90);
			if (orientation2 == SimplifiedCardinalDirection.WEST)
				return new FGEArc(new FGEPoint(s1.x2 - arcWidth, s1.y2 + arcHeight), new FGEDimension(arcWidth * 2, arcHeight * 2), 0, 90);
			return null;
		}
		if (orientation1 == SimplifiedCardinalDirection.SOUTH) {
			if (orientation2 == SimplifiedCardinalDirection.EAST)
				return new FGEArc(new FGEPoint(s1.x2 + arcWidth, s1.y2 - arcHeight), new FGEDimension(arcWidth * 2, arcHeight * 2), -180,
						90);
			if (orientation2 == SimplifiedCardinalDirection.WEST)
				return new FGEArc(new FGEPoint(s1.x2 - arcWidth, s1.y2 - arcHeight), new FGEDimension(arcWidth * 2, arcHeight * 2), 0, -90);
			return null;
		}
		if (orientation1 == SimplifiedCardinalDirection.EAST) {
			if (orientation2 == SimplifiedCardinalDirection.NORTH)
				return new FGEArc(new FGEPoint(s1.x2 - arcWidth, s1.y2 - arcHeight), new FGEDimension(arcWidth * 2, arcHeight * 2), -90, 90);
			if (orientation2 == SimplifiedCardinalDirection.SOUTH)
				return new FGEArc(new FGEPoint(s1.x2 - arcWidth, s1.y2 + arcHeight), new FGEDimension(arcWidth * 2, arcHeight * 2), 90, -90);
			return null;
		}
		if (orientation1 == SimplifiedCardinalDirection.WEST) {
			if (orientation2 == SimplifiedCardinalDirection.NORTH)
				return new FGEArc(new FGEPoint(s1.x2 + arcWidth, s1.y2 - arcHeight), new FGEDimension(arcWidth * 2, arcHeight * 2), -90,
						-90);
			if (orientation2 == SimplifiedCardinalDirection.SOUTH)
				return new FGEArc(new FGEPoint(s1.x2 + arcWidth, s1.y2 + arcHeight), new FGEDimension(arcWidth * 2, arcHeight * 2), 90, 90);
			return null;
		}
		return null;
	}

	/**
	 * Return the nearest segment (if any) on which supplied point p might be projected
	 * 
	 * @return the related segment, or null if no such segment has been found
	 */
	public FGESegment getProjectionSegment(FGEPoint p) {
		double shortestDistance = Double.POSITIVE_INFINITY;
		FGESegment returned = null;
		for (FGESegment s : getSegments()) {
			if (s.projectionIntersectsInsideSegment(p)) {
				double distance = FGEPoint.distance(p, s.getProjection(p));
				if (distance <= shortestDistance) {
					returned = s;
					shortestDistance = distance;
				}
			}
		}
		return returned;
	}

	public SimplifiedCardinalDirection getOrientationOfSegment(int index) {
		FGESegment segment = getSegmentAt(index);
		if (segment != null)
			return segment.getOrientation();
		return null;

		/*double angle = segment.getAngle();

		if (Math.abs(angle) < EPSILON) return SimplifiedCardinalDirection.WEST;
		else if ((Math.abs(angle-Math.PI) < EPSILON) 
				|| (Math.abs(angle+Math.PI) < EPSILON)) return SimplifiedCardinalDirection.EAST;
		else if (Math.abs(angle-Math.PI/2) < EPSILON) return SimplifiedCardinalDirection.SOUTH;
		else if (Math.abs(angle-3*Math.PI/2) < EPSILON) return SimplifiedCardinalDirection.NORTH;
		else return null;*/
	}

	public SimplifiedCardinalDirection getApproximatedOrientationOfSegment(int index) {
		FGESegment segment = getSegmentAt(index);
		if (segment != null)
			return segment.getApproximatedOrientation();
		return null;

		/*SimplifiedCardinalDirection returned = getOrientationOfSegment(index);
		if (returned != null) return returned;

		FGESegment segment = getSegmentAt(index);
		return FGEPoint.getSimplifiedOrientation(segment.getP1(), segment.getP2());*/
	}

	@Override
	public FGERectPolylin transform(AffineTransform t) {
		Vector<FGEPoint> points = new Vector<FGEPoint>();
		for (FGEPoint p : _points) {
			points.add(p.transform(t));
		}
		FGERectPolylin returned = new FGERectPolylin(points);
		return returned;
	}

	@Override
	public FGERectPolylin clone() {
		// return (FGERectPolylin)super.clone();
		FGERectPolylin returned = new FGERectPolylin();
		returned.overlapX = overlapX;
		returned.overlapY = overlapY;
		returned.straightWhenPossible = straightWhenPossible;
		for (FGEPoint p : getPoints()) {
			returned.addToPoints(p.clone());
		}
		return returned;
	}

	/**
	 * Return a flag indicating wether this polylin is normalized or not A rect polylin is normalized when: - all segments are rect (their
	 * orientations matches cardinal orientation NORTH, EAST, SOUTH, WEST) - no extra points are found (no point located inside a segment,
	 * which means that no colinear adjascent segment were found)
	 * 
	 * @return true if this polylin is normalized, false otherwise
	 */
	public boolean isNormalized() {
		return !hasNonRectSegments() && !hasExtraPoints();
	}

	/**
	 * Return a flag indicating wether this polylin has non-rect segment (their orientations doesn't matches cardinal orientation NORTH,
	 * EAST, SOUTH, WEST)
	 * 
	 * @return
	 */
	public boolean hasNonRectSegments() {
		for (int i = 0; i < getSegmentNb(); i++) {
			if (getOrientationOfSegment(i) == null)
				return true;
		}
		return false;
	}

	/**
	 * Return a flag indicating wether this polylin has extra point (no point located inside a segment, which means that no colinear
	 * adjascent segment were found)
	 * 
	 * @return true if this polylin has extra points, false otherwise
	 */
	public boolean hasExtraPoints() {
		return hasEqualsAdjascentPoints() || hasColinearAdjascentSegments();
	}

	// Check for 2 equal adjascent point
	private boolean hasEqualsAdjascentPoints() {
		FGEPoint previous = null;
		for (FGEPoint p : getPoints()) {
			if (previous != null && previous.equals(p))
				return true;
			previous = p;
		}
		return false;
	}

	// Remove point, if any
	private void removeFirstEqualsAdjascentPoints() {
		FGEPoint previous = null;
		for (int i = 0; i < getPointsNb(); i++) {
			if (previous != null && previous.equals(getPointAt(i))) {
				removePointAtIndex(i);
				return;
			}
			previous = getPointAt(i);
		}
	}

	// Check for 2 colinear adjascent segments
	private boolean hasColinearAdjascentSegments() {
		FGESegment previous_s = null;
		for (FGESegment s : getSegments()) {
			if (previous_s != null && previous_s.getApproximatedOrientation().equals(s.getApproximatedOrientation()))
				return true;
			previous_s = s;
		}
		return false;
	}

	// Remove point, if any
	private void removeFirstColinearAdjascentSegments() {
		FGESegment previous_s = null;
		for (int i = 0; i < getSegmentNb(); i++) {
			FGESegment s = getSegmentAt(i);
			if (previous_s != null && previous_s.getApproximatedOrientation().equals(s.getApproximatedOrientation())) {
				removePointAtIndex(i);
				return;
			}
			previous_s = s;
		}
	}

	public void removeExtraPoints() {
		/*logger.info("removeExtraPoints()");
		for (FGEPoint p : getPoints()) {
			System.out.println("WAS:"+p);
		}
		for (FGESegment s : getSegments()) {
			System.out.println("WAS:"+s);
		}*/
		while (hasEqualsAdjascentPoints()) {
			removeFirstEqualsAdjascentPoints();
		}
		while (hasColinearAdjascentSegments()) {
			removeFirstColinearAdjascentSegments();
		}
		/*for (FGEPoint p : getPoints()) {
			System.out.println("NOW:"+p);
		}
		for (FGESegment s : getSegments()) {
			System.out.println("NOW:"+s);
		}*/
	}

	public boolean isNormalizable() {
		if (isNormalized())
			return true;
		FGERectPolylin normalizedRect = makeNormalizedRectPolylin();
		return (normalizedRect.getPointsNb() == getPointsNb());
	}

	public void normalize() {
		if (isNormalized())
			return;
		FGERectPolylin normalizedRect = makeNormalizedRectPolylin();
		if (normalizedRect.getPointsNb() != getPointsNb()) {
			throw new IllegalArgumentException("Cannot normalize a non-normalizable rect");
		}
		for (int i = 0; i < getPointsNb(); i++) {
			updatePointAt(i, normalizedRect.getPointAt(i));
		}
	}

	/**
	 * Build and return a normalized rect polylin given this polylin
	 * 
	 * @return
	 */
	public FGERectPolylin makeNormalizedRectPolylin() {

		FGERectPolylin updatedPolylin = clone();

		// Now iterate both sides
		/*FGEPoint*/currentPointStartingSide = updatedPolylin.getPointAt(0);
		/*FGEPoint*/currentPointEndingSide = updatedPolylin.getPointAt(updatedPolylin.getPointsNb() - 1);
		int currentStartIndex = 0;
		int currentEndIndex = updatedPolylin.getPointsNb() - 1;

		for (int i = 1; i < getPointsNb() / 2; i++) {
			// Starting side
			if (logger.isLoggable(Level.FINEST))
				logger.finest("Checking point at " + i);
			currentStartIndex = i;

			FGEPoint previousPointStartingSide = updatedPolylin.getPointAt(i - 1);
			currentPointStartingSide = updatedPolylin.getPointAt(i);
			SimplifiedCardinalDirection orientation = updatedPolylin.getApproximatedOrientationOfSegment(i - 1);
			if (orientation.isVertical()) {
				if (currentPointStartingSide.x != previousPointStartingSide.x) {
					if (logger.isLoggable(Level.FINEST))
						logger.finest("Updating point at " + i + " for vertical orientation");
					currentPointStartingSide.x = previousPointStartingSide.x;
					updatedPolylin.updatePointAt(i, currentPointStartingSide);
				}
			} else if (orientation.isHorizontal()) {
				if (currentPointStartingSide.y != previousPointStartingSide.y) {
					if (logger.isLoggable(Level.FINEST))
						logger.finest("Updating point at " + i + " for horizontal orientation");
					currentPointStartingSide.y = previousPointStartingSide.y;
					updatedPolylin.updatePointAt(i, currentPointStartingSide);
				}
			}

			// Ending side
			if (logger.isLoggable(Level.FINEST))
				logger.finest("Checking point at " + (updatedPolylin.getPointsNb() - i - 1));
			currentEndIndex = updatedPolylin.getPointsNb() - i - 1;

			FGEPoint previousPointEndingSide = updatedPolylin.getPointAt(updatedPolylin.getPointsNb() - i);
			currentPointEndingSide = updatedPolylin.getPointAt(updatedPolylin.getPointsNb() - i - 1);
			SimplifiedCardinalDirection orientation2 = updatedPolylin.getApproximatedOrientationOfSegment(updatedPolylin.getPointsNb() - i
					- 1);
			if (orientation2.isVertical()) {
				if (currentPointEndingSide.x != previousPointEndingSide.x) {
					if (logger.isLoggable(Level.FINEST))
						logger.finest("Updating point at " + (updatedPolylin.getPointsNb() - i - 1) + " for vertical orientation");
					currentPointEndingSide.x = previousPointEndingSide.x;
					updatedPolylin.updatePointAt(updatedPolylin.getPointsNb() - i - 1, currentPointEndingSide);
				}
			} else if (orientation2.isHorizontal()) {
				if (currentPointEndingSide.y != previousPointEndingSide.y) {
					if (logger.isLoggable(Level.FINEST))
						logger.finest("Updating point at " + (updatedPolylin.getPointsNb() - i - 1) + " for horizontal orientation");
					currentPointEndingSide.y = previousPointEndingSide.y;
					updatedPolylin.updatePointAt(updatedPolylin.getPointsNb() - i - 1, currentPointEndingSide);
				}
			}
		}
		// Finish updating
		if (logger.isLoggable(Level.FINEST))
			logger.finest("Still have to finish updating, currentStartIndex=" + currentStartIndex + " currentEndIndex=" + currentEndIndex);

		if (currentPointEndingSide.equals(currentPointStartingSide)
				|| Math.abs(currentPointEndingSide.x - currentPointStartingSide.x) < EPSILON
				|| Math.abs(currentPointEndingSide.y - currentPointStartingSide.y) < EPSILON) {
			// No need to append extra path
		} else {
			Vector<SimplifiedCardinalDirection> excludedStartOrientations = new Vector<SimplifiedCardinalDirection>();
			Vector<SimplifiedCardinalDirection> excludedEndOrientations = new Vector<SimplifiedCardinalDirection>();
			if (currentStartIndex - 1 >= 0 && currentStartIndex - 1 < updatedPolylin.getSegmentNb()) {
				excludedStartOrientations.add(updatedPolylin.getApproximatedOrientationOfSegment(currentStartIndex - 1).getOpposite());
				// System.out.println("excludedStartOrientations="+excludedStartOrientations);
			}
			if (currentEndIndex >= 0 && currentEndIndex < updatedPolylin.getSegmentNb()) {
				excludedEndOrientations.add(updatedPolylin.getApproximatedOrientationOfSegment(currentEndIndex));
				// System.out.println("excludedEndOrientations="+excludedEndOrientations);
			}
			// System.out.println("overlap="+overlap);
			if (currentEndIndex - currentStartIndex == 1) {
				/*FGERectPolylin*/missingPath = makeShortestRectPolylin(currentPointStartingSide, currentPointEndingSide, true, overlapX,
						overlapY, excludedStartOrientations, excludedEndOrientations);
				for (int i = 1; i < missingPath.getPointsNb() - 1; i++) {
					updatedPolylin.insertPointAtIndex(missingPath.getPointAt(i), currentStartIndex + i);
				}
			} else if (currentEndIndex - currentStartIndex == 2) {
				/*FGERectPolylin*/// missingPath = makeShortestRectPolylin(currentPointStartingSide, currentPointEndingSide, true, overlap,
									// updatedPolylin.getPointAt(currentStartIndex+1), excludedStartOrientations, excludedEndOrientations);
				missingPath = makeRectPolylinCrossingPoint(currentPointStartingSide, currentPointEndingSide,
						updatedPolylin.getPointAt(currentStartIndex + 1), true, overlapX, overlapY, excludedStartOrientations,
						excludedEndOrientations);
				updatedPolylin.removePointAtIndex(currentStartIndex + 1);
				for (int i = 1; i < missingPath.getPointsNb() - 1; i++) {
					updatedPolylin.insertPointAtIndex(missingPath.getPointAt(i), currentStartIndex + i);
				}
			}
		}

		return updatedPolylin;
	}

	// TODO debug: remove this
	public FGERectPolylin missingPath;
	public FGEPoint currentPointStartingSide;
	public FGEPoint currentPointEndingSide;

	public double getOverlapX() {
		return overlapX;
	}

	public void setOverlapX(double overlap) {
		this.overlapX = overlap;
	}

	public double getOverlapY() {
		return overlapY;
	}

	public void setOverlapY(double overlap) {
		this.overlapY = overlap;
	}

	public FGEArea getEndArea() {
		// if no start area defined, return start point
		if (endArea == null && getPointsNb() > 0)
			return getPointAt(getPointsNb() - 1);
		return endArea;
	}

	public FGEArea getStartArea() {
		// if no start area defined, return start point
		if (startArea == null && getPointsNb() > 0)
			return getPointAt(0);
		return startArea;
	}

	public boolean doesRespectAllConstraints() {
		return respectAllConstraints && getPointsNb() > 0;
	}
}
