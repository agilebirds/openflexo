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

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.GeometryAdjustingControlPoint;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEPlane;
import org.openflexo.fge.geomedit.construction.ExplicitPointConstruction;
import org.openflexo.fge.geomedit.gr.GeometricObjectGraphicalRepresentation;
import org.openflexo.fge.graphics.FGEGraphics;


public abstract class DraggableControlPoint<O extends FGEArea> extends GeometryAdjustingControlPoint<O> {

	private ExplicitPointConstruction explicitPointConstruction;
	
	public DraggableControlPoint(GeometricObjectGraphicalRepresentation<O,?> gr, String aName, FGEPoint pt, ExplicitPointConstruction pc)
	{
		super(gr,aName,pt);
		explicitPointConstruction = pc;
		setDraggingAuthorizedArea(new FGEPlane());
	}

	@Override
	public GeometricObjectGraphicalRepresentation<O,?> getGraphicalRepresentation()
	{
		return (GeometricObjectGraphicalRepresentation<O,?>)super.getGraphicalRepresentation();
	}

	@Override
	public boolean isDraggable()
	{
		return true;
	}

	@Override
	public void startDragging(DrawingController controller, FGEPoint startPoint)
	{
	}

	@Override
	public abstract boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event);

	@Override
	public void stopDragging(DrawingController controller)
	{
	}
	
	@Override
	public abstract void update(O geometricObject);

	@Override
	public void setPoint(FGEPoint point)
	{
		super.setPoint(point);
		explicitPointConstruction.setPoint(point);
	}

	public ExplicitPointConstruction getExplicitPointConstruction()
	{
		return explicitPointConstruction;
	}
	
	public void paint(FGEGraphics graphics,boolean focused)
	{
		graphics.drawControlPoint(getPoint(),FGEConstants.CONTROL_POINT_SIZE);
		if (focused) {
			graphics.drawRoundArroundPoint(getPoint(), 8);
		}
	}

}
