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

import org.openflexo.fge.geom.FGEEllips;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEIntersectionArea;
import org.openflexo.fge.geom.area.FGESubstractionArea;
import org.openflexo.fge.geom.area.FGEUnionArea;

import junit.framework.TestCase;

public class TestOperations extends TestCase {

	static FGERectangle r1 = new FGERectangle(0,4,8,8,Filling.FILLED);
	static FGERectangle r2 = new FGERectangle(4,1,3,5,Filling.FILLED);
	static FGERectangle r3 = new FGERectangle(6,0,3,7,Filling.FILLED);
	static FGERectangle r4 = new FGERectangle(9,0,2,7,Filling.FILLED);
	static FGERectangle r5 = new FGERectangle(5,2,10,6,Filling.FILLED);
	static FGERectangle r6 = new FGERectangle(12,4,2,2,Filling.FILLED);
	
	static FGEArea r7;
	static FGEArea r8;
	static FGEArea r9;
	static FGEArea r10;
	
	public void test1()
	{
		r7 = FGEUnionArea.makeUnion(r3,r4);
		System.out.println("r7: "+r7);
		assertEquals(new FGERectangle(6,0,5,7,Filling.FILLED),r7);
	}
	
	public void test2()
	{
		r8 = FGEUnionArea.makeUnion(r2,FGEUnionArea.makeUnion(r3,r4));
		System.out.println("r8: "+r8);
		assertEquals(new FGEUnionArea(r2,r7),r8);
	}
	
	public void test3()
	{
		assertEquals(new FGEEmptyArea(),FGESubstractionArea.makeSubstraction(r6,r5,false));
		//assertEquals(r1,FGESubstractionArea.makeSubstraction(r1,r6,false));
		r9 = FGESubstractionArea.makeSubstraction(r5,r6,false);
		System.out.println("r9: "+r9);
		assertEquals(new FGESubstractionArea(r5,r6,false),r9);
	}
	
	public void test4()
	{
		r10 = FGEIntersectionArea.makeIntersection(r1,FGEUnionArea.makeUnion(r3,r4),FGESubstractionArea.makeSubstraction(r5,r6,false));
		System.out.println("r10: "+r10);
		assertEquals(new FGERectangle(6,4,2,3,Filling.FILLED),r10);
	}
	
	public void test5()
	{
		FGEEllips ellips1 = new FGEEllips(0,0,3,3,Filling.FILLED);
		FGEEllips ellips2 = new FGEEllips(5,1,3,3,Filling.FILLED);
		FGEArea area1 = ellips1.getOrthogonalPerspectiveArea(SimplifiedCardinalDirection.EAST);
		FGEArea area2 = ellips2.getOrthogonalPerspectiveArea(SimplifiedCardinalDirection.WEST);
		
		System.out.println("area1="+area1);
		System.out.println("area2="+area2);
		System.out.println("intersect="+area1.intersect(area2));
		
	}
	

}
