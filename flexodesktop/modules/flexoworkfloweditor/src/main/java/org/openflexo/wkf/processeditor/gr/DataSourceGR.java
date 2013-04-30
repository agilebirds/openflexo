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
package org.openflexo.wkf.processeditor.gr;

import java.awt.Color;
import java.util.logging.Logger;

import org.openflexo.fge.geom.FGESteppedDimensionConstraint;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.WKFDataSource;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public class DataSourceGR extends ArtefactGR<WKFDataSource> {

	private static final Logger logger = Logger.getLogger(DataSourceGR.class.getPackage().getName());

	public static final ForegroundStyle NO_FOREGROUND = ForegroundStyle.makeNone();

	// private static final ForegroundStyle ODD_FOREGROUND = ForegroundStyle.makeStyle(ODD_COLOR);

	// private boolean isUpdatingPosition = false;

	public DataSourceGR(WKFDataSource dataSource, ProcessRepresentation aDrawing) {
		super(dataSource, ShapeType.RECTANGLE, aDrawing);
		setIsFloatingLabel(true);
		setForeground(ForegroundStyle.makeNone());
		setBackground(BackgroundStyle.makeColoredBackground(Color.WHITE));
		setMinimalWidth(10);
		setMinimalHeight(10);
		setShapePainter(new ShapePainter() {
			@Override
			public void paintShape(FGEShapeGraphics g) {
				g.useForegroundStyle(ForegroundStyle.makeStyle(Color.BLACK));
				double height = 0.375;
				g.drawCircle(0, 0, 1, height);
				g.drawArc(0, 0.075, 1, height, 180, 180);
				g.drawArc(0, 0.15, 1, height, 180, 180);
				g.drawArc(0, 1 - height, 1, height, 180, 180);
				g.drawLine(0, height / 2, 0, 1 - height / 2);
				g.drawLine(1, height / 2, 1, 1 - height / 2);
			}
		});
	}

	@Override
	public double getDefaultWidth() {
		return 55;
	}

	@Override
	public double getDefaultHeight() {
		return 40;
	}

	@Override
	public boolean supportAlignOnGrid() {
		return true;
	}

	@Override
	public boolean supportResizeToGrid() {
		return true;
	}

	@Override
	public FGESteppedDimensionConstraint getDimensionConstraintStep() {
		if (getDrawing() != null) {
			return getDrawing().getDrawingGraphicalRepresentation().getDimensionConstraintsForObject(this);
		}
		return null;
	}

	@Override
	protected boolean supportShadow() {
		return false;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		super.update(observable, dataModification);
	}

}
