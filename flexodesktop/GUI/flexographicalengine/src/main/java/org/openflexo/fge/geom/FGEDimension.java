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

import java.awt.geom.Dimension2D;

public class FGEDimension extends Dimension2D {

	public double width;
	public double height;
	
	public FGEDimension(double aWidth, double aHeight) 
	{
		super();
		width = aWidth;
		height = aHeight;
	}

	@Override
	public double getHeight()
	{
		return height;
	}

	@Override
	public double getWidth() 
	{
		return width;
	}

	@Override
	public void setSize(double aWidth, double aHeight) 
	{
		width = aWidth;
		height = aHeight;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof FGEDimension) {
			FGEDimension s = (FGEDimension) obj;
			return (Math.abs(getWidth()-s.getWidth()) < FGEGeometricObject.EPSILON)
			&& (Math.abs(getHeight()-s.getHeight()) <  FGEGeometricObject.EPSILON);
		}
		return super.equals(obj);
	}

	@Override
	public FGEDimension clone() {
		return new FGEDimension(width,height);
	}

	@Override
	public String toString() {
		return "[FGEDimesion] "+width+"x"+height;
	}
}
