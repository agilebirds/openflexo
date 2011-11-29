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
package org.openflexo.fge.controller;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.controller.DrawingController.EditorTool;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.shapes.Polygon;
import org.openflexo.fge.shapes.Shape.ShapeType;

public class DrawPolygonToolController extends
		DrawShapeToolController<FGEPolygon> {

	private static final Logger logger = Logger
			.getLogger(DrawPolygonToolController.class.getPackage().getName());

	private boolean isBuildingPoints;

	public DrawPolygonToolController(DrawingController<?> controller,
			DrawShapeAction control) {
		super(controller, control);
		isBuildingPoints = true;
	}

	@Override
	public FGEPolygon makeDefaultShape() {
		return new FGEPolygon(Filling.NOT_FILLED, new FGEPoint(0, 0));
	}

	public FGEPolygon getPolygon() {
		return getShape();
	}

	@Override
	protected void geometryChanged() {
		getShape().geometryChanged();
		super.geometryChanged();
	}

	@Override
	public void setShape(FGEShape shape) {
		super.setShape(shape);
		stopMouseEdition();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		logger.info("Handle mouseClicked()");
		if (isBuildingPoints) {
			FGEPoint newPoint = getPoint(e);
			if (e.getClickCount() == 2 || e.isPopupTrigger()
					|| e.getButton() == MouseEvent.BUTTON3) {
				// System.out.println("Stopping point edition");
				getShape().getPoints().lastElement().setX(newPoint.x);
				getShape().getPoints().lastElement().setY(newPoint.y);
				stopMouseEdition();
			} else {
				getShape().addToPoints(newPoint);
			}
			getCurrentEditedShapeGR().rebuildControlPoints();
			geometryChanged();
		} else {
			// System.out.println("Done edited shape");
			getController().setCurrentTool(EditorTool.SelectionTool);
		}
	}

	private void stopMouseEdition() {
		getShape().setIsFilled(true);
		isBuildingPoints = false;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		if (isBuildingPoints && getShape().getPointsNb() > 0) {
			FGEPoint newPoint = getPoint(e);
			// logger.info("move last point to " + newPoint);
			getShape().getPoints().lastElement().setX(newPoint.x);
			getShape().getPoints().lastElement().setY(newPoint.y);
			geometryChanged();
		}
	}

	@Override
	public ShapeGraphicalRepresentation<?> buildShapeGraphicalRepresentation() {
		ShapeGraphicalRepresentation returned = new ShapeGraphicalRepresentation(
				ShapeType.CUSTOM_POLYGON, null, getController().getDrawing());
		returned.setBorder(new ShapeBorder(5, 5, 5, 5));
		returned.setBackground(getController().getCurrentBackgroundStyle());
		returned.setForeground(getController().getCurrentForegroundStyle());
		returned.setTextStyle(getController().getCurrentTextStyle());

		FGERectangle boundingBox = getPolygon().getBoundingBox();
		returned.setX(boundingBox.getX() - 5);
		returned.setY(boundingBox.getY() - 5);
		returned.setWidth(boundingBox.getWidth());
		returned.setHeight(boundingBox.getHeight());
		// System.out.println("Shape was: " + getPolygon());
		// System.out.println("Bounding box is: " + boundingBox);
		AffineTransform translateAT = AffineTransform.getTranslateInstance(
				-boundingBox.getX(), -boundingBox.getY());
		AffineTransform scaleAT = AffineTransform.getScaleInstance(
				1 / boundingBox.getWidth(), 1 / boundingBox.getHeight());
		FGEPolygon normalizedPolygon = getPolygon().transform(translateAT)
				.transform(scaleAT);
		// System.out.println("Shape is now: " + normalizedPolygon);
		returned.setShape(new Polygon(returned, normalizedPolygon));
		return returned;
	}
}
