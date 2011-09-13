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

import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGEPolylin;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEHalfPlane;
import org.openflexo.fge.geom.area.FGEUnionArea;

import junit.framework.TestCase;

public class TestUnion extends TestCase {

	static FGELine line1 = new FGELine(new FGEPoint(0,0),new FGEPoint(1,0));
	static FGELine line2 = new FGELine(new FGEPoint(0,0),new FGEPoint(1,1));
	static FGERectangle rectangle = new FGERectangle(new FGEPoint(0,0),new FGEPoint(1,1),Filling.FILLED);
	static FGEHalfPlane hp = new FGEHalfPlane(line1,new FGEPoint(1,1));
	static FGEPoint p1 = new FGEPoint(0,0);
	static FGEPoint p2 = new FGEPoint(4,0);
	static FGEPoint p3 = new FGEPoint(1,0);
	static FGEPoint p4 = new FGEPoint(3,0);
	static FGEPoint p5 = new FGEPoint(-2,0);
	static FGEPoint p6 = new FGEPoint(6,0);
	static FGEPoint p7 = new FGEPoint(2,1);
	static FGEPoint p8 = new FGEPoint(4,9);
	static FGEPoint p9 = new FGEPoint(6,6);
	static FGEPoint p10 = new FGEPoint(6,89);
	static FGEPoint p11 = new FGEPoint(632,23);
	static FGEPoint p12 = new FGEPoint(2,44);
	static FGESegment s1 = new FGESegment(p1,p2);
	static FGESegment s2 = new FGESegment(p3,p4);
	static FGESegment s3 = new FGESegment(p2,p3);
	static FGESegment s4 = new FGESegment(p3,p5);
	static FGESegment s5 = new FGESegment(p4,p6);
	static FGESegment s6 = new FGESegment(p7,p8);
	static FGESegment s7 = new FGESegment(p9,p10);
	static FGESegment s8 = new FGESegment(p9,p8);
	static FGESegment s9 = new FGESegment(p11,p10);
	static FGESegment s10 = new FGESegment(p11,p12);
	static FGESegment s11 = new FGESegment(p12,p1);
	
	public void testUnion1()
	{
		System.out.println("Union1: "+FGEUnionArea.makeUnion(line1,line2));
		assertEquals(new FGEUnionArea(line1,line2),FGEUnionArea.makeUnion(line1,line2));
	}
	
	public void testUnion2()
	{
		System.out.println("Union2: "+FGEUnionArea.makeUnion(rectangle,line1,p1));
		
		assertEquals(new FGEUnionArea(rectangle,line1),FGEUnionArea.makeUnion(rectangle,line1,p1));
	}
	
	public void testUnion3()
	{
		System.out.println("Union3: "+FGEUnionArea.makeUnion(p1,line1,rectangle,hp));
		
		assertEquals(hp,FGEUnionArea.makeUnion(p1,line1,rectangle,hp));
	}
	
	public void testUnion4()
	{
		System.out.println("Union4: "+FGEUnionArea.makeUnion(p1,line1,rectangle,hp,line2));
		
		assertEquals(new FGEUnionArea(hp,line2),FGEUnionArea.makeUnion(p1,line1,rectangle,hp,line2));
	}
	
	public void testSegments1()
	{
		assertEquals(s1,FGEUnionArea.makeUnion(s1,s2));
		assertEquals(s1,FGEUnionArea.makeUnion(s1,s3));
		
		
		assertEquals(new FGESegment(p5,p2),FGEUnionArea.makeUnion(s4,s3));
		assertEquals(new FGESegment(p2,p5),FGEUnionArea.makeUnion(new FGESegment(p5,p3),s3));

		assertEquals(new FGESegment(p5,p6),FGEUnionArea.makeUnion(s1,s2,s3,s4,s5));
		
	}
	
	public void testSegments2()
	{
		assertEquals(new FGEPolylin(p7,p8,p9),FGEUnionArea.makeUnion(s6,s8));
		assertEquals(new FGEPolylin(p9,p8,p7),FGEUnionArea.makeUnion(s6,s8));
		assertEquals(new FGEPolylin(p7,p8,p9,p10),FGEUnionArea.makeUnion(s6,s8,s7));
		assertEquals(new FGEPolylin(p10,p9,p8,p7),FGEUnionArea.makeUnion(s7,s6,s8));
		
		assertEquals(new FGEPolygon(Filling.NOT_FILLED,p7,p8,p9),FGEUnionArea.makeUnion(new FGESegment(p7,p9),FGEUnionArea.makeUnion(s6,s8)));
		assertEquals(new FGEPolygon(Filling.NOT_FILLED,p8,p7,p9),FGEUnionArea.makeUnion(new FGESegment(p7,p9),FGEUnionArea.makeUnion(s6,s8)));
		
		assertEquals(new FGEPolylin(p7,p8,p9,p10,p11,p12,p1),FGEUnionArea.makeUnion(s7,s6,s8,s9,s11,s10));
		assertEquals(new FGEUnionArea(new FGEPolylin(p7,p8,p9,p10),new FGEPolylin(p12,p1,p2)),FGEUnionArea.makeUnion(s6,s8,s11,s1,s7));
	}
		
	public void testSegments3()
	{
		FGESegment a = new FGESegment(p5,p6);
		 FGESegment b = new FGESegment(p6,p7);
		 FGESegment c = new FGESegment(p7,p8);
		 FGESegment d = new FGESegment(p8,p5);
		 
		 FGEArea poly1 = FGEUnionArea.makeUnion(a,b);
		 FGEArea poly2 = FGEUnionArea.makeUnion(b,c);
		 FGEArea poly3 = FGEUnionArea.makeUnion(c,d);
		 FGEArea poly4 = FGEUnionArea.makeUnion(d,a);
		 
		 System.out.println("poly1="+poly1);
		 System.out.println("poly2="+poly2);
		 System.out.println("poly3="+poly3);
		 System.out.println("poly4="+poly4);
		 
		 System.out.println("Union="+FGEUnionArea.makeUnion(poly1,poly2,poly3,poly4));
		 
		 /*FGEArea pp1 = FGEUnionArea.makeUnion(a,b,c);
		 FGEArea pp2 = FGEUnionArea.makeUnion(c,d,a);
		 
		 System.out.println("pp1="+pp1);
		 System.out.println("pp2="+pp2);
		 
		 System.out.println("2-Union="+FGEUnionArea.makeUnion(pp1,pp2));
		 System.out.println("2-Union2="+pp1.union(pp2));*/
		 

	}

}
