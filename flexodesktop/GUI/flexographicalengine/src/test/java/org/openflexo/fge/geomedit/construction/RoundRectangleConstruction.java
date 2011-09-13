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
package org.openflexo.fge.geomedit.construction;

import org.openflexo.fge.geom.FGERoundRectangle;

public abstract class RoundRectangleConstruction extends GeometricConstruction<FGERoundRectangle> {

	private boolean isFilled;

	private double arcWidth = 30;
	private double arcHeight = 30;

	public final FGERoundRectangle getRectangle()
	{
		return getData();
	}

	@Override
	protected abstract FGERoundRectangle computeData();

	public boolean getIsFilled()
	{
		return isFilled;
	}

	public void setIsFilled(boolean isFilled)
	{
		this.isFilled = isFilled;
		setModified();
	}

	public double getArcHeight()
	{
		return arcHeight;
	}

	public void setArcHeight(double arcHeight)
	{
		this.arcHeight = arcHeight;
		setModified();
	}

	public double getArcWidth()
	{
		return arcWidth;
	}

	public void setArcWidth(double arcWidth)
	{
		this.arcWidth = arcWidth;
		setModified();
	}
}
