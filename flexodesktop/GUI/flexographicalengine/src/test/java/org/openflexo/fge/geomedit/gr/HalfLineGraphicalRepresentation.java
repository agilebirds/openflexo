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
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.geomedit.ComputedControlPoint;
import org.openflexo.fge.geomedit.DraggableControlPoint;
import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.HalfLine;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.ExplicitPointConstruction;
import org.openflexo.fge.geomedit.construction.HalfLineConstruction;
import org.openflexo.fge.geomedit.construction.HalfLineWithTwoPointsConstruction;
import org.openflexo.xmlcode.XMLSerializable;


public class HalfLineGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGEHalfLine,HalfLine> implements XMLSerializable 
{
	// Called for LOAD
	public HalfLineGraphicalRepresentation(GeomEditBuilder builder)
	{
		this(null,builder.drawing);
		initializeDeserialization();
	}
	
	public HalfLineGraphicalRepresentation(HalfLine halfLine, GeometricDrawing aDrawing)
	{
		super(halfLine, aDrawing);
	}
	
	
	@Override
	protected List<ControlPoint> buildControlPointsForLine(FGEAbstractLine line)
	{
		Vector<ControlPoint> returned = new Vector<ControlPoint>();
		
		HalfLineConstruction lineConstruction = getDrawable().getConstruction();
			
		ExplicitPointConstruction pc1 = null;
		ExplicitPointConstruction pc2 = null;
			
		if (lineConstruction instanceof HalfLineWithTwoPointsConstruction) {
			if (((HalfLineWithTwoPointsConstruction)lineConstruction).limitPointConstruction instanceof ExplicitPointConstruction) {
				pc1 = (ExplicitPointConstruction)((HalfLineWithTwoPointsConstruction)lineConstruction).limitPointConstruction;
			}
			if (((HalfLineWithTwoPointsConstruction)lineConstruction).oppositePointConstruction instanceof ExplicitPointConstruction) {
				pc2 = (ExplicitPointConstruction)((HalfLineWithTwoPointsConstruction)lineConstruction).oppositePointConstruction;
			}
		}
		
		if (pc1 != null) {
			returned.add(new DraggableControlPoint<FGEHalfLine>(this,"limit",line.getP1(),pc1) {
				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event)
				{
					getGeometricObject().setLimit(newAbsolutePoint);
					setPoint(newAbsolutePoint);
					notifyGeometryChanged();
					return true;
				}
				@Override
				public void update(FGEHalfLine geometricObject)
				{
					setPoint(geometricObject.getLimit());
				}
			});
		}
		else {
			returned.add(new ComputedControlPoint<FGEHalfLine>(this,"limit",line.getP1()) {
				@Override
				public void update(FGEHalfLine geometricObject)
				{
					setPoint(geometricObject.getLimit());
				}
			});
		}

		if (pc2 != null) {
			returned.add(new DraggableControlPoint<FGEHalfLine>(this,"opposite",line.getP2(),pc2) {
				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event)
				{
					getGeometricObject().setOpposite(newAbsolutePoint);
					setPoint(newAbsolutePoint);
					notifyGeometryChanged();
					return true;
				}
				@Override
				public void update(FGEHalfLine geometricObject)
				{
					setPoint(geometricObject.getOpposite());
				}
			});
		}
		else {
			returned.add(new ComputedControlPoint<FGEHalfLine>(this,"opposite",line.getP1()) {
				@Override
				public void update(FGEHalfLine geometricObject)
				{
					setPoint(geometricObject.getOpposite());
				}
			});
		}

		return returned;
	}


}