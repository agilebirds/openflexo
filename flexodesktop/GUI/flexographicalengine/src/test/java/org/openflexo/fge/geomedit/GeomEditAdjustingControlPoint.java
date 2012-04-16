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
package org.openflexo.fge.geomedit;

import java.awt.event.MouseEvent;

import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.GeometryAdjustingControlPoint;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEArea;

public abstract class GeomEditAdjustingControlPoint<O extends FGEArea> extends GeometryAdjustingControlPoint<O> {

	private String name;

	public GeomEditAdjustingControlPoint(GeometricGraphicalRepresentation<?> gr, String aName, FGEPoint pt) {
		super(gr, aName, pt);
		name = aName;
	}

	@Override
	public GeometricGraphicalRepresentation<?> getGraphicalRepresentation() {
		return super.getGraphicalRepresentation();
	}

	@Override
	public boolean isDraggable() {
		return true;
	}

	@Override
	public void startDragging(DrawingController controller, FGEPoint startPoint) {
	}

	@Override
	public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
			FGEPoint initialPoint, MouseEvent event) {
		return true;
	}

	@Override
	public void stopDragging(DrawingController controller, GraphicalRepresentation focusedGR) {
		// TODO Auto-generated method stub
	}

	@Override
	public abstract void update(O geometricObject);

	@Override
	public String getName() {
		return name;
	}
}
