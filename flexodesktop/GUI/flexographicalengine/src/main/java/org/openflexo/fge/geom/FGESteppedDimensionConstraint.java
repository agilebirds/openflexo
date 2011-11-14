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
package org.openflexo.fge.geom;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;

public class FGESteppedDimensionConstraint {

	private static final Logger logger = FlexoLogger.getLogger(FGESteppedDimensionConstraint.class.getPackage().getName());

	private double hStep = 1.0;
	private double vStep = 1.0;

	public FGESteppedDimensionConstraint(double hStep, double vStep) {
		this.hStep = hStep;
		this.vStep = vStep;
	}

	public FGEDimension getNearestDimension(FGEDimension dimension, double minWidth, double maxWidth, double minHeight, double maxHeight) {
		FGEDimension d = dimension.clone();
		if (minWidth > maxWidth) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Cannot proceed with minWidth>maxWidth: " + minWidth + " " + maxWidth);
			}
			return d;
		}
		if (minHeight > maxHeight) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Cannot proceed with minHeight>maxHeight: " + minHeight + " " + maxHeight);
			}
			return d;
		}
		if (d.width > maxWidth) {
			d.width = maxWidth;
		}
		if (d.height > maxHeight) {
			d.height = maxHeight;
		}
		if (d.width < minWidth) {
			d.width = minWidth;
		}
		if (d.height < minHeight) {
			d.height = minWidth;
		}
		double lWidth = Math.floor(d.width / hStep) * hStep;
		double uWidth = lWidth + hStep;
		double width = 0.0;
		if ((Math.abs(lWidth - d.width) > Math.abs(uWidth - d.width) && uWidth < maxWidth) || lWidth < minWidth) {
			width = uWidth;
		} else {
			width = lWidth;
		}
		d.width = width;

		double lHeight = Math.floor(d.height / vStep) * vStep;
		double uHeight = lHeight + vStep;
		double height = 0.0;
		if ((Math.abs(lHeight - d.height) > Math.abs(uHeight - d.height) && uHeight < maxHeight) || lHeight < minHeight) {
			height = uHeight;
		} else {
			height = lHeight;
		}
		d.height = height;
		return d;
	}

	public double getHorizontalStep() {
		return hStep;
	}

	public double getVerticalStep() {
		return vStep;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof FGESteppedDimensionConstraint) {
			return ((FGESteppedDimensionConstraint) object).hStep == hStep && ((FGESteppedDimensionConstraint) object).vStep == vStep;
		}
		return false;
	}

	@Override
	public FGESteppedDimensionConstraint clone() {
		return new FGESteppedDimensionConstraint(hStep, vStep);
	}

}
