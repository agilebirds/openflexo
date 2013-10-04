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

import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.LineConstruction;
import org.openflexo.fge.geomedit.gr.LineGraphicalRepresentation;
import org.openflexo.fge.notifications.FGENotification;

public class Line extends GeometricObject<FGELine> {

	private LineGraphicalRepresentation graphicalRepresentation;

	// Called for LOAD
	public Line(GeomEditBuilder builder) {
		super(builder);
	}

	public Line(GeometricSet set, LineConstruction construction) {
		super(set, construction);
		graphicalRepresentation = new LineGraphicalRepresentation(this, set.getEditedDrawing());
	}

	@Override
	public LineGraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(LineGraphicalRepresentation aGR) {
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}

	@Override
	public LineConstruction getConstruction() {
		return (LineConstruction) super.getConstruction();
	}

	public void setConstruction(LineConstruction lineConstruction) {
		_setConstruction(lineConstruction);
	}

	@Override
	public String getInspectorName() {
		return "Line.inspector";
	}

	public double getX1() {
		return getGeometricObject().getX1();
	}

	public void setX1(double x1) {
		if (x1 != getX1()) {
			double oldX1 = getX1();
			getGeometricObject().setX1(x1);
			getGraphicalRepresentation().notify(new FGENotification("x1", oldX1, x1));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getX2() {
		return getGeometricObject().getX2();
	}

	public void setX2(double x2) {
		if (x2 != getX2()) {
			double oldX2 = getX2();
			getGeometricObject().setX2(x2);
			getGraphicalRepresentation().notify(new FGENotification("x2", oldX2, x2));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getY1() {
		return getGeometricObject().getY1();
	}

	public void setY1(double y1) {
		if (y1 != getY1()) {
			double oldY1 = getY1();
			getGeometricObject().setY1(y1);
			getGraphicalRepresentation().notify(new FGENotification("y1", oldY1, y1));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getY2() {
		return getGeometricObject().getY2();
	}

	public void setY2(double y2) {
		if (y2 != getY2()) {
			double oldY2 = getY2();
			getGeometricObject().setY2(y2);
			getGraphicalRepresentation().notify(new FGENotification("y2", oldY2, y2));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

}
