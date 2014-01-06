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
package org.openflexo.fge.control.tools;

import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.DianaInteractiveEditor.EditorTool;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.geom.FGEComplexCurve;
import org.openflexo.fge.geom.FGEGeneralShape.Closure;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.model.undo.CompoundEdit;

/**
 * Abstract implementation for the controller of the Complex Curve drawing tool
 * 
 * @author sylvain
 * 
 * @param <ME>
 *            technology-specific controlling events type
 */
public abstract class DrawComplexCurveToolController<ME> extends DrawCustomShapeToolController<FGEComplexCurve, ME> {

	private static final Logger logger = Logger.getLogger(DrawComplexCurveToolController.class.getPackage().getName());

	private boolean isBuildingPoints;

	private CompoundEdit drawCurveEdit;

	private boolean isClosedCurve;

	public DrawComplexCurveToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction control, boolean isClosedCurve) {
		super(controller, control);
		this.isClosedCurve = isClosedCurve;
	}

	/**
	 * Return the DrawingView of the controller this tool is associated to
	 * 
	 * @return
	 */
	public DrawingView<?, ?> getDrawingView() {
		if (getController() != null) {
			return getController().getDrawingView();
		}
		return null;
	}

	@Override
	public FGEComplexCurve makeDefaultShape(ME e) {
		FGEPoint newPoint = getPoint(e);
		return new FGEComplexCurve(isClosedCurve ? Closure.CLOSED_FILLED : Closure.OPEN_FILLED, newPoint, new FGEPoint(newPoint));
	}

	public FGEComplexCurve getComplexCurve() {
		return getShape();
	}

	@Override
	protected void geometryChanged() {
		getShape().geometryChanged();
		super.geometryChanged();
	}

	@Override
	public void setShape(FGEShape<?> shape) {
		super.setShape(shape);
		stopMouseEdition();
	}

	@Override
	public boolean mouseClicked(ME e) {
		super.mouseClicked(e);
		logger.fine("Handle mouseClicked()");
		// System.out.println("Mouse clicked");
		if (!editionHasBeenStarted()) {
			startMouseEdition(e);
		} else {
			logger.info("Edition started");
			if (isBuildingPoints) {
				FGEPoint newPoint = getPoint(e);
				if (isFinalizationEvent(e)) {
					// System.out.println("Stopping point edition");
					getShape().getPoints().lastElement().setX(newPoint.x);
					getShape().getPoints().lastElement().setY(newPoint.y);
					stopMouseEdition();
					getController().setCurrentTool(EditorTool.SelectionTool);
				} else {
					// System.out.println("add point " + newPoint);
					getShape().addToPoints(newPoint);
				}
				getCurrentEditedShape().rebuildControlPoints();
				geometryChanged();
			} else {
				// System.out.println("Done edited shape");
				getController().setCurrentTool(EditorTool.SelectionTool);
			}
		}
		return true;
	}

	protected abstract boolean isFinalizationEvent(ME e);

	@Override
	protected void startMouseEdition(ME e) {
		drawCurveEdit = startRecordEdit("Draw closed curve");
		super.startMouseEdition(e);
		isBuildingPoints = true;
	}

	@Override
	protected void stopMouseEdition() {
		getShape().setIsFilled(true);
		isBuildingPoints = false;
		makeNewShape();
		super.stopMouseEdition();
		stopRecordEdit(drawCurveEdit);
	}

	@Override
	public boolean mouseMoved(ME e) {
		super.mouseMoved(e);
		// System.out.println("ShapeSpecification=" + getShape());
		if (isBuildingPoints && getShape().getPointsNb() > 0) {
			FGEPoint newPoint = getPoint(e);
			// logger.info("move last point to " + newPoint);
			getShape().getPoints().lastElement().setX(newPoint.x);
			getShape().getPoints().lastElement().setY(newPoint.y);
			geometryChanged();
			return true;
		}
		return false;
	}

	@Override
	public ShapeGraphicalRepresentation buildShapeGraphicalRepresentation() {
		ShapeGraphicalRepresentation returned = getController().getFactory().makeShapeGraphicalRepresentation(ShapeType.COMPLEX_CURVE);
		returned.setBorder(getController().getFactory().makeShapeBorder(FGEConstants.DEFAULT_BORDER_SIZE, FGEConstants.DEFAULT_BORDER_SIZE,
				FGEConstants.DEFAULT_BORDER_SIZE, FGEConstants.DEFAULT_BORDER_SIZE));
		returned.setBackground(getController().getInspectedBackgroundStyle().cloneStyle());
		returned.setForeground(getController().getInspectedForegroundStyle().cloneStyle());
		returned.setTextStyle(getController().getInspectedTextStyle().cloneStyle());
		returned.setAllowToLeaveBounds(false);
		returned.setIsFloatingLabel(false);
		returned.setRelativeTextX(0.5);
		returned.setRelativeTextY(0.5);

		FGERectangle boundingBox = getComplexCurve().getBoundingBox();
		returned.setWidth(boundingBox.getWidth());
		returned.setHeight(boundingBox.getHeight());
		AffineTransform translateAT = AffineTransform.getTranslateInstance(-boundingBox.getX(), -boundingBox.getY());

		AffineTransform scaleAT = AffineTransform.getScaleInstance(1 / boundingBox.getWidth(), 1 / boundingBox.getHeight());
		FGEComplexCurve normalizedCurve = getComplexCurve().transform(translateAT).transform(scaleAT);
		if (parentNode instanceof ShapeGraphicalRepresentation) {
			FGEPoint pt = FGEUtils.convertNormalizedPoint(parentNode, new FGEPoint(0, 0), getController().getDrawing().getRoot());
			returned.setX(boundingBox.getX() - pt.x);
			returned.setY(boundingBox.getY() - pt.y);
		} else {
			returned.setX(boundingBox.getX() - FGEConstants.DEFAULT_BORDER_SIZE);
			returned.setY(boundingBox.getY() - FGEConstants.DEFAULT_BORDER_SIZE);
		}

		returned.setShapeSpecification(getController().getFactory().makeComplexCurve(normalizedCurve));
		return returned;
	}

}
