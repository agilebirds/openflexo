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
import org.openflexo.fge.geom.FGEComplexCurve;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.ComplexCurve;
import org.openflexo.fge.geomedit.ComputedControlPoint;
import org.openflexo.fge.geomedit.DraggableControlPoint;
import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.ComplexCurveConstruction;
import org.openflexo.fge.geomedit.construction.ComplexCurveWithNPointsConstruction;
import org.openflexo.fge.geomedit.construction.ExplicitPointConstruction;
import org.openflexo.fge.geomedit.construction.PointConstruction;
import org.openflexo.xmlcode.XMLSerializable;


public class ComplexCurveGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGEComplexCurve,ComplexCurve> implements XMLSerializable 
{
	// Called for LOAD
	public ComplexCurveGraphicalRepresentation(GeomEditBuilder builder)
	{
		this(null,builder.drawing);
		initializeDeserialization();
	}

	public ComplexCurveGraphicalRepresentation(ComplexCurve curve, GeometricDrawing aDrawing)
	{
		super(curve, aDrawing);
	}

	@Override
	protected List<ControlPoint> buildControlPointsForGeneralShape(FGEGeneralShape shape)
	{
		Vector<ControlPoint> returned = new Vector<ControlPoint>();

		ComplexCurveConstruction curveContruction = getDrawable().getConstruction();

		if (curveContruction instanceof ComplexCurveWithNPointsConstruction) {

			for (int i = 0; i<((ComplexCurveWithNPointsConstruction)curveContruction).pointConstructions.size(); i++) {

				final int pointIndex = i;
				PointConstruction pc = ((ComplexCurveWithNPointsConstruction)curveContruction).pointConstructions.get(i);

				if (pc instanceof ExplicitPointConstruction) {
					returned.add(new DraggableControlPoint<FGEComplexCurve>(this,"pt"+i,pc.getPoint(),(ExplicitPointConstruction)pc) {
						@Override
						public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event)
						{
							if (pointIndex == 0) {
								getGeometricObject().getElementAt(0).getP1().x = newAbsolutePoint.x;
								getGeometricObject().getElementAt(0).getP1().y = newAbsolutePoint.y;
								getGeometricObject().refresh();
							}
							else {
								getGeometricObject().getElementAt(pointIndex-1).getP2().x = newAbsolutePoint.x;
								getGeometricObject().getElementAt(pointIndex-1).getP2().y = newAbsolutePoint.y;
								getGeometricObject().refresh();
							}
							setPoint(newAbsolutePoint);
							notifyGeometryChanged();
							return true;
						}
						@Override
						public void update(FGEComplexCurve geometricObject)
						{
							if (pointIndex == 0) {
								setPoint(geometricObject.getElementAt(0).getP1());
							}
							else {
								setPoint(geometricObject.getElementAt(pointIndex-1).getP2());
							}
						}
					});
				}
				else {
					returned.add(new ComputedControlPoint<FGEComplexCurve>(this,"pt"+i,pc.getPoint()) {
						@Override
						public void update(FGEComplexCurve geometricObject)
						{
							if (pointIndex == 0) {
								setPoint(geometricObject.getElementAt(0).getP1());
							}
							else {
								setPoint(geometricObject.getElementAt(pointIndex-1).getP2());
							}
						}
					});
				}

			}
		}

		return returned;

	}


}