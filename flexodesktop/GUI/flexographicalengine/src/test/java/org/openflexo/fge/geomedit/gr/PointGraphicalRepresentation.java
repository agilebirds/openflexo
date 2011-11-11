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
import org.openflexo.fge.geomedit.ComputedControlPoint;
import org.openflexo.fge.geomedit.DraggableControlPoint;
import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.Point;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.ExplicitPointConstruction;
import org.openflexo.fge.geomedit.construction.PointConstruction;
import org.openflexo.xmlcode.XMLSerializable;

public class PointGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGEPoint, Point> implements XMLSerializable {
	// Called for LOAD
	public PointGraphicalRepresentation(GeomEditBuilder builder) {
		this(null, builder.drawing);
		initializeDeserialization();
	}

	public PointGraphicalRepresentation(Point point, GeometricDrawing aDrawing) {
		super(point, aDrawing);
	}

	@Override
	protected List<ControlPoint> buildControlPointsForPoint(FGEPoint point) {
		Vector<ControlPoint> returned = new Vector<ControlPoint>();

		PointConstruction pc = getDrawable().getConstruction();

		if (pc instanceof ExplicitPointConstruction) {
			returned.add(new DraggableControlPoint<FGEPoint>(this, "point", point, (ExplicitPointConstruction) pc) {
				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					getGeometricObject().x = newAbsolutePoint.x;
					getGeometricObject().y = newAbsolutePoint.y;
					setPoint(newAbsolutePoint);
					notifyGeometryChanged();
					return true;
				}

				@Override
				public void update(FGEPoint geometricObject) {
					setPoint(geometricObject);
				}
			});
		} else {
			returned.add(new ComputedControlPoint<FGEPoint>(this, "point", point) {
				@Override
				public void update(FGEPoint geometricObject) {
					setPoint(geometricObject);
				}
			});
		}

		return returned;
	}

}