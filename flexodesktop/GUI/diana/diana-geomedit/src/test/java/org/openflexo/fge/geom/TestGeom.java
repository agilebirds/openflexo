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

import java.util.logging.Logger;

import junit.framework.TestCase;

import org.openflexo.fge.geom.FGEGeometricObject.CardinalDirection;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEGrid;
import org.openflexo.fge.geomedit.GeometricObject;
import org.openflexo.fge.geomedit.GeometricSet;
import org.openflexo.fge.geomedit.ObjectIntersection;
import org.openflexo.fge.geomedit.construction.IntersectionConstruction;
import org.openflexo.toolbox.FileResource;

public class TestGeom extends TestCase {

	private static final Logger logger = Logger.getLogger(TestGeom.class.getPackage().getName());

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testBasicObjects() {
		executeTest(new FileResource("GeomJUnitTest/TestBasicObjects.drw"));
	}

	public void testLines() {
		executeTest(new FileResource("GeomJUnitTest/TestLines.drw"));
	}

	public void testLines2() {
		executeTest(new FileResource("GeomJUnitTest/TestLines2.drw"));
	}

	public void testLineIntersection1() {
		executeTest(new FileResource("GeomJUnitTest/TestLineIntersection1.drw"));
	}

	public void testLineIntersection2() {
		executeTest(new FileResource("GeomJUnitTest/TestLineIntersection2.drw"));
	}

	public void testLineLineIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestLineLineIntersection.drw"));
	}

	public void testLineHalfLineIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestLineHalfLineIntersection.drw"));
	}

	public void testHalfLineHalfLineIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestHalfLineHalfLineIntersection.drw"));
	}

	public void testLineSegmentIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestLineSegmentIntersection.drw"));
	}

	public void testHalfLineSegmentIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestHalfLineSegmentIntersection.drw"));
	}

	public void testFilledRectangleLineIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestFilledRectangleLineIntersection.drw"));
	}

	public void testOpenRectangleLineIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestOpenRectangleLineIntersection.drw"));
	}

	public void testFilledRectangleHalfLineIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestFilledRectangleHalfLineIntersection.drw"));
	}

	public void testOpenRectangleHalfLineIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestOpenRectangleHalfLineIntersection.drw"));
	}

	public void testFilledRectangleSegmentIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestFilledRectangleSegmentIntersection.drw"));
	}

	public void testOpenRectangleSegmentIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestOpenRectangleSegmentIntersection.drw"));
	}

	public void testOpenRectangleIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestOpenRectangleIntersection.drw"));
	}

	public void testFilledRectangleIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestFilledRectangleIntersection.drw"));
	}

	public void testMixedRectangleIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestMixedRectangleIntersection.drw"));
	}

	public void testShapeIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestShapeIntersection.drw"));
	}

	public void testHalfPlaneRectangleIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestHalfPlaneRectangleIntersection.drw"));
	}

	public void testHalfPlaneLineIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestHalfPlaneLineIntersection.drw"));
	}

	public void testHalfPlaneHalfLineIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestHalfPlaneHalfLineIntersection.drw"));
	}

	public void testHalfPlaneSegmentIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestHalfPlaneSegmentIntersection.drw"));
	}

	public void testBandLineIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestBandLineIntersection.drw"));
	}

	public void testBandHalfLineIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestBandHalfLineIntersection.drw"));
	}

	public void testBandSegmentIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestBandSegmentIntersection.drw"));
	}

	public void testBandFilledRectangleIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestBandFilledRectangleIntersection.drw"));
	}

	public void testBandOpenRectangleIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestBandOpenRectangleIntersection.drw"));
	}

	public void testBandHalfPlaneIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestBandHalfPlaneIntersection.drw"));
	}

	public void testBandBandIntersection1() {
		executeTest(new FileResource("GeomJUnitTest/TestBandBandIntersection1.drw"));
	}

	public void testBandBandIntersection2() {
		executeTest(new FileResource("GeomJUnitTest/TestBandBandIntersection2.drw"));
	}

	public void testBandHalfBandIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestBandHalfBandIntersection.drw"));
	}

	public void testHalfBandHalfPlaneIntersection1() {
		executeTest(new FileResource("GeomJUnitTest/TestHalfBandHalfPlaneIntersection1.drw"));
	}

	public void testHalfBandHalfPlaneIntersection2() {
		executeTest(new FileResource("GeomJUnitTest/TestHalfBandHalfPlaneIntersection2.drw"));
	}

	public void testHalfBandHalfLineIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestHalfBandHalfLineIntersection.drw"));
	}

	public void testCurves() {
		executeTest(new FileResource("GeomJUnitTest/TestCurves.drw"));
	}

	public void testComplexCurve() {
		executeTest(new FileResource("GeomJUnitTest/TestComplexCurve.drw"));
	}

	public void testHalfBandCircleIntersection() {
		executeTest(new FileResource("GeomJUnitTest/HalfBandCircleIntersection.drw"));
	}

	public void testCircleLineIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestCircleLineIntersection.drw"));
	}

	public void testRoundRectangleLineIntersection() {
		executeTest(new FileResource("GeomJUnitTest/TestRoundRectangleLineIntersection.drw"));
	}

	public void testGrid() {
		//
		FGEPoint testPoint1 = new FGEPoint(0, 0);
		FGEPoint testPoint2 = new FGEPoint(1, 1);
		FGEPoint testPoint3 = new FGEPoint(0.8, 0.8);
		FGEPoint testPoint4 = new FGEPoint(2.7, 1.3);
		FGEGrid grid = new FGEGrid();
		assertTrue(grid.containsPoint(new FGEPoint(0, 0)));
		assertTrue(grid.containsPoint(new FGEPoint(1, 0)));
		assertTrue(grid.containsPoint(new FGEPoint(0, 1)));
		assertTrue(grid.containsPoint(new FGEPoint(1, 1)));
		assertFalse(grid.containsPoint(new FGEPoint(0.5, 0)));
		assertFalse(grid.containsPoint(new FGEPoint(0, 0.5)));
		assertEquals(new FGEPoint(0, 0), grid.getNearestPoint(testPoint1));
		assertEquals(new FGEPoint(1, 1), grid.getNearestPoint(testPoint2));
		assertEquals(new FGEPoint(1, 1), grid.getNearestPoint(testPoint3));
		assertEquals(new FGEPoint(3, 1), grid.getNearestPoint(testPoint4));

		FGEGrid grid2 = new FGEGrid(new FGEPoint(0.5, 0.5), 0.25, 0.25);
		assertTrue(grid2.containsPoint(new FGEPoint(0, 0)));
		assertFalse(grid2.containsPoint(new FGEPoint(0.3, 0)));
		assertFalse(grid2.containsPoint(new FGEPoint(0, 0.3)));
		assertTrue(grid2.containsPoint(new FGEPoint(1, 0.5)));
		assertTrue(grid2.containsPoint(new FGEPoint(0.5, 1)));
		assertTrue(grid2.containsPoint(new FGEPoint(1, 1)));
		assertEquals(new FGEPoint(0, 0), grid2.getNearestPoint(testPoint1));
		assertEquals(new FGEPoint(1, 1), grid2.getNearestPoint(testPoint2));
		assertEquals(new FGEPoint(0.75, 0.75), grid2.getNearestPoint(testPoint3));
		assertEquals(new FGEPoint(2.75, 1.25), grid2.getNearestPoint(testPoint4));

		FGEGrid grid3 = new FGEGrid(new FGEPoint(0.3, 0.3), 0.4, 0.4);
		assertTrue(grid3.containsPoint(new FGEPoint(-0.1, -0.1)));
		assertTrue(grid3.containsPoint(new FGEPoint(0.3, 0.3)));
		assertTrue(grid3.containsPoint(new FGEPoint(0.7, 0.7)));
		assertTrue(grid3.containsPoint(new FGEPoint(0.3, 0.7)));
		assertTrue(grid3.containsPoint(new FGEPoint(0.3, 0.3)));
		assertEquals(new FGEPoint(-0.1, -0.1), grid3.getNearestPoint(testPoint1));
		assertEquals(new FGEPoint(1.1, 1.1), grid3.getNearestPoint(testPoint2));
		assertEquals(new FGEPoint(0.7, 0.7), grid3.getNearestPoint(testPoint3));
		assertEquals(new FGEPoint(2.7, 1.1), grid3.getNearestPoint(testPoint4));

	}

	public void testPointOrientation() {
		FGEPoint topLeft = new FGEPoint(0, 0);
		FGEPoint topRight = new FGEPoint(1, 0);
		FGEPoint bottomLeft = new FGEPoint(0, 1);
		FGEPoint bottomRight = new FGEPoint(1, 1);
		FGEPoint top = new FGEPoint(0.5, 0);
		FGEPoint left = new FGEPoint(0, 0.5);
		FGEPoint bottom = new FGEPoint(0.5, 1);
		FGEPoint right = new FGEPoint(1, 0.5);
		FGEPoint center = new FGEPoint(0.5, 0.5);
		assertEquals(CardinalDirection.NORTH_WEST, FGEPoint.getOrientation(center, topLeft));
		assertEquals(CardinalDirection.NORTH_WEST, FGEPoint.getOrientation(center, new FGEPoint(topLeft.x + 0.125, topLeft.y)));
		assertEquals(CardinalDirection.NORTH_WEST, FGEPoint.getOrientation(center, new FGEPoint(topLeft.x, topLeft.y + 0.125)));
		assertEquals(CardinalDirection.NORTH, FGEPoint.getOrientation(center, top));
		assertEquals(CardinalDirection.NORTH, FGEPoint.getOrientation(center, new FGEPoint(top.x + 0.125, top.y)));
		assertEquals(CardinalDirection.NORTH, FGEPoint.getOrientation(center, new FGEPoint(top.x - 0.125, top.y)));
		assertEquals(CardinalDirection.NORTH_EAST, FGEPoint.getOrientation(center, topRight));
		assertEquals(CardinalDirection.NORTH_EAST, FGEPoint.getOrientation(center, new FGEPoint(topRight.x - 0.125, topRight.y)));
		assertEquals(CardinalDirection.NORTH_EAST, FGEPoint.getOrientation(center, new FGEPoint(topRight.x, topRight.y + 0.125)));
		assertEquals(CardinalDirection.EAST, FGEPoint.getOrientation(center, right));
		assertEquals(CardinalDirection.EAST, FGEPoint.getOrientation(center, new FGEPoint(right.x, right.y + 0.125)));
		assertEquals(CardinalDirection.EAST, FGEPoint.getOrientation(center, new FGEPoint(right.x, right.y - 0.125)));
		assertEquals(CardinalDirection.SOUTH_EAST, FGEPoint.getOrientation(center, bottomRight));
		assertEquals(CardinalDirection.SOUTH_EAST, FGEPoint.getOrientation(center, new FGEPoint(bottomRight.x - 0.125, bottomRight.y)));
		assertEquals(CardinalDirection.SOUTH_EAST, FGEPoint.getOrientation(center, new FGEPoint(bottomRight.x, bottomRight.y - 0.125)));
		assertEquals(CardinalDirection.SOUTH, FGEPoint.getOrientation(center, bottom));
		assertEquals(CardinalDirection.SOUTH, FGEPoint.getOrientation(center, new FGEPoint(bottom.x + 0.125, bottom.y)));
		assertEquals(CardinalDirection.SOUTH, FGEPoint.getOrientation(center, new FGEPoint(bottom.x - 0.125, bottom.y)));
		assertEquals(CardinalDirection.SOUTH_WEST, FGEPoint.getOrientation(center, bottomLeft));
		assertEquals(CardinalDirection.SOUTH_WEST, FGEPoint.getOrientation(center, new FGEPoint(bottomLeft.x - 0.125, bottomLeft.y)));
		assertEquals(CardinalDirection.SOUTH_WEST, FGEPoint.getOrientation(center, new FGEPoint(bottomLeft.x, bottomLeft.y + 0.125)));
		assertEquals(CardinalDirection.WEST, FGEPoint.getOrientation(center, left));
		assertEquals(CardinalDirection.WEST, FGEPoint.getOrientation(center, new FGEPoint(left.x, left.y + 0.125)));
		assertEquals(CardinalDirection.WEST, FGEPoint.getOrientation(center, new FGEPoint(left.x, left.y - 0.125)));

		/*assertEquals(CardinalDirection.EAST, FGEPoint.getOrientation(center, right));
		assertEquals(CardinalDirection.EAST, FGEPoint.getOrientation(center, right));
		assertEquals(CardinalDirection.EAST, FGEPoint.getOrientation(center, right));
		assertEquals(CardinalDirection.EAST, FGEPoint.getOrientation(center, right));*/
	}

	private void executeTest(FileResource testFile) {
		logger.info(">>>>>>> Test " + testFile.getName());
		GeometricSet geometricSet = GeometricSet.load(testFile);
		for (GeometricObject object : geometricSet.getChilds()) {
			logger.fine("Check equals: " + object.getResultingGeometricObject() + " / " + object.getGeometricObject());
			assertEquals(object.getResultingGeometricObject(), object.getGeometricObject());
			if (object instanceof ObjectIntersection) {
				IntersectionConstruction construction = ((ObjectIntersection) object).getConstruction();
				FGEArea o1 = construction.objectConstructions.get(0).getData();
				FGEArea o2 = construction.objectConstructions.get(1).getData();
				logger.fine("Checking intersection reversibility");
				assertEquals(o1.intersect(o2), o2.intersect(o1));
			}
		}
	}
}
