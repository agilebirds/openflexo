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

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController.EditorTool;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;

public class DrawPolygonToolController extends DrawShapeToolController<FGEPolygon> {

	private static final Logger logger = Logger.getLogger(DrawPolygonToolController.class.getPackage().getName());

	private boolean isBuildingPoints;

	public DrawPolygonToolController(DrawingController controller, DrawShapeAction control) {
		super(controller, control);
	}

	@Override
	public FGEPolygon makeDefaultShape(MouseEvent e) {
		Point pt = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), getController().getDrawingView());
		FGEPoint newPoint = getController().getDrawingGraphicalRepresentation().convertRemoteViewCoordinatesToLocalNormalizedPoint(pt,
				getController().getDrawingGraphicalRepresentation(), getController().getScale());
		return new FGEPolygon(Filling.NOT_FILLED, newPoint, new FGEPoint(newPoint));
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
		logger.fine("Handle mouseClicked()");
		// System.out.println("Mouse clicked");
		if (!editionHasBeenStarted()) {
			startMouseEdition(e);
		} else {
			logger.info("Edition started");
			if (isBuildingPoints) {
				FGEPoint newPoint = getPoint(e);
				if (e.getClickCount() == 2 || e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
					// System.out.println("Stopping point edition");
					getShape().getPoints().lastElement().setX(newPoint.x);
					getShape().getPoints().lastElement().setY(newPoint.y);
					stopMouseEdition();
					getController().setCurrentTool(EditorTool.SelectionTool);
				} else {
					// System.out.println("add point " + newPoint);
					getShape().addToPoints(newPoint);
				}
				getCurrentEditedShapeGR().rebuildControlPoints();
				geometryChanged();
			} else {
				// System.out.println("Done edited shape");
				getController().setCurrentTool(EditorTool.SelectionTool);
			}
		}
	}

	@Override
	protected void startMouseEdition(MouseEvent e) {
		super.startMouseEdition(e);
		isBuildingPoints = true;
	}

	@Override
	protected void stopMouseEdition() {
		super.stopMouseEdition();
		getShape().setIsFilled(true);
		isBuildingPoints = false;
		makeNewShape();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		// System.out.println("ShapeSpecification=" + getShape());
		if (isBuildingPoints && getShape().getPointsNb() > 0) {
			FGEPoint newPoint = getPoint(e);
			// logger.info("move last point to " + newPoint);
			getShape().getPoints().lastElement().setX(newPoint.x);
			getShape().getPoints().lastElement().setY(newPoint.y);
			geometryChanged();
		}
	}

	@Override
	public ShapeGraphicalRepresentation buildShapeGraphicalRepresentation() {
		ShapeGraphicalRepresentation returned = getController().getFactory().makeShapeGraphicalRepresentation(ShapeType.CUSTOM_POLYGON,
				null, getController().getDrawing());
		returned.setBorder(getController().getFactory().makeShapeBorder(FGEConstants.DEFAULT_BORDER_SIZE, FGEConstants.DEFAULT_BORDER_SIZE,
				FGEConstants.DEFAULT_BORDER_SIZE, FGEConstants.DEFAULT_BORDER_SIZE));
		returned.setBackground(getController().getCurrentBackgroundStyle());
		returned.setForeground(getController().getCurrentForegroundStyle());
		returned.setTextStyle(getController().getCurrentTextStyle());
		returned.setAllowToLeaveBounds(false);

		FGERectangle boundingBox = getPolygon().getBoundingBox();
		returned.setWidth(boundingBox.getWidth());
		returned.setHeight(boundingBox.getHeight());
		AffineTransform translateAT = AffineTransform.getTranslateInstance(-boundingBox.getX(), -boundingBox.getY());

		AffineTransform scaleAT = AffineTransform.getScaleInstance(1 / boundingBox.getWidth(), 1 / boundingBox.getHeight());
		FGEPolygon normalizedPolygon = getPolygon().transform(translateAT).transform(scaleAT);
		if (parentGR instanceof ShapeGraphicalRepresentation) {
			FGEPoint pt = FGEUtils
					.convertNormalizedPoint(parentGR, new FGEPoint(0, 0), getController().getDrawingGraphicalRepresentation());
			returned.setX(boundingBox.getX() - pt.x);
			returned.setY(boundingBox.getY() - pt.y);
		} else {
			returned.setX(boundingBox.getX() - FGEConstants.DEFAULT_BORDER_SIZE);
			returned.setY(boundingBox.getY() - FGEConstants.DEFAULT_BORDER_SIZE);
		}
		returned.setShape(getController().getFactory().makePolygon(returned, normalizedPolygon));
		return returned;
	}
}
