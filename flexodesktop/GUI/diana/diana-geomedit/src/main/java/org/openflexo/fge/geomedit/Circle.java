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

import org.openflexo.fge.geom.FGECircle;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.CircleConstruction;
import org.openflexo.fge.geomedit.gr.EllipsGraphicalRepresentation;
import org.openflexo.fge.notifications.FGENotification;

public class Circle extends Ellips {

	private EllipsGraphicalRepresentation graphicalRepresentation;

	// Called for LOAD
	public Circle(GeomEditBuilder builder) {
		super(builder);
	}

	public Circle(GeometricSet set, CircleConstruction construction) {
		super(set, construction);
		graphicalRepresentation = new EllipsGraphicalRepresentation(this, set.getEditedDrawing());
	}

	@Override
	public EllipsGraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	@Override
	public void setGraphicalRepresentation(EllipsGraphicalRepresentation aGR) {
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}

	@Override
	public FGECircle getGeometricObject() {
		return (FGECircle) super.getGeometricObject();
	}

	@Override
	public CircleConstruction getConstruction() {
		return (CircleConstruction) super.getConstruction();
	}

	public void setConstruction(CircleConstruction circleConstruction) {
		_setConstruction(circleConstruction);
	}

	@Override
	public String getInspectorName() {
		return "Circle.inspector";
	}

	public double getCenterX() {
		return getGeometricObject().getCenterX();
	}

	public void setCenterX(double centerX) {
		if (centerX != getCenterX()) {
			double oldCenterX = getCenterX();
			getGeometricObject().x = centerX - getGeometricObject().getRadius();
			getGraphicalRepresentation().notify(new FGENotification("centerX", oldCenterX, centerX));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getCenterY() {
		return getGeometricObject().getCenterY();
	}

	public void setCenterY(double centerY) {
		if (centerY != getCenterY()) {
			double oldCenterY = getCenterY();
			getGeometricObject().y = centerY - getGeometricObject().getRadius();
			getGraphicalRepresentation().notify(new FGENotification("centerY", oldCenterY, centerY));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getRadius() {
		return getGeometricObject().getRadius();
	}

	public void setRadius(double aRadius) {
		if (aRadius != getRadius()) {
			double oldRadius = getRadius();
			getGeometricObject().setRadius(aRadius);
			getGraphicalRepresentation().notify(new FGENotification("radius", oldRadius, aRadius));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}
}
