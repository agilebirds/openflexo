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
package org.openflexo.fge.geomedit.gr;

import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geomedit.ComputedControlPoint;
import org.openflexo.fge.geomedit.DraggableControlPoint;
import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.Segment;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.ExplicitPointConstruction;
import org.openflexo.fge.geomedit.construction.SegmentConstruction;
import org.openflexo.fge.geomedit.construction.SegmentWithTwoPointsConstruction;
import org.openflexo.xmlcode.XMLSerializable;


public class SegmentGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGESegment,Segment> implements XMLSerializable 
{
	// Called for LOAD
	public SegmentGraphicalRepresentation(GeomEditBuilder builder)
	{
		this(null,builder.drawing);
		initializeDeserialization();
	}
	
	public SegmentGraphicalRepresentation(Segment segment, GeometricDrawing aDrawing)
	{
		super(segment, aDrawing);
	}
	
	
	@Override
	protected List<ControlPoint> buildControlPointsForLine(FGEAbstractLine line)
	{
		Vector<ControlPoint> returned = new Vector<ControlPoint>();
		
		SegmentConstruction segmentConstruction = getDrawable().getConstruction();
			
		ExplicitPointConstruction pc1 = null;
		ExplicitPointConstruction pc2 = null;
			
		if (segmentConstruction instanceof SegmentWithTwoPointsConstruction) {
			if (((SegmentWithTwoPointsConstruction)segmentConstruction).pointConstruction1 instanceof ExplicitPointConstruction) {
				pc1 = (ExplicitPointConstruction)((SegmentWithTwoPointsConstruction)segmentConstruction).pointConstruction1;
			}
			if (((SegmentWithTwoPointsConstruction)segmentConstruction).pointConstruction2 instanceof ExplicitPointConstruction) {
				pc2 = (ExplicitPointConstruction)((SegmentWithTwoPointsConstruction)segmentConstruction).pointConstruction2;
			}
		}
		
		if (pc1 != null) {
			returned.add(new DraggableControlPoint<FGESegment>(this,"p1",line.getP1(),pc1) {
				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event)
				{
					getGeometricObject().setP1(newAbsolutePoint);
					setPoint(newAbsolutePoint);
					notifyGeometryChanged();
					return true;
				}
				@Override
				public void update(FGESegment geometricObject)
				{
					setPoint(geometricObject.getP1());
				}
			});
		}
		else {
			returned.add(new ComputedControlPoint<FGESegment>(this,"p1",line.getP1()) {
				@Override
				public void update(FGESegment geometricObject)
				{
					setPoint(geometricObject.getP1());
				}
			});
		}

		if (pc2 != null) {
			returned.add(new DraggableControlPoint<FGESegment>(this,"p2",line.getP2(),pc2) {
				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event)
				{
					getGeometricObject().setP2(newAbsolutePoint);
					setPoint(newAbsolutePoint);
					notifyGeometryChanged();
					return true;
				}
				@Override
				public void update(FGESegment geometricObject)
				{
					setPoint(geometricObject.getP2());
				}
			});
		}
		else {
			returned.add(new ComputedControlPoint<FGESegment>(this,"p2",line.getP1()) {
				@Override
				public void update(FGESegment geometricObject)
				{
					setPoint(geometricObject.getP2());
				}
			});
		}

		return returned;
	}


}