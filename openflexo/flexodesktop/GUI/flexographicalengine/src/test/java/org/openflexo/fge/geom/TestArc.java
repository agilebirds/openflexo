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

import org.openflexo.fge.geom.FGEArc;
import org.openflexo.fge.geom.FGEEllips;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEArc.ArcType;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEUnionArea;

import junit.framework.TestCase;

public class TestArc extends TestCase {

	public void testEquals()
	{
		FGEArc arc1 = buildArc(0,90);
		FGEArc arc2 = buildArc(0,450);
		FGEArc arc3 = buildArc(360,90);
		FGEArc arc4 = buildArc(360,450);
		assertEquals(arc2, arc1);
		assertEquals(arc3, arc1);
		assertEquals(arc4, arc1);
	}
	
	public void testIntersect1()
	{
		FGEArc arc1 = buildArc(0,90);
		FGEArc arc2 = buildArc(0,45);
		assertEquals(arc2, arc1.intersect(arc2));
		assertEquals(arc2, arc2.intersect(arc1));
	}
	
	public void testIntersect2()
	{
		FGEArc arc1 = buildArc(-45,45);
		FGEArc arc2 = buildArc(0,90);
		assertEquals(buildArc(0,45), arc1.intersect(arc2));
	}
	
	public void testIntersect3()
	{
		FGEArc arc1 = buildArc(20,45);
		FGEArc arc2 = buildArc(0,90);
		assertEquals(arc1, arc1.intersect(arc2));
	}
	
	public void testIntersect4()
	{
		FGEArc arc1 = buildArc(20,90);
		FGEArc arc2 = buildArc(0,45);
		assertEquals(buildArc(20,45), arc1.intersect(arc2));
	}
	
	public void testIntersect5()
	{
		FGEArc arc1 = buildArc(20,180);
		FGEArc arc2 = buildArc(0,225);
		FGEArc arc3 = buildArc(-340,180);
		FGEArc arc4 = buildArc(360,225);
		assertEquals(buildArc(20,180), arc1.intersect(arc2));
		assertEquals(buildArc(20,180), arc4.intersect(arc3));
	}
	
	public void testIntersect6()
	{
		FGEArc arc1 = buildArc(0,180);
		FGEArc arc2 = buildArc(20,110);
		assertEquals(arc2, arc1.intersect(arc2));
		assertEquals(arc2, arc2.intersect(arc1));
	}
	
	public void testIntersect7()
	{
		FGEArc arc1 = buildArc(0,180);
		FGEArc arc2 = buildArc(170,200);
		assertEquals(buildArc(170,-180), arc1.intersect(arc2));
		assertEquals(buildArc(170,180), arc2.intersect(arc1));
	}
	
	public void testIntersect8()
	{
		FGEArc arc1 = buildArc(0,180);
		FGEArc arc2 = buildArc(170,370);
		assertEquals(new FGEUnionArea(buildArc(170,180),buildArc(0,10)), arc1.intersect(arc2));
		assertEquals(new FGEUnionArea(buildArc(170,180),buildArc(0,10)), arc2.intersect(arc1));
	}
	
	public void testIntersect9()
	{
		FGEArc arc1 = buildArc(20,30);
		FGEArc arc2 = buildArc(30,210);
		assertEquals(arc1.getPointAtAngle(30), arc1.intersect(arc2));
	}
	
	public void testIntersect10()
	{
		FGEArc arc1 = buildArc(0,180);
		FGEArc arc2 = buildArc(180,0);
		assertEquals(new FGEUnionArea(new FGEPoint(0,0.5),new FGEPoint(1,0.5)), arc1.intersect(arc2));
	}
	
	public void testIntersect11()
	{
		FGEArc arc1 = buildArc(30,80);
		FGEArc arc2 = buildArc(90,130);
		assertEquals(new FGEEmptyArea(), arc1.intersect(arc2));
	}
	
	public void testIntersect12()
	{
		FGEArc arc1 = buildArc(30,90);
		FGEArc arc2 = buildArc(-200,80);
		assertEquals(new FGEEmptyArea(), arc1.intersect(arc2));
	}
	
	public void testIntersect13()
	{
		FGEArc arc1 = buildEllips();
		FGEArc arc2 = buildArc(0,45);
		assertEquals(arc2, arc1.intersect(arc2));
		assertEquals(arc2, arc2.intersect(arc1));
	}
	
	// This test handle special cases of arcs starting or ending at 180°/-180°
	// Not a piece of cake
	public void testIntersect14()
	{
		FGEArc arc1 = buildEllips();
		FGEArc arc2 = buildArc(90,270);
		FGEArc arc3 = buildArc(90,180);
		assertEquals(true, arc2.containsArea(arc3));
		assertEquals(arc2, arc1.intersect(arc2));
		assertEquals(arc2, arc2.intersect(arc1));
	}
	
	public void testUnion1()
	{
		FGEArc arc1 = buildArc(0,90);
		FGEArc arc2 = buildArc(10,80);
		assertEquals(buildArc(0,90), arc1.union(arc2));
	}
	
	public void testUnion2()
	{
		FGEArc arc1 = buildArc(0,90);
		FGEArc arc2 = buildArc(10,100);
		assertEquals(buildArc(0,100), arc1.union(arc2));
	}
	
	public void testUnion3()
	{
		FGEArc arc1 = buildArc(0,90);
		FGEArc arc2 = buildArc(90,170);
		assertEquals(buildArc(0,170), arc1.union(arc2));
	}
	
	public void testUnion4()
	{
		FGEArc arc1 = buildArc(0,90);
		FGEArc arc2 = buildArc(90,200);
		assertEquals(buildArc(0,200), arc1.union(arc2));
	}
	
	public void testUnion5()
	{
		FGEArc arc1 = buildArc(0,90);
		FGEArc arc2 = buildArc(90,180);
		assertEquals(buildArc(0,180), arc1.union(arc2));
	}
	
	public void testUnion6()
	{
		FGEArc arc1 = buildArc(-100,100);
		FGEArc arc2 = buildArc(90,270);
		assertEquals(buildEllips(), arc1.union(arc2));
	}

	public void testUnion7()
	{
		FGEArc arc1 = buildArc(0,90);
		FGEArc arc2 = buildArc(90,180);
		FGEArc arc3 = buildArc(-180,-90);
		FGEArc arc4 = buildArc(-90,0);
		FGEArea result = FGEUnionArea.makeUnion(arc1,arc2,arc3,arc4);
		assertEquals(buildEllips(), result);
	}

	// This test handle special cases of arcs starting or ending at 180°/-180°
	// Not a piece of cake
	public void testUnion8()
	{
		FGEArc arc2 = buildArc(180,0);
		FGEArc arc3 = buildArc(-180,0);
		assertEquals(arc2, arc3);
		assertTrue(arc3.containsArea(arc2));
		assertTrue(arc2.containsArea(arc3));
	}
	
	public void testLineIntersection1()
	{
		FGEArc arc = buildArc(90,270);
		FGELine line = new FGELine(new FGEPoint(0.5001,0),new FGEPoint(0.5,1));
		System.out.println("arc="+arc);
		System.out.println("line="+line);
		System.out.println("arc.intersect(line)="+arc.intersect(line));
	}


	private FGEArc buildArc(double angleStart, double angleEnd)
	{
		return new FGEArc(0,0,1,1,angleStart,Math.toDegrees(FGEArc.angleExtent(Math.toRadians(angleStart), Math.toRadians(angleEnd))),ArcType.OPEN);
	}

	private FGEArc buildEllips()
	{
		return new FGEEllips(0,0,1,1,Filling.NOT_FILLED);
	}

}
