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

import org.junit.Assert;
import org.junit.Test;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.area.FGEHalfPlane;
import org.openflexo.fge.geom.area.FGEIntersectionArea;

public class TestIntersection extends Assert {

	private static final FGESegment line1 = new FGESegment(new FGEPoint(0, 0), new FGEPoint(0, 1)); // Vertical line
	private static final FGELine line2 = new FGELine(new FGEPoint(0, 0), new FGEPoint(1, 1)); // Diagonal (top-left to bottom-right)
	private static final FGERectangle rectangle = new FGERectangle(new FGEPoint(0, 0), new FGEPoint(1, 1), Filling.FILLED);
	private static final FGEHalfPlane hp = new FGEHalfPlane(line1, new FGEPoint(-1, 1));
	private static final FGEPoint TOP_LEFT = new FGEPoint(0, 0);
	private static final FGEPoint TOP_RIGHT = new FGEPoint(1, 0);
	private static final FGEPoint BOTTOM_LEFT = new FGEPoint(0, 1);
	private static final FGEPoint BOTTOM_RIGHT = new FGEPoint(1, 1);
	private static final FGEPoint MIDDLE = new FGEPoint(0.5, 0.5);
	private static final FGEPolygon DIAMOND = new FGEPolygon(Filling.FILLED, new FGEPoint(0.5, 0), new FGEPoint(1, 0.5), new FGEPoint(0.5,
			1), new FGEPoint(0, 0.5));

	private static final FGELine HORIZONTAL_LINE = new FGELine(0, 1, 0);
	private static final FGELine OFFSETED_HORIZONTAL_LINE = new FGELine(0, 1, -1);
	private static final FGELine VERTICAL_LINE = new FGELine(1, 0, 0);
	private static final FGELine OFFSETED_VERTICAL_LINE = new FGELine(1, 0, -1);

	private static final FGERoundRectangle ROUND_RECTANGLE = new FGERoundRectangle(0, 0, 1, 1, 0.01, 0.01);

	@Test
	public void testParallelism() {
		assertFalse(VERTICAL_LINE.isParallelTo(rectangle.getNorth()));
		assertFalse(VERTICAL_LINE.isParallelTo(rectangle.getSouth()));
		assertTrue(VERTICAL_LINE.isParallelTo(rectangle.getEast()));
		assertTrue(VERTICAL_LINE.isParallelTo(rectangle.getWest()));

		assertFalse(OFFSETED_VERTICAL_LINE.isParallelTo(rectangle.getNorth()));
		assertFalse(OFFSETED_VERTICAL_LINE.isParallelTo(rectangle.getSouth()));
		assertTrue(OFFSETED_VERTICAL_LINE.isParallelTo(rectangle.getEast()));
		assertTrue(OFFSETED_VERTICAL_LINE.isParallelTo(rectangle.getWest()));

		assertTrue(HORIZONTAL_LINE.isParallelTo(rectangle.getNorth()));
		assertTrue(HORIZONTAL_LINE.isParallelTo(rectangle.getSouth()));
		assertFalse(HORIZONTAL_LINE.isParallelTo(rectangle.getEast()));
		assertFalse(HORIZONTAL_LINE.isParallelTo(rectangle.getWest()));

		assertTrue(OFFSETED_HORIZONTAL_LINE.isParallelTo(rectangle.getNorth()));
		assertTrue(OFFSETED_HORIZONTAL_LINE.isParallelTo(rectangle.getSouth()));
		assertFalse(OFFSETED_HORIZONTAL_LINE.isParallelTo(rectangle.getEast()));
		assertFalse(OFFSETED_HORIZONTAL_LINE.isParallelTo(rectangle.getWest()));
	}

	@Test
	public void testOrthonalism() {
		assertTrue(VERTICAL_LINE.isOrthogonalTo(rectangle.getNorth()));
		assertTrue(VERTICAL_LINE.isOrthogonalTo(rectangle.getSouth()));
		assertFalse(VERTICAL_LINE.isOrthogonalTo(rectangle.getEast()));
		assertFalse(VERTICAL_LINE.isOrthogonalTo(rectangle.getWest()));

		assertTrue(OFFSETED_VERTICAL_LINE.isOrthogonalTo(rectangle.getNorth()));
		assertTrue(OFFSETED_VERTICAL_LINE.isOrthogonalTo(rectangle.getSouth()));
		assertFalse(OFFSETED_VERTICAL_LINE.isOrthogonalTo(rectangle.getEast()));
		assertFalse(OFFSETED_VERTICAL_LINE.isOrthogonalTo(rectangle.getWest()));

		assertFalse(HORIZONTAL_LINE.isOrthogonalTo(rectangle.getNorth()));
		assertFalse(HORIZONTAL_LINE.isOrthogonalTo(rectangle.getSouth()));
		assertTrue(HORIZONTAL_LINE.isOrthogonalTo(rectangle.getEast()));
		assertTrue(HORIZONTAL_LINE.isOrthogonalTo(rectangle.getWest()));

		assertFalse(OFFSETED_HORIZONTAL_LINE.isOrthogonalTo(rectangle.getNorth()));
		assertFalse(OFFSETED_HORIZONTAL_LINE.isOrthogonalTo(rectangle.getSouth()));
		assertTrue(OFFSETED_HORIZONTAL_LINE.isOrthogonalTo(rectangle.getEast()));
		assertTrue(OFFSETED_HORIZONTAL_LINE.isOrthogonalTo(rectangle.getWest()));
	}

	@Test
	public void testContainment() {
		assertTrue(line2.contains(MIDDLE));
		assertFalse(line2.contains(TOP_RIGHT));
		assertFalse(line2.contains(BOTTOM_LEFT));

		assertTrue(HORIZONTAL_LINE.contains(TOP_LEFT));
		assertTrue(HORIZONTAL_LINE.contains(TOP_RIGHT));
		assertFalse(HORIZONTAL_LINE.contains(BOTTOM_LEFT));
		assertFalse(HORIZONTAL_LINE.contains(BOTTOM_RIGHT));
		assertEquals(HORIZONTAL_LINE.getPlaneLocation(BOTTOM_LEFT), HORIZONTAL_LINE.getPlaneLocation(MIDDLE));
		assertEquals(HORIZONTAL_LINE.getPlaneLocation(BOTTOM_RIGHT), HORIZONTAL_LINE.getPlaneLocation(MIDDLE));

		assertFalse(OFFSETED_HORIZONTAL_LINE.contains(TOP_LEFT));
		assertFalse(OFFSETED_HORIZONTAL_LINE.contains(TOP_RIGHT));
		assertTrue(OFFSETED_HORIZONTAL_LINE.contains(BOTTOM_LEFT));
		assertTrue(OFFSETED_HORIZONTAL_LINE.contains(BOTTOM_RIGHT));
		assertEquals(OFFSETED_HORIZONTAL_LINE.getPlaneLocation(TOP_LEFT), OFFSETED_HORIZONTAL_LINE.getPlaneLocation(MIDDLE));
		assertEquals(OFFSETED_HORIZONTAL_LINE.getPlaneLocation(TOP_RIGHT), OFFSETED_HORIZONTAL_LINE.getPlaneLocation(MIDDLE));

		assertTrue(VERTICAL_LINE.contains(TOP_LEFT));
		assertTrue(VERTICAL_LINE.contains(BOTTOM_LEFT));
		assertFalse(VERTICAL_LINE.contains(TOP_RIGHT));
		assertFalse(VERTICAL_LINE.contains(BOTTOM_RIGHT));
		assertEquals(VERTICAL_LINE.getPlaneLocation(TOP_RIGHT), VERTICAL_LINE.getPlaneLocation(MIDDLE));
		assertEquals(VERTICAL_LINE.getPlaneLocation(BOTTOM_RIGHT), VERTICAL_LINE.getPlaneLocation(MIDDLE));

		assertTrue(OFFSETED_VERTICAL_LINE.contains(BOTTOM_RIGHT));
		assertTrue(OFFSETED_VERTICAL_LINE.contains(TOP_RIGHT));
		assertFalse(OFFSETED_VERTICAL_LINE.contains(BOTTOM_LEFT));
		assertFalse(OFFSETED_VERTICAL_LINE.contains(TOP_LEFT));
		assertEquals(OFFSETED_VERTICAL_LINE.getPlaneLocation(TOP_LEFT), OFFSETED_VERTICAL_LINE.getPlaneLocation(MIDDLE));
		assertEquals(OFFSETED_VERTICAL_LINE.getPlaneLocation(BOTTOM_LEFT), OFFSETED_VERTICAL_LINE.getPlaneLocation(MIDDLE));

	}

	@Test
	public void testIntersection() {
		assertFalse(hp.containsArea(DIAMOND));
		assertFalse(hp.containsArea(rectangle));
		assertFalse(DIAMOND.containsArea(hp));
		assertFalse(rectangle.containsArea(hp));
		assertEquals(new FGEPoint(0, 0.5), FGEIntersectionArea.makeIntersection(hp, DIAMOND));
		assertEquals(line1, FGEIntersectionArea.makeIntersection(hp, rectangle));
		assertEquals(line1, FGEIntersectionArea.makeIntersection(line1, rectangle));
		assertEquals(line1, FGEIntersectionArea.makeIntersection(rectangle, line1));
		assertEquals(TOP_LEFT, FGEIntersectionArea.makeIntersection(line1, TOP_LEFT));
		assertEquals(TOP_LEFT, FGEIntersectionArea.makeIntersection(TOP_LEFT, line1));
		assertEquals(TOP_LEFT, FGEIntersectionArea.makeIntersection(TOP_LEFT, line2));
		assertEquals(ROUND_RECTANGLE.getArcExcludedWest(), ROUND_RECTANGLE.intersect(hp));
	}
}
