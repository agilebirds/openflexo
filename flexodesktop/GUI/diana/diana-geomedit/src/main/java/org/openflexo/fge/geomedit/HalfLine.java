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

import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.HalfLineConstruction;
import org.openflexo.fge.geomedit.gr.HalfLineGraphicalRepresentation;
import org.openflexo.fge.notifications.FGENotification;

public class HalfLine extends GeometricObject<FGEHalfLine> {

	private HalfLineGraphicalRepresentation graphicalRepresentation;

	// Called for LOAD
	public HalfLine(GeomEditBuilder builder) {
		super(builder);
	}

	public HalfLine(GeometricSet set, HalfLineConstruction construction) {
		super(set, construction);
		graphicalRepresentation = new HalfLineGraphicalRepresentation(this, set.getEditedDrawing());
	}

	@Override
	public HalfLineGraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(HalfLineGraphicalRepresentation aGR) {
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}

	@Override
	public HalfLineConstruction getConstruction() {
		return (HalfLineConstruction) super.getConstruction();
	}

	public void setConstruction(HalfLineConstruction lineConstruction) {
		_setConstruction(lineConstruction);
	}

	@Override
	public String getInspectorName() {
		return "HalfLine.inspector";
	}

	public double getLimitX() {
		return getGeometricObject().getX1();
	}

	public void setLimitX(double limitX) {
		if (limitX != getLimitX()) {
			double oldLimitX = getLimitX();
			getGeometricObject().setX1(limitX);
			getGraphicalRepresentation().notify(new FGENotification("x1", oldLimitX, limitX));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getLimitY() {
		return getGeometricObject().getY1();
	}

	public void setLimitY(double limitY) {
		if (limitY != getLimitY()) {
			double oldLimitY = getLimitY();
			getGeometricObject().setY1(limitY);
			getGraphicalRepresentation().notify(new FGENotification("y1", oldLimitY, limitY));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getOppositeX() {
		return getGeometricObject().getX2();
	}

	public void setOppositeX(double oppositeX) {
		if (oppositeX != getOppositeX()) {
			double oldOppositeX = getOppositeX();
			getGeometricObject().setX2(oppositeX);
			getGraphicalRepresentation().notify(new FGENotification("x2", oldOppositeX, oppositeX));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getOppositeY() {
		return getGeometricObject().getY2();
	}

	public void setOppositeY(double oppositeY) {
		if (oppositeY != getOppositeY()) {
			double oldOppositeY = getOppositeY();
			getGeometricObject().setY2(oppositeY);
			getGraphicalRepresentation().notify(new FGENotification("y2", oldOppositeY, oppositeY));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

}
