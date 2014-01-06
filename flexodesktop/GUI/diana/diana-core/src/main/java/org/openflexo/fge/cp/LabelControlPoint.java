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
package org.openflexo.fge.cp;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.geom.FGEPoint;

/**
 * A {@link LabelControlPoint} encodes an interactive control point which purpose is to control a label location<br>
 * 
 * @author sylvain
 */
public class LabelControlPoint extends ControlPoint {

	public LabelControlPoint(DrawingTreeNode<?, ?> node, FGEPoint pt) {
		super(node, pt);
	}

	@Override
	public Cursor getDraggingCursor() {
		return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
	}

	@Override
	public boolean isDraggable() {
		return true;
	}

	@Override
	public void startDragging(DianaEditor<?> controller, FGEPoint startPoint) {
		super.startDragging(controller, startPoint);
	}

	@Override
	public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
			FGEPoint initialPoint, MouseEvent event) {
		return true;
	}

}
