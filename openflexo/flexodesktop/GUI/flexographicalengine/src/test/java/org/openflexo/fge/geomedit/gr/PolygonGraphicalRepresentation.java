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
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geomedit.ComputedControlPoint;
import org.openflexo.fge.geomedit.DraggableControlPoint;
import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.Polygon;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.ExplicitPointConstruction;
import org.openflexo.fge.geomedit.construction.PointConstruction;
import org.openflexo.fge.geomedit.construction.PolygonConstruction;
import org.openflexo.fge.geomedit.construction.PolygonWithNPointsConstruction;
import org.openflexo.xmlcode.XMLSerializable;


public class PolygonGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGEPolygon,Polygon> implements XMLSerializable 
{
	// Called for LOAD
	public PolygonGraphicalRepresentation(GeomEditBuilder builder)
	{
		this(null,builder.drawing);
		initializeDeserialization();
	}

	public PolygonGraphicalRepresentation(Polygon polygon, GeometricDrawing aDrawing)
	{
		super(polygon, aDrawing);
	}

	@Override
	protected List<ControlPoint> buildControlPointsForPolygon(FGEPolygon polygon)
	{
		Vector<ControlPoint> returned = new Vector<ControlPoint>();

		PolygonConstruction polygonContruction = getDrawable().getConstruction();

		if (polygonContruction instanceof PolygonWithNPointsConstruction) {

			for (int i = 0; i<((PolygonWithNPointsConstruction)polygonContruction).pointConstructions.size(); i++) {
			
				final int pointIndex = i;
				PointConstruction pc = ((PolygonWithNPointsConstruction)polygonContruction).pointConstructions.get(i);
				
				if (pc instanceof ExplicitPointConstruction) {
					returned.add(new DraggableControlPoint<FGEPolygon>(this,"pt"+i,pc.getPoint(),(ExplicitPointConstruction)pc) {
						@Override
						public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event)
						{
							getGeometricObject().getPointAt(pointIndex).x = newAbsolutePoint.x;
							getGeometricObject().getPointAt(pointIndex).y = newAbsolutePoint.y;
							setPoint(newAbsolutePoint);
							notifyGeometryChanged();
							return true;
						}
						@Override
						public void update(FGEPolygon geometricObject)
						{
							setPoint(geometricObject.getPointAt(pointIndex));
						}
					});
				}
				else {
					returned.add(new ComputedControlPoint<FGEPolygon>(this,"pt"+i,pc.getPoint()) {
						@Override
						public void update(FGEPolygon geometricObject)
						{
							setPoint(geometricObject.getPointAt(pointIndex));
						}
					});
				}

			}
		}

		return returned;

	}

}