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

import org.openflexo.foundation.wkf.action.WKFMove;


/**
 * Notification fired when a WKFRepresentable object will be relocated in a
 * succession of many relocation (dragging)
 * 
 * @author sguerin
 * @deprecated
 * 
 */
@Deprecated
public class ObjectWillMove extends WKFDataModification
{
	
	private String context = null;
	private WKFMove _moveAction;

    public ObjectWillMove(Point initialLocation, WKFMove moveAction, String context)
    {
        super(initialLocation, null);
        _moveAction = moveAction;
        this.context = context;
    }

    public Point getInitialLocation()
    {
        return (Point) oldValue();
    }

	public WKFMove getMoveAction() 
	{
		return _moveAction;
	}

	public String getContext() 
	{
		return context;
	}

}
