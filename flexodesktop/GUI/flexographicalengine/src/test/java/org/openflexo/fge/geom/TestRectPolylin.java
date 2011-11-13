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

import org.openflexo.fge.geomedit.GeometricObject;
import org.openflexo.fge.geomedit.GeometricSet;
import org.openflexo.toolbox.FileResource;

import junit.framework.TestCase;

public class TestRectPolylin extends TestCase {

	private static final Logger logger = Logger.getLogger(TestRectPolylin.class.getPackage().getName());

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testNorthNorth() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_NORTH_NORTH.drw"));
	}

	public void testEastEast() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_EAST_EAST.drw"));
	}

	public void testSouthSouth() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_SOUTH_SOUTH.drw"));
	}

	public void testWestWest() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_WEST_WEST.drw"));
	}

	public void testEastWest() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_EAST_WEST.drw"));
	}

	public void testNorthSouth() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_NORTH_SOUTH.drw"));
	}

	public void testNorthEast() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_NORTH_EAST.drw"));
	}

	public void testEastNorth() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_EAST_NORTH.drw"));
	}

	public void testSouthEast() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_SOUTH_EAST.drw"));
	}

	public void testEastSouth() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_EAST_SOUTH.drw"));
	}

	public void testNorthWest() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_NORTH_WEST.drw"));
	}

	public void testWestNorth() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_WEST_NORTH.drw"));
	}

	public void testSouthWest() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_SOUTH_WEST.drw"));
	}

	public void testWestSouth() {
		executeTest(new FileResource("GeomJUnitTest/RectPolylin/Polylin_WEST_SOUTH.drw"));
	}

	private void executeTest(FileResource testFile) {
		logger.info(">>>>>>> Test " + testFile.getName());
		GeometricSet geometricSet = GeometricSet.load(testFile);
		for (GeometricObject object : geometricSet.getChilds()) {
			logger.fine("Check equals: " + object.getResultingGeometricObject() + " / " + object.getGeometricObject());
			assertEquals(object.getResultingGeometricObject(), object.getGeometricObject());
		}
	}
}
