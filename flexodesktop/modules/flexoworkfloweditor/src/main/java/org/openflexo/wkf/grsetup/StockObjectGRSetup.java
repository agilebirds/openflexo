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
package org.openflexo.wkf.grsetup;

import java.awt.Dimension;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.foundation.wkf.WKFStockObject;

public class StockObjectGRSetup {

	public static void setupGR(ShapeGraphicalRepresentation<WKFStockObject> gr) {
		gr.setIsFloatingLabel(true);
		gr.setForeground(ForegroundStyle.makeDefault());
		gr.setBackground(BackgroundStyle.makeEmptyBackground());
		gr.setMinimalWidth(10);
		gr.setMinimalHeight(10);
		gr.setAdjustMinimalHeightToLabelHeight(false);
		gr.setAdjustMinimalWidthToLabelWidth(false);
	}

	public static double getRelativeTextY(ShapeGraphicalRepresentation<WKFStockObject> gr) {
		return 2.0 / 3.0 * gr.getShape().getShape().getEmbeddingBounds().getHeight();
	}

	public static double getRequiredSize(ShapeGraphicalRepresentation<WKFStockObject> gr, double labelWidth) {
		Dimension d = gr.getNormalizedLabelSize();
		double minWidth = 3.0 / 2.0 * (d.width + d.height / 2.0);
		minWidth /= 1 - gr.getShape().getShape().getEmbeddingBounds().x;
		double minHeight = 3.0 / 2.0 * d.height / gr.getShape().getShape().getEmbeddingBounds().getHeight();
		return Math.max(minWidth, minHeight);
	}
}
