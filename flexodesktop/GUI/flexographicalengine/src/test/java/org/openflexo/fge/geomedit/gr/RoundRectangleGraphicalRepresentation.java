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

import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEGeometricObject.CardinalQuadrant;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGERoundRectangle;
import org.openflexo.fge.geom.area.FGEQuarterPlane;
import org.openflexo.fge.geomedit.ComputedControlPoint;
import org.openflexo.fge.geomedit.DraggableControlPoint;
import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.RoundRectangle;
import org.openflexo.fge.geomedit.construction.ExplicitPointConstruction;
import org.openflexo.fge.geomedit.construction.RoundRectangleConstruction;
import org.openflexo.fge.geomedit.construction.RoundRectangleWithTwoPointsConstruction;
import org.openflexo.xmlcode.XMLSerializable;

public class RoundRectangleGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGERoundRectangle, RoundRectangle>
		implements XMLSerializable {
	// Called for LOAD
	public RoundRectangleGraphicalRepresentation(GeomEditBuilder builder) {
		this(null, builder.drawing);
		initializeDeserialization();
	}

	public RoundRectangleGraphicalRepresentation(RoundRectangle rectangle, GeometricDrawing aDrawing) {
		super(rectangle, aDrawing);
	}

	private DraggableControlPoint nwCP1;
	private DraggableControlPoint seCP2;

	@Override
	protected List<ControlPoint> buildControlPointsForRectangle(FGERectangle rectangle) {
		Vector<ControlPoint> returned = new Vector<ControlPoint>();

		RoundRectangleConstruction rectangleConstruction = getDrawable().getConstruction();

		ExplicitPointConstruction pc1 = null;
		ExplicitPointConstruction pc2 = null;

		if (rectangleConstruction instanceof RoundRectangleWithTwoPointsConstruction) {
			if (((RoundRectangleWithTwoPointsConstruction) rectangleConstruction).pointConstruction1 instanceof ExplicitPointConstruction) {
				pc1 = (ExplicitPointConstruction) ((RoundRectangleWithTwoPointsConstruction) rectangleConstruction).pointConstruction1;
			}
			if (((RoundRectangleWithTwoPointsConstruction) rectangleConstruction).pointConstruction2 instanceof ExplicitPointConstruction) {
				pc2 = (ExplicitPointConstruction) ((RoundRectangleWithTwoPointsConstruction) rectangleConstruction).pointConstruction2;
			}
		}

		if (pc1 != null) {
			returned.add(nwCP1 = new DraggableControlPoint<FGERoundRectangle>(this, "northWest", rectangle.getNorthWestPt(), pc1) {
				private double initialWidth;
				private double initialHeight;

				@Override
				public void startDragging(DrawingController controller, FGEPoint startPoint) {
					super.startDragging(controller, startPoint);
					initialWidth = (getGeometricObject()).width;
					initialHeight = (getGeometricObject()).height;
					setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane((getGeometricObject()).getSouthEastPt(),
							CardinalQuadrant.NORTH_WEST));
				}

				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					FGEPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
					setPoint(pt);

					(getGeometricObject()).x = pt.x;
					(getGeometricObject()).y = pt.y;
					(getGeometricObject()).width = -pt.x + initialPoint.x + initialWidth;
					(getGeometricObject()).height = -pt.y + initialPoint.y + initialHeight;

					notifyGeometryChanged();
					return true;
				}

				@Override
				public void update(FGERoundRectangle geometricObject) {
					setPoint(geometricObject.getNorthWestPt());
				}
			});
		} else {
			returned.add(new ComputedControlPoint<FGERoundRectangle>(this, "northWest", rectangle.getNorthWestPt()) {
				@Override
				public void update(FGERoundRectangle geometricObject) {
					setPoint(geometricObject.getNorthWestPt());
				}
			});
		}

		if (pc2 != null) {
			returned.add(seCP2 = new DraggableControlPoint<FGERoundRectangle>(this, "southEast", rectangle.getSouthEastPt(), pc2) {
				private double initialWidth;
				private double initialHeight;

				@Override
				public void startDragging(DrawingController controller, FGEPoint startPoint) {
					super.startDragging(controller, startPoint);
					initialWidth = (getGeometricObject()).width;
					initialHeight = (getGeometricObject()).height;
					setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane((getGeometricObject()).getNorthWestPt(),
							CardinalQuadrant.SOUTH_EAST));
				}

				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					FGEPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
					setPoint(pt);

					(getGeometricObject()).width = pt.x - initialPoint.x + initialWidth;
					(getGeometricObject()).height = pt.y - initialPoint.y + initialHeight;

					notifyGeometryChanged();
					return true;
				}

				@Override
				public void update(FGERoundRectangle geometricObject) {
					setPoint(geometricObject.getSouthEastPt());
				}
			});
		} else {
			returned.add(new ComputedControlPoint<FGERoundRectangle>(this, "southEast", rectangle.getSouthEastPt()) {
				@Override
				public void update(FGERoundRectangle geometricObject) {
					setPoint(geometricObject.getSouthEastPt());
				}
			});
		}

		if (pc1 != null && pc2 != null) {
			returned.add(new ComputedControlPoint<FGERoundRectangle>(this, "northEast", rectangle.getNorthEastPt()) {
				private double initialWidth;
				private double initialHeight;

				@Override
				public boolean isDraggable() {
					return true;
				}

				@Override
				public void startDragging(DrawingController controller, FGEPoint startPoint) {
					super.startDragging(controller, startPoint);
					initialWidth = (getGeometricObject()).width;
					initialHeight = (getGeometricObject()).height;
					setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane((getGeometricObject()).getSouthWestPt(),
							CardinalQuadrant.NORTH_EAST));
				}

				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					FGEPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
					setPoint(pt);

					(getGeometricObject()).y = pt.y;
					(getGeometricObject()).width = pt.x - initialPoint.x + initialWidth;
					(getGeometricObject()).height = -pt.y + initialPoint.y + initialHeight;

					nwCP1.setPoint(new FGEPoint((getGeometricObject()).x, pt.y));
					seCP2.setPoint(new FGEPoint(pt.x, (getGeometricObject()).y + (getGeometricObject()).height));

					notifyGeometryChanged();
					return true;
				}

				@Override
				public void update(FGERoundRectangle geometricObject) {
					setPoint(geometricObject.getNorthEastPt());
				}
			});
		} else {
			returned.add(new ComputedControlPoint<FGERoundRectangle>(this, "northEast", rectangle.getSouthEastPt()) {
				@Override
				public void update(FGERoundRectangle geometricObject) {
					setPoint(geometricObject.getNorthEastPt());
				}
			});
		}

		if (pc1 != null && pc2 != null) {
			returned.add(new ComputedControlPoint<FGERoundRectangle>(this, "southWest", rectangle.getSouthWestPt()) {
				private double initialWidth;
				private double initialHeight;

				@Override
				public boolean isDraggable() {
					return true;
				}

				@Override
				public void startDragging(DrawingController controller, FGEPoint startPoint) {
					super.startDragging(controller, startPoint);
					initialWidth = (getGeometricObject()).width;
					initialHeight = (getGeometricObject()).height;
					setDraggingAuthorizedArea(FGEQuarterPlane.makeFGEQuarterPlane((getGeometricObject()).getNorthEastPt(),
							CardinalQuadrant.SOUTH_WEST));
				}

				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					FGEPoint pt = getNearestPointOnAuthorizedArea(newAbsolutePoint);
					setPoint(pt);

					(getGeometricObject()).x = pt.x;
					(getGeometricObject()).width = -pt.x + initialPoint.x + initialWidth;
					(getGeometricObject()).height = pt.y - initialPoint.y + initialHeight;

					nwCP1.setPoint(new FGEPoint(pt.x, (getGeometricObject()).y));
					seCP2.setPoint(new FGEPoint((getGeometricObject()).x + (getGeometricObject()).width, pt.y));

					notifyGeometryChanged();
					return true;
				}

				@Override
				public void update(FGERoundRectangle geometricObject) {
					setPoint(geometricObject.getSouthWestPt());
				}
			});
		} else {
			returned.add(new ComputedControlPoint<FGERoundRectangle>(this, "southWest", rectangle.getSouthEastPt()) {
				@Override
				public void update(FGERoundRectangle geometricObject) {
					setPoint(geometricObject.getSouthWestPt());
				}
			});
		}

		return returned;
	}

}