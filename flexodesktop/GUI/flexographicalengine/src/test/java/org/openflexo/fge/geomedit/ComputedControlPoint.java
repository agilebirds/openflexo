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

import java.awt.Cursor;

import org.openflexo.fge.cp.GeometryAdjustingControlPoint;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geomedit.gr.GeometricObjectGraphicalRepresentation;


public abstract class ComputedControlPoint<O extends FGEArea> extends GeometryAdjustingControlPoint<O> {

	public ComputedControlPoint(GeometricObjectGraphicalRepresentation<O,?> gr, String aName, FGEPoint pt)
	{
		super(gr,aName,pt);
		setDraggingAuthorizedArea(new FGEEmptyArea());
	}

	@Override
	public GeometricObjectGraphicalRepresentation<O,?> getGraphicalRepresentation()
	{
		return (GeometricObjectGraphicalRepresentation<O,?>)super.getGraphicalRepresentation();
	}

	@Override
	public boolean isDraggable()
	{
		return false;
	}
	
	@Override
	public Cursor getDraggingCursor()
	{
		return Cursor.getDefaultCursor();
	}

	@Override
	public abstract void update(O geometricObject);

	
	@Override
	public FGEPoint getPoint()
	{
		update(((GeometricObject<O>)getGraphicalRepresentation().getDrawable()).getGeometricObject());
		return super.getPoint();
	}
}
