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
package org.openflexo.wkf.swleditor.gr;

import java.awt.Color;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ShapeResizingControlPoint;
import org.openflexo.fge.geom.FGEGeometricObject.CardinalDirection;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle.CapStyle;
import org.openflexo.fge.graphics.ForegroundStyle.DashStyle;
import org.openflexo.fge.graphics.ForegroundStyle.JoinStyle;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.shapes.RegularPolygon;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.WKFDataObject;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.toolbox.ConcatenedList;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;
import org.openflexo.wkf.utils.DataObjectShapePainter;

public class DataObjectGR extends ArtefactGR<WKFDataObject> {

	private static final Logger logger = Logger.getLogger(DataObjectGR.class.getPackage().getName());

	private static final BackgroundStyle BACKGROUND = BackgroundStyle.makeColoredBackground(Color.WHITE);
	private static final ForegroundStyle FOREGROUND = ForegroundStyle.makeStyle(new Color(0, 34, 73), 1.6f, JoinStyle.JOIN_ROUND,
			CapStyle.CAP_ROUND, DashStyle.PLAIN_STROKE);

	private static final double height = 1;
	private static final double width = height / FGEUtils.PHI;
	private static final double folding = 0.2;

	private static final FGEPoint topLeftCorner = new FGEPoint((1 - width) / 2, (1 - height) / 2);
	private static final FGEPoint bottomLeftCorner = new FGEPoint(topLeftCorner.x, 1 - topLeftCorner.y);
	private static final FGEPoint middleLeftPoint = new FGEPoint(topLeftCorner.x, (topLeftCorner.y + bottomLeftCorner.y) / 2);
	private static final FGEPoint topRightCorner = new FGEPoint(1 - topLeftCorner.x, topLeftCorner.y);
	private static final FGEPoint bottomRightCorner = new FGEPoint(topRightCorner.x, bottomLeftCorner.y);
	private static final FGEPoint middleRightPoint = new FGEPoint(topRightCorner.x, (topRightCorner.y + bottomRightCorner.y) / 2);

	private static final FGEPoint foldingPoint = new FGEPoint(topRightCorner.x - folding, topRightCorner.y + folding);
	private static final FGEPoint topFoldingPoint = new FGEPoint(foldingPoint.x, topRightCorner.y);
	private static final FGEPoint rightFoldingPoint = new FGEPoint(topRightCorner.x, foldingPoint.y);
	public static final FGEPolygon fileShape = new FGEPolygon(Filling.FILLED, topLeftCorner, topFoldingPoint, rightFoldingPoint,
			bottomRightCorner, bottomLeftCorner);

	private ConcatenedList<ControlArea<?>> controlAreas;

	private DataObjectShapePainter dataObjectShapePainter;

	public DataObjectGR(WKFDataObject dataSource, SwimmingLaneRepresentation aDrawing) {
		super(dataSource, ShapeType.POLYGON, aDrawing);
		((RegularPolygon) getShape()).setPoints(fileShape.getPoints());
		setIsFloatingLabel(true);
		setForeground(FOREGROUND);
		setBackground(BACKGROUND);
		setMinimalWidth(10);
		setMinimalHeight(10);
		dataObjectShapePainter = new DataObjectShapePainter(this);
		setShapePainter(new ShapePainter() {
			@Override
			public void paintShape(FGEShapeGraphics g) {
				g.setDefaultForeground(FOREGROUND);
				g.useDefaultForegroundStyle();
				g.drawLine(topFoldingPoint, foldingPoint);
				g.drawLine(foldingPoint, rightFoldingPoint);
				dataObjectShapePainter.paintShape(g);
			}
		});
		updateControlAreas();
	}

	@Override
	public double getDefaultWidth() {
		return 50;
	}

	@Override
	public double getDefaultHeight() {
		return 50;
	}

	@Override
	public void notifyObjectHasResized() {
		super.notifyObjectHasResized();
		updateControlAreas();
	}

	private void updateControlAreas() {
		controlAreas = new ConcatenedList<ControlArea<?>>();
		controlAreas.addElementList(super.getControlAreas());
		controlAreas.addElement(new ShapeResizingControlPoint(this, middleLeftPoint, CardinalDirection.WEST));
		controlAreas.addElement(new ShapeResizingControlPoint(this, middleRightPoint, CardinalDirection.EAST));
	}

	@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		return controlAreas;
	}

	@Override
	protected boolean supportShadow() {
		return false;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		super.update(observable, dataModification);
		if (dataModification instanceof WKFAttributeDataModification) {
			String propertyName = ((WKFAttributeDataModification) dataModification).propertyName();
			if (WKFDataObject.IS_COLLECTION.equals(propertyName)) {
				notifyShapeNeedsToBeRedrawn();
			} else if (WKFDataObject.TYPE.equals(propertyName)) {
				notifyShapeNeedsToBeRedrawn();
			}
		}
	}

}
