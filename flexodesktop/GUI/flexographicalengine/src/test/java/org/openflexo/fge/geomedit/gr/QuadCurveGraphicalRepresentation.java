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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEQuadCurve;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geomedit.ComputedControlPoint;
import org.openflexo.fge.geomedit.DraggableControlPoint;
import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.QuadCurve;
import org.openflexo.fge.geomedit.construction.ExplicitPointConstruction;
import org.openflexo.fge.geomedit.construction.PointConstruction;
import org.openflexo.fge.geomedit.construction.QuadCurveConstruction;
import org.openflexo.fge.geomedit.construction.QuadCurveWithThreePointsConstruction;
import org.openflexo.fge.graphics.FGEGeometricGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle.DashStyle;
import org.openflexo.xmlcode.XMLSerializable;

public class QuadCurveGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGEQuadCurve, QuadCurve> implements
		XMLSerializable {
	// Called for LOAD
	public QuadCurveGraphicalRepresentation(GeomEditBuilder builder) {
		this(null, builder.drawing);
		initializeDeserialization();
	}

	public QuadCurveGraphicalRepresentation(QuadCurve curve, GeometricDrawing aDrawing) {
		super(curve, aDrawing);
	}

	@Override
	public void paint(Graphics g, DrawingController controller) {
		// TODO: un petit @brutal pour avancer, il faudrait faire les choses plus proprement
		rebuildControlPoints();
		super.paint(g, controller);
	}

	@Override
	public void paintGeometricObject(FGEGeometricGraphics graphics) {
		getGeometricObject().paint(graphics);

		if (getIsSelected() || getIsFocused()) {
			// Draw construction
			FGEPoint p1 = getGeometricObject().getP1();
			FGEPoint p2 = getGeometricObject().getP2();
			FGEPoint cp = getGeometricObject().getCtrlPoint();

			FGEPoint pp1 = getGeometricObject().getPP1();
			FGEPoint pp2 = getGeometricObject().getPP2();

			graphics.setDefaultForeground(ForegroundStyle.makeStyle(Color.LIGHT_GRAY, 0.5f, DashStyle.PLAIN_STROKE));

			FGESegment line1 = new FGESegment(p1, cp);
			FGESegment line2 = new FGESegment(p2, cp);
			FGESegment line3 = new FGESegment(pp1, pp2);
			line1.paint(graphics);
			line2.paint(graphics);
			line3.paint(graphics);

			graphics.useForegroundStyle(ForegroundStyle.makeStyle(Color.RED, 1));

			graphics.drawPoint(pp1);
			graphics.drawPoint(pp2);
		}
	}

	@Override
	protected List<ControlPoint> buildControlPointsForCurve(FGEQuadCurve curve) {
		Vector<ControlPoint> returned = new Vector<ControlPoint>();

		QuadCurveConstruction curveConstruction = getDrawable().getConstruction();

		if (curveConstruction instanceof QuadCurveWithThreePointsConstruction) {
			PointConstruction startPointConstruction = ((QuadCurveWithThreePointsConstruction) curveConstruction).startPointConstruction;
			if (startPointConstruction instanceof ExplicitPointConstruction) {
				returned.add(new DraggableControlPoint<FGEQuadCurve>(this, "p1", curve.getP1(),
						(ExplicitPointConstruction) startPointConstruction) {
					@Override
					public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
							FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
						getGeometricObject().setP1(newAbsolutePoint);
						setPoint(newAbsolutePoint);
						notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(FGEQuadCurve geometricObject) {
						setPoint(geometricObject.getP1());
					}
				});
			} else {
				returned.add(new ComputedControlPoint<FGEQuadCurve>(this, "p1", curve.getP1()) {
					@Override
					public void update(FGEQuadCurve geometricObject) {
						setPoint(geometricObject.getP1());
					}
				});
			}
			PointConstruction controlPointConstruction = ((QuadCurveWithThreePointsConstruction) curveConstruction).controlPointConstruction;
			if (controlPointConstruction instanceof ExplicitPointConstruction) {
				returned.add(new DraggableControlPoint<FGEQuadCurve>(this, "cp", curve.getCtrlPoint(),
						(ExplicitPointConstruction) controlPointConstruction) {
					@Override
					public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
							FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
						getGeometricObject().setCtrlPoint(newAbsolutePoint);
						setPoint(newAbsolutePoint);
						notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(FGEQuadCurve geometricObject) {
						setPoint(geometricObject.getCtrlPoint());
					}
				});
			} else {
				returned.add(new ComputedControlPoint<FGEQuadCurve>(this, "cp", curve.getCtrlPoint()) {
					@Override
					public void update(FGEQuadCurve geometricObject) {
						setPoint(geometricObject.getCtrlPoint());
					}
				});
			}
			PointConstruction endPointConstruction = ((QuadCurveWithThreePointsConstruction) curveConstruction).endPointConstruction;
			if (endPointConstruction instanceof ExplicitPointConstruction) {
				returned.add(new DraggableControlPoint<FGEQuadCurve>(this, "p2", curve.getP2(),
						(ExplicitPointConstruction) endPointConstruction) {
					@Override
					public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
							FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
						getGeometricObject().setP2(newAbsolutePoint);
						setPoint(newAbsolutePoint);
						notifyGeometryChanged();
						return true;
					}

					@Override
					public void update(FGEQuadCurve geometricObject) {
						setPoint(geometricObject.getP2());
					}
				});
			} else {
				returned.add(new ComputedControlPoint<FGEQuadCurve>(this, "p2", curve.getP2()) {
					@Override
					public void update(FGEQuadCurve geometricObject) {
						setPoint(geometricObject.getP2());
					}
				});
			}
			returned.add(new ComputedControlPoint<FGEQuadCurve>(this, "p3", curve.getP3()) {
				@Override
				public void update(FGEQuadCurve geometricObject) {
					setPoint(geometricObject.getP3());
				}
			});

		}
		return returned;
	}

}