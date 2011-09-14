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

import org.openflexo.fge.FGEUtils;
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
	private static final int NUMBER_OF_CYLINDER = 6;
	private static final Color ODD_COLOR = new Color(233,235,53);
	private static final Color EVEN_COLOR = new Color(211,101,38);
	public static final BackgroundStyle EVEN_BACKGROUND = BackgroundStyle.makeColorGradientBackground(EVEN_COLOR, FGEUtils.mergeColors(EVEN_COLOR, ODD_COLOR), BackgroundStyle.ColorGradient.ColorGradientDirection.NORTH_SOUTH);
	public static final BackgroundStyle ODD_BACKROUND=BackgroundStyle.makeColorGradientBackground(ODD_COLOR, FGEUtils.mergeColors(EVEN_COLOR, ODD_COLOR), BackgroundStyle.ColorGradient.ColorGradientDirection.NORTH_SOUTH);
	public static final ForegroundStyle NO_FOREGROUND = ForegroundStyle.makeNone();
	//private static final ForegroundStyle ODD_FOREGROUND = ForegroundStyle.makeStyle(ODD_COLOR);

	//private boolean isUpdatingPosition = false;

	public DataSourceGR(WKFDataSource dataSource, ProcessRepresentation aDrawing)
	{
		super(dataSource, ShapeType.RECTANGLE, aDrawing);
		setIsFloatingLabel(true);
		setForeground(ForegroundStyle.makeNone());
		setBackground(BackgroundStyle.makeEmptyBackground());
		setMinimalWidth(10);
		setMinimalHeight(10);
		setShapePainter(new ShapePainter() {
			@Override
			public void paintShape(FGEShapeGraphics g) {
				double height = 2.0/(NUMBER_OF_CYLINDER+1);
				double halfHeight = height/2;
				for(int i=NUMBER_OF_CYLINDER;i>0;i--) {
					g.setDefaultForeground(NO_FOREGROUND);
					g.setDefaultBackground(i%2==0?EVEN_BACKGROUND:ODD_BACKROUND);
					g.useDefaultBackgroundStyle();
					//g.useDefaultForegroundStyle();
					g.fillCircle(0, (i-1)*halfHeight, 1, height);
					if (i>1)
						g.fillRect(0, (i-1)*halfHeight, 1, halfHeight);
				}
			}
		});
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
		if (getDrawing()!=null)
			return getDrawing().getDrawingGraphicalRepresentation().getDimensionConstraintsForObject(this);
		return null;
	}

	@Override
	protected boolean supportShadow()
	{
		return false;
	}

	@Override
	public void update (FlexoObservable observable, DataModification dataModification)
	{
		super.update(observable, dataModification);
	}

}
