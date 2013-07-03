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

import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolylin;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.geomedit.ComputedControlPoint;
import org.openflexo.fge.geomedit.DraggableControlPoint;
import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.Polylin;
import org.openflexo.fge.geomedit.construction.ExplicitPointConstruction;
import org.openflexo.fge.geomedit.construction.PointConstruction;
import org.openflexo.fge.geomedit.construction.PolylinConstruction;
import org.openflexo.fge.geomedit.construction.PolylinWithNPointsConstruction;
import org.openflexo.fge.graphics.FGEGeometricGraphics;
import org.openflexo.xmlcode.XMLSerializable;

public class PolylinGraphicalRepresentation extends GeometricObjectGraphicalRepresentation<FGEPolylin, Polylin> implements XMLSerializable {
	// Called for LOAD
	public PolylinGraphicalRepresentation(GeomEditBuilder builder) {
		this(null, builder.drawing);
		initializeDeserialization();
	}

	public PolylinGraphicalRepresentation(Polylin polylin, GeometricDrawing aDrawing) {
		super(polylin, aDrawing);
	}

	@Override
	public void paint(Graphics g, DrawingController controller) {
		// TODO: un petit @brutal pour avancer, il faudrait faire les choses plus proprement
		if (getGeometricObject() instanceof FGEPolylin) {
			rebuildControlPoints();
		}
		super.paint(g, controller);
	}

	// DEBUG
	@Override
	public void paintGeometricObject(FGEGeometricGraphics graphics) {
		super.paintGeometricObject(graphics);
		// System.out.println("getGeometricObject()"+getGeometricObject());
		if (getGeometricObject() instanceof FGERectPolylin) {
			FGERectPolylin rectPoly = (FGERectPolylin) getGeometricObject();
			if (rectPoly.missingPath != null) {
				graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.YELLOW, 1.0f, DashStyle.SMALL_DASHES));
				rectPoly.missingPath.paint(graphics);
			}
			graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.GREEN));
			FGERectPolylin debugPolylin = rectPoly.makeNormalizedRectPolylin();
			debugPolylin.paint(graphics);
			for (FGEPoint p : debugPolylin.getPoints()) {
				p.paint(graphics);
			}
			if (rectPoly.currentPointStartingSide != null) {
				graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.RED, 2.0f));
				rectPoly.currentPointStartingSide.paint(graphics);
				rectPoly.currentPointEndingSide.paint(graphics);
			}
			if (rectPoly.getPointsNb() == 5) {
				/*FGERectPolylin tempPoly
				= FGERectPolylin.makeShortestRectPolylin(
						rectPoly.getPointAt(1), 
						rectPoly.getPointAt(3), 
						true, 
						rectPoly.getOverlap());
				graphics.setDefaultForeground(ForegroundStyleImpl.makeStyle(Color.GRAY));
				tempPoly.paint(graphics);*/
				FGERectPolylin polylinCrossingPoint = FGERectPolylin.makeRectPolylinCrossingPoint(rectPoly.getPointAt(1),
						rectPoly.getPointAt(3), rectPoly.getPointAt(2), true, rectPoly.getOverlapX(), rectPoly.getOverlapY()
				/*,null, 
					null*/);

				graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.BLUE));
				polylinCrossingPoint.paint(graphics);

			}

		}
	}

	@Override
	protected List<ControlPoint> buildControlPointsForPolylin(FGEPolylin polylin) {
		Vector<ControlPoint> returned = new Vector<ControlPoint>();

		PolylinConstruction polylinContruction = getDrawable().getConstruction();

		if (polylinContruction instanceof PolylinWithNPointsConstruction) {

			for (int i = 0; i < ((PolylinWithNPointsConstruction) polylinContruction).pointConstructions.size(); i++) {

				final int pointIndex = i;
				PointConstruction pc = ((PolylinWithNPointsConstruction) polylinContruction).pointConstructions.get(i);

				if (pc instanceof ExplicitPointConstruction) {
					returned.add(new DraggableControlPoint<FGEPolylin>(this, "pt" + i, pc.getPoint(), (ExplicitPointConstruction) pc) {
						@Override
						public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
								FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
							getGeometricObject().getPointAt(pointIndex).x = newAbsolutePoint.x;
							getGeometricObject().getPointAt(pointIndex).y = newAbsolutePoint.y;
							setPoint(newAbsolutePoint);
							notifyGeometryChanged();
							return true;
						}

						@Override
						public void update(FGEPolylin geometricObject) {
							setPoint(geometricObject.getPointAt(pointIndex));
						}
					});
				} else {
					returned.add(new ComputedControlPoint<FGEPolylin>(this, "pt" + i, pc.getPoint()) {
						@Override
						public void update(FGEPolylin geometricObject) {
							setPoint(geometricObject.getPointAt(pointIndex));
						}
					});
				}

			}
		}

		return returned;

	}
}