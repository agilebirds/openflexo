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

import java.util.logging.Logger;

import org.openflexo.fge.geom.FGESteppedDimensionConstraint;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.wkf.WKFStockObject;
import org.openflexo.wkf.grsetup.StockObjectGRSetup;
import org.openflexo.wkf.processeditor.ProcessRepresentation;


public class StockObjectGR extends ArtefactGR<WKFStockObject> {

	private static final Logger logger = Logger.getLogger(StockObjectGR.class.getPackage().getName());

	public StockObjectGR(WKFStockObject dataSource, ProcessRepresentation aDrawing)
	{
		super(dataSource, ShapeType.TRIANGLE, aDrawing);
		StockObjectGRSetup.setupGR(this);
	}

	@Override
	public double getRelativeTextY() {
		return StockObjectGRSetup.getRelativeTextY(this);
	}

	@Override
	public double getRequiredWidth(double labelWidth) {
		return getRequiredSize(labelWidth);
	}

	@Override
	public double getRequiredHeight(double labelHeight) {
		return getRequiredSize(labelHeight);
	}

	/**
	 * This method computes the minimal width or minimal height so that the
	 * label is centered on the two thirds of the triangle height (beware that
	 * the triangle does not take the whole height of the shape, actually it's
	 * three quarter of it) and the bounds of the labels are strictly enclosed in the triangle
	 *
	 * @param labelWidth
	 * @return
	 */
	private double getRequiredSize(double labelWidth) {
		return StockObjectGRSetup.getRequiredSize(this, labelWidth);
	}

	@Override
	public void setWidthNoNotification(double width) {
		super.setHeightNoNotification(width);
		super.setWidthNoNotification(width);
	}

	@Override
	public void setHeightNoNotification(double height) {
		super.setWidthNoNotification(height);
		super.setHeightNoNotification(height);
	}

	@Override
	public boolean supportResizeToGrid() {
		return true;
	}

	@Override
	public FGESteppedDimensionConstraint getDimensionConstraintStep() {
		if (getDrawing()!=null) {
			FGESteppedDimensionConstraint constraint = getDrawing().getDrawingGraphicalRepresentation().getDimensionConstraintsForObject(this);
			if (constraint!=null)
				return new FGESteppedDimensionConstraint(constraint.getHorizontalStep()/(getShape().getShape().getEmbeddingBounds().width/*-getShape().getShape().getEmbeddingBounds().x*/), constraint.getVerticalStep()/(getShape().getShape().getEmbeddingBounds().height/*-getShape().getShape().getEmbeddingBounds().y*/));
			return constraint;
		}
		return null;
	}

	@Override
	protected boolean supportShadow()
	{
		return false;
	}

}
