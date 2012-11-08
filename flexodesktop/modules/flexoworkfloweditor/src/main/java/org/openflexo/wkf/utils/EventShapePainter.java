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
package org.openflexo.wkf.utils;

import java.awt.Color;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.geom.FGECircle;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.impl.BackgroundStyleImpl;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.EventNode.EVENT_TYPE;

public class EventShapePainter implements ShapePainter {

	private EventNode model;

	public EventShapePainter(EventNode drawable) {
		super();
		model = drawable;
	}

	FGECircle _internalCircle = new FGECircle(new FGEPoint(0.516, 0.516), 0.42, Filling.NOT_FILLED);

	@Override
	public void paintShape(FGEShapeGraphics g) {
		if (model.isIntermediate() || model.isIntermediateDrop()) {
			g.drawCircle(_internalCircle.getX(), _internalCircle.getY(), _internalCircle.getWidth(), _internalCircle.getHeight());
		}
		if (model.getEventType() == EVENT_TYPE.NonInteruptiveBoundary) {
			g.drawCircle(_internalCircle.getX(), _internalCircle.getY(), _internalCircle.getWidth(), _internalCircle.getHeight(),
					FGEConstants.DASHED);
		}
		g.getDefaultForeground().setDashStyle(ForegroundStyle.DashStyle.PLAIN_STROKE);
		if (model.isMessageReceive()) {
			FGEPolygon _polygon = new FGEPolygon(Filling.NOT_FILLED);
			_polygon.addToPoints(new FGEPoint(0.225, 0.3));
			_polygon.addToPoints(new FGEPoint(0.825, 0.3));
			_polygon.addToPoints(new FGEPoint(0.825, 0.7));
			_polygon.addToPoints(new FGEPoint(0.225, 0.7));

			g.drawPolygon(_polygon);
			g.drawLine(0.225, 0.3, 0.525, 0.6);
			g.drawLine(0.825, 0.3, 0.525, 0.6);
		}

		if (model.isMessageSent()) {
			FGEPolygon _polygon = new FGEPolygon(Filling.FILLED);
			_polygon.addToPoints(new FGEPoint(0.250, 0.300));
			_polygon.addToPoints(new FGEPoint(0.525, 0.575));
			_polygon.addToPoints(new FGEPoint(0.800, 0.300));
			g.setDefaultBackground(BackgroundStyleImpl.makeColoredBackground(Color.BLACK));
			g.fillPolygon(_polygon);

			FGEPolygon _polygon2 = new FGEPolygon(Filling.FILLED);
			_polygon2.addToPoints(new FGEPoint(0.225, 0.325));
			_polygon2.addToPoints(new FGEPoint(0.225, 0.700));
			_polygon2.addToPoints(new FGEPoint(0.825, 0.700));
			_polygon2.addToPoints(new FGEPoint(0.825, 0.325));
			_polygon2.addToPoints(new FGEPoint(0.525, 0.625));

			g.fillPolygon(_polygon2);
		}

		if (model.isTriggerTimer()) {
			double xTr = 0.516;
			double yTr = 0.516;
			double innerFactor = 0.2;
			double outerFactor = 0.3;
			for (int i = 0; i < 12; i++) {
				double alpha = i * Math.PI / 6;
				double sin = Math.sin(alpha);
				double cos = Math.cos(alpha);
				double x1 = sin * innerFactor + xTr;
				double y1 = cos * innerFactor + yTr;
				double x2 = sin * outerFactor + xTr;
				double y2 = cos * outerFactor + yTr;
				g.drawLine(x1, y1, x2, y2);
			}
			FGECircle r = new FGECircle(new FGEPoint(xTr, yTr), outerFactor, Filling.NOT_FILLED);
			g.drawCircle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
			g.drawLine(xTr, yTr, 0.516, 0.355);
			g.drawLine(xTr, yTr, 0.700, 0.510);

		}

		if (model.isTriggerError()) {
			double xTr = 0.516;
			double yTr = 0.516;
			double outerFactor = 0.4;
			double alpha = Math.PI / 6;
			double[] xi = new double[4];
			double[] yi = new double[4];
			for (int i = 0; i < 4; i++) {

				double sin = Math.sin(alpha);
				double cos = Math.cos(alpha);
				double x = sin * outerFactor + xTr;
				double y = cos * outerFactor + yTr;
				xi[i] = x;
				yi[i] = y;
				alpha = alpha + Math.PI / 2;
			}
			FGEPolygon _polygon2 = new FGEPolygon(Filling.NOT_FILLED);
			_polygon2.addToPoints(new FGEPoint(xi[0], yi[0]));
			_polygon2.addToPoints(new FGEPoint(xi[1], yi[1]));
			_polygon2.addToPoints(new FGEPoint(xTr + 0.13, yTr + 0.08));
			_polygon2.addToPoints(new FGEPoint(xi[2], yi[2]));
			_polygon2.addToPoints(new FGEPoint(xi[3], yi[3]));
			_polygon2.addToPoints(new FGEPoint(xTr - 0.13, yTr - 0.08));
			if (model.getIsCatching()) {
				g.drawPolygon(_polygon2);
			} else {
				g.fillPolygon(_polygon2);

			}
		}

		if (model.isTriggerCancel()) {
			double xTr = 0.516;
			double yTr = 0.516;
			double factor = 0.25;
			double d = 0.031;
			double dprime = 0.1;
			FGEPoint p1 = new FGEPoint(factor, -factor);
			FGEPoint p2 = new FGEPoint(-factor, -factor);
			FGEPoint p3 = new FGEPoint(-factor, factor);
			FGEPoint p4 = new FGEPoint(factor, factor);
			FGEPoint p1a = new FGEPoint(p1.x + d + xTr, p1.y + d + yTr);
			FGEPoint p1b = new FGEPoint(p1.x - d + xTr, p1.y - d + yTr);
			FGEPoint p2a = new FGEPoint(p2.x + d + xTr, p2.y - d + yTr);
			FGEPoint p2b = new FGEPoint(p2.x - d + xTr, p2.y + d + yTr);
			FGEPoint p3a = new FGEPoint(p3.x - d + xTr, p3.y - d + yTr);
			FGEPoint p3b = new FGEPoint(p3.x + d + xTr, p3.y + d + yTr);
			FGEPoint p4a = new FGEPoint(p4.x - d + xTr, p4.y + d + yTr);
			FGEPoint p4b = new FGEPoint(p4.x + d + xTr, p4.y - d + yTr);

			FGEPoint pc0 = new FGEPoint(dprime + xTr, 0 + yTr);
			FGEPoint pc1 = new FGEPoint(0 + xTr, -dprime + yTr);
			FGEPoint pc2 = new FGEPoint(-dprime + xTr, 0 + yTr);
			FGEPoint pc3 = new FGEPoint(0 + xTr, dprime + yTr);

			FGEPolygon _polygon2 = new FGEPolygon(Filling.NOT_FILLED);
			_polygon2.addToPoints(p1a);
			_polygon2.addToPoints(p1b);
			_polygon2.addToPoints(pc1);

			_polygon2.addToPoints(p2a);
			_polygon2.addToPoints(p2b);
			_polygon2.addToPoints(pc2);

			_polygon2.addToPoints(p3a);
			_polygon2.addToPoints(p3b);
			_polygon2.addToPoints(pc3);

			_polygon2.addToPoints(p4a);
			_polygon2.addToPoints(p4b);
			_polygon2.addToPoints(pc0);

			if (model.getIsCatching()) {
				g.drawPolygon(_polygon2);
			} else {
				g.fillPolygon(_polygon2);

			}
		}

		if (model.isTriggerSignal()) {
			double d = 0.25;
			double xTr = 0.516;
			double yTr = 0.486;
			FGEPoint p1 = new FGEPoint(0 + xTr, -d + yTr);
			FGEPoint p2 = new FGEPoint(-d + xTr, d + yTr);
			FGEPoint p3 = new FGEPoint(d + xTr, d + yTr);
			FGEPolygon _polygon2 = new FGEPolygon(Filling.NOT_FILLED);
			_polygon2.addToPoints(p1);
			_polygon2.addToPoints(p2);
			_polygon2.addToPoints(p3);
			if (model.getIsCatching()) {
				g.drawPolygon(_polygon2);
			} else {
				g.fillPolygon(_polygon2);
			}
		}

		if (model.isTriggerEscalation()) {
			FGEPolygon _polygon2 = new FGEPolygon(Filling.NOT_FILLED);
			_polygon2.addToPoints(new FGEPoint(0.5, 0.5));
			_polygon2.addToPoints(new FGEPoint(0.65, 0.7));
			_polygon2.addToPoints(new FGEPoint(0.5, 0.2));
			_polygon2.addToPoints(new FGEPoint(0.35, 0.7));
			if (model.getIsCatching()) {
				g.drawPolygon(_polygon2);
			} else {
				g.fillPolygon(_polygon2);
			}
		}

		if (model.isTriggerMultiple()) {
			double xTr = 0.516;
			double yTr = 0.516;
			double factor = 0.35;

			FGEPolygon _polygon2 = new FGEPolygon(Filling.NOT_FILLED);

			for (int i = 0; i < 5; i++) {
				double alpha = i * 2 * Math.PI / 5 + Math.PI;
				double sin = Math.sin(alpha);
				double cos = Math.cos(alpha);
				double x1 = sin * factor + xTr;
				double y1 = cos * factor + yTr;
				_polygon2.addToPoints(new FGEPoint(x1, y1));
			}
			if (model.getIsCatching()) {
				g.drawPolygon(_polygon2);
			} else {
				g.fillPolygon(_polygon2);

			}
		}

		if (model.isTriggerMultiplePara()) {
			double xTr = 0.516;
			double yTr = 0.516;
			double factor = 0.09;
			double d = 0.17;
			double dprime = 0.1;
			FGEPoint p1 = new FGEPoint(factor + xTr, factor + yTr);
			FGEPoint p2 = new FGEPoint(factor + xTr, factor + d + yTr);
			FGEPoint p3 = new FGEPoint(-factor + xTr, factor + d + yTr);
			FGEPoint p4 = new FGEPoint(-factor + xTr, factor + yTr);
			FGEPoint p5 = new FGEPoint(-factor - d + xTr, factor + yTr);
			FGEPoint p6 = new FGEPoint(-factor - d + xTr, -factor + yTr);
			FGEPoint p7 = new FGEPoint(-factor + xTr, -factor + yTr);
			FGEPoint p8 = new FGEPoint(-factor + xTr, -factor - d + yTr);
			FGEPoint p9 = new FGEPoint(factor + xTr, -factor - d + yTr);
			FGEPoint p10 = new FGEPoint(factor + xTr, -factor + yTr);
			FGEPoint p11 = new FGEPoint(factor + d + xTr, -factor + yTr);
			FGEPoint p12 = new FGEPoint(factor + d + xTr, factor + yTr);

			FGEPolygon _polygon2 = new FGEPolygon(Filling.NOT_FILLED);
			_polygon2.addToPoints(p1);
			_polygon2.addToPoints(p2);
			_polygon2.addToPoints(p3);

			_polygon2.addToPoints(p4);
			_polygon2.addToPoints(p5);
			_polygon2.addToPoints(p6);

			_polygon2.addToPoints(p7);
			_polygon2.addToPoints(p8);
			_polygon2.addToPoints(p9);

			_polygon2.addToPoints(p10);
			_polygon2.addToPoints(p11);
			_polygon2.addToPoints(p12);

			g.drawPolygon(_polygon2);
		}

		if (model.isTriggerLink()) {
			FGEPolygon _polygon = new FGEPolygon(Filling.NOT_FILLED);
			_polygon.addToPoints(new FGEPoint(0.225, 0.37));
			_polygon.addToPoints(new FGEPoint(0.7, 0.37));
			_polygon.addToPoints(new FGEPoint(0.7, 0.25));
			_polygon.addToPoints(new FGEPoint(0.9, 0.5));
			_polygon.addToPoints(new FGEPoint(0.7, 0.75));
			_polygon.addToPoints(new FGEPoint(0.7, 0.63));
			_polygon.addToPoints(new FGEPoint(0.225, 0.63));
			if (model.getIsCatching()) {
				g.drawPolygon(_polygon);
			} else {
				g.fillPolygon(_polygon);
			}
		}

		if (model.isTriggerCompensation()) {
			FGEPolygon _polygon = new FGEPolygon(Filling.NOT_FILLED);
			_polygon.addToPoints(new FGEPoint(0.75, 0.25));
			_polygon.addToPoints(new FGEPoint(0.5, 0.5));
			_polygon.addToPoints(new FGEPoint(0.75, 0.75));

			FGEPolygon _polygon2 = new FGEPolygon(Filling.NOT_FILLED);
			_polygon2.addToPoints(new FGEPoint(0.5, 0.25));
			_polygon2.addToPoints(new FGEPoint(0.25, 0.5));
			_polygon2.addToPoints(new FGEPoint(0.5, 0.75));
			if (model.getIsCatching()) {
				g.drawPolygon(_polygon);
				g.drawPolygon(_polygon2);
			} else {
				g.fillPolygon(_polygon);
				g.fillPolygon(_polygon2);
			}
		}

		if (model.isTriggerConditional()) {
			FGEPolygon _polygon = new FGEPolygon(Filling.NOT_FILLED);
			_polygon.addToPoints(new FGEPoint(0.75, 0.25));
			_polygon.addToPoints(new FGEPoint(0.25, 0.25));
			_polygon.addToPoints(new FGEPoint(0.25, 0.75));
			_polygon.addToPoints(new FGEPoint(0.75, 0.75));

			double innerLineLeft = 0.295;
			double innerLineRight = 0.705;
			g.drawLine(innerLineLeft, 0.3, innerLineRight, 0.3);
			g.drawLine(innerLineLeft, 0.426, innerLineRight, 0.426);
			g.drawLine(innerLineLeft, 0.572, innerLineRight, 0.572);
			g.drawLine(innerLineLeft, 0.7, innerLineRight, 0.7);

			g.drawPolygon(_polygon);
		}

		if (model.isTriggerTerminate()) {
			double xTr = 0.516;
			double yTr = 0.516;
			double outerFactor = 0.28;
			FGECircle r = new FGECircle(new FGEPoint(xTr, yTr), outerFactor, Filling.FILLED);
			g.fillCircle(r.getX(), r.getY(), r.getWidth(), r.getHeight());

		}
	}
}