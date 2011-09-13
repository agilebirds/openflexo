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
package org.openflexo.foundation.wkf.dm;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Notification fired when a WKFRepresentable object is relocated
 * 
 * @author sguerin
 * 
 */
public class ObjectLocationChanged extends WKFDataModification
{

	private String context = null;
    private Point initialLocation = null;

    public ObjectLocationChanged(Point2D oldLocation, Point2D newLocation, String context)
    {
        super(oldLocation, newLocation);
        this.context = context;
    }

    @Deprecated
    public ObjectLocationChanged(Point2D oldLocation, Point2D newLocation, Point initialLocation, String context)
    {
        this(oldLocation, newLocation, context);
        this.initialLocation = initialLocation;
    }

    @Deprecated
    public Point getInitialLocation()
    {
        return initialLocation;
    }

	public String getContext() 
	{
		return context;
	}
}
